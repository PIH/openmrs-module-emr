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
import org.openmrs.module.appframework.SimpleAppDescriptor;
import org.openmrs.module.emr.EmrContext;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.mockito.Matchers.anyString;
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
        taskB.setId("B");
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

    @Test
    public void shouldSortTasksBasedOnPriority() {
        List<TaskDescriptor> allTasks = new ArrayList<TaskDescriptor>();

        SimpleTaskDescriptor taskA = new SimpleTaskDescriptor();
        taskA.setId("A");
        taskA.setPriority(3);
        allTasks.add(taskA);

        SimpleTaskDescriptor taskC = new SimpleTaskDescriptor();
        taskC.setId("C");
        taskC.setPriority(1);
        allTasks.add(taskC);

        SimpleTaskDescriptor taskB = new SimpleTaskDescriptor();
        taskB.setId("B");
        taskB.setPriority(2);
        allTasks.add(taskB);

        TaskServiceImpl service = new TaskServiceImpl();
        service.setAllTasksInternal(allTasks);

        UserContext userContext = mock(UserContext.class);
        when(userContext.hasPrivilege(anyString())).thenReturn(true);

        EmrContext context = new EmrContext();
        context.setUserContext(userContext);

        List<TaskDescriptor> availableTasks = service.getAvailableTasks(context);
        Assert.assertEquals(3, availableTasks.size());
        Assert.assertEquals(taskA, availableTasks.get(0));
        Assert.assertEquals(taskB, availableTasks.get(1));
        Assert.assertEquals(taskC, availableTasks.get(2));
    }

    @Test
    public void shouldGetAvailableTasksByCurrentApp() {
        TaskServiceImpl service = new TaskServiceImpl();

        List<TaskDescriptor> allTasks = new ArrayList<TaskDescriptor>();

        SimpleTaskDescriptor taskA = new SimpleTaskDescriptor();
        taskA.setId("A");
        taskA.setAppIds(Arrays.asList("appA"));
        allTasks.add(taskA);

        SimpleTaskDescriptor taskB = new SimpleTaskDescriptor();
        taskB.setId("B");
        taskB.setAppIds(Arrays.asList("appB"));
        allTasks.add(taskB);
        service.setAllTasksInternal(allTasks);

        UserContext userContext = mock(UserContext.class);
        when(userContext.hasPrivilege(taskA.getRequiredPrivilegeName())).thenReturn(true);
        when(userContext.hasPrivilege(taskB.getRequiredPrivilegeName())).thenReturn(true);

        SimpleAppDescriptor appDescriptor = new SimpleAppDescriptor();
        appDescriptor.setId("appA");

        EmrContext context = mock(EmrContext.class);
        when(context.getUserContext()).thenReturn(userContext);
        when(context.getCurrentApp()).thenReturn(appDescriptor);

        List<TaskDescriptor> availableTasks = service.getAvailableTasks(context);
        Assert.assertEquals(1, availableTasks.size());
        Assert.assertEquals(taskA, availableTasks.get(0));
    }

}
