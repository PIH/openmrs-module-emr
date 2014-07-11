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

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.openmrs.Location;
import org.openmrs.LocationTag;
import org.openmrs.Patient;
import org.openmrs.Privilege;
import org.openmrs.Role;
import org.openmrs.api.AdministrationService;
import org.openmrs.api.ConceptService;
import org.openmrs.api.LocationService;
import org.openmrs.api.UserService;
import org.openmrs.api.context.Context;
import org.openmrs.module.emr.EmrProperties;
import org.openmrs.module.emrapi.EmrApiConstants;
import org.openmrs.test.BaseModuleContextSensitiveTest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.StringUtils;

import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.openmrs.module.emr.TestUtils.assertContainsElementWithProperty;

public class EmrServiceComponentTest extends BaseModuleContextSensitiveTest {
	
	protected final Log log = LogFactory.getLog(getClass());
	
	EmrService service;

    AdministrationService administrationService;
	
	Location where;

    @Autowired
    EmrProperties emrProperties;

    @Autowired
    ConceptService conceptService;

	@Before
	public void before() throws Exception{
		executeDataSet("privilegeTestDataset.xml");
		service = Context.getService(EmrService.class);
        administrationService = Context.getAdministrationService();

        LocationService locationService = Context.getLocationService();

        LocationTag supportsVisits = new LocationTag(EmrApiConstants.LOCATION_TAG_SUPPORTS_VISITS, "no description");
        locationService.saveLocationTag(supportsVisits);

        where = locationService.getLocation(1);
        where.addTag(supportsVisits);
        locationService.saveLocation(where);
	}
	
	@Test
	public void testFindPatientsWithActiveVisits() throws Exception {
		List<Patient> patients = service.findPatients("", where, null, null);
		assertEquals(1, patients.size());
		assertContainsElementWithProperty(patients, "patientId", 2);
	}

    @Test
    public void testFindPatientsByPrimaryIdentifier() throws Exception {
        administrationService.setGlobalProperty(EmrApiConstants.PRIMARY_IDENTIFIER_TYPE, "1");
        List<Patient> patients = service.findPatients("6TS-4", null, null, null);
        assertEquals(1, patients.size());
        assertContainsElementWithProperty(patients, "patientId", 7);
    }

	@Test
	public void testFindPatientsByName() throws Exception {
		List<Patient> patients = service.findPatients("Test", null, null, null);
		assertEquals(4, patients.size());
		assertContainsElementWithProperty(patients, "patientId", 2);
		assertContainsElementWithProperty(patients, "patientId", 6);
		assertContainsElementWithProperty(patients, "patientId", 7);
		assertContainsElementWithProperty(patients, "patientId", 8);
	}
	
	@Test
	public void testFindPatientsByNameWithActiveVisits() throws Exception {
		List<Patient> patients = service.findPatients("Hora", where, null, null);
		assertEquals(1, patients.size());
		assertContainsElementWithProperty(patients, "patientId", 2);
	}
	
	@Test
	public void testFindAPIPrivileges() throws Exception{
		UserService userService = Context.getUserService();
		List<Role> roles= userService.getAllRoles();
		if(roles!=null && roles.size()>0){
			for(Role role : roles){
				log.debug("roleName:" +  role.getName());
			}
		}
		Role fullRole = userService.getRole(EmrApiConstants.PRIVILEGE_LEVEL_FULL_ROLE);
		if(fullRole==null){
			fullRole = new Role();
			fullRole.setName(EmrApiConstants.PRIVILEGE_LEVEL_FULL_ROLE);
			fullRole.setRole(EmrApiConstants.PRIVILEGE_LEVEL_FULL_ROLE);
			fullRole.setDescription(EmrApiConstants.PRIVILEGE_LEVEL_FULL_ROLE);
			userService.saveRole(fullRole);
		}
		
		List<Privilege> allPrivileges = userService.getAllPrivileges();
		if(allPrivileges!=null && allPrivileges.size()>0){
			for(Privilege privilege : allPrivileges){
				log.debug("" + privilege.getName());
				String privilegeName = privilege.getName();
				if(!fullRole.hasPrivilege(privilegeName)){
					if(!StringUtils.startsWithIgnoreCase(privilegeName, EmrApiConstants.PRIVILEGE_PREFIX_APP) &&
							!StringUtils.startsWithIgnoreCase(privilegeName, EmrApiConstants.PRIVILEGE_PREFIX_TASK)){
						fullRole.addPrivilege(privilege);
					}
				}
			}
		}
		userService.saveRole(fullRole);
		
		assertEquals(fullRole.getName(), EmrApiConstants.PRIVILEGE_LEVEL_FULL_ROLE);
	}

}
