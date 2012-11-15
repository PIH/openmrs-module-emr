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

package org.openmrs.module.emr.page.controller;

import org.openmrs.Patient;
import org.openmrs.api.PatientService;
import org.openmrs.module.emr.EmrConstants;
import org.openmrs.module.emr.EmrProperties;
import org.openmrs.module.emr.adt.AdtService;
import org.openmrs.module.emr.patient.PatientDomainWrapper;
import org.openmrs.serialization.SerializationException;
import org.openmrs.ui.framework.SimpleObject;
import org.openmrs.ui.framework.UiUtils;
import org.openmrs.ui.framework.annotation.SpringBean;
import org.openmrs.ui.framework.page.PageModel;
import org.springframework.web.bind.annotation.RequestParam;

import javax.servlet.http.HttpServletRequest;

public class MergePatientsPageController {

    public void get(@RequestParam(required = false, value = "patient1") Patient patient1,
                    @RequestParam(required = false, value = "patient2") Patient patient2,
                    @SpringBean("emrProperties") EmrProperties emrProperties,
                    @SpringBean("adtService") AdtService adtService,
                    HttpServletRequest request,
                    PageModel pageModel) {

        pageModel.addAttribute("patient1", null);
        pageModel.addAttribute("patient2", null);

        if (patient1 != null && patient2 != null) {
            if (patient1.equals(patient2)) {
                request.getSession().setAttribute(EmrConstants.SESSION_ATTRIBUTE_ERROR_MESSAGE, "emr.mergePatients.error.samePatient");
                return;
            }
            pageModel.addAttribute("patient1", new PatientDomainWrapper(patient1, emrProperties, adtService));
            pageModel.addAttribute("patient2", new PatientDomainWrapper(patient2, emrProperties, adtService));
        }
    }

    public String post(UiUtils ui,
                       HttpServletRequest request,
                       @RequestParam("patient1") Patient patient1,
                       @RequestParam("patient2") Patient patient2,
                       @RequestParam("preferred") Patient preferred,
                       @SpringBean("patientService") PatientService patientService) {
        Patient notPreferred = patient1.equals(preferred) ? patient2 : patient1;
        try {
            patientService.mergePatients(preferred, notPreferred);
        } catch (SerializationException e) {
            throw new RuntimeException("Unexpected error logging results of merge", e);
        }

        request.getSession().setAttribute(EmrConstants.SESSION_ATTRIBUTE_INFO_MESSAGE, "emr.mergePatients.success");
        return "redirect:" + ui.pageLink("emr", "patient", SimpleObject.create("patientId", preferred.getId()));
    }

}
