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

package org.openmrs.module.emr.consult;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentMatcher;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;
import org.openmrs.Concept;
import org.openmrs.ConceptAnswer;
import org.openmrs.ConceptMap;
import org.openmrs.ConceptMapType;
import org.openmrs.ConceptName;
import org.openmrs.ConceptReferenceTerm;
import org.openmrs.ConceptSource;
import org.openmrs.Encounter;
import org.openmrs.EncounterRole;
import org.openmrs.Location;
import org.openmrs.Obs;
import org.openmrs.Patient;
import org.openmrs.Provider;
import org.openmrs.User;
import org.openmrs.api.EncounterService;
import org.openmrs.api.context.Context;
import org.openmrs.module.emr.EmrConstants;
import org.openmrs.module.emr.EmrProperties;
import org.openmrs.util.OpenmrsUtil;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import java.util.Locale;
import java.util.Set;

import static org.hamcrest.collection.IsIterableContainingInAnyOrder.containsInAnyOrder;
import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertThat;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.powermock.api.mockito.PowerMockito.mockStatic;

/**
 *
 */
@RunWith(PowerMockRunner.class)
@PrepareForTest(Context.class)
public class ConsultServiceTest {

    private ConsultServiceImpl consultService;

    private EmrProperties emrProperties;
    private EncounterService encounterService;
    private Patient patient;
    private Concept diabetes;
    private Concept malaria;
    private ConceptName malariaSynonym;
    private Location mirebalaisHospital;
    private EncounterRole clinician;
    private Provider drBob;
    private User currentUser;

    private Concept diagnosisGroupingConcept;
    private Concept codedDiagnosis;
    private Concept nonCodedDiagnosis;
    private Concept diagnosisOrder;
    private Concept primary;
    private Concept secondary;
    private Concept freeTextComments;

    @Before
    public void setUp() throws Exception {
        currentUser = new User();
        mockStatic(Context.class);
        PowerMockito.when(Context.getAuthenticatedUser()).thenReturn(currentUser);

        patient = new Patient(123);
        diabetes = buildConcept(1, "Diabetes");
        malaria = buildConcept(2, "Malaria");
        malariaSynonym = new ConceptName();
        malaria.addName(malariaSynonym);
        mirebalaisHospital = new Location();
        clinician = new EncounterRole();
        drBob = new Provider();
        freeTextComments = buildConcept(3, "Comments");

        ConceptSource emrConceptSource = new ConceptSource();
        emrConceptSource.setName(EmrConstants.EMR_CONCEPT_SOURCE_NAME);

        ConceptMapType sameAs = new ConceptMapType();

        primary = buildConcept(4, "Primary");
        primary.addConceptMapping(new ConceptMap(new ConceptReferenceTerm(emrConceptSource, EmrConstants.CONCEPT_CODE_DIAGNOSIS_ORDER_PRIMARY, null), sameAs));

        secondary = buildConcept(5, "Secondary");
        secondary.addConceptMapping(new ConceptMap(new ConceptReferenceTerm(emrConceptSource, EmrConstants.CONCEPT_CODE_DIAGNOSIS_ORDER_SECONDARY, null), sameAs));

        diagnosisOrder = buildConcept(6, "Diagnosis Order");
        diagnosisOrder.addAnswer(new ConceptAnswer(primary));
        diagnosisOrder.addAnswer(new ConceptAnswer(secondary));

        codedDiagnosis = buildConcept(7, "Diagnosis (Coded)");
        nonCodedDiagnosis = buildConcept(8, "Diagnosis (Non-Coded)");

        diagnosisGroupingConcept = buildConcept(9, "Grouping for Diagnosis");
        diagnosisGroupingConcept.addSetMember(diagnosisOrder);
        diagnosisGroupingConcept.addSetMember(codedDiagnosis);
        diagnosisGroupingConcept.addSetMember(nonCodedDiagnosis);

        DiagnosisMetadata diagnosisMetadata = new DiagnosisMetadata();
        diagnosisMetadata.setDiagnosisSetConcept(diagnosisGroupingConcept);
        diagnosisMetadata.setCodedDiagnosisConcept(codedDiagnosis);
        diagnosisMetadata.setNonCodedDiagnosisConcept(nonCodedDiagnosis);
        diagnosisMetadata.setDiagnosisOrderConcept(diagnosisOrder);

        emrProperties = mock(EmrProperties.class);
        when(emrProperties.getConsultFreeTextCommentsConcept()).thenReturn(freeTextComments);
        when(emrProperties.getDiagnosisMetadata()).thenReturn(diagnosisMetadata);
        when(emrProperties.getClinicianEncounterRole()).thenReturn(clinician);

        encounterService = mock(EncounterService.class);
        when(encounterService.saveEncounter(any(Encounter.class))).thenAnswer(new Answer<Object>() {
            @Override
            public Object answer(InvocationOnMock invocationOnMock) throws Throwable {
                return invocationOnMock.getArguments()[0];
            }
        });

        consultService = new ConsultServiceImpl();
        consultService.setEncounterService(encounterService);
        consultService.setEmrProperties(emrProperties);
    }

    private Concept buildConcept(int conceptId, String name) {
        Concept concept = new Concept();
        concept.setConceptId(conceptId);
        concept.addName(new ConceptName(name, Locale.ENGLISH));
        return concept;
    }

    @Test
    public void saveConsultNote_shouldHandleCodedPrimaryDiagnosis() {
        ConsultNote consultNote = buildConsultNote();
        consultNote.setPrimaryDiagnosis(new Diagnosis(new CodedOrFreeTextAnswer(malaria)));
        Encounter encounter = consultService.saveConsultNote(consultNote);

        assertNotNull(encounter);
        verify(encounterService).saveEncounter(encounter);

        Set<Obs> obsAtTopLevel = encounter.getObsAtTopLevel(false);

        assertThat(obsAtTopLevel.size(), is(1));
        Obs primaryDiagnosis = obsAtTopLevel.iterator().next();
        assertThat(primaryDiagnosis, diagnosisMatcher(primary, malaria, null));
    }

    @Test
    public void saveConsultNote_shouldHandleCodedPrimaryDiagnosisWithSpecificName() {
        ConsultNote consultNote = buildConsultNote();
        consultNote.setPrimaryDiagnosis(new Diagnosis(new CodedOrFreeTextAnswer(malariaSynonym)));
        Encounter encounter = consultService.saveConsultNote(consultNote);

        assertNotNull(encounter);
        verify(encounterService).saveEncounter(encounter);

        Set<Obs> obsAtTopLevel = encounter.getObsAtTopLevel(false);
        assertThat(obsAtTopLevel.size(), is(1));
        Obs primaryDiagnosis = obsAtTopLevel.iterator().next();
        assertThat(primaryDiagnosis, diagnosisMatcher(primary, malaria, malariaSynonym));
    }

    @Test
    public void saveConsultNote_shouldHandleNonCodedPrimaryDiagnosis() {
        String nonCodedAnswer = "New disease we've never heard of";
        ConsultNote consultNote = buildConsultNote();
        consultNote.setPrimaryDiagnosis(new Diagnosis(new CodedOrFreeTextAnswer(nonCodedAnswer)));
        Encounter encounter = consultService.saveConsultNote(consultNote);

        assertNotNull(encounter);
        verify(encounterService).saveEncounter(encounter);

        Set<Obs> obsAtTopLevel = encounter.getObsAtTopLevel(false);
        assertThat(obsAtTopLevel.size(), is(1));
        Obs primaryDiagnosis = obsAtTopLevel.iterator().next();
        assertThat(primaryDiagnosis, diagnosisMatcher(primary, nonCodedAnswer));
    }

    @Test
    public void saveConsultNote_shouldHandleAllFields() {
        String nonCodedAnswer = "New disease we've never heard of";
        final String comments = "This is a very interesting case";

        ConsultNote consultNote = buildConsultNote();
        consultNote.setPrimaryDiagnosis(new Diagnosis(new CodedOrFreeTextAnswer(malaria)));
        consultNote.addAdditionalDiagnosis(new Diagnosis(new CodedOrFreeTextAnswer(diabetes)));
        consultNote.addAdditionalDiagnosis(new Diagnosis(new CodedOrFreeTextAnswer(nonCodedAnswer)));
        consultNote.setComments(comments);
        Encounter encounter = consultService.saveConsultNote(consultNote);

        assertNotNull(encounter);
        verify(encounterService).saveEncounter(encounter);

        Set<Obs> obsAtTopLevel = encounter.getObsAtTopLevel(false);
        assertThat(obsAtTopLevel.size(), is(4));

        assertThat(obsAtTopLevel, containsInAnyOrder(
                diagnosisMatcher(primary, malaria, null),
                diagnosisMatcher(secondary, diabetes, null),
                diagnosisMatcher(secondary, nonCodedAnswer),
                new ArgumentMatcher<Obs>() {
                    @Override
                    public boolean matches(Object o) {
                        Obs obs = (Obs) o;
                        return obs.getConcept().equals(freeTextComments) && obs.getValueText().equals(comments);
                    }
                }));
    }

    private ConsultNote buildConsultNote() {
        ConsultNote consultNote = new ConsultNote();
        consultNote.setPatient(patient);
        consultNote.setEncounterLocation(mirebalaisHospital);
        consultNote.setClinician(drBob);

        return consultNote;
    }

    private ArgumentMatcher<Obs> diagnosisMatcher(final Concept order, final Concept diagnosis, final ConceptName specificName) {
        return new ArgumentMatcher<Obs>() {
            @Override
            public boolean matches(Object o) {
                Obs obsGroup = (Obs) o;
                if (obsGroup.getConcept().equals(diagnosisGroupingConcept) &&
                        containsInAnyOrder(new CodedObsMatcher(diagnosisOrder, order),
                                new CodedObsMatcher(codedDiagnosis, diagnosis, specificName)).matches(obsGroup.getGroupMembers())) {
                }
                return obsGroup.getConcept().equals(diagnosisGroupingConcept) &&
                        containsInAnyOrder(new CodedObsMatcher(diagnosisOrder, order),
                                new CodedObsMatcher(codedDiagnosis, diagnosis, specificName)).matches(obsGroup.getGroupMembers());
            }

            @Override
            public String toString() {
                return "Diagnosis matcher for " + order + " = (coded) " + diagnosis;
            }
        };
    }

    private ArgumentMatcher<Obs> diagnosisMatcher(final Concept order, final String nonCodedAnswer) {
        return new ArgumentMatcher<Obs>() {
            @Override
            public boolean matches(Object o) {
                Obs obsGroup = (Obs) o;
                if (obsGroup.getConcept().equals(diagnosisGroupingConcept) &&
                        containsInAnyOrder(new CodedObsMatcher(diagnosisOrder, order),
                                new TextObsMatcher(nonCodedDiagnosis, nonCodedAnswer)).matches(obsGroup.getGroupMembers())) {
                }
                return obsGroup.getConcept().equals(diagnosisGroupingConcept) &&
                        containsInAnyOrder(new CodedObsMatcher(diagnosisOrder, order),
                                new TextObsMatcher(nonCodedDiagnosis, nonCodedAnswer)).matches(obsGroup.getGroupMembers());
            }
        };
    }

    private class CodedObsMatcher extends ArgumentMatcher<Obs> {
        private Concept question;
        private Concept answer;
        private ConceptName specificAnswer;

        public CodedObsMatcher(Concept question, Concept answer) {
            this.question = question;
            this.answer = answer;
        }

        public CodedObsMatcher(Concept question, Concept answer, ConceptName specificAnswer) {
            this.question = question;
            this.answer = answer;
            this.specificAnswer = specificAnswer;
        }

        @Override
        public boolean matches(Object o) {
            Obs obs = (Obs) o;
            return obs.getConcept().equals(question) && obs.getValueCoded().equals(answer) && OpenmrsUtil.nullSafeEquals(obs.getValueCodedName(), specificAnswer);
        }
    }

    private class TextObsMatcher extends ArgumentMatcher<Obs> {
        private Concept question;
        private String answer;
        public TextObsMatcher(Concept question, String answer) {
            this.question = question;
            this.answer = answer;
        }

        @Override
        public boolean matches(Object o) {
            Obs obs = (Obs) o;
            return obs.getConcept().equals(question) && obs.getValueText().equals(answer);
        }
    }
}
