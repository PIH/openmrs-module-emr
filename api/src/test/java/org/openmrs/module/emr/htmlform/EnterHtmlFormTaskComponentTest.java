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

package org.openmrs.module.emr.htmlform;

import org.junit.Before;
import org.junit.Test;
import org.openmrs.Patient;
import org.openmrs.api.FormService;
import org.openmrs.api.context.Context;
import org.openmrs.module.htmlformentry.FormEntryContext;
import org.openmrs.module.htmlformentry.FormEntrySession;
import org.openmrs.module.htmlformentry.HtmlForm;
import org.openmrs.module.htmlformentry.HtmlFormEntryService;
import org.openmrs.test.BaseModuleContextSensitiveTest;
import org.openmrs.ui.framework.resource.ResourceFactory;
import org.springframework.beans.factory.annotation.Autowired;

import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

/**
 *
 */
public class EnterHtmlFormTaskComponentTest extends BaseModuleContextSensitiveTest {

    @Autowired
    FormService formService;

    // can't use @Autowired because the current HFE code doesn't define this as a top-level bean
    HtmlFormEntryService htmlFormEntryService;

    @Before
    public void before() throws Exception {
        htmlFormEntryService = Context.getService(HtmlFormEntryService.class);
    }

    @Test
    public void testGettingHtmlFormByUiResource() throws Exception {
        ResourceFactory resourceFactory = mock(ResourceFactory.class);

        String formUuid = "form-uuid";
        String formName = "Form Name";
        String formVersion = "2012.12.17";
        String xmlContent = "<htmlform formUuid=\"" + formUuid + "\" formName=\"" + formName + "\" formVersion=\"" + formVersion + "\">Weight: <obs conceptId=\"5089\"/></htmlform>";
        when(resourceFactory.getResourceAsString("emr", "htmlforms/vitals.xml")).thenReturn(xmlContent);

        EnterHtmlFormTask task = new EnterHtmlFormTask();
        task.setResourceFactory(resourceFactory);
        task.setFormService(formService);
        task.setHtmlFormEntryService(htmlFormEntryService);

        task.setFormDefinitionFromUiResource("emr:htmlforms/vitals.xml");

        HtmlForm htmlForm = task.getHtmlForm();
        assertThat(htmlForm.getForm().getUuid(), is(formUuid));
        assertThat(htmlForm.getForm().getName(), is(formName));
        assertThat(htmlForm.getForm().getVersion(), is(formVersion));
        assertThat(htmlForm.getXmlData(), is(xmlContent));

        FormEntrySession formEntrySession = new FormEntrySession(new Patient(), htmlForm, FormEntryContext.Mode.ENTER, null);
        String html = formEntrySession.getHtmlToDisplay();
        assertThat(html, containsString("name=\"w2\""));
        assertThat(html, containsString("onBlur=\"checkNumber("));
    }

}
