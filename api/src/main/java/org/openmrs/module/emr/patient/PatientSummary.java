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

package org.openmrs.module.emr.patient;

import org.openmrs.Encounter;
import org.openmrs.Location;
import org.openmrs.Patient;
import org.openmrs.PatientIdentifier;
import org.openmrs.PatientIdentifierType;
import org.openmrs.Visit;
import org.openmrs.module.emr.EmrProperties;
import org.openmrs.module.emr.adt.AdtService;

import java.util.ArrayList;
import java.util.List;

/**
 * A rich-domain-model class that wraps a Patient, and lets you perform common queries.
 */
public class PatientSummary {

    private Patient patient;
    protected EmrProperties emrProperties;
    protected AdtService adtService;

    public PatientSummary(Patient patient, EmrProperties emrProperties, AdtService adtService) {
        this.patient = patient;
        this.emrProperties = emrProperties;
        this.adtService = adtService;
    }

    public Patient getPatient() {
        return patient;
    }

    public PatientIdentifier getPrimaryIdentifier() {
        List<PatientIdentifier> primaryIdentifiers = getPrimaryIdentifiers();
        if (primaryIdentifiers.size() == 0) {
            return null;
        } else {
            return primaryIdentifiers.get(0);
        }
    }

    public List<PatientIdentifier> getPrimaryIdentifiers() {
        return patient.getPatientIdentifiers(emrProperties.getPrimaryIdentifierType());
    }

    public List<PatientIdentifier> getPaperRecordIdentifiers() {
        PatientIdentifierType paperRecordIdentifierType = emrProperties.getPaperRecordIdentifierType();
        if (paperRecordIdentifierType == null) {
            return new ArrayList<PatientIdentifier>();
        }
        return patient.getPatientIdentifiers(paperRecordIdentifierType);
    }

    public Encounter getLastEncounter() {
        return adtService.getLastEncounter(patient);
    }

    public Visit getActiveVisit(Location location) {
        return adtService.getActiveVisit(patient, location);
    }

}
