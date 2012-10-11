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

package org.openmrs.module.emr.api;

import java.util.Date;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentMatcher;
import org.openmrs.Concept;
import org.openmrs.Encounter;
import org.openmrs.EncounterRole;
import org.openmrs.EncounterType;
import org.openmrs.Location;
import org.openmrs.Order;
import org.openmrs.OrderType;
import org.openmrs.Patient;
import org.openmrs.Person;
import org.openmrs.Provider;
import org.openmrs.TestOrder;
import org.openmrs.User;
import org.openmrs.api.EncounterService;
import org.openmrs.api.context.Context;
import org.openmrs.module.emr.api.impl.EmrServiceImpl;
import org.openmrs.module.emr.domain.RadiologyRequisition;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertThat;
import static org.junit.matchers.JUnitMatchers.hasItem;
import static org.mockito.Matchers.argThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.powermock.api.mockito.PowerMockito.mockStatic;

@RunWith(PowerMockRunner.class)
@PrepareForTest(Context.class)
public class EmrServiceTest {

    EncounterService mockEncounterService;

    @Before
    public void setup() {
        mockEncounterService = mock(EncounterService.class);
    }

    @Test
    public void testPlaceRadiologyOrders() throws Exception {
        User authenticatedUser = new User();
        EncounterType radiologyOrderEncounterType = new EncounterType();
        EncounterRole clinicianEncounterRole = new EncounterRole();
        OrderType testOrderType = new OrderType();

        mockStatic(Context.class);
        when(Context.getAuthenticatedUser()).thenReturn(authenticatedUser);

        EmrService service = new EmrServiceStub(mockEncounterService, radiologyOrderEncounterType, clinicianEncounterRole, testOrderType);

        Patient patient = new Patient();
        patient.setPatientId(17);
        Concept armXray = new Concept();
        Location radiologyDepartment = new Location();
        Concept walking = new Concept();
        String patientHistory = "Patient fell off a ladder and may have broken arm";
        Date encounterDatetime = new Date();
        Location encounterLocation = new Location();

        Person drBobPerson = new Person();
        Provider drBob = new Provider();
        drBob.setPerson(drBobPerson);
        User drBobUser = new User();
        drBobUser.setPerson(drBobPerson);

        RadiologyRequisition radiologyRequisition = new RadiologyRequisition();
        radiologyRequisition.setPatient(patient);
        radiologyRequisition.setModality(RadiologyRequisition.Modality.XRAY);
        radiologyRequisition.setRequestedBy(drBob);
        radiologyRequisition.setClinicalHistory(patientHistory);
        radiologyRequisition.setEncounterLocation(encounterLocation);
        radiologyRequisition.setEncounterDatetime(encounterDatetime);
        radiologyRequisition.addStudy(armXray);
        radiologyRequisition.setUrgency(Order.Urgency.STAT);
        radiologyRequisition.setLaterality(TestOrder.Laterality.LEFT);
        //radiologyRequisition.setExamLocation(radiologyDepartment);
        //radiologyRequisition.setTransportation(walking);

        service.placeRadiologyRequisition(radiologyRequisition);

        TestOrder expectedOrder = new TestOrder();
        expectedOrder.setPatient(patient);
        //expectedOrder.setOrderer(drBobUser);
        expectedOrder.setClinicalHistory(patientHistory);
        expectedOrder.setConcept(armXray);
        expectedOrder.setStartDate(encounterDatetime);
        expectedOrder.setOrderType(testOrderType);
        expectedOrder.setUrgency(Order.Urgency.STAT);
        expectedOrder.setLaterality(TestOrder.Laterality.LEFT);

        Encounter expected = new Encounter();
        expected.setEncounterDatetime(encounterDatetime);
        expected.setLocation(encounterLocation);
        expected.setPatient(patient);
        expected.setEncounterType(radiologyOrderEncounterType);
        expected.addOrder(expectedOrder);

        verify(mockEncounterService).saveEncounter(argThat(new IsExpectedEncounter(expected)));
    }

    class IsExpectedEncounter extends ArgumentMatcher<Encounter> {
        private Encounter expected;

        IsExpectedEncounter(Encounter expected) {
            this.expected = expected;
        }

        @Override
        public boolean matches(Object o) {
            Encounter actualEncounter = (Encounter) o;

            assertThat(actualEncounter.getEncounterType(), is(expected.getEncounterType()));
            assertThat(actualEncounter.getPatient(), is(expected.getPatient()));
            assertThat(actualEncounter.getEncounterDatetime(), is(expected.getEncounterDatetime()));
            assertThat(actualEncounter.getLocation(), is(expected.getLocation()));
            assertThat(actualEncounter.getOrders().size(), is(expected.getOrders().size()));

            for (Order order : expected.getOrders()) {
                assertThat(actualEncounter.getOrders(), hasItem(new IsExpectedOrder(order)));
            }

            return true;
        }
    }

    private class EmrServiceStub extends EmrServiceImpl {
        private EncounterType radiologyOrderEncounterType;
        private final EncounterRole clinicianEncounterRole;
        private final OrderType testOrderType;

        public EmrServiceStub(EncounterService mockEncounterService, EncounterType radiologyOrderEncounterType, EncounterRole clinicianEncounterRole, OrderType testOrderType) {
            setEncounterService(mockEncounterService);
            this.radiologyOrderEncounterType = radiologyOrderEncounterType;
            this.clinicianEncounterRole = clinicianEncounterRole;
            this.testOrderType = testOrderType;
        }

        @Override
        protected EncounterType getPlaceOrdersEncounterType() {
            return radiologyOrderEncounterType;
        }

        @Override
        protected EncounterRole getClinicianEncounterRole() {
            return clinicianEncounterRole;
        }

        @Override
        protected OrderType getTestOrderType() {
            return testOrderType;
        }
    }

    private class IsExpectedOrder extends ArgumentMatcher<Order> {
        private TestOrder expected;

        public IsExpectedOrder(Order expected) {
            this.expected = (TestOrder) expected;
        }

        @Override
        public boolean matches(Object o) {
            TestOrder actual = (TestOrder) o;
            assertThat(actual.getOrderType(), is(expected.getOrderType()));
            assertThat(actual.getPatient(), is(expected.getPatient()));
            assertThat(actual.getConcept(), is(expected.getConcept()));
            assertThat(actual.getInstructions(), is(expected.getInstructions()));
            assertThat(actual.getStartDate(), is(expected.getStartDate()));
            assertThat(actual.getUrgency(), is(expected.getUrgency()));
            assertThat(actual.getClinicalHistory(), is(expected.getClinicalHistory()));
            assertThat(actual.getLaterality(), is(expected.getLaterality()));

            return true;
        }
    }
}
