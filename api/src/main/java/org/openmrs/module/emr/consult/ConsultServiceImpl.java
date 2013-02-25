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
import org.openmrs.module.emr.EmrProperties;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.util.Date;

/**
 *
 */
public class ConsultServiceImpl extends BaseOpenmrsService implements ConsultService {

    private EmrProperties emrProperties;

    private EncounterService encounterService;

    @Transactional
    @Override
    public Encounter saveConsultNote(ConsultNote consultNote) {
        if (consultNote.getPatient() == null || consultNote.getEncounterLocation() == null || consultNote.getPrimaryDiagnosis() == null || consultNote.getClinician() == null) {
            throw new IllegalArgumentException("Required: patient, encounter location, clinician provider, primary diagnosis");
        }

        DiagnosisMetadata diagnosisMetadata = emrProperties.getDiagnosisMetadata();

        Encounter encounter = new Encounter();
        encounter.setEncounterDatetime(new Date());
        encounter.setLocation(consultNote.getEncounterLocation());
        encounter.setEncounterType(emrProperties.getConsultEncounterType());
        encounter.setPatient(consultNote.getPatient());

        encounter.addProvider(emrProperties.getClinicianEncounterRole(), consultNote.getClinician());

        encounter.addObs(diagnosisMetadata.buildDiagnosisObsGroup(consultNote.getPrimaryDiagnosis()));

        if (consultNote.getAdditionalDiagnoses() != null) {
            for (Diagnosis diagnosis : consultNote.getAdditionalDiagnoses()) {
                encounter.addObs(diagnosisMetadata.buildDiagnosisObsGroup(diagnosis));
            }
        }

        if (StringUtils.hasText(consultNote.getComments())) {
            encounter.addObs(buildTextObs(emrProperties.getConsultFreeTextCommentsConcept(), consultNote.getComments()));
        }

        return encounterService.saveEncounter(encounter);
    }

    private Obs buildTextObs(Concept question, String answer) {
        Obs obs = new Obs();
        obs.setConcept(question);
        obs.setValueText(answer);
        return obs;
    }

    public void setEmrProperties(EmrProperties emrProperties) {
        this.emrProperties = emrProperties;
    }

    public void setEncounterService(EncounterService encounterService) {
        this.encounterService = encounterService;
    }

}
