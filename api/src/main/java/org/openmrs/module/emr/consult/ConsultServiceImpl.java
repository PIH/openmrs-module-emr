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

package org.openmrs.module.emr.consult;

import org.openmrs.Concept;
import org.openmrs.Encounter;
import org.openmrs.Obs;
import org.openmrs.api.EncounterService;
import org.openmrs.api.impl.BaseOpenmrsService;
import org.openmrs.module.emrapi.EmrApiProperties;
import org.openmrs.module.emrapi.diagnosis.Diagnosis;
import org.openmrs.module.emrapi.diagnosis.DiagnosisMetadata;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.util.Date;

/**
 *
 */
public class ConsultServiceImpl extends BaseOpenmrsService implements ConsultService {

    private EmrApiProperties emrApiProperties;

    private EncounterService encounterService;

    @Transactional
    @Override
    public Encounter saveConsultNote(ConsultNote consultNote) {
        if (consultNote.getPatient() == null || consultNote.getEncounterLocation() == null || consultNote.getPrimaryDiagnosis() == null || consultNote.getClinician() == null) {
            throw new IllegalArgumentException("Required: patient, encounter location, clinician provider, primary diagnosis");
        }

        DiagnosisMetadata diagnosisMetadata = emrApiProperties.getDiagnosisMetadata();

        Encounter encounter = new Encounter();
        encounter.setEncounterDatetime(new Date());
        encounter.setLocation(consultNote.getEncounterLocation());
        encounter.setEncounterType(emrApiProperties.getConsultEncounterType());
        encounter.setPatient(consultNote.getPatient());

        encounter.addProvider(emrApiProperties.getClinicianEncounterRole(), consultNote.getClinician());

        encounter.addObs(diagnosisMetadata.buildDiagnosisObsGroup(consultNote.getPrimaryDiagnosis()));

        if (consultNote.getAdditionalDiagnoses() != null) {
            for (Diagnosis diagnosis : consultNote.getAdditionalDiagnoses()) {
                encounter.addObs(diagnosisMetadata.buildDiagnosisObsGroup(diagnosis));
            }
        }

        if (StringUtils.hasText(consultNote.getComments())) {
            encounter.addObs(buildTextObs(emrApiProperties.getConsultFreeTextCommentsConcept(), consultNote.getComments()));
        }

        return encounterService.saveEncounter(encounter);
    }

    private Obs buildTextObs(Concept question, String answer) {
        Obs obs = new Obs();
        obs.setConcept(question);
        obs.setValueText(answer);
        return obs;
    }

    public void setEmrApiProperties(EmrApiProperties emrApiProperties) {
        this.emrApiProperties = emrApiProperties;
    }

    public void setEncounterService(EncounterService encounterService) {
        this.encounterService = encounterService;
    }

}
