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

package org.openmrs.module.emr.adt;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.openmrs.*;
import org.openmrs.api.AdministrationService;
import org.openmrs.api.ConceptService;
import org.openmrs.api.LocationService;
import org.openmrs.api.context.Context;
import org.openmrs.module.emr.EmrConstants;
import org.openmrs.module.emr.EmrProperties;
import org.openmrs.test.BaseModuleContextSensitiveTest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import static org.hamcrest.Matchers.contains;
import static org.hamcrest.Matchers.containsInAnyOrder;
import static org.hamcrest.core.Is.is;
import static org.hamcrest.core.IsNull.notNullValue;
import static org.junit.Assert.assertThat;
import static org.openmrs.module.emr.TestUtils.isJustNow;

@RunWith(SpringJUnit4ClassRunner.class)
public class AdtServiceComponentTest extends BaseModuleContextSensitiveTest {

    @Autowired
    private AdtService service;
    @Autowired
    private EmrProperties emrProperties;
    @Autowired
    private ConceptService conceptService;

    @Before
    public void before() throws Exception {
        executeDataSet("retrospectiveCheckinComponentTestDataset.xml");
    }

    @Test
    public void integrationTest_ADT_workflow() {
        LocationService locationService = Context.getLocationService();

        Patient patient = Context.getPatientService().getPatient(7);

        // parent location should support visits
        LocationTag supportsVisits = new LocationTag();
        supportsVisits.setName(EmrConstants.LOCATION_TAG_SUPPORTS_VISITS);
        locationService.saveLocationTag(supportsVisits);

        Location parentLocation = locationService.getLocation(2);
        parentLocation.addTag(supportsVisits);
        locationService.saveLocation(parentLocation);

        // add a child location where we'll do the actual check-in
        Location outpatientDepartment = new Location();
        outpatientDepartment.setName("Outpatient Clinic in Xanadu");
        outpatientDepartment.setParentLocation(parentLocation);
        locationService.saveLocation(outpatientDepartment);

        // configure payment observation
        List<Obs> observations = new ArrayList<Obs>();
        Obs paymentReasonObservation = createPaymentReasonObservation();
        Obs paymentAmountObservation = createPaymentAmountObservation(20);
        Obs paymentReceiptObservation = createPaymentReceiptObservation("AB23423");
        observations.add(paymentReasonObservation);
        observations.add(paymentAmountObservation);
        observations.add(paymentReceiptObservation);

        // step 1: check in the patient (which should create a visit and an encounter)
        Encounter checkInEncounter = service.checkInPatient(patient, outpatientDepartment, null, observations, null, false);

        assertThat(checkInEncounter.getVisit(), notNullValue());
        assertThat(checkInEncounter.getPatient(), is(patient));
        assertThat(checkInEncounter.getEncounterDatetime(), isJustNow());
        assertThat(checkInEncounter.getVisit().getPatient(), is(patient));
        assertThat(checkInEncounter.getVisit().getStartDatetime(), isJustNow());
        assertThat(checkInEncounter.getObs(), containsInAnyOrder(paymentReasonObservation, paymentAmountObservation, paymentReceiptObservation));
        assertThat(checkInEncounter.getAllObs().size(), is(1));
        assertThat(checkInEncounter.getAllObs().iterator().next().getGroupMembers(), containsInAnyOrder(paymentReasonObservation, paymentAmountObservation, paymentReceiptObservation));

        // TODO once these are implemented, add Admission and Discharge to this test
    }

    private Obs createPaymentAmountObservation(double amount) {
        Obs paymentAmount = new Obs();
        paymentAmount.setConcept(emrProperties.getPaymentAmountConcept());
        paymentAmount.setValueNumeric(amount);
        return paymentAmount;
    }

    private Obs createPaymentReasonObservation() {
        Obs paymentReason = new Obs();
        paymentReason.setConcept(emrProperties.getPaymentReasonsConcept());
        paymentReason.setValueCoded(conceptService.getConcept(16));
        return paymentReason;
    }

    private Obs createPaymentReceiptObservation(String receiptNumber) {
        Obs pr = new Obs();
        pr.setConcept(emrProperties.getPaymentReceiptNumberConcept());
        pr.setValueText(receiptNumber);

        return pr;
    }

}
