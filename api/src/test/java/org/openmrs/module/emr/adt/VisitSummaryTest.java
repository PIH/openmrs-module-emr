package org.openmrs.module.emr.adt;

import java.util.Date;
import java.util.LinkedHashSet;

import org.apache.commons.lang.time.DateUtils;
import org.hamcrest.core.Is;
import org.junit.Assert;
import org.junit.Test;
import org.mockito.Mockito;
import org.openmrs.Encounter;
import org.openmrs.EncounterType;
import org.openmrs.Visit;
import org.openmrs.module.emr.EmrProperties;

public class VisitSummaryTest {
	
	@Test
	public void test() throws Exception {
		EncounterType checkInEncounterType = new EncounterType();
		
		EmrProperties props = Mockito.mock(EmrProperties.class);
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
		
		VisitSummary summary = new VisitSummary(visit, props);
		Assert.assertThat(summary.getCheckInEncounter(), Is.is(checkIn));
		Assert.assertThat(summary.getLastEncounter(), Is.is(consult));
	}
}
