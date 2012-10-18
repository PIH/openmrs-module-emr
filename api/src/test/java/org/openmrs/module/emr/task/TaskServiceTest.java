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

import org.junit.Assert;
import org.junit.Test;
import org.openmrs.api.context.UserContext;
import org.openmrs.module.emr.EmrContext;

import java.util.ArrayList;
import java.util.List;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class TaskServiceTest {

    @Test
    public void shouldGetAvailableTasks() {
        TaskServiceImpl service = new TaskServiceImpl();

        List<TaskDescriptor> allTasks = new ArrayList<TaskDescriptor>();
        SimpleTaskDescriptor taskA = new SimpleTaskDescriptor();
        taskA.setId("A");
        allTasks.add(taskA);
        SimpleTaskDescriptor taskB = new SimpleTaskDescriptor();
        taskA.setId("B");
        allTasks.add(taskB);
        service.setAllTasksInternal(allTasks);

        UserContext userContext = mock(UserContext.class);
        when(userContext.hasPrivilege(taskA.getRequiredPrivilegeName())).thenReturn(false);
        when(userContext.hasPrivilege(taskB.getRequiredPrivilegeName())).thenReturn(true);

        EmrContext context = new EmrContext();
        context.setUserContext(userContext);

        List<TaskDescriptor> availableTasks = service.getAvailableTasks(context);
        Assert.assertEquals(1, availableTasks.size());
        Assert.assertEquals(taskB, availableTasks.get(0));
    }

}
