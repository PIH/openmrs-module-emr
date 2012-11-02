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


import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openmrs.GlobalProperty;
import org.openmrs.api.AdministrationService;
import org.openmrs.api.context.Context;
import org.openmrs.module.ModuleActivator;
import org.openmrs.module.emr.adt.EmrVisitAssignmentHandler;
import org.openmrs.module.emr.task.TaskDescriptor;
import org.openmrs.module.emr.task.TaskService;
import org.openmrs.module.emr.utils.GeneralUtils;
import org.openmrs.util.OpenmrsConstants;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * This class contains the logic that is run every time this module is either started or stopped.
 */
public class EMRActivator implements ModuleActivator {
	
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

		log.info("EMR Module refreshed. " + allTasks.size() + " tasks available.");
        if (log.isDebugEnabled()) {
            for (TaskDescriptor task : allTasks)
                log.debug(task.getId() + " (" + task.getClass().getName() + ")");
        }
        if(!GeneralUtils.addPrivilegeFullRole()){
        	log.error("failed to add Privilege Level: Full role");
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
        // When https://tickets.openmrs.org/browse/TRUNK-3773 is resolved, refactor this
        AdministrationService administrationService = Context.getAdministrationService();
        GlobalProperty gp = administrationService.getGlobalPropertyObject(OpenmrsConstants.GP_VISIT_ASSIGNMENT_HANDLER);
        if (gp == null) {
            gp = new GlobalProperty();
            gp.setProperty(OpenmrsConstants.GP_VISIT_ASSIGNMENT_HANDLER);
        }
        gp.setPropertyValue(EmrVisitAssignmentHandler.class.getName());
        administrationService.saveGlobalProperty(gp);

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
