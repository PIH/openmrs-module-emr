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
package org.openmrs.module.emr.api;

import org.junit.Before;
import org.junit.Test;
import org.openmrs.Encounter;
import org.openmrs.GlobalProperty;
import org.openmrs.Location;
import org.openmrs.LocationTag;
import org.openmrs.Patient;
import org.openmrs.api.AdministrationService;
import org.openmrs.api.LocationService;
import org.openmrs.api.context.Context;
import org.openmrs.module.emr.EmrConstants;
import org.openmrs.module.emr.TestUtils;
import org.openmrs.test.BaseModuleContextSensitiveTest;

import java.util.Date;
import java.util.List;

import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThat;

public class EmrServiceComponentTest extends BaseModuleContextSensitiveTest {
	
	EmrService service;
	
	Location where;

	@Before
	public void before() {
		service = Context.getService(EmrService.class);
		where = Context.getLocationService().getLocation(1);
	}
	
	@Test
	public void testFindPatientsWithActiveVisits() throws Exception {
		List<Patient> patients = service.findPatients("", where, null, null);
		assertEquals(1, patients.size());
		TestUtils.assertContainsElementWithProperty(patients, "patientId", 2);
	}
	
	@Test
	public void testFindPatientsByName() throws Exception {
		List<Patient> patients = service.findPatients("Test", null, null, null);
		assertEquals(4, patients.size());
		TestUtils.assertContainsElementWithProperty(patients, "patientId", 2);
		TestUtils.assertContainsElementWithProperty(patients, "patientId", 6);
		TestUtils.assertContainsElementWithProperty(patients, "patientId", 7);
		TestUtils.assertContainsElementWithProperty(patients, "patientId", 8);
	}
	
	@Test
	public void testFindPatientsByNameWithActiveVisits() throws Exception {
		List<Patient> patients = service.findPatients("Hora", where, null, null);
		assertEquals(1, patients.size());
		TestUtils.assertContainsElementWithProperty(patients, "patientId", 2);
	}

    @Test
    public void integrationTest_ADT_workflow() {
        LocationService locationService = Context.getLocationService();

        Date now = new Date();
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

        // configure GPs
        AdministrationService administrationService = Context.getAdministrationService();
        administrationService.saveGlobalProperty(new GlobalProperty(EmrConstants.GP_UNSPECIFIED_VISIT_TYPE, "c0c579b0-8e59-401d-8a4a-976a0b183519"));
        administrationService.saveGlobalProperty(new GlobalProperty(EmrConstants.GP_CHECK_IN_ENCOUNTER_TYPE, "61ae96f4-6afe-4351-b6f8-cd4fc383cce1"));

        // step 1: check in the patient (which should create a visit and an encounter)
        Encounter checkInEncounter = service.checkInPatient(patient, outpatientDepartment, now, null, null);

        assertThat(checkInEncounter.getPatient(), is(patient));
        assertThat(checkInEncounter.getEncounterDatetime(), is(now));
        assertThat(checkInEncounter.getVisit().getPatient(), is(patient));
        assertThat(checkInEncounter.getVisit().getStartDatetime(), is(now));

        // TODO once these are implemented, add Admission and Discharge to this test
    }

}
