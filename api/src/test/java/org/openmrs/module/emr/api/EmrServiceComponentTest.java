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
import org.openmrs.Location;
import org.openmrs.Patient;
import org.openmrs.api.context.Context;
import org.openmrs.module.emr.TestUtils;
import org.openmrs.test.BaseModuleContextSensitiveTest;

import java.util.List;

import static org.junit.Assert.assertEquals;

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

}
