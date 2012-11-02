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

package org.openmrs.module.emr.radiology;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.openmrs.Encounter;
import org.openmrs.Order;
import org.openmrs.Patient;
import org.openmrs.api.*;
import org.openmrs.api.context.Context;
import org.openmrs.module.emr.EmrContext;
import org.openmrs.module.emr.domain.RadiologyRequisition;
import org.openmrs.test.BaseModuleContextSensitiveTest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;

import java.util.Collections;
import java.util.List;
import java.util.Set;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class RadiologyServiceComponentTest extends BaseModuleContextSensitiveTest {

    @Autowired
    @Qualifier("radiologyService")
    RadiologyService radiologyService;

    @Autowired
    @Qualifier("patientService")
    private PatientService patientService;

    @Autowired
    @Qualifier("orderService")
    private OrderService orderService;

    @Autowired
    @Qualifier("conceptService")
    private ConceptService conceptService;

    @Autowired
    @Qualifier("locationService")
    private LocationService locationService;

    @Autowired
    @Qualifier("encounterService")
    private EncounterService encounterService;

    @Before
    public void beforeAllTests() throws Exception {
        executeDataSet("radiologyServiceComponentTestDataset.xml");
    }

    @Test
    public void testThatServiceIsConfiguredCorrectly() {
        Assert.assertNotNull("Couldn't autowire TaskService", radiologyService);
        Assert.assertNotNull("Couldn't get TaskService from Context", Context.getService(RadiologyService.class));
    }

    @Test
    public void shouldPlaceARadiologyRequisition() {

        Patient patient = patientService.getPatient(6);

        // sanity check
        Assert.assertEquals(0, encounterService.getEncountersByPatient(patient).size());

        EmrContext emrContext = mock(EmrContext.class);
        when(emrContext.getSessionLocation()).thenReturn(locationService.getLocation(1));
        when(emrContext.getActiveVisitSummary()).thenReturn(null);

        RadiologyRequisition requisition = new RadiologyRequisition();

        requisition.setPatient(patient);
        requisition.setStudies(Collections.singleton(conceptService.getConcept(18)));
        requisition.setUrgency(Order.Urgency.STAT);

        radiologyService.placeRadiologyRequisition(emrContext, requisition);

        List<Encounter> encounters = encounterService.getEncountersByPatient(patient);
        Assert.assertEquals(1, encounters.size());

        Set<Order> orders = encounters.get(0).getOrders();
        Assert.assertEquals(1, orders.size());

    }

}
