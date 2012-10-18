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

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentMatcher;
import org.openmrs.*;
import org.openmrs.api.context.Context;
import org.openmrs.module.emr.api.db.PaperRecordRequestDAO;
import org.openmrs.module.emr.api.impl.PaperRecordServiceImpl;
import org.openmrs.module.emr.domain.PaperRecordRequest;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertThat;
import static org.mockito.Matchers.argThat;
import static org.mockito.Mockito.verify;
import static org.powermock.api.mockito.PowerMockito.*;

@RunWith(PowerMockRunner.class)
@PrepareForTest(Context.class)
public class PaperRecordServiceTest {

    private PaperRecordService paperRecordService;
    private PaperRecordRequestDAO mockPaperRecordDAO;
    private User authenicatedUser;
    private PatientIdentifierType paperRecordIdentifierType;

    @Before
    public void setup() {
        mockStatic(Context.class);

        authenicatedUser = new User();
        when(Context.getAuthenticatedUser()).thenReturn(authenicatedUser);

        mockPaperRecordDAO = mock(PaperRecordRequestDAO.class);

        paperRecordIdentifierType = new PatientIdentifierType();
        paperRecordIdentifierType.setId(2);

        paperRecordService = new PaperRecordServiceStub(paperRecordIdentifierType);
        ((PaperRecordServiceImpl) paperRecordService).setPaperRecordRequestDAO(mockPaperRecordDAO);
    }

    @Test
    public void testCreatePaperRecordRequest() throws Exception {

        Patient patient = new Patient();
        patient.setId(15);

        Location medicalRecordLocation = new Location();
        medicalRecordLocation.setId(3);
        medicalRecordLocation.setName("Mirebalais");

        Location requestLocation = new Location();
        requestLocation.setId(4);
        requestLocation.setName("Outpatient Clinic");

        PatientIdentifier identifer = new PatientIdentifier();
        identifer.setIdentifier("ABCZYX");
        identifer.setIdentifierType(paperRecordIdentifierType);
        identifer.setLocation(medicalRecordLocation);

        patient.addIdentifier(identifer);

        PaperRecordRequest expectedRequest = new PaperRecordRequest();
        expectedRequest.setAssignee(null);
        expectedRequest.setCreator(authenicatedUser);
        expectedRequest.setIdentifier("ABCZYX");
        expectedRequest.setMedicalRecordLocation(medicalRecordLocation);
        expectedRequest.setPatient(patient);
        expectedRequest.setStatus(PaperRecordRequest.Status.OPEN);

        paperRecordService.requestPaperRecord(patient, medicalRecordLocation, requestLocation);

        verify(mockPaperRecordDAO).saveOrUpdate(argThat(new IsExpectedRequest(expectedRequest)));

    }

    @Test
    public void testCreatePaperRecordRequestForPatientWithMultipleIdentifiersAtSameLocation() throws Exception {

        Patient patient = new Patient();
        patient.setId(15);

        Location medicalRecordLocation = new Location();
        medicalRecordLocation.setId(3);
        medicalRecordLocation.setName("Mirebalais");

        Location otherLocation = new Location();
        otherLocation.setId(5);
        otherLocation.setName("Cange");

        Location requestLocation = new Location();
        requestLocation.setId(4);
        requestLocation.setName("Outpatient Clinic");


        PatientIdentifier wrongIdentifer = new PatientIdentifier();
        wrongIdentifer.setIdentifier("ZYXCBA");
        wrongIdentifer.setIdentifierType(paperRecordIdentifierType);
        wrongIdentifer.setLocation(otherLocation);
        patient.addIdentifier(wrongIdentifer);

        PatientIdentifier identifer = new PatientIdentifier();
        identifer.setIdentifier("ABCZYX");
        identifer.setIdentifierType(paperRecordIdentifierType);
        identifer.setLocation(medicalRecordLocation);
        patient.addIdentifier(identifer);

        PaperRecordRequest expectedRequest = new PaperRecordRequest();
        expectedRequest.setAssignee(null);
        expectedRequest.setCreator(authenicatedUser);
        expectedRequest.setIdentifier("ABCZYX");
        expectedRequest.setMedicalRecordLocation(medicalRecordLocation);
        expectedRequest.setPatient(patient);
        expectedRequest.setStatus(PaperRecordRequest.Status.OPEN);

        paperRecordService.requestPaperRecord(patient, medicalRecordLocation, requestLocation);

        verify(mockPaperRecordDAO).saveOrUpdate(argThat(new IsExpectedRequest(expectedRequest)));

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
            assertThat(actualRequest.getMedicalRecordLocation(), is(expectedRequest.getMedicalRecordLocation()));
            assertThat(actualRequest.getPatient(), is(expectedRequest.getPatient()));
            assertThat(actualRequest.getStatus(), is(expectedRequest.getStatus()));
            assertNotNull(actualRequest.getDateCreated());

            return true;
        }

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

}
