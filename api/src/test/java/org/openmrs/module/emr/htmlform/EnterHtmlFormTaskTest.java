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

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.runners.MockitoJUnitRunner;
import org.openmrs.Form;
import org.openmrs.api.FormService;
import org.openmrs.module.htmlformentry.HtmlForm;
import org.openmrs.module.htmlformentry.HtmlFormEntryService;
import org.openmrs.ui.framework.resource.ResourceFactory;

import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.when;
import static org.mockito.MockitoAnnotations.Mock;

/**
 *
 */
@RunWith(MockitoJUnitRunner.class)
public class EnterHtmlFormTaskTest {

    @Mock
    FormService formService;

    @Mock
    HtmlFormEntryService htmlFormEntryService;

    @Mock
    ResourceFactory resourceFactory;

    @InjectMocks
    EnterHtmlFormTask task = new EnterHtmlFormTask();

    @Test
    public void testGettingHtmlFormByFormUuid() throws Exception {
        String formUuid = "uuid-of-a-form";

        Form form = new Form();
        form.setUuid(formUuid);

        HtmlForm htmlForm = new HtmlForm();
        htmlForm.setForm(form);

        when(formService.getFormByUuid(formUuid)).thenReturn(form);
        when(htmlFormEntryService.getHtmlFormByForm(form)).thenReturn(htmlForm);

        task.setFormUuid(formUuid);
        HtmlForm taskHtmlForm = task.getHtmlForm();

        assertThat(taskHtmlForm, is(htmlForm));
    }

    @Test
    public void testGettingHtmlFormByUiResource() throws Exception {
        String xmlContent = "<htmlform formUuid=\"form-uuid\">This is a test</htmlform>";
        when(resourceFactory.getResourceAsString("emr", "htmlforms/vitals.xml")).thenReturn(xmlContent);

        task.setFormDefinitionFromUiResource("emr:htmlforms/vitals.xml");

        HtmlForm htmlForm = task.getHtmlForm();
        assertThat(htmlForm.getXmlData(), is(xmlContent));
    }

}
