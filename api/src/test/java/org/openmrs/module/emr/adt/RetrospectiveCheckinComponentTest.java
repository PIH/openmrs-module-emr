package org.openmrs.module.emr.adt;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
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
    @Autowired
    private VisitService visitService;

    private Patient patient;
    private Location location;
    private Provider clerk;
    private Obs paymentReason;
    private Obs paymentAmount;
    private Obs paymentReceipt;
    private Date checkinDate;

    @Before
    public void setupDatabase() throws Exception {
        executeDataSet("retrospectiveCheckinComponentTestDataset.xml");

        patient = patientService.getPatient(2);
        location = locationService.getLocation(2);
        clerk = providerService.getProvider(1);
        paymentReason = createPaymentReasonObservation();
        paymentAmount = createPaymentAmountObservation(50);
        paymentReceipt = createPaymentReceiptObservation("123456");
    }

    @Test
    public void createRetrospectiveCheckinWithinNewVisit() {
        checkinDate = generateDateFor(2011, 07, 25, 10, 39);

        Encounter checkinEncounter = adtService.createCheckinInRetrospective(patient, location, clerk, paymentReason, paymentAmount, paymentReceipt, checkinDate);
        Visit visit = checkinEncounter.getVisit();

        assertCheckinEncounter(checkinEncounter);
        assertThat(visit.getStartDatetime(), is(checkinDate));
    }

    @Test
    public void createRetrospectiveCheckinWithinVisitAfter() {
        Visit visitAfter = new Visit();
        visitAfter.setPatient(patient);
        visitAfter.setStartDatetime(generateDateFor(2011, 07, 25, 14, 25));
        visitAfter.setStopDatetime(generateDateFor(2011, 07, 25, 18, 00));
        visitAfter.setVisitType(emrProperties.getAtFacilityVisitType());
        visitService.saveVisit(visitAfter);

        checkinDate = generateDateFor(2011, 07, 25, 10, 00);

        Encounter checkinEncounter = adtService.createCheckinInRetrospective(patient, location, clerk, paymentReason, paymentAmount, paymentReceipt, checkinDate);
        Visit visit = checkinEncounter.getVisit();

        assertCheckinEncounter(checkinEncounter);
        assertThat(visit, is(visitAfter));
    }

    @Test
    public void createRetrospectiveCheckinWithinVisitBefore() {
        Visit visitBefore = new Visit();
        visitBefore.setPatient(patient);
        visitBefore.setStartDatetime(generateDateFor(2011, 07, 25, 8, 00));
        visitBefore.setStopDatetime(generateDateFor(2011, 07, 25, 10, 00));
        visitBefore.setVisitType(emrProperties.getAtFacilityVisitType());
        visitService.saveVisit(visitBefore);

        checkinDate = generateDateFor(2011, 07, 25, 14, 00);

        Encounter checkinEncounter = adtService.createCheckinInRetrospective(patient, location, clerk, paymentReason, paymentAmount, paymentReceipt, checkinDate);
        Visit visit = checkinEncounter.getVisit();

        assertCheckinEncounter(checkinEncounter);
        assertThat(visit, is(visitBefore));
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

    private Obs createPaymentReceiptObservation(String receiptNumber) {
        Obs pr = new Obs();
        pr.setConcept(conceptService.getConceptByUuid("20438dc7-c5b4-4d9c-8480-e888f4795123"));
        pr.setValueText(receiptNumber);

        return pr;
    }

    private void assertCheckinEncounter(Encounter checkinEncounter) {
        assertThat(checkinEncounter.getPatient(), is(patient));
        assertThat(checkinEncounter.getLocation(), is(location));
        assertThat(checkinEncounter.getProvidersByRole(emrProperties.getCheckInClerkEncounterRole()), containsInAnyOrder(clerk));
        assertThat(checkinEncounter.getObs(), containsInAnyOrder(paymentReason, paymentAmount, paymentReceipt));
        assertThat(checkinEncounter.getEncounterDatetime(), is(checkinDate));
    }
}
