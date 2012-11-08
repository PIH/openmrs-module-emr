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

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentMatcher;
import org.openmrs.Location;
import org.openmrs.Patient;
import org.openmrs.PatientIdentifier;
import org.openmrs.PatientIdentifierType;
import org.openmrs.Person;
import org.openmrs.User;
import org.openmrs.api.PatientService;
import org.openmrs.api.context.Context;
import org.openmrs.messagesource.MessageSourceService;
import org.openmrs.module.emr.paperrecord.db.PaperRecordRequestDAO;
import org.openmrs.module.idgen.service.IdentifierSourceService;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import java.util.ArrayList;
import java.util.List;

import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertThat;
import static org.mockito.Matchers.argThat;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.powermock.api.mockito.PowerMockito.mockStatic;
import static org.powermock.api.mockito.PowerMockito.when;

@RunWith(PowerMockRunner.class)
@PrepareForTest(Context.class)
public class PaperRecordServiceTest {

    private PaperRecordServiceImpl paperRecordService;
    private PaperRecordRequestDAO mockPaperRecordDAO;
    private User authenticatedUser;
    private PatientIdentifierType paperRecordIdentifierType;
    private IdentifierSourceService identifierSourceService;
    private PatientService patientService;

    @Before
    public void setup() {
        mockStatic(Context.class);

        authenticatedUser = new User();
        when(Context.getAuthenticatedUser()).thenReturn(authenticatedUser);

        mockPaperRecordDAO = mock(PaperRecordRequestDAO.class);
        identifierSourceService = mock(IdentifierSourceService.class);
        patientService = mock(PatientService.class);

        paperRecordIdentifierType = new PatientIdentifierType();
        paperRecordIdentifierType.setId(2);

        paperRecordService = new PaperRecordServiceStub(paperRecordIdentifierType);
        paperRecordService.setPaperRecordRequestDAO(mockPaperRecordDAO);
        paperRecordService.setIdentifierSourceService(identifierSourceService);
        paperRecordService.setPatientService(patientService);
    }

    @Test
    public void testRequestPaperRecord() throws Exception {

        Patient patient = new Patient();
        patient.setId(15);

        Location medicalRecordLocation = createMedicalRecordLocation();
        Location requestLocation = createLocation(4, "Outpatient Clinic");

        PatientIdentifier identifer = createIdentifier(medicalRecordLocation, "ABCZYX");
        patient.addIdentifier(identifer);

        PaperRecordRequest expectedRequest = createExpectedRequest(patient, medicalRecordLocation, "ABCZYX");
        IsExpectedRequest expectedRequestMatcher = new IsExpectedRequest(expectedRequest);

        PaperRecordRequest returnedRequest = paperRecordService.requestPaperRecord(patient, medicalRecordLocation, requestLocation);
        verify(mockPaperRecordDAO).saveOrUpdate(argThat(expectedRequestMatcher));
        expectedRequestMatcher.matches(returnedRequest);
    }

    private PatientIdentifier createIdentifier(Location medicalRecordLocation, String identifier) {
        PatientIdentifier identifer = new PatientIdentifier();
        identifer.setIdentifier(identifier);
        identifer.setIdentifierType(paperRecordIdentifierType);
        identifer.setLocation(medicalRecordLocation);
        return identifer;
    }

    private Location createLocation(int locationId, String locationName) {
        Location requestLocation = new Location();
        requestLocation.setId(locationId);
        requestLocation.setName(locationName);
        return requestLocation;
    }

    private Location createMedicalRecordLocation() {
        return createLocation(3,"Mirebalais");
    }

    @Test(expected = IllegalStateException.class)
    public void testRequestPaperRecordShouldThrowExceptionIfPatientNull() throws Exception {

        Location medicalRecordLocation = createMedicalRecordLocation();

        Location requestLocation = createLocation(4, "Outpatient Clinic");

        paperRecordService.requestPaperRecord(null, medicalRecordLocation, requestLocation);

    }

    @Test(expected = IllegalStateException.class)
    public void testRequestPaperRecordShouldThrowExceptionIfRecordLocationNull() throws Exception {

        Patient patient = new Patient();
        patient.setId(15);

        Location requestLocation = createLocation(4, "Outpatient Clinic");

        paperRecordService.requestPaperRecord(patient, null, requestLocation);

    }


    @Test(expected = IllegalStateException.class)
    public void testRequestPaperRecordShouldThrowExceptionIfRequestLocationNull() throws Exception {

        Patient patient = new Patient();
        patient.setId(15);

        Location medicalRecordLocation = createMedicalRecordLocation();

        paperRecordService.requestPaperRecord(patient, medicalRecordLocation, null);

    }


    @Test
    public void testRequestPaperRecordForPatientWithMultipleIdentifiersOfSameTypeAtDifferentLocations() throws Exception {

        Patient patient = new Patient();
        patient.setId(15);

        Location medicalRecordLocation = createMedicalRecordLocation();

        Location otherLocation = createLocation(5, "Cange");

        Location requestLocation = createLocation(4, "Outpatient Clinic");


        PatientIdentifier wrongIdentifer = createIdentifier(otherLocation, "ZYXCBA");
        patient.addIdentifier(wrongIdentifer);

        PatientIdentifier identifer = createIdentifier(medicalRecordLocation, "ABCZYX");
        patient.addIdentifier(identifer);

        PaperRecordRequest expectedRequest = createExpectedRequest(patient, medicalRecordLocation, "ABCZYX");

        IsExpectedRequest expectedRequestMatcher = new IsExpectedRequest(expectedRequest);

        PaperRecordRequest returnedRequest = paperRecordService.requestPaperRecord(patient, medicalRecordLocation, requestLocation);
        verify(mockPaperRecordDAO).saveOrUpdate(argThat(expectedRequestMatcher));
        expectedRequestMatcher.matches(returnedRequest);
    }

    private PaperRecordRequest createExpectedRequest(Patient patient, Location medicalRecordLocation, String identifier) {
        PaperRecordRequest expectedRequest = new PaperRecordRequest();
        expectedRequest.setAssignee(null);
        expectedRequest.setCreator(authenticatedUser);
        expectedRequest.setIdentifier(identifier);
        expectedRequest.setRecordLocation(medicalRecordLocation);
        expectedRequest.setPatient(patient);
        expectedRequest.setStatus(PaperRecordRequest.Status.OPEN);
        return expectedRequest;
    }

    @Test
    public void testRequestPaperRecordWhenPatientHasNoValidIdentifier() throws Exception {

        MessageSourceService messageSourceService = mock(MessageSourceService.class);
        when(messageSourceService.getMessage("emr.missingPaperRecordIdentifierCode")).thenReturn("UNKNOWN");
        ((PaperRecordServiceImpl) paperRecordService).setMessageSourceService(messageSourceService);

        Patient patient = new Patient();
        patient.setId(15);

        Location medicalRecordLocation = createMedicalRecordLocation();

        Location requestLocation = createLocation(4, "Outpatient Clinic");

        PaperRecordRequest expectedRequest = createExpectedRequest(patient, medicalRecordLocation, null);

        IsExpectedRequest expectedRequestMatcher = new IsExpectedRequest(expectedRequest);

        PaperRecordRequest returnedRequest = paperRecordService.requestPaperRecord(patient, medicalRecordLocation, requestLocation);
        verify(mockPaperRecordDAO).saveOrUpdate(argThat(expectedRequestMatcher));
        expectedRequestMatcher.matches(returnedRequest);
    }

    @Test
    public void testAssignRequestsWithoutIdentifiers() throws Exception {
        Person assignTo = new Person(15);

        List<PaperRecordRequest> requests = new ArrayList<PaperRecordRequest>();
        requests.add(buildPaperRecordRequestWithoutIdentifier());
        requests.add(buildPaperRecordRequestWithoutIdentifier());
        requests.add(buildPaperRecordRequestWithoutIdentifier());

        paperRecordService.assignRequests(requests, assignTo);

        verify(mockPaperRecordDAO, times(3)).saveOrUpdate(argThat(new IsAssignedTo(assignTo, PaperRecordRequest.Status.ASSIGNED_TO_CREATE)));
    }

    @Test
    public void testAssignRequestsWithIdentifiers() throws Exception {
        Person assignTo = new Person(15);

        List<PaperRecordRequest> requests = new ArrayList<PaperRecordRequest>();
        requests.add(buildPaperRecordRequestWithIdentifier());
        requests.add(buildPaperRecordRequestWithIdentifier());
        requests.add(buildPaperRecordRequestWithIdentifier());

        paperRecordService.assignRequests(requests, assignTo);

        verify(mockPaperRecordDAO, times(3)).saveOrUpdate(argThat(new IsAssignedTo(assignTo, PaperRecordRequest.Status.ASSIGNED_TO_PULL)));
    }


    @Test(expected = IllegalStateException.class)
    public void testAssignRequestsShouldFailIfRequestsNull() throws Exception {

        Person assignTo = new Person(15);
        paperRecordService.assignRequests(null, assignTo);
    }

    @Test(expected = IllegalStateException.class)
    public void testAssignRequestsShouldFailIfAssigneeNull() throws Exception {

        List<PaperRecordRequest> requests = new ArrayList<PaperRecordRequest>();
        requests.add(buildPaperRecordRequestWithoutIdentifier());
        requests.add(buildPaperRecordRequestWithoutIdentifier());
        requests.add(buildPaperRecordRequestWithoutIdentifier());

        paperRecordService.assignRequests(requests, null);
    }

    @Test
    public void testAssignRequestsShouldSetToPullIfPatientHasValidIdentifierEvenIfRequestDoesNot() throws Exception {
        Person assignTo = new Person(15);

        List<PaperRecordRequest> requests = new ArrayList<PaperRecordRequest>();
        requests.add(buildPaperRecordRequestWithoutIdentifier());

        // add an identifier to this patient
        Patient patient = requests.get(0).getPatient();
        PatientIdentifier patientIdentifier = new PatientIdentifier();
        patientIdentifier.setIdentifier("ABC");
        patientIdentifier.setIdentifierType(paperRecordIdentifierType);
        patientIdentifier.setLocation(requests.get(0).getRecordLocation());
        patient.addIdentifier(patientIdentifier);

        paperRecordService.assignRequests(requests, assignTo);

        verify(mockPaperRecordDAO, times(1)).saveOrUpdate(argThat(new IsAssignedTo(assignTo, PaperRecordRequest.Status.ASSIGNED_TO_PULL, "ABC")));
    }

    private PaperRecordRequest buildPaperRecordRequestWithoutIdentifier() {
        Patient patient = new Patient(1);
        Location location = new Location(1);
        PaperRecordRequest request = new PaperRecordRequest();
        request.setPatient(patient);
        request.setStatus(PaperRecordRequest.Status.OPEN);
        request.setRecordLocation(location);
        return request;
    }

    private PaperRecordRequest buildPaperRecordRequestWithIdentifier() {
        Patient patient = new Patient(1);
        Location location = new Location(1);
        PaperRecordRequest request = new PaperRecordRequest();
        request.setPatient(patient);
        request.setStatus(PaperRecordRequest.Status.OPEN);
        request.setRecordLocation(location);
        request.setIdentifier("ABC");
        return request;
    }


    @Test
    public void whenPatientDoesNotHaveAnPaperMedicalRecordIdentifierShouldCreateAnPaperMedicalRecordNumberAndAssignToHim(){
        String paperMedicalRecordNumberAsExpected = "A000001";
        when(identifierSourceService.generateIdentifier(paperRecordIdentifierType,"generating a new dossier number")).thenReturn(paperMedicalRecordNumberAsExpected);

        Patient patient = new Patient();

        PatientIdentifier identifier = new PatientIdentifier(paperMedicalRecordNumberAsExpected, paperRecordIdentifierType, createMedicalRecordLocation());

        String paperMedicalRecordNumber = paperRecordService.createPaperMedicalRecordNumberFor(patient, createMedicalRecordLocation());

        //cannot compare using one identifier because the equals is not implemented correctly
        verify(patientService).savePatientIdentifier(any(PatientIdentifier.class));

        assertEquals(paperMedicalRecordNumberAsExpected, paperMedicalRecordNumber);
    }

    private class PaperRecordServiceStub extends PaperRecordServiceImpl {

        private PatientIdentifierType paperRecordIdentifierType;

        public PaperRecordServiceStub(PatientIdentifierType paperRecordIdentifierType) {
            this.paperRecordIdentifierType = paperRecordIdentifierType;
        }

        @Override
        protected PatientIdentifierType getPaperRecordIdentifierType()  {
            return paperRecordIdentifierType;
        }

    }

    private class IsExpectedRequest extends ArgumentMatcher<PaperRecordRequest> {

        private PaperRecordRequest expectedRequest;

        public IsExpectedRequest(PaperRecordRequest expectedRequest) {
            this.expectedRequest = expectedRequest;
        }

        @Override
        public boolean matches(Object o) {

            PaperRecordRequest actualRequest = (PaperRecordRequest) o;

            assertThat(actualRequest.getAssignee(), is(expectedRequest.getAssignee()));
            assertThat(actualRequest.getCreator(), is(expectedRequest.getCreator()));
            assertThat(actualRequest.getIdentifier(), is(expectedRequest.getIdentifier()));
            assertThat(actualRequest.getRecordLocation(), is(expectedRequest.getRecordLocation()));
            assertThat(actualRequest.getPatient(), is(expectedRequest.getPatient()));
            assertThat(actualRequest.getStatus(), is(expectedRequest.getStatus()));
            assertNotNull(actualRequest.getDateCreated());

            return true;
        }

    }

    private class IsAssignedTo extends ArgumentMatcher<PaperRecordRequest> {

        private Person shouldBeAssignedTo;

        private PaperRecordRequest.Status assignmentStatus;

        private String identifier;

        public IsAssignedTo(Person shouldBeAssignedTo, PaperRecordRequest.Status assignmentStatus) {
            this.shouldBeAssignedTo = shouldBeAssignedTo;
            this.assignmentStatus = assignmentStatus;
        }


        public IsAssignedTo(Person shouldBeAssignedTo, PaperRecordRequest.Status assignmentStatus, String identifier) {
            this.shouldBeAssignedTo = shouldBeAssignedTo;
            this.assignmentStatus = assignmentStatus;
            this.identifier = identifier;
        }

        @Override
        public boolean matches(Object o) {
            PaperRecordRequest request = (PaperRecordRequest) o;
            assertThat(request.getStatus(), is(assignmentStatus));
            assertThat(request.getAssignee(), is(shouldBeAssignedTo));

            if (identifier != null) {
                assertThat(request.getIdentifier(), is(identifier));
            }

            return true;
        }
    }
}
