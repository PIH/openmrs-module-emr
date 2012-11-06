/*
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

package org.openmrs.module.emr.task;

import org.openmrs.Privilege;
import org.openmrs.api.OpenmrsService;
import org.openmrs.module.emr.EmrContext;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * API functionality related to Tasks
 * @see TaskDescriptor
 */
public interface TaskService extends OpenmrsService {

    /**
     * @param context
     * @return all tasks available for the given context
     */
    List<TaskDescriptor> getAvailableTasks(EmrContext context);

    /**
     * Creates a privilege for task, if it doesn't already exist.
     *
     * This is a utility method intended for internal use by this module. Do not call it yourself from client code.
     *
     * @param task
     * @return the existing or newly-created privilege
     */
    @Transactional
    Privilege ensurePrivilegeExistsInternal(TaskDescriptor task);

    /**
     * Sets the complete list of tasks available.
     *
     * This is a utility method intended for internal use by this module. Do not call it yourself from client code.
     *
     * @param tasks
     */
    void setAllTasksInternal(List<TaskDescriptor> tasks);

    /**
     * Gets the task with the given taskId
     *
     * @param taskId
     */
    TaskDescriptor getTask(String taskId);

}
