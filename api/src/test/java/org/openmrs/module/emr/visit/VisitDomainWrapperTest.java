package org.openmrs.module.emr.visit;


import org.apache.commons.lang.time.DateUtils;
import org.hamcrest.core.Is;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.openmrs.Encounter;
import org.openmrs.EncounterType;
import org.openmrs.Visit;
import org.openmrs.module.emrapi.EmrApiProperties;
import org.openmrs.module.emrapi.visit.VisitDomainWrapper;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import java.util.Calendar;
import java.util.Date;
import java.util.LinkedHashSet;

import static java.util.Calendar.DAY_OF_MONTH;
import static java.util.Calendar.HOUR;
import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.powermock.api.mockito.PowerMockito.mockStatic;

@PrepareForTest(Calendar.class)
@RunWith(PowerMockRunner.class)
public class VisitDomainWrapperTest {

    private VisitDomainWrapper visitDomainWrapper;
    private Visit visit;

    @Before
    public void setUp(){
        visit = mock(Visit.class);
        visitDomainWrapper = new VisitDomainWrapper(visit);
    }

    // this test was merged in when VisitSummary was merged into VisitDomainWrapper
    @Test
    public void test() throws Exception {
        EncounterType checkInEncounterType = new EncounterType();

        EmrApiProperties props = Mockito.mock(EmrApiProperties.class);
        Mockito.when(props.getCheckInEncounterType()).thenReturn(checkInEncounterType);

        Encounter checkIn = new Encounter();
        checkIn.setEncounterDatetime(DateUtils.addHours(new Date(), -2));
        checkIn.setEncounterType(checkInEncounterType);
        Encounter vitals = new Encounter();
        vitals.setEncounterDatetime(DateUtils.addHours(new Date(), -1));
        Encounter consult = new Encounter();
        consult.setEncounterDatetime(new Date());

        // per the hbm.xml file, visit.encounters are sorted by encounterDatetime desc
        Visit visit = new Visit();
        visit.setStartDatetime(checkIn.getEncounterDatetime());
        visit.setEncounters(new LinkedHashSet<Encounter>(3));
        visit.addEncounter(consult);
        visit.addEncounter(vitals);
        visit.addEncounter(checkIn);

        VisitDomainWrapper wrapper = new VisitDomainWrapper(visit, props);
        Assert.assertThat(wrapper.getCheckInEncounter(), Is.is(checkIn));
        Assert.assertThat(wrapper.getLastEncounter(), Is.is(consult));
    }

    @Test
    public void shouldReturnDifferenceInDaysBetweenCurrentDateAndStartDate(){
        Calendar startDate = Calendar.getInstance();
        startDate.add(DAY_OF_MONTH, -5);

        when(visit.getStartDatetime()).thenReturn(startDate.getTime());

        int days = visitDomainWrapper.getDifferenceInDaysBetweenCurrentDateAndStartDate();

        assertThat(days, is(5));

    }
    @Test
    public void shouldReturnDifferenceInDaysBetweenCurrentDateAndStartDateWhenTimeIsDifferent(){

        Calendar today = Calendar.getInstance();
        today.set(HOUR, 7);

        Calendar startDate = Calendar.getInstance();
        startDate.add(DAY_OF_MONTH, -5);
        startDate.set(HOUR, 9);

        mockStatic(Calendar.class);
        when(Calendar.getInstance()).thenReturn(today);

        when(visit.getStartDatetime()).thenReturn(startDate.getTime());

        int days = visitDomainWrapper.getDifferenceInDaysBetweenCurrentDateAndStartDate();

        assertThat(days, is(5));

    }
}
