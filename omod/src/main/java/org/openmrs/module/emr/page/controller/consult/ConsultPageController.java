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

package org.openmrs.module.emr.page.controller.consult;

import org.openmrs.Patient;
import org.openmrs.module.emr.EmrContext;
import org.openmrs.module.emr.EmrProperties;
import org.openmrs.module.emr.consult.ConsultNote;
import org.openmrs.module.emr.consult.ConsultService;
import org.openmrs.module.emr.consult.Diagnosis;
import org.openmrs.ui.framework.UiUtils;
import org.openmrs.ui.framework.annotation.SpringBean;
import org.openmrs.ui.framework.page.PageModel;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

/**
 *
 */
public class ConsultPageController {

    public void get(@RequestParam("patientId") Patient patient,
                    UiUtils ui,
                    PageModel model) {
        //model.addAttribute("patient", patient);
    }

    public void post(@RequestParam("patientId") Patient patient,
                     @RequestParam("primaryDiagnosis") String primaryDiagnosisCode,
                     @RequestParam("secondaryDiagnoses") List<String> secondaryDiagnosisCodes,
                     @SpringBean("consultService") ConsultService consultService,
                     @SpringBean EmrProperties emrProperties,
                     EmrContext emrContext) {
        ConsultNote consultNote = new ConsultNote();
        consultNote.setPatient(patient);
        consultNote.setPrimaryDiagnosis(new Diagnosis(primaryDiagnosisCode));
        if (secondaryDiagnosisCodes != null) {
            for (String code : secondaryDiagnosisCodes) {
                consultNote.addAdditionalDiagnosis(new Diagnosis(code));
            }
        }
        consultNote.setClinician(emrContext.getCurrentProvider());
        consultNote.setEncounterLocation(emrContext.getSessionLocation());

        consultService.saveConsultNote(consultNote);
    }

}
