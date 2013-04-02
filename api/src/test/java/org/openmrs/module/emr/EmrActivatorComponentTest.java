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
import org.openmrs.LocationAttributeType;
import org.openmrs.api.LocationService;
import org.openmrs.module.emr.printer.Printer;
import org.openmrs.module.htmlformentry.HtmlFormEntryService;
import org.openmrs.scheduler.SchedulerService;
import org.openmrs.scheduler.TaskDefinition;
import org.openmrs.test.BaseModuleContextSensitiveTest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;

import static org.hamcrest.Matchers.nullValue;
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
    @Qualifier("emrProperties")
    private EmrProperties emrProperties;

    @Test
    public void testStarted() throws Exception {
        new EmrActivator().started();
        assertThat(emrProperties.getEmrConceptSource().getName(), is(EmrConstants.EMR_CONCEPT_SOURCE_NAME));
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

    @Test
    public void testAddingAndRemovingHtmlFormEntryTag() throws Exception {
        EmrActivator activator = new EmrActivator();

        activator.started();
        assertThat(htmlFormEntryService.getHandlerByTagName(EmrConstants.HTMLFORMENTRY_UI_MESSAGE_TAG_NAME), is(notNullValue()));

        activator.stopped();
        assertThat(htmlFormEntryService.getHandlerByTagName(EmrConstants.HTMLFORMENTRY_UI_MESSAGE_TAG_NAME), is(nullValue()));
    }

    @Test
    public void confirmThatLocationAttributeTypesHaveBeenCreated() {

        new EmrActivator().started();

        LocationAttributeType defaultIdCardPrinter = locationService.getLocationAttributeTypeByUuid(EmrConstants.LOCATION_ATTRIBUTE_TYPE_DEFAULT_PRINTER.get(Printer.Type.ID_CARD.name()));
        LocationAttributeType defaultLabelPrinter = locationService.getLocationAttributeTypeByUuid(EmrConstants.LOCATION_ATTRIBUTE_TYPE_DEFAULT_PRINTER.get(Printer.Type.LABEL.name()));
        LocationAttributeType nameToPrintOnIdCard = locationService.getLocationAttributeTypeByUuid(EmrConstants.LOCATION_ATTRIBUTE_TYPE_NAME_TO_PRINT_ON_ID_CARD);

        assertThat(defaultIdCardPrinter, is(notNullValue()));
        assertThat(defaultLabelPrinter, is(notNullValue()));
        assertThat(nameToPrintOnIdCard, is(notNullValue()));
    }
}
