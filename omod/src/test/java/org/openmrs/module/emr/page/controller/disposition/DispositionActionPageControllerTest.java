package org.openmrs.module.emr.page.controller.disposition;

import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentMatcher;
import org.openmrs.Concept;
import org.openmrs.Encounter;
import org.openmrs.Obs;
import org.openmrs.Patient;
import org.openmrs.Visit;
import org.openmrs.module.emrapi.disposition.Disposition;
import org.openmrs.module.emrapi.disposition.DispositionDescriptor;
import org.openmrs.module.emrapi.disposition.DispositionFactory;
import org.openmrs.module.emrapi.disposition.actions.DispositionAction;
import org.openmrs.module.emrapi.encounter.EncounterDomainWrapper;
import org.openmrs.ui.framework.UiUtils;
import org.springframework.context.ApplicationContext;

import java.io.IOException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import static org.mockito.Matchers.anyMap;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.mockito.Mockito.argThat;

public class DispositionActionPageControllerTest {

    private DispositionActionPageController controller = new DispositionActionPageController();

    private DispositionFactory dispositionFactory;

    private ApplicationContext applicationContext;

    private UiUtils uiUtils;

    private Concept dispositionObsGroupConcept = new Concept();

    private Concept dispositionObsConcept = new Concept();

    private Concept nonDispositionObsGroupConcept = new Concept();

    private Concept nonDispositionObsConcept = new Concept();

    private Concept death = new Concept();

    private Patient patient = new Patient(1);

    private Visit visit = new Visit(1);

    private Encounter encounter = new Encounter(1);

    private Obs deathDispositionObsGroup = new Obs();

    private Obs deathDispositionObs = new Obs();

    private Obs nonDispositionObsGroup = new Obs();

    private Obs nonDispositionObs = new Obs();

    private Disposition deathDisposition = new Disposition();

    private DispositionAction dischargeIfAdmittedDispositionAction;

    private DispositionAction markPatientDeadDispositionAction;

    private Map<String, Object> expectedParams = new HashMap<String, Object>();

    @Before
    public void setup() throws IOException {
        encounter.setPatient(patient);
        encounter.setVisit(visit);

        deathDispositionObsGroup.setConcept(dispositionObsGroupConcept);
        deathDispositionObs.setConcept(dispositionObsConcept);
        deathDispositionObs.setValueCoded(death);
        deathDispositionObsGroup.addGroupMember(deathDispositionObs);

        nonDispositionObsGroup.setConcept(nonDispositionObsGroupConcept);
        nonDispositionObs.setConcept(nonDispositionObsConcept);
        nonDispositionObsGroup.addGroupMember(nonDispositionObs);

        deathDisposition.setActions(Arrays.asList("dischargeIfAdmittedDispositionAction", "markPatientDeadDispositionAction"));

        uiUtils = mock(UiUtils.class);
        dispositionFactory = mock(DispositionFactory.class);
        applicationContext = mock(ApplicationContext.class);
        dischargeIfAdmittedDispositionAction = mock(DispositionAction.class);
        markPatientDeadDispositionAction = mock(DispositionAction.class);

        when(applicationContext.getBean("dischargeIfAdmittedDispositionAction", DispositionAction.class)).thenReturn(dischargeIfAdmittedDispositionAction);
        when(applicationContext.getBean("markPatientDeadDispositionAction", DispositionAction.class)).thenReturn(markPatientDeadDispositionAction);

        DispositionDescriptor dispositionDescriptor = new DispositionDescriptor();
        dispositionDescriptor.setDispositionConcept(dispositionObsConcept);
        dispositionDescriptor.setDispositionSetConcept(dispositionObsGroupConcept);

        when(dispositionFactory.getDispositionDescriptor()).thenReturn(dispositionDescriptor);
        when(dispositionFactory.getDispositionFromObsGroup(deathDispositionObsGroup)).thenReturn(deathDisposition);

        expectedParams.put("patientId", "1");
        expectedParams.put("visitId", "2");
    }

    // TODO: handle edit case?

    @Test
    public void shouldAllActionsAssociatedWithDeathDisposition() throws IOException {
        encounter.addObs(deathDispositionObsGroup);
        controller.controller(encounter, "1", "2", dispositionFactory, applicationContext, uiUtils);
        verify(dischargeIfAdmittedDispositionAction).action(argThat(new IsExpectedEncounterDomainWrapper(encounter)), eq(deathDispositionObsGroup), anyMap());
        verify(markPatientDeadDispositionAction).action(argThat(new IsExpectedEncounterDomainWrapper(encounter)), eq(deathDispositionObsGroup), anyMap());
        verify(uiUtils).pageLinkWithoutContextPath(eq("coreapps"), eq("patientdashboard/patientDashboard"), eq(expectedParams));
    }


    @Test
    public void shouldRedirectToDashboardIfNoDispositionSpecified() throws IOException {
        encounter.addObs(nonDispositionObsGroup);
        controller.controller(encounter, "1", "2", dispositionFactory, applicationContext, uiUtils);
        verify(dischargeIfAdmittedDispositionAction, never()).action(argThat(new IsExpectedEncounterDomainWrapper(encounter)), eq(deathDispositionObsGroup), anyMap());
        verify(markPatientDeadDispositionAction,never()).action(argThat(new IsExpectedEncounterDomainWrapper(encounter)), eq(deathDispositionObsGroup), anyMap());
        verify(uiUtils).pageLinkWithoutContextPath(eq("coreapps"), eq("patientdashboard/patientDashboard"), eq(expectedParams));
    }


    @Test
    public void shouldRedirectToDashboardIfNoEncounterSpecified() throws IOException {
        controller.controller(null, "1", "2", dispositionFactory, applicationContext, uiUtils);
        verify(dischargeIfAdmittedDispositionAction, never()).action(argThat(new IsExpectedEncounterDomainWrapper(encounter)), eq(deathDispositionObsGroup), anyMap());
        verify(markPatientDeadDispositionAction,never()).action(argThat(new IsExpectedEncounterDomainWrapper(encounter)), eq(deathDispositionObsGroup), anyMap());
        verify(uiUtils).pageLinkWithoutContextPath(eq("coreapps"), eq("patientdashboard/patientDashboard"), eq(expectedParams));
    }

    private class IsExpectedEncounterDomainWrapper extends ArgumentMatcher<EncounterDomainWrapper> {

        private Encounter expectedEncounter;

        public IsExpectedEncounterDomainWrapper(Encounter expectedEncounter) {
            this.expectedEncounter = expectedEncounter;
        }

        @Override
        public boolean matches(Object actualEncounterDomainWrapper) {
            return expectedEncounter.equals(((EncounterDomainWrapper) actualEncounterDomainWrapper).getEncounter());
        }
    }
}
