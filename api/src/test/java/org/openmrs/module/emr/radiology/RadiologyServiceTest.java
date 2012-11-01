package org.openmrs.module.emr.radiology;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentMatcher;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;
import org.openmrs.*;
import org.openmrs.api.EncounterService;
import org.openmrs.api.context.Context;
import org.openmrs.module.emr.EmrContext;
import org.openmrs.module.emr.EmrProperties;
import org.openmrs.module.emr.adt.VisitSummary;
import org.openmrs.module.emr.domain.RadiologyRequisition;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import java.util.Date;
import java.util.Set;

import static org.hamcrest.CoreMatchers.*;
import static org.junit.Assert.assertThat;
import static org.junit.matchers.JUnitMatchers.hasItem;
import static org.mockito.Matchers.isA;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@RunWith(PowerMockRunner.class)
@PrepareForTest(Context.class)
public class RadiologyServiceTest {

    private RadiologyServiceImpl radiologyService;
    private EmrProperties emrProperties;
    private EncounterService encounterService;
    private EmrContext emrContext;
    private OrderType orderType;
    private Patient patient;
    private Concept study;
    private Date encounterDate;
    private String clinicalHistory;

    @Before
    public void setup() {
        PowerMockito.mockStatic(Context.class);
        User authenticatedUser = new User();
        PowerMockito.when(Context.getAuthenticatedUser()).thenReturn(authenticatedUser);

        emrProperties = mock(EmrProperties.class);
        encounterService = mock(EncounterService.class);
        emrContext = mock(EmrContext.class);

        radiologyService = new RadiologyServiceImpl();
        radiologyService.setEmrProperties(emrProperties);
        radiologyService.setEncounterService(encounterService);
    }

    @Test
    public void shouldPlaceARadiologyRequisitionWithOneStudyOnFixedMachine() {
        Visit currentVisit = new Visit();
        VisitSummary currentVisitSummary = new VisitSummary(currentVisit, null);
        Location currentLocation = new Location();
        EncounterType placeOrdersEncounterType = new EncounterType();
        EncounterRole clinicianEncounterRole = new EncounterRole();
        Provider provider = new Provider();
        patient = new Patient();
        encounterDate = new Date();
        study = new Concept();
        orderType = new OrderType();
        clinicalHistory = "Patient fell from a building";

        RadiologyRequisition radiologyRequisition = new RadiologyRequisition();
        radiologyRequisition.setPatient(patient);
        radiologyRequisition.setRequestedBy(provider);
        radiologyRequisition.setClinicalHistory(clinicalHistory);
        radiologyRequisition.setEncounterDatetime(encounterDate);
        radiologyRequisition.addStudy(study);
        radiologyRequisition.setUrgency(Order.Urgency.STAT);

        when(emrContext.getActiveVisitSummary()).thenReturn(currentVisitSummary);
        when(emrContext.getSessionLocation()).thenReturn(currentLocation);
        when(emrProperties.getPlaceOrdersEncounterType()).thenReturn(placeOrdersEncounterType);
        when(emrProperties.getClinicianEncounterRole()).thenReturn(clinicianEncounterRole);
        when(emrProperties.getTestOrderType()).thenReturn(orderType);
        when(encounterService.saveEncounter(isA(Encounter.class))).thenAnswer(new Answer<Object>() {
            @Override
            public Object answer(InvocationOnMock invocation) throws Throwable {
                Object[] args = invocation.getArguments();
                return args[0];
            }
        });

        Encounter encounter = radiologyService.placeRadiologyRequisition(emrContext, radiologyRequisition);

        Set<Provider> providersByRole = encounter.getProvidersByRole(clinicianEncounterRole);
        assertThat(encounter.getEncounterType(), is(placeOrdersEncounterType));
        assertThat(providersByRole.size(), is(1));
        assertThat(providersByRole.iterator().next(), is(provider));
        assertThat(encounter.getPatient(), is(patient));
        assertThat(encounter.getLocation(), is(currentLocation));
        assertThat(encounter.getEncounterDatetime(), notNullValue());
        assertThat(encounter.getVisit(), is(currentVisit));
        assertThat(encounter.getOrders().size(), is(1));
        assertThat(encounter.getOrders(), hasItem(new IsExpectedOrder()));
    }

    private class IsExpectedOrder extends ArgumentMatcher<Order> {
        @Override
        public boolean matches(Object o) {
            RadiologyOrder actual = (RadiologyOrder) o;

            assertThat(actual.getOrderType(), is(orderType));
            assertThat(actual.getPatient(), is(patient));
            assertThat(actual.getConcept(), is(study));
            assertThat(actual.getStartDate(), is(encounterDate));
            assertThat(actual.getUrgency(), is(Order.Urgency.STAT));
            assertThat(actual.getClinicalHistory(), is(clinicalHistory));
            assertThat(actual.getExamLocation(), is(nullValue()));

            return true;
        }
    }
}
