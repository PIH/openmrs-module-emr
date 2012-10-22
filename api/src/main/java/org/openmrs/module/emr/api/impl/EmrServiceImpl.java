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

import org.apache.commons.lang.time.DateUtils;
import org.openmrs.Concept;
import org.openmrs.Encounter;
import org.openmrs.EncounterRole;
import org.openmrs.EncounterType;
import org.openmrs.Location;
import org.openmrs.Obs;
import org.openmrs.Order;
import org.openmrs.OrderType;
import org.openmrs.Patient;
import org.openmrs.TestOrder;
import org.openmrs.Visit;
import org.openmrs.VisitType;
import org.openmrs.api.AdministrationService;
import org.openmrs.api.EncounterService;
import org.openmrs.api.OrderService;
import org.openmrs.api.VisitService;
import org.openmrs.module.emr.EmrConstants;
import org.openmrs.module.emr.api.EmrService;
import org.openmrs.module.emr.api.db.EmrDAO;
import org.openmrs.module.emr.domain.RadiologyRequisition;
import org.openmrs.util.OpenmrsUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
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

    @Autowired
    @Qualifier("visitService")
    private VisitService visitService;

    public void setDao(EmrDAO dao) {
        this.dao = dao;
    }

    protected void setEncounterService(EncounterService encounterService) {
        this.encounterService = encounterService;
    }

    protected void setVisitService(VisitService visitService) {
        this.visitService = visitService;
    }

    protected void setAdministrationService(AdministrationService administrationService) {
        this.administrationService = administrationService;
    }

    @Override
    public List<Patient> findPatients(String query, Location checkedInAt, Integer start, Integer length) {
        return dao.findPatients(query, checkedInAt, start, length);
    }

    @Transactional
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
            TestOrder o = new TestOrder();
            o.setPatient(requisition.getPatient());
            o.setOrderType(testOrderType);
            o.setConcept(concept);
            o.setStartDate(requisition.getEncounterDatetime());
            o.setClinicalHistory(requisition.getClinicalHistory());
            o.setUrgency(requisition.getUrgency());
            o.setLaterality(requisition.getLaterality());
            encounter.addOrder(o);
        }

        return encounterService.saveEncounter(encounter);
    }

    @Override
    public boolean isActive(Visit visit, Date when) {
        if (when == null) {
            when = new Date();
        }
        Date mustHaveSomethingAfter = DateUtils.addHours(when, -EmrConstants.VISIT_EXPIRE_HOURS);

        if (OpenmrsUtil.compare(visit.getStartDatetime(), mustHaveSomethingAfter) >= 0) {
            return true;
        }

        if (visit.getEncounters() != null) {
            for (Encounter candidate : visit.getEncounters()) {
                if (OpenmrsUtil.compare(candidate.getEncounterDatetime(), mustHaveSomethingAfter) >= 0) {
                    return true;
                }
            }
        }

        return false;
    }

    @Override
    @Transactional
    public Visit ensureActiveVisit(Patient patient, Location department, Date when) {
        if (when == null) {
            when = new Date();
        }
        List<Visit> candidates = visitService.getVisitsByPatient(patient);
        Visit ret = null;
        for (Visit candidate : candidates) {
            if (!isActive(candidate, when)) {
                candidate.setStopDatetime(guessVisitStopDatetime(candidate));
                visitService.saveVisit(candidate);
                continue;
            }
            if (isSuitableVisit(candidate, department, when)) {
                ret = candidate;
            }
        }

        if (ret == null) {
            ret = buildVisit(patient, department, when);
            visitService.saveVisit(ret);
        }
        return ret;
    }

    @Override
    public Date guessVisitStopDatetime(Visit visit) {
        if (visit.getStopDatetime() != null) {
            throw new IllegalStateException("Visit already stopped");
        }
        Encounter latest = null;
        if (visit.getEncounters() != null) {
            for (Encounter candidate : visit.getEncounters()) {
                if (OpenmrsUtil.compareWithNullAsEarliest(candidate.getEncounterDatetime(), latest.getEncounterDatetime()) > 0) {
                    latest = candidate;
                }
            }
        }
        Date lastKnownDate = latest == null ? visit.getStartDatetime() : latest.getEncounterDatetime();
        return DateUtils.addHours(lastKnownDate, EmrConstants.VISIT_EXPIRE_HOURS);
    }

    @Override
    @Transactional
    public Encounter checkInPatient(Patient patient, Location where, Date when, List<Obs> obsForCheckInEncounter, List<Order> ordersForCheckInEncounter) {
        Visit activeVisit = ensureActiveVisit(patient, where, when);
        Encounter encounter = buildEncounter(getCheckInEncounterType(), patient, where, when, obsForCheckInEncounter, ordersForCheckInEncounter);
        encounter.setVisit(activeVisit);
        encounterService.saveEncounter(encounter);
        return encounter;
    }

    protected EncounterType getPlaceOrdersEncounterType() {
        return getEncounterTypeByGlobalProperty(EmrConstants.GP_PLACE_ORDERS_ENCOUNTER_TYPE);
    }

    protected EncounterType getCheckInEncounterType() {
        return getEncounterTypeByGlobalProperty(EmrConstants.GP_CHECK_IN_ENCOUNTER_TYPE);
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

    protected VisitType getUnspecifiedVisitType() {
        String uuid = administrationService.getGlobalProperty(EmrConstants.GP_UNSPECIFIED_VISIT_TYPE);
        VisitType visitType = visitService.getVisitTypeByUuid(uuid);
        if (visitType == null) {
            throw new IllegalStateException("Configuration required: " + EmrConstants.GP_UNSPECIFIED_VISIT_TYPE);
        }
        return visitType;
    }

    private EncounterType getEncounterTypeByGlobalProperty(String globalPropertyName) {
        String encounterTypeUuid = administrationService.getGlobalProperty(globalPropertyName);
        EncounterType encounterType = encounterService.getEncounterTypeByUuid(encounterTypeUuid);
        if (encounterType == null) {
            throw new IllegalStateException("Configuration required: " + globalPropertyName);
        }
        return encounterType;
    }

    private Encounter buildEncounter(EncounterType encounterType, Patient patient, Location location, Date when, List<Obs> obsToCreate, List<Order> ordersToCreate) {
        Encounter encounter = new Encounter();
        encounter.setPatient(patient);
        encounter.setEncounterType(encounterType);
        encounter.setLocation(location);
        encounter.setEncounterDatetime(when);
        if (obsToCreate != null) {
            for (Obs obs : obsToCreate) {
                encounter.addObs(obs);
            }
        }
        if (ordersToCreate != null) {
            for (Order order : ordersToCreate) {
                encounter.addOrder(order);
            }
        }
        return encounter;
    }

    private Visit buildVisit(Patient patient, Location location, Date when) {
        Visit visit = new Visit();
        visit.setPatient(patient);
        visit.setLocation(getLocationThatSupportsVisits(location));
        visit.setStartDatetime(when);
        visit.setVisitType(getUnspecifiedVisitType());
        return visit;
    }

    /**
     * Looks at location, and if necessary its ancestors in the location hierarchy, until it finds one tagged with
     * "Visit Location"
     * @param location
     * @return location, or an ancestor
     * @throws IllegalArgumentException if neither location nor its ancestors support visits
     */
    private Location getLocationThatSupportsVisits(Location location) {
        if (location == null) {
            throw new IllegalArgumentException("Location does not support visits");
        } else if (location.hasTag(EmrConstants.LOCATION_TAG_SUPPORTS_VISITS)) {
            return location;
        } else {
            return getLocationThatSupportsVisits(location.getParentLocation());
        }
    }

    /**
     * @param visit
     * @param location
     * @param when
     * @return true if when falls in the visits timespan AND location is within visit.location
     */
    private boolean isSuitableVisit(Visit visit, Location location, Date when) {
        if (OpenmrsUtil.compare(when, visit.getStartDatetime()) < 0) {
            return false;
        }
        if (OpenmrsUtil.compareWithNullAsLatest(when, visit.getStopDatetime()) > 0) {
            return false;
        }
        return isSameOrAncestor(visit.getLocation(), location);
    }

    /**
     * @param a
     * @param b
     * @return true if a.equals(b) or a is an ancestor of b.
     */
    private boolean isSameOrAncestor(Location a, Location b) {
        if (b == null) {
            return a == null;
        }
        return a.equals(b) || isSameOrAncestor(a, b.getParentLocation());
    }

}
