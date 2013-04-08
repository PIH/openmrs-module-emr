/**
 * The contents of this file are subject to the OpenMRS Public License
 * Version 1.0 (the "License"); you may not use this file except in
 * compliance with the License. You may obtain a copy of the License at
 * http://license.openmrs.org
 *
 * Software distributed under the License is distributed on an "AS IS"
 * basis, WITHOUT WARRANTY OF ANY KIND, either express or implied. See the
 * License for the specific language governing rights and limitations
 * under the License.
 *
 * Copyright (C) OpenMRS, LLC.  All Rights Reserved.
 */
package org.openmrs.module.emr;


import org.apache.commons.lang.time.DateUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openmrs.ConceptSource;
import org.openmrs.PersonAttributeType;
import org.openmrs.api.ConceptService;
import org.openmrs.api.LocationService;
import org.openmrs.api.PersonService;
import org.openmrs.api.context.Context;
import org.openmrs.module.Module;
import org.openmrs.module.ModuleActivator;
import org.openmrs.module.ModuleFactory;
import org.openmrs.module.emr.htmlformentry.UiMessageTagHandler;
import org.openmrs.module.emr.task.TaskDescriptor;
import org.openmrs.module.emr.task.TaskFactory;
import org.openmrs.module.emr.task.TaskService;
import org.openmrs.module.emrapi.adt.CloseStaleVisitsTask;
import org.openmrs.module.emrapi.utils.GeneralUtils;
import org.openmrs.module.htmlformentry.HtmlFormEntryService;
import org.openmrs.scheduler.SchedulerException;
import org.openmrs.scheduler.SchedulerService;
import org.openmrs.scheduler.TaskDefinition;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static org.openmrs.module.emr.EmrConstants.EMR_MODULE_ID;
import static org.openmrs.module.emr.EmrConstants.TEST_PATIENT_ATTRIBUTE_UUID;

/**
 * This class contains the logic that is run every time this module is either started or stopped.
 */
public class EmrActivator implements ModuleActivator {

    protected Log log = LogFactory.getLog(getClass());
    private PersonService personService;

    /**
     * @see ModuleActivator#willRefreshContext()
     */
    public void willRefreshContext() {
        log.info("Refreshing EMR Module");
    }

    /**
     * @see ModuleActivator#contextRefreshed()
     */
    public void contextRefreshed() {
        TaskService taskService = Context.getService(TaskService.class);

        List<TaskDescriptor> allTasks = new ArrayList<TaskDescriptor>();
        allTasks.addAll(Context.getRegisteredComponents(TaskDescriptor.class));

        Set<String> ids = new HashSet<String>();
        for (TaskDescriptor task : allTasks) {
            if (ids.contains(task.getId()))
                log.warn("Found multiple tasks with id: " + task.getId());
            else
                ids.add(task.getId());
        }

        taskService.setAllTasksInternal(allTasks);
        for (TaskDescriptor task : allTasks) {
            taskService.ensurePrivilegeExistsInternal(task);
        }

        List<TaskFactory> allTaskFactories = Context.getRegisteredComponents(TaskFactory.class);
        taskService.setAllTaskFactoriesInternal(allTaskFactories);

        log.info("EMR Module refreshed. " + allTasks.size() + " tasks and " + allTaskFactories.size() + " task factories available.");

        ensureScheduledTasks();
    }

    private void ensureScheduledTasks() {
        SchedulerService schedulerService = Context.getSchedulerService();
        TaskDefinition task = schedulerService.getTaskByName(EmrConstants.TASK_CLOSE_STALE_VISITS_NAME);
        if (task == null) {
            task = new TaskDefinition();
            task.setName(EmrConstants.TASK_CLOSE_STALE_VISITS_NAME);
            task.setDescription(EmrConstants.TASK_CLOSE_STALE_VISITS_DESCRIPTION);
            task.setTaskClass(CloseStaleVisitsTask.class.getName());
            task.setStartTime(DateUtils.addMinutes(new Date(), 5));
            task.setRepeatInterval(EmrConstants.TASK_CLOSE_STALE_VISITS_REPEAT_INTERVAL);
            task.setStartOnStartup(true);
            try {
                schedulerService.scheduleTask(task);
            } catch (SchedulerException e) {
                throw new RuntimeException("Failed to schedule close stale visits task", e);
            }
        } else {
            // if you modify any of the properties above, you also need to set them here, in order to update existing servers
            boolean changed = GeneralUtils.setPropertyIfDifferent(task, "taskClass", CloseStaleVisitsTask.class.getName());
            if (changed) {
                schedulerService.saveTask(task);
            }

            if (!task.getStarted()) {
                task.setStarted(true);
                try {
                    schedulerService.scheduleTask(task);
                } catch (SchedulerException e) {
                    throw new RuntimeException("Failed to schedule close stale visits task", e);
                }
            }
        }
    }

    /**
     * @see ModuleActivator#willStart()
     */
    public void willStart() {
        log.info("Starting EMR Module");
    }

    private PersonAttributeType buildTestPersonAttributeType() {
        PersonAttributeType personAttributeType = new PersonAttributeType();
        personAttributeType.setName("Test Patient");
        personAttributeType.setDescription("Flag to describe if the patient was created to a test or not");
        personAttributeType.setUuid(TEST_PATIENT_ATTRIBUTE_UUID);
        personAttributeType.setFormat("java.lang.Boolean");


        return personAttributeType;
    }

    /**
     * @see ModuleActivator#started()
     */
    public void started() {

        try {
            LocationService locationService = Context.getLocationService();
            HtmlFormEntryService htmlFormEntryService = Context.getService(HtmlFormEntryService.class);
            ConceptService conceptService = Context.getConceptService();
            personService = Context.getPersonService();

            saveTestPatientAttribute();

            createConceptSources(conceptService);

            htmlFormEntryService.addHandler(EmrConstants.HTMLFORMENTRY_UI_MESSAGE_TAG_NAME, new UiMessageTagHandler());
        } catch (Exception e) {
            Module mod = ModuleFactory.getModuleById(EMR_MODULE_ID);
            ModuleFactory.stopModule(mod);
            throw new RuntimeException("failed to setup the EMR modules", e);
        }

        log.info("EMR Module started");
    }

    private void saveTestPatientAttribute() {
        PersonAttributeType personAttributeTypeByUuid = personService.getPersonAttributeTypeByUuid(TEST_PATIENT_ATTRIBUTE_UUID);

        if (personAttributeTypeByUuid == null) {
            personService.savePersonAttributeType(buildTestPersonAttributeType());
        }
    }

    /**
     * (public so that it can be used in tests, but you shouldn't use this in production code)
     * Creates a single ConceptSource which we will use to tag concepts relevant to this module
     *
     * @param conceptService
     */
    public ConceptSource createConceptSources(ConceptService conceptService) {
        ConceptSource conceptSource = conceptService.getConceptSourceByName(EmrConstants.EMR_CONCEPT_SOURCE_NAME);
        if (conceptSource == null) {
            conceptSource = new ConceptSource();
            conceptSource.setName(EmrConstants.EMR_CONCEPT_SOURCE_NAME);
            conceptSource.setDescription(EmrConstants.EMR_CONCEPT_SOURCE_DESCRIPTION);
            conceptSource.setUuid(EmrConstants.EMR_CONCEPT_SOURCE_UUID);
            conceptService.saveConceptSource(conceptSource);
        }
        return conceptSource;
    }

    /**
     * @see ModuleActivator#willStop()
     */
    public void willStop() {
        log.info("Stopping EMR Module");
    }

    /**
     * @see ModuleActivator#stopped()
     */
    public void stopped() {
        HtmlFormEntryService htmlFormEntryService = Context.getService(HtmlFormEntryService.class);
        try {
            htmlFormEntryService.getHandlers().remove(EmrConstants.HTMLFORMENTRY_UI_MESSAGE_TAG_NAME);
        } catch (Exception ex) {
            // pass
        }
        log.info("EMR Module stopped");
    }

}
