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

package org.openmrs.module.emr;

import org.junit.Test;
import org.openmrs.api.LocationService;
import org.openmrs.module.emrapi.EmrApiProperties;
import org.openmrs.module.htmlformentry.HtmlFormEntryService;
import org.openmrs.scheduler.SchedulerService;
import org.openmrs.scheduler.TaskDefinition;
import org.openmrs.test.BaseModuleContextSensitiveTest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;

import static org.hamcrest.core.Is.is;
import static org.hamcrest.core.IsNull.notNullValue;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;

/**
 *
 */
public class EmrActivatorComponentTest extends BaseModuleContextSensitiveTest {

    @Autowired
    @Qualifier("schedulerService")
    private SchedulerService schedulerService;

    @Autowired
    @Qualifier("locationService")
    private LocationService locationService;

    @Autowired
    @Qualifier("htmlFormEntryService")
    private HtmlFormEntryService htmlFormEntryService;

    @Autowired
    @Qualifier("emrApiProperties")
    private EmrApiProperties emrApiProperties;

    @Test
    public void testStarted() throws Exception {
        new EmrActivator().started();
        assertThat(emrApiProperties.getEmrApiConceptSource().getName(), is(EmrConstants.EMR_CONCEPT_SOURCE_NAME));
    }

    @Test
    public void testContextRefreshed() throws Exception {
        new EmrActivator().contextRefreshed();

        // verify scheduled task is started
        TaskDefinition closeStaleVisitsTask = schedulerService.getTaskByName(EmrConstants.TASK_CLOSE_STALE_VISITS_NAME);
        assertThat(closeStaleVisitsTask, is(notNullValue()));
        assertThat(closeStaleVisitsTask.getStarted(), is(true));
        assertThat(closeStaleVisitsTask.getStartOnStartup(), is(true));
        assertTrue(closeStaleVisitsTask.getSecondsUntilNextExecutionTime() <= 300);
    }

}
