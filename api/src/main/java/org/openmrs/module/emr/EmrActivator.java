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
import org.openmrs.api.context.Context;
import org.openmrs.module.ModuleActivator;
import org.openmrs.module.emr.task.TaskDescriptor;
import org.openmrs.module.emr.task.TaskFactory;
import org.openmrs.module.emr.task.TaskService;
import org.openmrs.module.emrapi.adt.CloseStaleVisitsTask;
import org.openmrs.module.emrapi.utils.GeneralUtils;
import org.openmrs.scheduler.SchedulerException;
import org.openmrs.scheduler.SchedulerService;
import org.openmrs.scheduler.TaskDefinition;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * This class contains the logic that is run every time this module is either started or stopped.
 */
public class EmrActivator implements ModuleActivator {

    protected Log log = LogFactory.getLog(getClass());

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

    /**
     * @see ModuleActivator#started()
     */
    public void started() {
        log.info("EMR Module started");
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
        log.info("EMR Module stopped");
    }

}
