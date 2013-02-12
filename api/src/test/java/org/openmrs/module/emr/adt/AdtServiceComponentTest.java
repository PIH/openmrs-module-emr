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

import java.util.ArrayList;
import java.util.List;

import org.hamcrest.Matchers;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentMatcher;
import org.openmrs.Encounter;
import org.openmrs.Location;
import org.openmrs.LocationTag;
import org.openmrs.Obs;
import org.openmrs.Patient;
import org.openmrs.api.ConceptService;
import org.openmrs.api.LocationService;
import org.openmrs.api.PatientService;
import org.openmrs.api.context.Context;
import org.openmrs.module.emr.EmrConstants;
import org.openmrs.module.emr.EmrProperties;
import org.openmrs.module.emr.paperrecord.PaperRecordRequest;
import org.openmrs.module.emr.paperrecord.PaperRecordService;
import org.openmrs.test.BaseModuleContextSensitiveTest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import static org.hamcrest.Matchers.containsInAnyOrder;
import static org.hamcrest.core.Is.is;
import static org.hamcrest.core.IsNull.notNullValue;
import static org.junit.Assert.assertThat;
import static org.junit.matchers.JUnitMatchers.hasItem;
import static org.openmrs.module.emr.TestUtils.isJustNow;

@RunWith(SpringJUnit4ClassRunner.class)
public class AdtServiceComponentTest extends BaseModuleContextSensitiveTest {

    @Autowired
    private AdtService service;
    @Autowired
    private EmrProperties emrProperties;
    @Autowired
    private ConceptService conceptService;
    @Autowired
    private PaperRecordService paperRecordService;
    @Autowired
    LocationService locationService;

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
        Obs firstReason = createPaymentReasonObservation();
        Obs firstAmount = createPaymentAmountObservation(20);
        Obs firstReceipt = createPaymentReceiptObservation("AB23423");
        Obs firstGroup = createPaymentGroup(firstReason, firstAmount, firstReceipt);
        observations.add(firstGroup);

        Obs secondReason = createPaymentReasonObservation();
        Obs secondAmount = createPaymentAmountObservation(20);
        Obs secondReceipt = createPaymentReceiptObservation("AB23423");
        Obs secondGroup = createPaymentGroup(secondReason, secondAmount, secondReceipt);
        observations.add(secondGroup);

        // step 1: check in the patient (which should create a visit and an encounter)
        Encounter checkInEncounter = service.checkInPatient(patient, outpatientDepartment, null, observations, null,
            false);

        assertThat(checkInEncounter.getVisit(), notNullValue());
        assertThat(checkInEncounter.getPatient(), is(patient));
        assertThat(checkInEncounter.getEncounterDatetime(), isJustNow());
        assertThat(checkInEncounter.getVisit().getPatient(), is(patient));
        assertThat(checkInEncounter.getVisit().getStartDatetime(), isJustNow());
        assertThat(checkInEncounter.getObs(),
            containsInAnyOrder(firstReason, firstAmount, firstReceipt, secondReason, secondAmount, secondReceipt));
        assertThat(checkInEncounter.getAllObs(), containsInAnyOrder(firstGroup, secondGroup));

        // TODO once these are implemented, add Admission and Discharge to this test
    }

    @Test
    public void shouldCancelPendingPaperRecordRequestsTAfterMerge() {
        PatientService patientService = Context.getPatientService();
        Location paperRecordLocation = locationService.getLocation(1);
        Location someLocation = locationService.getLocation(2);
        Location anotherLocation = locationService.getLocation(3);

        assertThat(paperRecordService.getOpenPaperRecordRequestsToCreate().size(), is(0));

        Patient preferredPatient = patientService.getPatient(7);
        Patient notPreferredPatient = patientService.getPatient(8);

        // first, create a couple record requests
        paperRecordService.requestPaperRecord(preferredPatient, paperRecordLocation, someLocation);
        paperRecordService.requestPaperRecord(notPreferredPatient, paperRecordLocation, anotherLocation);

        assertThat(paperRecordService.getOpenPaperRecordRequestsToCreate().size(), is(2));

        service.mergePatients(preferredPatient, notPreferredPatient);

        assertThat(paperRecordService.getOpenPaperRecordRequestsToCreate().size(), is(0));

        List<PaperRecordRequest> paperRecordRequestsPreferred = paperRecordService.getPaperRecordRequestsByPatient(
            preferredPatient);
        assertThat(paperRecordRequestsPreferred.size(), is(2));

        for (PaperRecordRequest request : paperRecordRequestsPreferred) {
            assertThat(request.getStatus(), is(PaperRecordRequest.Status.CANCELLED));
        }

        assertThat(paperRecordService.getPaperRecordRequestsByPatient(notPreferredPatient).size(), is(0));
    }

  /*  @Test
    public void shoulMoveOpenPaperRecordRequestsToCreateToOpenRequestToPullAfterMerge() {
        PatientService patientService = Context.getPatientService();
        Location paperRecordLocation = locationService.getLocation(1);
        Location anotherLocation = locationService.getLocation(3);

        assertThat(paperRecordService.getOpenPaperRecordRequestsToCreate().size(), is(0));

        Patient preferredPatient = patientService.getPatient(2);
        Patient notPreferredPatient = patientService.getPatient(8);

        // first, create a record request
        paperRecordService.requestPaperRecord(notPreferredPatient, paperRecordLocation, anotherLocation);

        assertThat(paperRecordService.getOpenPaperRecordRequestsToCreate().size(), is(1));

        service.mergePatients(preferredPatient, notPreferredPatient);

        assertThat(paperRecordService.getOpenPaperRecordRequestsToPull().size(), is(1));

        List<PaperRecordRequest> requestList = paperRecordService.getPaperRecordRequestsByPatient(
            preferredPatient);
        assertThat(requestList.size(), is(1));
        assertThat(requestList,
            hasItem(new IsExpectedPaperRecordRequest(PaperRecordRequest.Status.OPEN)));
        assertThat(requestList.get(0).getIdentifier(),
            is(preferredPatient.getPatientIdentifier(emrProperties.getPaperRecordIdentifierType()).getIdentifier()));
    }

    @Test
    public void shoulMoveOpenPaperRecordRequestsToCreateToOpenRequestToPullAfterMergeOnNotPreferredWithPaperRecordIdentifier() {
        PatientService patientService = Context.getPatientService();
        Location paperRecordLocation = locationService.getLocation(1);
        Location anotherLocation = locationService.getLocation(3);

        assertThat(paperRecordService.getOpenPaperRecordRequestsToCreate().size(), is(0));

        Patient preferredPatient = patientService.getPatient(8);
        Patient notPreferredPatient = patientService.getPatient(2);

        // first, create a record request
        paperRecordService.requestPaperRecord(preferredPatient, paperRecordLocation, anotherLocation);

        assertThat(paperRecordService.getOpenPaperRecordRequestsToCreate().size(), is(1));

        service.mergePatients(preferredPatient, notPreferredPatient);

        assertThat(paperRecordService.getOpenPaperRecordRequestsToPull().size(), is(1));

        List<PaperRecordRequest> requestList = paperRecordService.getPaperRecordRequestsByPatient(
            preferredPatient);
        assertThat(requestList.size(), is(1));
        assertThat(requestList,
            hasItem(new IsExpectedPaperRecordRequest(PaperRecordRequest.Status.OPEN)));
        assertThat(requestList.get(0).getIdentifier(),
            is(preferredPatient.getPatientIdentifier(emrProperties.getPaperRecordIdentifierType()).getIdentifier()));
    }
*/
    private class IsExpectedPaperRecordRequest extends ArgumentMatcher<PaperRecordRequest> {
        private PaperRecordRequest.Status status;

        public IsExpectedPaperRecordRequest(PaperRecordRequest.Status status) {
            this.status = status;
        }

        @Override
        public boolean matches(Object o) {
            PaperRecordRequest actual = (PaperRecordRequest) o;

            try {
                assertThat(actual.getStatus(), is(status));
                return true;
            } catch (AssertionError e) {
                return false;
            }
        }
    }

    private Obs createPaymentGroup(Obs paymentReasonObservation, Obs paymentAmountObservation, Obs paymentReceiptObservation) {
        Obs firstGroup = new Obs();
        firstGroup.setConcept(emrProperties.getPaymentConstructConcept());
        firstGroup.addGroupMember(paymentReasonObservation);
        firstGroup.addGroupMember(paymentAmountObservation);
        firstGroup.addGroupMember(paymentReceiptObservation);
        return firstGroup;
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
