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

package org.openmrs.module.emr.fragment.controller.paperrecord;

import org.openmrs.Patient;
import org.openmrs.api.PatientService;
import org.openmrs.module.emr.EmrProperties;
import org.openmrs.module.emr.paperrecord.PaperRecordService;
import org.openmrs.ui.framework.UiUtils;
import org.openmrs.ui.framework.annotation.SpringBean;
import org.openmrs.ui.framework.fragment.action.FailureResult;
import org.openmrs.ui.framework.fragment.action.FragmentActionResult;
import org.openmrs.ui.framework.fragment.action.SuccessResult;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.Collections;
import java.util.List;

public class ArchivesRoomFragmentController {

    public FragmentActionResult markPaperRecordRequestAsSent(@RequestParam(value = "patientIdentifier", required = true) String patientIdentifier,
                                                             @SpringBean("paperRecordService") PaperRecordService paperRecordService,
                                                             @SpringBean("patientService") PatientService patientService,
                                                             @SpringBean("emrProperties") EmrProperties emrProperties,
                                                             UiUtils ui) {

        // first, fetch the patient with the specified identifier
        List<Patient> patients = patientService.getPatients(null, patientIdentifier, Collections.singletonList(emrProperties.getPrimaryIdentifierType()),true);

        if (patients == null || patients.size() == 0) {
            return new FailureResult(ui.message("emr.archivesRoom.error.noPatientWithIdentifier", ui.format(patientIdentifier)));
        }



        return new SuccessResult(patientIdentifier);

    }


}
