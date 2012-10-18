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
import org.openmrs.api.UserService;
import org.openmrs.module.emr.EmrContext;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.ArrayList;
import java.util.List;

/**
 * @see TaskService
 */
public class TaskServiceImpl implements TaskService {

    @Autowired
    UserService userService;

    List<TaskDescriptor> allTasks;

    @Override
    public List<TaskDescriptor> getAvailableTasks(EmrContext context) {
        List<TaskDescriptor> available = new ArrayList<TaskDescriptor>();
        for (TaskDescriptor candidate : allTasks) {
            if (candidate.isAvailable(context)) {
                available.add(candidate);
            }
        }
        return available;
    }

    @Override
    public Privilege ensurePrivilegeExistsInternal(TaskDescriptor task) {
        String privName = task.getRequiredPrivilegeName();
        if (privName == null) {
            return null;
        }
        Privilege priv = userService.getPrivilege(privName);
        if (priv == null) {
            priv = new Privilege();
            priv.setPrivilege(task.getRequiredPrivilegeName());
        }
        priv.setDescription("Access the " + task.getId() + " task");
        userService.savePrivilege(priv);
        return priv;
    }

    @Override
    public void setAllTasksInternal(List<TaskDescriptor> tasks) {
        this.allTasks = tasks;
    }
}
