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

package org.openmrs.module.emr.fragment.controller.paperrecord;

import org.junit.Test;
import org.openmrs.Location;
import org.openmrs.Patient;
import org.openmrs.PatientIdentifier;
import org.openmrs.PatientIdentifierType;
import org.openmrs.PersonName;
import org.openmrs.module.emr.EmrProperties;
import org.openmrs.module.emr.TestUiUtils;
import org.openmrs.module.emr.paperrecord.PaperRecordRequest;
import org.openmrs.module.emr.paperrecord.PaperRecordService;
import org.openmrs.ui.framework.SimpleObject;
import org.openmrs.ui.framework.UiUtils;
import org.openmrs.ui.framework.fragment.action.FailureResult;
import org.openmrs.ui.framework.fragment.action.FragmentActionResult;
import org.openmrs.ui.framework.fragment.action.SuccessResult;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import static org.hamcrest.CoreMatchers.instanceOf;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.Matchers.containsString;
import static org.junit.Assert.assertThat;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class ArchivesRoomFragmentControllerTest {

    @Test
    public void testControllerShouldReturnFailureResultIfNoMatchingRequestFound() throws Exception {

        ArchivesRoomFragmentController controller = new ArchivesRoomFragmentController();

        UiUtils ui = new TestUiUtils();
        PaperRecordService paperRecordService = mock(PaperRecordService.class);

        when(paperRecordService.getPendingPaperRecordRequestByIdentifier(eq("123"))).thenReturn(null);
        when(paperRecordService.getSentPaperRecordRequestByIdentifier(eq("123"))).thenReturn(null);

        FragmentActionResult result = controller.markPaperRecordRequestAsSent("123", paperRecordService, ui);

        assertThat(result, instanceOf(FailureResult.class));
        FailureResult failureResult = (FailureResult) result;
        assertThat(((FailureResult) result).getSingleError(), containsString("123"));

    }

    @Test
    public void testControllerShouldReturnFailureResultIfSentRequestFound() throws Exception {

        ArchivesRoomFragmentController controller = new ArchivesRoomFragmentController();

        UiUtils ui = new TestUiUtils();
        PaperRecordService paperRecordService = mock(PaperRecordService.class);

        PaperRecordRequest request = new PaperRecordRequest();
        Location location = new Location();
        location.setName("Test location");
        request.setRequestLocation(location);

        when(paperRecordService.getPendingPaperRecordRequestByIdentifier(eq("123"))).thenReturn(null);
        when(paperRecordService.getSentPaperRecordRequestByIdentifier(eq("123"))).thenReturn(request);

        FragmentActionResult result = controller.markPaperRecordRequestAsSent("123", paperRecordService, ui);

        assertThat(result, instanceOf(FailureResult.class));
        FailureResult failureResult = (FailureResult) result;
        assertThat(failureResult.getSingleError(), containsString("123"));
        assertThat(failureResult.getSingleError(), containsString(location.getDisplayString()));
    }

    @Test
    public void testControllerShouldMarkRecordAsSent() throws Exception {

        ArchivesRoomFragmentController controller = new ArchivesRoomFragmentController();

        UiUtils ui = new TestUiUtils();
        PaperRecordService paperRecordService = mock(PaperRecordService.class);

        PaperRecordRequest request = new PaperRecordRequest();
        Location location = new Location();
        location.setName("Test location");
        request.setRequestLocation(location);
        request.setDateCreated(new Date());

        when(paperRecordService.getPendingPaperRecordRequestByIdentifier(eq("123"))).thenReturn(request);

        FragmentActionResult result = controller.markPaperRecordRequestAsSent("123", paperRecordService, ui);

        verify(paperRecordService).markPaperRequestRequestAsSent(request);
        assertThat(result, instanceOf(SuccessResult.class));
        SuccessResult successResult = (SuccessResult) result;
        assertThat(successResult.getMessage(), containsString("123"));
        assertThat(successResult.getMessage(), containsString("Test location"));
    }

    @Test
    public void testControllerShouldReturnAssignedRequestsToPull() throws Exception {

        ArchivesRoomFragmentController controller = new ArchivesRoomFragmentController();

        UiUtils ui = new TestUiUtils();
        EmrProperties emrProperties = mock(EmrProperties.class);
        PaperRecordService paperRecordService = mock(PaperRecordService.class);

        // create a couple sample paper record request to return
        PatientIdentifierType patientIdentifierType = new PatientIdentifierType();

        Patient patient = new Patient();
        PersonName name = new PersonName();
        name.setFamilyName("Jones");
        name.setGivenName("Tom");
        patient.addName(name);

        PatientIdentifier patientIdentifier = new PatientIdentifier();
        patientIdentifier.setIdentifier("987");
        patientIdentifier.setIdentifierType(patientIdentifierType);
        patient.addIdentifier(patientIdentifier);

        Patient patient2 = new Patient();
        name = new PersonName();
        name.setFamilyName("Wallace");
        name.setGivenName("Mike");
        patient2.addName(name);

        patientIdentifier = new PatientIdentifier();
        patientIdentifier.setIdentifier("763");
        patientIdentifier.setIdentifierType(patientIdentifierType);
        patient2.addIdentifier(patientIdentifier);

        Location location = new Location();
        location.setName("Test location");

        Location location2 = new Location();
        location2.setName("Another location");

        Calendar calendar = Calendar.getInstance();
        calendar.set(2012, 2, 22, 11, 10);

        PaperRecordRequest request = new PaperRecordRequest();
        request.setId(1);
        request.setIdentifier("123");
        request.setRequestLocation(location);
        request.setDateCreated(calendar.getTime());
        request.setPatient(patient);

        PaperRecordRequest request2 = new PaperRecordRequest();
        request2.setId(2);
        request2.setIdentifier("ABC");
        request2.setRequestLocation(location2);
        calendar.set(2012,2,22,12, 11);
        request2.setDateCreated(calendar.getTime());
        request2.setPatient(patient2);

        List<PaperRecordRequest> requests = new ArrayList<PaperRecordRequest>();
        requests.add(request);
        requests.add(request2);

        when(paperRecordService.getAssignedPaperRecordRequestsToPull()).thenReturn(requests);
        when(emrProperties.getPrimaryIdentifierType()).thenReturn(patientIdentifierType);

        List<SimpleObject> results = controller.getAssignedRecordsToPull(paperRecordService, emrProperties, ui);

        assertThat(results.size(), is(2));

        SimpleObject result = results.get(0);
        assertThat((String) result.get("requestId"), is("1"));
        assertThat((String) result.get("requestLocation"), is("Test location"));
        assertThat((String) result.get("identifier"), is("123"));
        assertThat((String) result.get("patient"), is("Tom Jones"));
        assertThat((String) result.get("patientIdentifier"), is("987"));
        assertThat((String) result.get("dateCreated"), is("11:10"));

        SimpleObject result2 = results.get(1);
        assertThat((String) result2.get("requestId"), is("2"));
        assertThat((String) result2.get("requestLocation"), is("Another location"));
        assertThat((String) result2.get("identifier"), is("ABC"));
        assertThat((String) result2.get("patient"), is("Mike Wallace"));
        assertThat((String) result2.get("patientIdentifier"), is("763"));
        assertThat((String) result2.get("dateCreated"), is("12:11"));
    }
}
