package org.openmrs.module.emr.adt;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.openmrs.*;
import org.openmrs.api.*;
import org.openmrs.module.emr.EmrProperties;
import org.openmrs.test.BaseModuleContextSensitiveTest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.util.Calendar;
import java.util.Date;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.containsInAnyOrder;

@RunWith(SpringJUnit4ClassRunner.class)
public class RetrospectiveCheckinComponentTest extends BaseModuleContextSensitiveTest {

    @Autowired
    private AdtService adtService;
    @Autowired
    private EmrProperties emrProperties;

    @Autowired
    private PatientService patientService;
    @Autowired
    private LocationService locationService;
    @Autowired
    private ProviderService providerService;
    @Autowired
    private ConceptService conceptService;

    @Before
    public void setupDatabase() throws Exception {
        executeDataSet("retrospectiveCheckinComponentTestDataset.xml");
    }

    @Test
    public void createRetrospectiveCheckinWithinNewVisit() {
        Patient patient = patientService.getPatient(2); // from standardTestDataset
        Location location = locationService.getLocation(2); // from standardTestDataset (but configured in retrospectiveCheckinComponentTestDataset for checkin)
        Provider clerk = providerService.getProvider(1); // from standardTestDataset
        Obs paymentReason = createPaymentReasonObservation();
        Obs paymentAmount = createPaymentAmountObservation(50);
        Date checkinDate = generateDateFor(2011, 07, 25, 10, 39);

        Encounter checkinEncounter = adtService.createCheckinInRetrospective(patient, location, clerk, paymentReason, paymentAmount, checkinDate);
        Visit visit = checkinEncounter.getVisit();

        assertThat(checkinEncounter.getPatient(), is(patient));
        assertThat(checkinEncounter.getLocation(), is(location));
        assertThat(checkinEncounter.getProvidersByRole(emrProperties.getCheckInClerkEncounterRole()), containsInAnyOrder(clerk));
        assertThat(checkinEncounter.getObs(), containsInAnyOrder(paymentReason, paymentAmount));
        assertThat(checkinEncounter.getEncounterDatetime(), is(checkinDate));
        assertThat(visit.getStartDatetime(), is(checkinDate));
    }

    private Obs createPaymentAmountObservation(double amount) {
        Obs paymentAmount = new Obs();
        paymentAmount.setConcept(conceptService.getConceptByUuid("5d1bc5de-6a35-4195-8631-7322941fe528"));
        paymentAmount.setValueNumeric(amount);
        return paymentAmount;
    }

    private Obs createPaymentReasonObservation() {
        Obs paymentReason = new Obs();
        paymentReason.setConcept(conceptService.getConceptByUuid("36ba7721-fae0-4da4-aef2-7e476cc04bdf"));
        paymentReason.setValueCoded(conceptService.getConcept(16));
        return paymentReason;
    }

    private Date generateDateFor(int year, int month, int day, int hour, int minutes) {
        Calendar calendar = Calendar.getInstance();
        calendar.set(year, month, day, hour, minutes);
        return calendar.getTime();
    }
}
