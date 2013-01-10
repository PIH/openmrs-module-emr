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

package org.openmrs.module.emr.page.controller.htmlform;

import org.apache.commons.lang.StringUtils;
import org.openmrs.Patient;
import org.openmrs.Visit;
import org.openmrs.module.emr.EmrContext;
import org.openmrs.module.emr.htmlform.EntryTiming;
import org.openmrs.ui.framework.SimpleObject;
import org.openmrs.ui.framework.UiUtils;
import org.openmrs.ui.framework.page.PageModel;
import org.springframework.web.bind.annotation.RequestParam;

/**
 *
 */
public class EnterHtmlFormPageController {

    public void get(EmrContext emrContext,
                    @RequestParam("timing") EntryTiming timing,
                    @RequestParam(value = "formUuid", required = false) String formUuid,
                    @RequestParam(value = "htmlFormId", required = false) String htmlFormId,
                    @RequestParam(value = "visitId", required = false) Visit visit,
                    @RequestParam(value = "returnUrl", required = false) String returnUrl,
                    UiUtils ui,
                    PageModel model) {

        emrContext.requireAuthentication();

        if (timing == EntryTiming.REAL_TIME) {
            if (emrContext.getActiveVisitSummary() == null) {
                throw new IllegalStateException("No active visit");
            }
            if (visit != null && !visit.equals(emrContext.getActiveVisitSummary().getVisit())) {
                throw new IllegalStateException("Can't enter a real-time HTML Form for a different visit than the active one");
            }
            if (visit == null) {
                visit = emrContext.getActiveVisitSummary().getVisit();
            }
        }

        Patient currentPatient = emrContext.getCurrentPatient();

        if (StringUtils.isEmpty(returnUrl)) {
            if (currentPatient != null) {
                returnUrl = ui.pageLink("emr", "patient", SimpleObject.create("patientId", currentPatient.getId()));
            } else {
                returnUrl = ui.pageLink("emr", "home");
            }
        }

        model.addAttribute("formUuid", formUuid);
        model.addAttribute("htmlFormId", htmlFormId);
        model.addAttribute("patient", currentPatient);
        model.addAttribute("visit", visit);
        model.addAttribute("returnUrl", returnUrl);
    }

}
