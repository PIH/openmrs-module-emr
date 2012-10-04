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

package org.openmrs.module.emr.domain;

import org.openmrs.Concept;
import org.openmrs.Location;
import org.openmrs.Patient;
import org.openmrs.Provider;
import org.openmrs.Visit;
import org.openmrs.module.emr.OrderConstants;

import java.util.Date;
import java.util.LinkedHashSet;
import java.util.Set;

/**
 * Requisition of one or more radiology studies (XRay, CT, Ultrasound), with associated metadata about the entire group
 */
public class RadiologyRequisition {

    public enum Modality { XRAY }

    private Modality modality;

    private Patient patient;

    private Provider requestedBy;

    private String indication;

    private OrderConstants.Urgency urgency;

    /**
     * where the exam should take place
     */
    private Location examLocation;

    private Concept transportation;

    /**
     * where the order is placed from
     */
    private Location encounterLocation;

    /**
     * when the order is placed
     */
    private Date encounterDatetime = new Date();

    private Set<Concept> studies = new LinkedHashSet<Concept>();

    private Visit visit;

    public Modality getModality() {
        return modality;
    }

    public Patient getPatient() {
        return patient;
    }

    public Provider getRequestedBy() {
        return requestedBy;
    }

    public String getIndication() {
        return indication;
    }

    public OrderConstants.Urgency getUrgency() {
        return urgency;
    }

    public Location getExamLocation() {
        return examLocation;
    }

    public Concept getTransportation() {
        return transportation;
    }

    public Set<Concept> getStudies() {
        return studies;
    }

    public void setStudies(Set<Concept> studies) {
        this.studies = studies;
    }

    public void setRequestedBy(Provider requestedBy) {
        this.requestedBy = requestedBy;
    }

    public void setIndication(String indication) {
        this.indication = indication;
    }

    public void addStudy(Concept orderable) {
        studies.add(orderable);
    }

    public void setUrgency(OrderConstants.Urgency urgency) {
        this.urgency = urgency;
    }

    public void setExamLocation(Location examLocation) {
        this.examLocation = examLocation;
    }

    public void setTransportation(Concept transportation) {
        this.transportation = transportation;
    }

    public void setPatient(Patient patient) {
        this.patient = patient;
    }

    public void setModality(Modality modality) {
        this.modality = modality;
    }

    public Location getEncounterLocation() {
        return encounterLocation;
    }

    public void setEncounterLocation(Location encounterLocation) {
        this.encounterLocation = encounterLocation;
    }

    public Visit getVisit() {
        return visit;
    }

    public void setVisit(Visit visit) {
        this.visit = visit;
    }

    public Date getEncounterDatetime() {
        return encounterDatetime;
    }

    public void setEncounterDatetime(Date encounterDatetime) {
        this.encounterDatetime = encounterDatetime;
    }
}
