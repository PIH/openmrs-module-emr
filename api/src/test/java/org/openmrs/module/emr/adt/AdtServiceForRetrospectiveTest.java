package org.openmrs.module.emr.adt;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;
import org.openmrs.*;
import org.openmrs.api.EncounterService;
import org.openmrs.api.context.Context;
import org.openmrs.module.emr.EmrConstants;
import org.openmrs.module.emr.EmrProperties;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import java.util.Calendar;
import java.util.Date;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.containsInAnyOrder;
import static org.hamcrest.Matchers.notNullValue;
import static org.mockito.Matchers.isA;
import static org.mockito.Matchers.notNull;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@PrepareForTest(Context.class)
@RunWith(PowerMockRunner.class)
public class AdtServiceForRetrospectiveTest {

    private AdtServiceImpl service;
    private EncounterService encounterService;
    private EmrProperties emrProperties;

    private Patient patient;
    private Location location;
    private Provider clerk;
    private Obs paymentReason;
    private Obs paymentAmount;
    private Date checkinDate;

    @Before
    public void setUp() {
        PowerMockito.mockStatic(Context.class);
        service = new AdtServiceImpl();
        encounterService = mock(EncounterService.class);
        emrProperties = mock(EmrProperties.class);

        service.setEncounterService(encounterService);
        service.setEmrProperties(emrProperties);

        patient = new Patient(); location = new Location();
        clerk = new Provider();
        paymentReason = new Obs();
        paymentAmount = new Obs();
    }

    @Test
    public void shouldAddCheckinToVisitActiveLessThanTenHoursBeforeTheCheckinTime() {
        Calendar calendar = Calendar.getInstance();
        calendar.set(2012, Calendar.JANUARY, 17, 13, 27);
        checkinDate = new Date();
        Encounter checkinEncounter = service.createCheckinInRetrospective(patient, location, clerk, paymentReason, paymentAmount, checkinDate);
        assertThat(checkinEncounter, is(notNull()));
    }

    @Test
    public void shouldAddCheckinToVisitActiveLessThanTenHoursAfterTheCheckinTime() {
        assert(false);
    }

    @Test
    public void shouldAddCheckinToNewVisit() {
        when(encounterService.saveEncounter(isA(Encounter.class))).thenAnswer(new Answer<Object>() {
            @Override
            public Object answer(InvocationOnMock invocation) throws Throwable {
                Object[] args = invocation.getArguments();
                return args[0];
            }
        });
        VisitType visitType = new VisitType();
        when(emrProperties.getAtFacilityVisitType()).thenReturn(visitType);
        EncounterRole encounterRole = new EncounterRole();
        when(emrProperties.getCheckInClerkEncounterRole()).thenReturn(encounterRole);
        when(Context.getAuthenticatedUser()).thenReturn(null);

        Calendar calendar = Calendar.getInstance();
        calendar.set(2012, Calendar.JANUARY, 17, 13, 27);
        checkinDate = calendar.getTime();
        location = new Location();
        LocationTag tag = new LocationTag();
        tag.setName(EmrConstants.LOCATION_TAG_SUPPORTS_VISITS);
        location.addTag(tag);


        Encounter checkinEncounter = service.createCheckinInRetrospective(patient, location, clerk, paymentReason, paymentAmount, checkinDate);

        assertThat(checkinEncounter.getPatient(), is(patient));
        assertThat(checkinEncounter.getLocation(), is(location));
        assertThat(checkinEncounter.getProvidersByRole(encounterRole), containsInAnyOrder(clerk));
        assertThat(checkinEncounter.getObs(), containsInAnyOrder(paymentReason, paymentAmount));
        assertThat(checkinEncounter.getEncounterDatetime(), is(checkinDate));

        assertThat(checkinEncounter.getVisit(), is(notNullValue()));
        assertThat(checkinEncounter.getVisit().getStartDatetime(), is(checkinDate));
    }
}
