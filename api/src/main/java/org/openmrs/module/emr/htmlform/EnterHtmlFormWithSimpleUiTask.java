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

import org.openmrs.module.emr.EmrContext;
import org.openmrs.module.htmlformentry.HtmlForm;

import java.util.HashMap;
import java.util.Map;

/**
 * Wraps our simple UI around the given HTML Form
 */
public class EnterHtmlFormWithSimpleUiTask extends BaseEnterPatientHtmlFormTask {

    @Override
    public String getUrl(EmrContext context) {
        HtmlForm htmlForm = getHtmlForm();
        Map<String, Object> params = new HashMap<String, Object>();
        params.put("patientId", context.getCurrentPatient().getId());
        params.put("timing", timing);
        params.put("htmlFormId", htmlForm.getId());
        if (getReturnUrl() != null) {
            params.put("returnUrl", getReturnUrl());
        }
        return uiUtils.pageLinkWithoutContextPath("emr", "htmlform/enterHtmlFormWithSimpleUi", params);
    }

}
