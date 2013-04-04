package org.openmrs.module.emr.radiology;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentMatcher;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;
import org.openmrs.Concept;
import org.openmrs.Encounter;
import org.openmrs.EncounterRole;
import org.openmrs.EncounterType;
import org.openmrs.Location;
import org.openmrs.Order;
import org.openmrs.OrderType;
import org.openmrs.Patient;
import org.openmrs.Provider;
import org.openmrs.User;
import org.openmrs.Visit;
import org.openmrs.api.EncounterService;
import org.openmrs.api.context.Context;
import org.openmrs.module.emr.EmrContext;
import org.openmrs.module.emr.EmrProperties;
import org.openmrs.module.emr.TestUtils;
import org.openmrs.module.emr.adt.VisitSummary;
import org.openmrs.module.emr.order.EmrOrderService;
import org.openmrs.module.emr.radiology.db.RadiologyOrderDAO;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import java.util.Set;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.CoreMatchers.nullValue;
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
    private RadiologyProperties radiologyProperties;
    private EncounterService encounterService;
    private RadiologyOrderDAO radiologyOrderDAO;
    private EmrOrderService emrOrderService;
    private EmrContext emrContext;
    private OrderType orderType;
    private Patient patient;
    private String clinicalHistory;
    private EncounterRole clinicianEncounterRole;
    private EncounterType placeOrdersEncounterType;
    private Visit currentVisit;
    private Provider provider;
    private Location currentLocation;

    @Before
    public void setup() {
        PowerMockito.mockStatic(Context.class);
        User authenticatedUser = new User();
        PowerMockito.when(Context.getAuthenticatedUser()).thenReturn(authenticatedUser);

        patient = new Patient();
        orderType = new OrderType();
        clinicalHistory = "Patient fell from a building";
        provider = new Provider();
        currentLocation = new Location();

        currentVisit = new Visit();
        placeOrdersEncounterType = new EncounterType();
        clinicianEncounterRole = new EncounterRole();

        prepareMocks();

        radiologyService = new RadiologyServiceImpl();
        radiologyService.setEmrProperties(emrProperties);
        radiologyService.setRadiologyProperties(radiologyProperties);
        radiologyService.setEncounterService(encounterService);
        radiologyService.setEmrOrderService(emrOrderService);
        radiologyService.setRadiologyOrderDAO(radiologyOrderDAO);
    }

    private void prepareMocks() {
        emrProperties = mock(EmrProperties.class);
        radiologyProperties = mock(RadiologyProperties.class);
        encounterService = mock(EncounterService.class);
        emrContext = mock(EmrContext.class);
        emrOrderService = mock(EmrOrderService.class);
        radiologyOrderDAO = mock(RadiologyOrderDAO.class);

        VisitSummary currentVisitSummary = new VisitSummary(currentVisit, null);
        when(emrContext.getActiveVisitSummary()).thenReturn(currentVisitSummary);
        when(radiologyProperties.getRadiologyOrderEncounterType()).thenReturn(placeOrdersEncounterType);
        when(emrProperties.getOrderingProviderEncounterRole()).thenReturn(clinicianEncounterRole);
        when(emrContext.getSessionLocation()).thenReturn(currentLocation);
        when(radiologyProperties.getRadiologyTestOrderType()).thenReturn(orderType);
        when(encounterService.saveEncounter(isA(Encounter.class))).thenAnswer(new Answer<Object>() {
            @Override
            public Object answer(InvocationOnMock invocation) throws Throwable {
                Object[] args = invocation.getArguments();
                return args[0];
            }
        });
    }

    @Test
    public void shouldPlaceARadiologyRequisitionWithOneStudyOnFixedMachine() {
        Concept study = new Concept();
        RadiologyRequisition radiologyRequisition = new RadiologyRequisition();
        radiologyRequisition.setPatient(patient);
        radiologyRequisition.setRequestedBy(provider);
        radiologyRequisition.setClinicalHistory(clinicalHistory);
        radiologyRequisition.addStudy(study);
        radiologyRequisition.setUrgency(Order.Urgency.STAT);

        Encounter encounter = radiologyService.placeRadiologyRequisition(emrContext, radiologyRequisition);

        assertThat(encounter, is(new IsExpectedEncounter(null, study)));
    }

    @Test
    public void shouldPlaceARadiologyRequisitionWithTwoStudiesOnPortableMachine() {
        Location examLocation = new Location();
        Concept study = new Concept();
        Concept secondStudy = new Concept();

        RadiologyRequisition radiologyRequisition = new RadiologyRequisition();
        radiologyRequisition.setPatient(patient);
        radiologyRequisition.setRequestedBy(provider);
        radiologyRequisition.setClinicalHistory(clinicalHistory);
        radiologyRequisition.addStudy(study);
        radiologyRequisition.addStudy(secondStudy);
        radiologyRequisition.setUrgency(Order.Urgency.STAT);
        radiologyRequisition.setExamLocation(examLocation);

        Encounter encounter = radiologyService.placeRadiologyRequisition(emrContext, radiologyRequisition);

        assertThat(encounter, new IsExpectedEncounter(examLocation, study, secondStudy));
    }

    @Test
    public void shouldPlaceOrderEvenIfThereIsNoVisit() {
        RadiologyRequisition radiologyRequisition = new RadiologyRequisition();
        radiologyRequisition.setPatient(patient);

        when(emrContext.getActiveVisitSummary()).thenReturn(null);

        Encounter encounter = radiologyService.placeRadiologyRequisition(emrContext, radiologyRequisition);

        assertThat(encounter.getVisit(), is(nullValue()));
    }

    private class IsExpectedOrder extends ArgumentMatcher<Order> {
        private Location expectedLocation;
        private Concept expectedStudy;

        public IsExpectedOrder(Location expectedLocation, Concept expectedStudy) {
            this.expectedLocation = expectedLocation;
            this.expectedStudy = expectedStudy;
        }

        @Override
        public boolean matches(Object o) {
            RadiologyOrder actual = (RadiologyOrder) o;

            try {
                assertThat(actual.getOrderType(), is(orderType));
                assertThat(actual.getPatient(), is(patient));
                assertThat(actual.getConcept(), is(expectedStudy));
                assertThat(actual.getStartDate(), TestUtils.isJustNow());
                assertThat(actual.getUrgency(), is(Order.Urgency.STAT));
                assertThat(actual.getClinicalHistory(), is(clinicalHistory));
                assertThat(actual.getExamLocation(), is(expectedLocation));
                return true;
            } catch (AssertionError e) {
                return false;
            }
        }
    }

    private class IsExpectedEncounter extends ArgumentMatcher<Encounter> {
        private Location expectedLocation;
        private Concept[] expectedStudies;

        public IsExpectedEncounter(Location expectedLocation, Concept... expectedStudies) {
            this.expectedLocation = expectedLocation;
            this.expectedStudies = expectedStudies;
        }

        @Override
        public boolean matches(Object o) {
            Encounter encounter = (Encounter) o;

            Set<Provider> providersByRole = encounter.getProvidersByRole(clinicianEncounterRole);
            assertThat(encounter.getEncounterType(), is(placeOrdersEncounterType));
            assertThat(providersByRole.size(), is(1));
            assertThat(providersByRole.iterator().next(), is(provider));
            assertThat(encounter.getPatient(), is(patient));
            assertThat(encounter.getLocation(), is(currentLocation));
            assertThat(encounter.getEncounterDatetime(), notNullValue());
            assertThat(encounter.getVisit(), is(currentVisit));
            assertThat(encounter.getOrders().size(), is(expectedStudies.length));

            for (Concept expectedStudy : expectedStudies) {
                assertThat(encounter.getOrders(), hasItem(new IsExpectedOrder(expectedLocation, expectedStudy)));
            }

            return true;
        }
    }
}
