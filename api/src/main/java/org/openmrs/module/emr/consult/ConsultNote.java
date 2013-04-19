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
import org.openmrs.module.emrapi.diagnosis.Diagnosis;

import java.util.ArrayList;
import java.util.List;

/**
 * Represents a note written by a clinician after performing a consultation on a patient
 */
public class ConsultNote {

    private Patient patient;

    private Location encounterLocation;

    private List<Diagnosis> diagnoses = new ArrayList<Diagnosis>();

    private String comments;

    private Provider clinician;

    public Patient getPatient() {
        return patient;
    }

    public void setPatient(Patient patient) {
        this.patient = patient;
    }

    public List<Diagnosis> getDiagnoses() {
        return diagnoses;
    }

    public void setDiagnoses(List<Diagnosis> diagnoses) {
        this.diagnoses = diagnoses;
    }

    public void addDiagnosis(Diagnosis diagnosis) {
        if (diagnosis.getOrder() == null) {
            throw new IllegalArgumentException("diagnosis.order is required");
        }
        if (diagnosis.getCertainty() == null) {
            throw new IllegalArgumentException("diagnosis.certainty is required");
        }
        diagnoses.add(diagnosis);
    }

    public Location getEncounterLocation() {
        return encounterLocation;
    }

    public void setEncounterLocation(Location encounterLocation) {
        this.encounterLocation = encounterLocation;
    }

    public void addPrimaryDiagnosis(Diagnosis primaryDiagnosis) {
        primaryDiagnosis.setOrder(Diagnosis.Order.PRIMARY);
        addDiagnosis(primaryDiagnosis);
    }

    public List<Diagnosis> getDiagnoses(Diagnosis.Order withOrder) {
        List<Diagnosis> matches = new ArrayList<Diagnosis>();
        for (Diagnosis candidate : diagnoses) {
            if (candidate.getOrder().equals(withOrder)) {
                matches.add(candidate);
            }
        }
        return matches;
    }

    public void addSecondaryDiagnosis(Diagnosis secondaryDiagnosis) {
        secondaryDiagnosis.setOrder(Diagnosis.Order.SECONDARY);
        addDiagnosis(secondaryDiagnosis);
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

}
