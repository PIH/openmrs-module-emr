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

import org.apache.commons.lang.time.DateUtils;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentMatcher;
import org.openmrs.Encounter;
import org.openmrs.EncounterRole;
import org.openmrs.EncounterType;
import org.openmrs.Location;
import org.openmrs.LocationTag;
import org.openmrs.Patient;
import org.openmrs.Person;
import org.openmrs.PersonName;
import org.openmrs.Provider;
import org.openmrs.User;
import org.openmrs.Visit;
import org.openmrs.VisitType;
import org.openmrs.api.EncounterService;
import org.openmrs.api.ProviderService;
import org.openmrs.api.VisitService;
import org.openmrs.api.context.Context;
import org.openmrs.module.emr.EmrConstants;
import org.openmrs.module.emr.EmrProperties;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNotSame;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertThat;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.argThat;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.openmrs.module.emr.TestUtils.isCollectionOfExactlyElementsWithProperties;
import static org.openmrs.module.emr.TestUtils.isJustNow;
import static org.powermock.api.mockito.PowerMockito.mockStatic;

@RunWith(PowerMockRunner.class)
@PrepareForTest(Context.class)
public class AdtServiceTest {

    private AdtService service;

    VisitService mockVisitService;
    EncounterService mockEncounterService;
    ProviderService mockProviderService;

    private Person personForCurrentUser;
    private Provider providerForCurrentUser;

    private EncounterRole checkInClerkEncounterRole;
    private EncounterType checkInEncounterType;
    private VisitType atFacilityVisitType;
    private LocationTag supportsVisits;
    private Location mirebalaisHospital;
    private Location outpatientDepartment;

    @Before
    public void setup() {
        personForCurrentUser = new Person();
        personForCurrentUser.addName(new PersonName("Current", "User", "Person"));

        User authenticatedUser = new User();
        authenticatedUser.setPerson(personForCurrentUser);
        mockStatic(Context.class);
        when(Context.getAuthenticatedUser()).thenReturn(authenticatedUser);

        providerForCurrentUser = new Provider();
        providerForCurrentUser.setPerson(personForCurrentUser);
        mockProviderService = mock(ProviderService.class);
        when(mockProviderService.getProvidersByPerson(personForCurrentUser)).thenReturn(Collections.singletonList(providerForCurrentUser));

        mockVisitService = mock(VisitService.class);
        mockEncounterService = mock(EncounterService.class);

        checkInClerkEncounterRole = new EncounterRole();
        checkInEncounterType = new EncounterType();
        atFacilityVisitType = new VisitType();

        supportsVisits = new LocationTag();
        supportsVisits.setName(EmrConstants.LOCATION_TAG_SUPPORTS_VISITS);

        outpatientDepartment = new Location();

        mirebalaisHospital = new Location();
        mirebalaisHospital.addTag(supportsVisits);
        mirebalaisHospital.addChildLocation(outpatientDepartment);

        EmrProperties emrProperties = mock(EmrProperties.class);
        when(emrProperties.getVisitExpireHours()).thenReturn(10);
        when(emrProperties.getCheckInEncounterType()).thenReturn(checkInEncounterType);
        when(emrProperties.getAtFacilityVisitType()).thenReturn(atFacilityVisitType);
        when(emrProperties.getCheckInClerkEncounterRole()).thenReturn(checkInClerkEncounterRole);
        when(emrProperties.getCheckInClerkEncounterRole()).thenReturn(checkInClerkEncounterRole);

        AdtServiceImpl service = new AdtServiceImpl();
        service.setVisitService(mockVisitService);
        service.setEncounterService(mockEncounterService);
        service.setProviderService(mockProviderService);
        service.setEmrProperties(emrProperties);
        this.service = service;
    }


    @Test
    public void testThatRecentVisitIsActive() throws Exception {
        Visit visit = new Visit();
        visit.setStartDatetime(new Date());

        Assert.assertThat(service.isActive(visit), is(true));
    }

    @Test
    public void testThatOldVisitIsNotActive() throws Exception {
        Visit visit = new Visit();
        visit.setStartDatetime(DateUtils.addDays(new Date(), -7));

        Assert.assertThat(service.isActive(visit), is(false));
    }

    @Test
    public void testThatOldVisitWithRecentEncounterIsActive() throws Exception {
        Encounter encounter = new Encounter();
        encounter.setEncounterDatetime(new Date());

        Visit visit = new Visit();
        visit.setStartDatetime(DateUtils.addDays(new Date(), -7));
        visit.addEncounter(encounter);

        Assert.assertThat(service.isActive(visit), is(true));
    }

    @Test
    public void testEnsureActiveVisitCreatesNewVisit() throws Exception {
        final Patient patient = new Patient();

        when(mockVisitService.getVisitsByPatient(patient)).thenReturn(new ArrayList<Visit>());

        service.ensureActiveVisit(patient, outpatientDepartment);

        verify(mockVisitService).saveVisit(argThat(new ArgumentMatcher<Visit>() {
            @Override
            public boolean matches(Object o) {
                Visit actual = (Visit) o;
                assertThat(actual.getVisitType(), is(atFacilityVisitType));
                assertThat(actual.getPatient(), is(patient));
                assertThat(actual.getLocation(), is(mirebalaisHospital));
                assertThat(actual.getStartDatetime(), isJustNow());
                return true;
            }
        }));
    }

    @Test
    public void testEnsureActiveVisitFindsRecentVisit() throws Exception {
        final Patient patient = new Patient();

        Visit recentVisit = new Visit();
        recentVisit.setLocation(mirebalaisHospital);
        recentVisit.setStartDatetime(DateUtils.addHours(new Date(), -1));

        when(mockVisitService.getVisitsByPatient(patient)).thenReturn(Collections.singletonList(recentVisit));

        assertThat(service.ensureActiveVisit(patient, outpatientDepartment), is(recentVisit));

        verify(mockVisitService, times(0)).saveVisit(any(Visit.class));
    }

    @Test
    public void testEnsureActiveVisitDoesNotFindOldVisit() throws Exception {
        final Patient patient = new Patient();

        final Visit oldVisit = new Visit();
        oldVisit.setLocation(mirebalaisHospital);
        oldVisit.setStartDatetime(DateUtils.addDays(new Date(), -7));

        when(mockVisitService.getVisitsByPatient(patient)).thenReturn(Collections.singletonList(oldVisit));

        final Visit created = service.ensureActiveVisit(patient, outpatientDepartment);
        assertNotNull(created);
        assertNotSame(oldVisit, created);

        // should be called once to save oldVisit (having stopped it)
        verify(mockVisitService).saveVisit(argThat(new ArgumentMatcher<Visit>() {
            @Override
            public boolean matches(Object o) {
                Visit actual = (Visit) o;
                if (actual == oldVisit) {
                    // no encounters, so closed at the moment it started
                    assertThat(actual.getStopDatetime(), is(oldVisit.getStartDatetime()));
                    return true;
                } else {
                    return false;
                }
            }
        }));

        // should be called once to create a new visit
        verify(mockVisitService).saveVisit(argThat(new ArgumentMatcher<Visit>() {
            @Override
            public boolean matches(Object o) {
                Visit actual = (Visit) o;
                if (actual != oldVisit) {
                    assertSame(created, actual);
                    assertThat(actual.getVisitType(), is(atFacilityVisitType));
                    assertThat(actual.getPatient(), is(patient));
                    assertThat(actual.getLocation(), is(mirebalaisHospital));
                    assertThat(actual.getStartDatetime(), isJustNow());
                    return true;
                } else {
                    return false;
                }
            }
        }));
    }

    @Test
    public void test_checkInPatient_forNewVisit() throws Exception {
        final Patient patient = new Patient();

        when(mockVisitService.getVisitsByPatient(patient)).thenReturn(new ArrayList<Visit>());

        service.checkInPatient(patient, outpatientDepartment, null, null, null);

        verify(mockVisitService).saveVisit(argThat(new ArgumentMatcher<Visit>() {
            @Override
            public boolean matches(Object o) {
                Visit actual = (Visit) o;
                assertThat(actual.getVisitType(), is(atFacilityVisitType));
                assertThat(actual.getPatient(), is(patient));
                assertThat(actual.getLocation(), is(mirebalaisHospital));
                assertThat(actual.getStartDatetime(), isJustNow());
                return true;
            }
        }));

        verify(mockEncounterService).saveEncounter(argThat(new ArgumentMatcher<Encounter>() {
            @Override
            public boolean matches(Object o) {
                Encounter actual = (Encounter) o;
                assertThat(actual.getEncounterType(), is(checkInEncounterType));
                assertThat(actual.getPatient(), is(patient));
                assertThat(actual.getLocation(), is(outpatientDepartment));
                assertThat(actual.getEncounterDatetime(), isJustNow());
                assertThat(actual.getProvidersByRoles().size(), is(1));
                assertThat(actual.getProvidersByRole(checkInClerkEncounterRole).iterator().next(), is(providerForCurrentUser));
                return true;
            }
        }));
    }

	@SuppressWarnings({ "unchecked" })
	@Test
	public void shouldGetAllVisitSummariesOfAllActiveVisit() throws Exception {
		final Visit visit1 = new Visit();
		visit1.setStartDatetime(DateUtils.addHours(new Date(), -2));
		visit1.setLocation(mirebalaisHospital);
		
		final Visit visit2 = new Visit();
		visit2.setStartDatetime(DateUtils.addHours(new Date(), -1));
		visit2.setLocation(outpatientDepartment);
		
		Visit visit3 = new Visit();
		visit3.setStartDatetime(DateUtils.addDays(new Date(), -10));
		visit3.setStopDatetime(DateUtils.addDays(new Date(), -8));
		visit3.setLocation(mirebalaisHospital);

        Set<Location> expectedLocations = new HashSet<Location>();
        expectedLocations.add(mirebalaisHospital);
        expectedLocations.add(outpatientDepartment);

		when(
		    mockVisitService.getVisits(any(Collection.class), any(Collection.class), eq(expectedLocations),
		        any(Collection.class), any(Date.class), any(Date.class), any(Date.class), any(Date.class), any(Map.class),
		        any(Boolean.class), any(Boolean.class))).thenReturn(Arrays.asList(visit1, visit2, visit3));
		
		List<VisitSummary> activeVisitSummaries = service.getActiveVisitSummaries(mirebalaisHospital);

        assertThat(activeVisitSummaries, isCollectionOfExactlyElementsWithProperties("visit", visit1, visit2));
	}

}
