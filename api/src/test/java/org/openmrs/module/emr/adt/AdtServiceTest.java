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
import org.openmrs.EncounterType;
import org.openmrs.Location;
import org.openmrs.LocationTag;
import org.openmrs.Patient;
import org.openmrs.User;
import org.openmrs.Visit;
import org.openmrs.VisitType;
import org.openmrs.api.EncounterService;
import org.openmrs.api.VisitService;
import org.openmrs.api.context.Context;
import org.openmrs.module.emr.EmrConstants;
import org.openmrs.module.emr.EmrProperties;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;

import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNotSame;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertThat;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.argThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.openmrs.module.emr.TestUtils.isJustNow;
import static org.powermock.api.mockito.PowerMockito.mockStatic;

@RunWith(PowerMockRunner.class)
@PrepareForTest(Context.class)
public class AdtServiceTest {

    private AdtService service;

    VisitService mockVisitService;
    EncounterService mockEncounterService;
    private EncounterType checkInEncounterType;
    private VisitType unspecifiedVisitType;
    private LocationTag supportsVisits;
    private Location mirebalaisHospital;
    private Location outpatientDepartment;

    @Before
    public void setup() {
        User authenticatedUser = new User();
        mockStatic(Context.class);
        when(Context.getAuthenticatedUser()).thenReturn(authenticatedUser);

        mockVisitService = mock(VisitService.class);
        mockEncounterService = mock(EncounterService.class);

        checkInEncounterType = new EncounterType();
        unspecifiedVisitType = new VisitType();

        supportsVisits = new LocationTag();
        supportsVisits.setName(EmrConstants.LOCATION_TAG_SUPPORTS_VISITS);

        mirebalaisHospital = new Location();
        mirebalaisHospital.addTag(supportsVisits);

        outpatientDepartment = new Location();
        outpatientDepartment.setParentLocation(mirebalaisHospital);

        EmrProperties emrProperties = mock(EmrProperties.class);
        when(emrProperties.getVisitExpireHours()).thenReturn(10);
        when(emrProperties.getCheckInEncounterType()).thenReturn(checkInEncounterType);
        when(emrProperties.getUnspecifiedVisitType()).thenReturn(unspecifiedVisitType);

        service = new AdtServiceStub(mockVisitService, mockEncounterService, emrProperties);
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
                assertThat(actual.getVisitType(), is(unspecifiedVisitType));
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

        // should be called once to save oldVisit (having stopped it) and once to create a new visit
        verify(mockVisitService, times(2)).saveVisit(argThat(new ArgumentMatcher<Visit>() {
            @Override
            public boolean matches(Object o) {
                Visit actual = (Visit) o;
                if (actual == oldVisit) {
                    assertNotNull(actual.getStopDatetime());
                    return true;
                } else {
                    assertSame(created, actual);
                    assertThat(actual.getVisitType(), is(unspecifiedVisitType));
                    assertThat(actual.getPatient(), is(patient));
                    assertThat(actual.getLocation(), is(mirebalaisHospital));
                    assertThat(actual.getStartDatetime(), isJustNow());
                    return true;
                }
            }
        }));
    }

    @Test
    public void test_checkInPatient_forNewVisit() throws Exception {
        final Patient patient = new Patient();

        when(mockVisitService.getVisitsByPatient(patient)).thenReturn(new ArrayList<Visit>());

        service.checkInPatient(patient, outpatientDepartment, null, null);

        verify(mockVisitService).saveVisit(argThat(new ArgumentMatcher<Visit>() {
            @Override
            public boolean matches(Object o) {
                Visit actual = (Visit) o;
                assertThat(actual.getVisitType(), is(unspecifiedVisitType));
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
                return true;
            }
        }));
    }

    private class AdtServiceStub extends AdtServiceImpl {
        public AdtServiceStub(VisitService mockVisitService, EncounterService mockEncounterService, EmrProperties emrProperties) {
            setVisitService(mockVisitService);
            setEncounterService(mockEncounterService);
            setEmrProperties(emrProperties);
        }
    }

}