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

package org.openmrs.module.emr.paperrecord;

import junit.framework.Assert;
import org.junit.Before;
import org.junit.Test;
import org.openmrs.Location;
import org.openmrs.Patient;
import org.openmrs.PatientIdentifier;
import org.openmrs.Person;
import org.openmrs.api.LocationService;
import org.openmrs.api.PatientService;
import org.openmrs.api.PersonService;
import org.openmrs.api.context.Context;
import org.openmrs.module.emr.EmrProperties;
import org.openmrs.test.BaseModuleContextSensitiveTest;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Arrays;
import java.util.Date;
import java.util.List;

public class PaperRecordServiceComponentTest extends BaseModuleContextSensitiveTest {

    @Autowired
    PaperRecordService paperRecordService;

    @Autowired
    PatientService patientService;

    @Autowired
    PersonService personService;

    @Autowired
    LocationService locationService;

    @Autowired
    EmrProperties emrProperties;

    @Before
    public void beforeAllTests() throws Exception {
        executeDataSet("paperRecordServiceComponentTestDataset.xml");
    }

    @Test
    public void testThatServiceIsConfiguredCorrectly() {
        Assert.assertNotNull("Couldn't autowire PaperRecordService", paperRecordService);
        Assert.assertNotNull("Couldn't get PaperRecordService from Context", Context.getService(PaperRecordService.class));
    }

    @Test
    public void testRequestPaperRecord() {

        // all these are from the standard test dataset
        Patient patient = patientService.getPatient(2) ;
        Location medicalRecordLocation = locationService.getLocation(1);
        Location requestLocation = locationService.getLocation(3);

        paperRecordService.requestPaperRecord(patient, medicalRecordLocation, requestLocation);

        // first, make sure that this record is not returned by the "to create" service method
        Assert.assertEquals(0, paperRecordService.getOpenPaperRecordRequestsToCreate().size());

        // make sure the record is in the database
        List<PaperRecordRequest> requests = paperRecordService.getOpenPaperRecordRequestsToPull();
        Assert.assertEquals(1, requests.size());
        PaperRecordRequest request = requests.get(0);
        Assert.assertEquals(new Integer(2), request.getPatient().getId());
        Assert.assertEquals(new Integer(1), request.getRecordLocation().getId());
        Assert.assertEquals(new Integer(3), request.getRequestLocation().getId());
        Assert.assertEquals("101", request.getIdentifier());
        Assert.assertEquals(PaperRecordRequest.Status.OPEN, request.getStatus());
        Assert.assertNull(request.getAssignee());

    }

    @Test
    public void testRequestPaperRecordWhenNoValidPatientIdentifierForPaperRecord() {

        // all these are from the standard test dataset
        Patient patient = patientService.getPatient(2) ;
        Location medicalRecordLocation = locationService.getLocation(3);
        Location requestLocation = locationService.getLocation(3);

        paperRecordService.requestPaperRecord(patient, medicalRecordLocation, requestLocation);

        // first, make sure that this record is not returned by the "to pull" service method
        Assert.assertEquals(0, paperRecordService.getOpenPaperRecordRequestsToPull().size());

        // make sure the record is in the database
        List<PaperRecordRequest> requests = paperRecordService.getOpenPaperRecordRequestsToCreate();
        Assert.assertEquals(1, requests.size());
        PaperRecordRequest request = requests.get(0);
        Assert.assertEquals(new Integer(2), request.getPatient().getId());
        Assert.assertEquals(new Integer(3), request.getRecordLocation().getId());
        Assert.assertEquals(new Integer(3), request.getRequestLocation().getId());
        Assert.assertEquals(null, request.getIdentifier());
        Assert.assertEquals(PaperRecordRequest.Status.OPEN, request.getStatus());
        Assert.assertNull(request.getAssignee());

    }

    @Test
    public void testGetOpenPaperRecordRequestsToCreate() {

        Assert.assertEquals(0, paperRecordService.getOpenPaperRecordRequestsToCreate().size());

        // all these are from the standard test dataset (neither patient have medical record identifiers at location 2)
        Patient patient = patientService.getPatient(2) ;
        Patient anotherPatient = patientService.getPatient(8);
        Location medicalRecordLocation = locationService.getLocation(2);
        Location requestLocation = locationService.getLocation(3);

        paperRecordService.requestPaperRecord(patient, medicalRecordLocation, requestLocation);
        paperRecordService.requestPaperRecord(anotherPatient, medicalRecordLocation, requestLocation);

        // make sure both records are now in the database
        Assert.assertEquals(2, paperRecordService.getOpenPaperRecordRequestsToCreate().size());
    }


    @Test
    public void testGetOpenPaperRecordRequestsToPull() {

        Assert.assertEquals(0, paperRecordService.getOpenPaperRecordRequestsToPull().size());

        // all these are from the standard test dataset (both patients have medical record identifiers at location 1)
        Patient patient = patientService.getPatient(2);
        Patient anotherPatient = patientService.getPatient(999);
        Location medicalRecordLocation = locationService.getLocation(1);
        Location requestLocation = locationService.getLocation(3);

        paperRecordService.requestPaperRecord(patient, medicalRecordLocation, requestLocation);
        paperRecordService.requestPaperRecord(anotherPatient, medicalRecordLocation, requestLocation);

        // make sure both records are now is in the database
        Assert.assertEquals(2, paperRecordService.getOpenPaperRecordRequestsToPull().size());
    }

    @Test
    public void testGetPaperRecordRequestById() {

        PaperRecordRequest request = paperRecordService.getPaperRecordRequestById(1);

        Assert.assertNotNull(request);
        Assert.assertEquals(new Integer(7), request.getPatient().getId());
        Assert.assertEquals(new Integer(1), request.getRecordLocation().getId());
        Assert.assertEquals(new Integer(2), request.getRequestLocation().getId());
        Assert.assertEquals("CATBALL", request.getIdentifier());
        Assert.assertEquals(PaperRecordRequest.Status.SENT, request.getStatus());
        Assert.assertNull(request.getAssignee());

    }

    @Test
    public void testAssignRequest() {

        // all these are from the standard test dataset
        Patient patient = patientService.getPatient(2) ;
        Location medicalRecordLocation = locationService.getLocation(1);
        Location requestLocation = locationService.getLocation(3);

        // request a record
        paperRecordService.requestPaperRecord(patient, medicalRecordLocation, requestLocation);

        // retrieve that record
        List<PaperRecordRequest> paperRecordRequests = paperRecordService.getOpenPaperRecordRequestsToPull();
        Assert.assertEquals(1, paperRecordRequests.size()); // sanity check

        // assign the person to the request
        Person person = personService.getPerson(7);
        paperRecordService.assignRequests(paperRecordRequests, person, null);

        // verify
        paperRecordRequests = paperRecordService.getAssignedPaperRecordRequestsToPull();
        Assert.assertEquals(1, paperRecordRequests.size());
        PaperRecordRequest request = paperRecordRequests.get(0);
        Assert.assertEquals(PaperRecordRequest.Status.ASSIGNED_TO_PULL, request.getStatus());
        Assert.assertEquals(new Integer(7), request.getAssignee().getId());
        Assert.assertEquals("101", request.getIdentifier());

    }

    @Test
    public void testRequestPaperRecordWhenDuplicateRecord() {

        // all these are from the standard test dataset
        Patient patient = patientService.getPatient(2) ;
        Location medicalRecordLocation = locationService.getLocation(1);
        Location requestLocation = locationService.getLocation(3);

        paperRecordService.requestPaperRecord(patient, medicalRecordLocation, requestLocation);

        // sanity check; make sure the record is in the database
        List<PaperRecordRequest> requests = paperRecordService.getOpenPaperRecordRequestsToPull();
        Assert.assertEquals(1, requests.size());
        Date dateCreated = requests.get(0).getDateCreated();

        // now request the same record again
        paperRecordService.requestPaperRecord(patient, medicalRecordLocation, requestLocation);

        // there should still only be one paper record request
        requests = paperRecordService.getOpenPaperRecordRequestsToPull();
        Assert.assertEquals(1, requests.size());
        PaperRecordRequest request = requests.get(0);
        Assert.assertEquals(new Integer(2), request.getPatient().getId());
        Assert.assertEquals(new Integer(1), request.getRecordLocation().getId());
        Assert.assertEquals(new Integer(3), request.getRequestLocation().getId());
        Assert.assertEquals("101", request.getIdentifier());
        Assert.assertEquals(PaperRecordRequest.Status.OPEN, request.getStatus());
        Assert.assertEquals(dateCreated, request.getDateCreated());
        Assert.assertNull(request.getAssignee());
    }

    @Test
    public void testRequestPaperRecordWhenDuplicateRecordShouldUpdateLocation() {

        // all these are from the standard test dataset
        Patient patient = patientService.getPatient(2) ;
        Location medicalRecordLocation = locationService.getLocation(1);
        Location requestLocation = locationService.getLocation(3);
        Location anotherRequestLocation = locationService.getLocation(2);

        paperRecordService.requestPaperRecord(patient, medicalRecordLocation, requestLocation);

        // sanity check; make sure the record is in the database
        List<PaperRecordRequest> requests = paperRecordService.getOpenPaperRecordRequestsToPull();
        Assert.assertEquals(1, requests.size());
        Date dateCreated = requests.get(0).getDateCreated();

        // now request the same record again
        paperRecordService.requestPaperRecord(patient, medicalRecordLocation, anotherRequestLocation);

        // there should still only be one paper record request, but with the new location
        requests = paperRecordService.getOpenPaperRecordRequestsToPull();
        Assert.assertEquals(1, requests.size());
        PaperRecordRequest request = requests.get(0);
        Assert.assertEquals(new Integer(2), request.getPatient().getId());
        Assert.assertEquals(new Integer(1), request.getRecordLocation().getId());
        Assert.assertEquals(new Integer(2), request.getRequestLocation().getId());
        Assert.assertEquals("101", request.getIdentifier());
        Assert.assertEquals(PaperRecordRequest.Status.OPEN, request.getStatus());
        Assert.assertEquals(dateCreated, request.getDateCreated());
        Assert.assertNull(request.getAssignee());
    }

    @Test
    public void testRequestPaperRecordWhenDuplicateRecordThatHasAlreadyBeenAssigned() {

        // all these are from the standard test dataset
        Patient patient = patientService.getPatient(2) ;
        Location medicalRecordLocation = locationService.getLocation(1);
        Location requestLocation = locationService.getLocation(3);

        PaperRecordRequest paperRecordRequest = paperRecordService.requestPaperRecord(patient, medicalRecordLocation, requestLocation);

        Person person = personService.getPerson(7);
        paperRecordService.assignRequests(Arrays.asList(paperRecordRequest), person, null);

        // sanity check; make sure the record is in the database
        List<PaperRecordRequest> requests = paperRecordService.getAssignedPaperRecordRequestsToPull();
        Assert.assertEquals(1, requests.size());
        Date dateCreated = requests.get(0).getDateCreated();

        // now request the same record again
        paperRecordService.requestPaperRecord(patient, medicalRecordLocation, requestLocation);

        // there should not be any open requested, and only the one assigned request
        requests = paperRecordService.getOpenPaperRecordRequestsToPull();
        Assert.assertEquals(0, requests.size());
        requests = paperRecordService.getAssignedPaperRecordRequestsToPull();
        Assert.assertEquals(1, requests.size());
        PaperRecordRequest request = requests.get(0);
        Assert.assertEquals(new Integer(2), request.getPatient().getId());
        Assert.assertEquals(new Integer(1), request.getRecordLocation().getId());
        Assert.assertEquals(new Integer(3), request.getRequestLocation().getId());
        Assert.assertEquals("101", request.getIdentifier());
        Assert.assertEquals(PaperRecordRequest.Status.ASSIGNED_TO_PULL, request.getStatus());
        Assert.assertEquals(dateCreated, request.getDateCreated());
        Assert.assertEquals(person, request.getAssignee());
    }

    @Test
    public void testRequestPaperRecordWhenSamePatientButDifferentMedicalRecordLocation() {

        // all these are from the standard test dataset
        Patient patient = patientService.getPatient(2) ;
        Location medicalRecordLocation = locationService.getLocation(2);
        Location anotherMedicalRecordLocation = locationService.getLocation(3);
        Location requestLocation = locationService.getLocation(1);

        paperRecordService.requestPaperRecord(patient, medicalRecordLocation, requestLocation);

        // sanity check; make sure the record is in the database
        List<PaperRecordRequest> requests = paperRecordService.getOpenPaperRecordRequestsToCreate();
        Assert.assertEquals(1, requests.size());
        Date dateCreated = requests.get(0).getDateCreated();

        // now request the the record from the same patient, but a different medical record location
        paperRecordService.requestPaperRecord(patient, anotherMedicalRecordLocation, requestLocation);

        // both of these requests should be in the queue (should not be flagged as a duplicate)
        requests = paperRecordService.getOpenPaperRecordRequestsToCreate();
        Assert.assertEquals(2, requests.size());
    }


    @Test
    public void testRequestPaperRecordShouldNotConsiderCompletedRequestAsDuplicate() {

        // create a request for the patient that has a "completed" request defined in paperRecordServiceComponentTestDataset.xml
        Patient patient = patientService.getPatient(7) ;
        Location medicalRecordLocation = locationService.getLocation(1);
        Location requestLocation = locationService.getLocation(2);

        paperRecordService.requestPaperRecord(patient, medicalRecordLocation, requestLocation);

        // make sure this request has been created
        List<PaperRecordRequest> requests = paperRecordService.getOpenPaperRecordRequestsToCreate();
        Assert.assertEquals(1, requests.size());
        Date dateCreated = requests.get(0).getDateCreated();

        PaperRecordRequest request = requests.get(0);
        Assert.assertEquals(new Integer(7), request.getPatient().getId());
        Assert.assertEquals(new Integer(1), request.getRecordLocation().getId());
        Assert.assertEquals(new Integer(2), request.getRequestLocation().getId());
        Assert.assertNull(request.getIdentifier());
        Assert.assertEquals(PaperRecordRequest.Status.OPEN, request.getStatus());
        Assert.assertEquals(dateCreated, request.getDateCreated());
        Assert.assertNull(request.getAssignee());
    }

    @Test
    public void testGetPendingRequestByIdentifierShouldReturnOpenRequest() {

        // all these are from the standard test dataset
        Patient patient = patientService.getPatient(2) ;
        Location medicalRecordLocation = locationService.getLocation(1);
        Location requestLocation = locationService.getLocation(3);

        paperRecordService.requestPaperRecord(patient, medicalRecordLocation, requestLocation);

        PaperRecordRequest request = paperRecordService.getPendingPaperRecordRequestByIdentifier("101");

        Assert.assertEquals(new Integer(2), request.getPatient().getId());
        Assert.assertEquals(new Integer(1), request.getRecordLocation().getId());
        Assert.assertEquals(new Integer(3), request.getRequestLocation().getId());
        Assert.assertEquals("101", request.getIdentifier());
        Assert.assertEquals(PaperRecordRequest.Status.OPEN, request.getStatus());
        Assert.assertNull(request.getAssignee());

    }

    @Test
    public void testGetPendingRequestByIdentifierShouldReturnAssignedPullRequest() {

        // all these are from the standard test dataset
        Patient patient = patientService.getPatient(2) ;
        Location medicalRecordLocation = locationService.getLocation(1);
        Location requestLocation = locationService.getLocation(3);

        // request a record
        paperRecordService.requestPaperRecord(patient, medicalRecordLocation, requestLocation);

        // retrieve that record
        List<PaperRecordRequest> paperRecordRequests = paperRecordService.getOpenPaperRecordRequestsToPull();
        Assert.assertEquals(1, paperRecordRequests.size()); // sanity check

        // assign the person to the request
        Person person = personService.getPerson(7);
        paperRecordService.assignRequests(paperRecordRequests, person, null);

        PaperRecordRequest request = paperRecordService.getPendingPaperRecordRequestByIdentifier("101");

        Assert.assertEquals(PaperRecordRequest.Status.ASSIGNED_TO_PULL, request.getStatus());
        Assert.assertEquals(new Integer(7), request.getAssignee().getId());
        Assert.assertEquals("101", request.getIdentifier());

    }

    @Test
    public void testGetPendingRequestByIdentifierShouldReturnNullIfNoActiveRequests() {
        // there is a paper record request in the sample database with this identifier, but it is marked as SENT
        Assert.assertNull(paperRecordService.getPendingPaperRecordRequestByIdentifier("CATBALL"));
    }

    @Test
    public void testMarkPaperRecordRequestAsSentShouldMarkRecordRequestAsSent() {

        // all these are from the standard test dataset
        Patient patient = patientService.getPatient(2) ;
        Location medicalRecordLocation = locationService.getLocation(1);
        Location requestLocation = locationService.getLocation(3);

        // request a record
        paperRecordService.requestPaperRecord(patient, medicalRecordLocation, requestLocation);

        // retrieve that record
       PaperRecordRequest request = paperRecordService.getOpenPaperRecordRequestsToPull().get(0);

        // store the id for future retrieval
        int id = request.getId();

        paperRecordService.markPaperRecordRequestAsSent(request);

        // make sure this request has been changed to "sent" in the database
        Context.flushSession();
        Context.clearSession();

        PaperRecordRequest returnedRequest = paperRecordService.getPaperRecordRequestById(id);
        Assert.assertEquals(PaperRecordRequest.Status.SENT, request.getStatus());
    }

    @Test
    public void testGetSentPaperRecordRequestShouldFetchSentRecordRequest() {
        // this identifier exists in the sample test data
        PaperRecordRequest request = paperRecordService.getSentPaperRecordRequestByIdentifier("CATBALL");
        Assert.assertNotNull(request);
        Assert.assertEquals(new Integer(1), request.getId());
    }

    @Test
    public void testGetSentRequestByIdentifierShouldReturnNullIfNoSentRequests() {
        Assert.assertNull(paperRecordService.getPendingPaperRecordRequestByIdentifier("101"));
    }

    @Test
    public void testUpdateStatusSetDateLastUpdated() throws InterruptedException {

        PaperRecordRequest request = new PaperRecordRequest();

        request.updateStatus(PaperRecordRequest.Status.OPEN);
        Date date = request.getDateStatusChanged();

        paperRecordService.savePaperRecordRequest(request);
        int id = request.getId();

        Context.flushSession();
        Context.clearSession();

        PaperRecordRequest retrievedRequest = paperRecordService.getPaperRecordRequestById(id);
        Assert.assertNotNull(retrievedRequest.getDateStatusChanged());
    }

    @Test(expected = UnableToPrintPaperRecordLabelException.class)
    public void testPrintPaperRecordLabel() throws Exception {

        executeDataSet("printerServiceComponentTestDataset.xml");

        Patient patient = patientService.getPatient(2) ;

        // remove the address associated with this patient, so that we can avoid having to configure the Address template
        // (which isn't what we are looking to test here anyhow)
        patient.removeAddress(patient.getPersonAddress());

        PaperRecordRequest request = new PaperRecordRequest();
        request.setIdentifier("123");
        request.setPatient(patient);

        Location location = Context.getLocationService().getLocation(3);

        // try printing when no printer is configured
        paperRecordService.printPaperRecordLabel(request, location);
    }

    @Test
    public void testMarkPapersRecordForMergeShouldCreatePaperRecordMergeRequest() throws Exception {

        // retrieve a couple patient identifiers from the standard test dataset
        PatientIdentifier preferredIdentifier = patientService.getPatient(2).getPatientIdentifier(emrProperties.getPaperRecordIdentifierType());
        PatientIdentifier notPreferredIdentifier = patientService.getPatient(999).getPatientIdentifier(emrProperties.getPaperRecordIdentifierType());

        paperRecordService.markPaperRecordsForMerge(preferredIdentifier, notPreferredIdentifier);

        Assert.assertEquals(1, paperRecordService.getOpenPaperRecordMergeRequests().size());
        PaperRecordMergeRequest request = paperRecordService.getOpenPaperRecordMergeRequests().get(0);

        Assert.assertEquals(preferredIdentifier.getIdentifier(), request.getPreferredIdentifier());
        Assert.assertEquals(notPreferredIdentifier.getIdentifier(), request.getNotPreferredIdentifier());
        Assert.assertEquals(preferredIdentifier.getPatient(), request.getPreferredPatient());
        Assert.assertEquals(notPreferredIdentifier.getPatient(), request.getNotPreferredPatient());
        Assert.assertEquals(preferredIdentifier.getLocation(), request.getRecordLocation());
        Assert.assertEquals(PaperRecordMergeRequest.Status.OPEN, request.getStatus());
        Assert.assertNotNull(request.getDateCreated());
        Assert.assertEquals(Context.getAuthenticatedUser(), request.getCreator());

        Assert.assertTrue(notPreferredIdentifier.isVoided());
    }

    @Test
    public void testMarkPaperRecordsAsMergedShouldMarkPaperRecordsAsMerged() throws Exception {

        // first, create the merge request
        PatientIdentifier preferredIdentifier = patientService.getPatient(2).getPatientIdentifier(emrProperties.getPaperRecordIdentifierType());
        PatientIdentifier notPreferredIdentifier = patientService.getPatient(999).getPatientIdentifier(emrProperties.getPaperRecordIdentifierType());

        paperRecordService.markPaperRecordsForMerge(preferredIdentifier, notPreferredIdentifier);

        Assert.assertEquals(1, paperRecordService.getOpenPaperRecordMergeRequests().size()); // sanity check
        PaperRecordMergeRequest request = paperRecordService.getOpenPaperRecordMergeRequests().get(0);

        paperRecordService.markPaperRecordsAsMerged(request);

        int id = request.getId();

        Context.flushSession();
        Context.clearSession();

        request = paperRecordService.getPaperRecordMergeRequestById(id);
        Assert.assertEquals(PaperRecordMergeRequest.Status.MERGED, request.getStatus());
    }

    @Test
    public void testMarkPaperRecordsAsMergedShouldMergeExistingPaperRecordRequests() throws Exception {

        Location paperRecordLocation = locationService.getLocation(1);
        Location someLocation = locationService.getLocation(2);
        Location anotherLocation = locationService.getLocation(3);

        PatientIdentifier preferredIdentifier = patientService.getPatient(2).getPatientIdentifier(emrProperties.getPaperRecordIdentifierType());
        PatientIdentifier notPreferredIdentifier = patientService.getPatient(999).getPatientIdentifier(emrProperties.getPaperRecordIdentifierType());

        // first, create a couple record requests
        paperRecordService.requestPaperRecord(preferredIdentifier.getPatient(), paperRecordLocation, someLocation);
        paperRecordService.requestPaperRecord(notPreferredIdentifier.getPatient(), paperRecordLocation, anotherLocation);
        Assert.assertEquals(2, paperRecordService.getOpenPaperRecordRequestsToPull().size());   // sanity check


        // now create the merge request & then mark it as merged
        paperRecordService.markPaperRecordsForMerge(preferredIdentifier, notPreferredIdentifier);
        PaperRecordMergeRequest mergeRequest = paperRecordService.getOpenPaperRecordMergeRequests().get(0);
        paperRecordService.markPaperRecordsAsMerged(mergeRequest);

        // now there should be only one outstanding paper record request
        Assert.assertEquals(1, paperRecordService.getOpenPaperRecordRequestsToPull().size());

        PaperRecordRequest request = paperRecordService.getOpenPaperRecordRequestsToPull().get(0);

        Assert.assertEquals(preferredIdentifier.getIdentifier(), request.getIdentifier());
        Assert.assertEquals(preferredIdentifier.getPatient(), request.getPatient());
        Assert.assertEquals(anotherLocation, request.getRequestLocation());  // since the last requested location wins

    }
}
