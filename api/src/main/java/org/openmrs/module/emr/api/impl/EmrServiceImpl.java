/**
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
package org.openmrs.module.emr.api.impl;

import org.openmrs.Concept;
import org.openmrs.Encounter;
import org.openmrs.EncounterRole;
import org.openmrs.EncounterType;
import org.openmrs.Location;
import org.openmrs.Order;
import org.openmrs.OrderType;
import org.openmrs.Patient;
import org.openmrs.api.AdministrationService;
import org.openmrs.api.EncounterService;
import org.openmrs.api.OrderService;
import org.openmrs.module.emr.EmrConstants;
import org.openmrs.module.emr.api.EmrService;
import org.openmrs.module.emr.api.db.EmrDAO;
import org.openmrs.module.emr.domain.RadiologyRequisition;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;

import java.util.List;

public class EmrServiceImpl implements EmrService {

    private EmrDAO dao;

    @Autowired
    @Qualifier("adminService")
    private AdministrationService administrationService;

    @Autowired
    @Qualifier("encounterService")
    private EncounterService encounterService;

    @Autowired
    @Qualifier("orderService")
    private OrderService orderService;

    public void setDao(EmrDAO dao) {
        this.dao = dao;
    }

    protected void setEncounterService(EncounterService encounterService) {
        this.encounterService = encounterService;
    }

    @Override
    public List<Patient> findPatients(String query, Location checkedInAt, Integer start, Integer length) {
        return dao.findPatients(query, checkedInAt, start, length);
    }

    @Override
    public Encounter placeRadiologyRequisition(RadiologyRequisition requisition) {
        EncounterType placeOrdersEncounterType = getPlaceOrdersEncounterType();
        EncounterRole clinicianEncounterRole = getClinicianEncounterRole();
        OrderType testOrderType = getTestOrderType();

        Encounter encounter = new Encounter();
        encounter.setEncounterType(placeOrdersEncounterType);
        encounter.setPatient(requisition.getPatient());
        encounter.addProvider(clinicianEncounterRole, requisition.getRequestedBy());
        encounter.setLocation(requisition.getEncounterLocation());
        encounter.setVisit(requisition.getVisit());
        encounter.setEncounterDatetime(requisition.getEncounterDatetime());

        for (Concept concept : requisition.getStudies()) {
            Order o = new Order();
            o.setPatient(requisition.getPatient());
            o.setOrderType(testOrderType);
            o.setConcept(concept);
            o.setStartDate(requisition.getEncounterDatetime());
            o.setInstructions(requisition.getClinicalHistory());
            encounter.addOrder(o);
        }

        return encounterService.saveEncounter(encounter);
    }

    protected EncounterType getPlaceOrdersEncounterType() {
        String encounterTypeUuid = administrationService.getGlobalProperty(EmrConstants.GP_PLACE_ORDERS_ENCOUNTER_TYPE);
        EncounterType encounterType = encounterService.getEncounterTypeByUuid(encounterTypeUuid);
        if (encounterType == null) {
            throw new IllegalStateException("Configuration required: " + EmrConstants.GP_PLACE_ORDERS_ENCOUNTER_TYPE);
        }
        return encounterType;
    }

    protected EncounterRole getClinicianEncounterRole() {
        String uuid = administrationService.getGlobalProperty(EmrConstants.GP_CLINICIAN_ENCOUNTER_ROLE);
        EncounterRole encounterRole = encounterService.getEncounterRoleByUuid(uuid);
        if (encounterRole == null) {
            throw new IllegalStateException("Configuration required: " + EmrConstants.GP_CLINICIAN_ENCOUNTER_ROLE);
        }
        return encounterRole;
    }

    protected OrderType getTestOrderType() {
        String uuid = administrationService.getGlobalProperty(EmrConstants.GP_TEST_ORDER_TYPE);
        OrderType orderType = orderService.getOrderTypeByUuid(uuid);
        if (orderType == null) {
            throw new IllegalStateException("Configuration required: " + EmrConstants.GP_TEST_ORDER_TYPE);
        }
        return orderType;
    }

}
