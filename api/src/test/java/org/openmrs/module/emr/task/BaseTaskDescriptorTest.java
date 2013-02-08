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

import org.junit.Test;
import org.openmrs.api.context.UserContext;
import org.openmrs.module.emr.EmrContext;

import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

/**
 *
 */
public class BaseTaskDescriptorTest {

    @Test
    public void testDefaultRequiredPrivilegeName() {
        TaskDescriptorImpl task = new TaskDescriptorImpl("emr.some.form", "Enter Some Form");
        assertThat(task.getRequiredPrivilegeName(), is("Task: emr.some.form"));

        UserContext userContext = mock(UserContext.class);

        EmrContext context = new EmrContext();
        context.setUserContext(userContext);

        task.isAvailable(context);
        verify(userContext).hasPrivilege("Task: emr.some.form");
    }

    @Test
    public void testOverriddenRequiredPrivilegeName() {
        String requiredPrivilege = "Task: emr.enterClinicalForms";

        TaskDescriptorImpl task = new TaskDescriptorImpl("emr.some.form", "Enter Some Form");
        task.setRequiredPrivilegeName(requiredPrivilege);
        assertThat(task.getRequiredPrivilegeName(), is(requiredPrivilege));

        UserContext userContext = mock(UserContext.class);

        EmrContext context = new EmrContext();
        context.setUserContext(userContext);

        task.isAvailable(context);
        verify(userContext).hasPrivilege(requiredPrivilege);
    }

    class TaskDescriptorImpl extends BaseTaskDescriptor {

        private String id;
        private String label;

        private TaskDescriptorImpl(String id, String label) {
            this.id = id;
            this.label = label;
        }

        @Override
        public String getId() {
            return id;
        }

        @Override
        public String getLabel(EmrContext context) {
            return label;
        }

        @Override
        public String getIconUrl(EmrContext context) {
            return null;
        }

        @Override
        public String getTinyIconUrl(EmrContext context) {
            return null;
        }

        @Override
        public String getUrl(EmrContext context) {
            return null;
        }
    }
}
