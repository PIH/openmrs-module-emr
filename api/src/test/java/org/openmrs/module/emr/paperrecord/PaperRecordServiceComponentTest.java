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
import org.openmrs.Person;
import org.openmrs.api.LocationService;
import org.openmrs.api.PatientService;
import org.openmrs.api.PersonService;
import org.openmrs.api.context.Context;
import org.openmrs.test.BaseModuleContextSensitiveTest;
import org.springframework.beans.factory.annotation.Autowired;

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

        Assert.assertEquals(0, paperRecordService.getOpenPaperRecordRequestsToPull().size());

        // all these are from the standard test dataset
        Patient patient = patientService.getPatient(2) ;
        Location medicalRecordLocation = locationService.getLocation(3);
        Location requestLocation = locationService.getLocation(3);

        // just make the request twice
        paperRecordService.requestPaperRecord(patient, medicalRecordLocation, requestLocation);
        paperRecordService.requestPaperRecord(patient, medicalRecordLocation, requestLocation);

        // make sure both records are now in the database
        Assert.assertEquals(2, paperRecordService.getOpenPaperRecordRequestsToCreate().size());
    }


    @Test
    public void testGetOpenPaperRecordRequestsToPull() {

        Assert.assertEquals(0, paperRecordService.getOpenPaperRecordRequestsToPull().size());

        // all these are from the standard test dataset
        Patient patient = patientService.getPatient(2) ;
        Location medicalRecordLocation = locationService.getLocation(1);
        Location requestLocation = locationService.getLocation(3);

        // just make the request twice
        paperRecordService.requestPaperRecord(patient, medicalRecordLocation, requestLocation);
        paperRecordService.requestPaperRecord(patient, medicalRecordLocation, requestLocation);

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
        Assert.assertEquals(PaperRecordRequest.Status.CANCELLED, request.getStatus());
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
        paperRecordService.assignRequests(paperRecordRequests, person);

        // verify
        PaperRecordRequest request = paperRecordRequests.get(0);
        Assert.assertEquals(PaperRecordRequest.Status.ASSIGNED_TO_PULL, request.getStatus());
        Assert.assertEquals(new Integer(7), request.getAssignee().getId());
        Assert.assertEquals("101", request.getIdentifier());

    }
}
