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

import org.openmrs.Location;
import org.openmrs.Patient;
import org.openmrs.Provider;

import java.util.ArrayList;
import java.util.List;

/**
 * Represents a note written by a clinician after performing a consultation on a patient
 */
public class ConsultNote {

    private Patient patient;

    private Location encounterLocation;

    private Diagnosis primaryDiagnosis;

    private List<Diagnosis> additionalDiagnoses;

    private String comments;

    private Provider clinician;

    public Patient getPatient() {
        return patient;
    }

    public void setPatient(Patient patient) {
        this.patient = patient;
    }

    public Diagnosis getPrimaryDiagnosis() {
        return primaryDiagnosis;
    }

    public Location getEncounterLocation() {
        return encounterLocation;
    }

    public void setEncounterLocation(Location encounterLocation) {
        this.encounterLocation = encounterLocation;
    }

    public void setPrimaryDiagnosis(Diagnosis primaryDiagnosis) {
        primaryDiagnosis.setOrder(Diagnosis.Order.PRIMARY);
        this.primaryDiagnosis = primaryDiagnosis;
    }

    public List<Diagnosis> getAdditionalDiagnoses() {
        return additionalDiagnoses;
    }

    public void setAdditionalDiagnoses(List<Diagnosis> additionalDiagnoses) {
        this.additionalDiagnoses = additionalDiagnoses;
    }

    public String getComments() {
        return comments;
    }

    public void setComments(String comments) {
        this.comments = comments;
    }

    public Provider getClinician() {
        return clinician;
    }

    public void setClinician(Provider clinician) {
        this.clinician = clinician;
    }

    public void addAdditionalDiagnosis(Diagnosis diagnosis) {
        if (additionalDiagnoses == null) {
            additionalDiagnoses = new ArrayList<Diagnosis>();
        }
        diagnosis.setOrder(Diagnosis.Order.SECONDARY);
        additionalDiagnoses.add(diagnosis);
    }

}
