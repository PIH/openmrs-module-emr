package org.openmrs.module.emr.patient;

import org.junit.Before;
import org.junit.Test;
import org.openmrs.*;
import org.openmrs.api.EncounterService;
import org.openmrs.api.VisitService;
import org.openmrs.module.emr.EmrConstants;
import org.openmrs.module.emr.EmrProperties;
import org.openmrs.module.emr.adt.AdtService;
import org.openmrs.module.emr.visit.VisitDomainWrapper;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static java.util.Arrays.asList;
import static org.hamcrest.core.Is.is;
import static org.junit.Assert.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class PatientDomainWrapperTest {

    private PatientDomainWrapper patientDomainWrapper;
    private EmrProperties emrProperties;
    private Patient patient;
    private VisitService visitService;

    @Before
    public void setUp() throws Exception {
        patient = new Patient();
        emrProperties = mock(EmrProperties.class);
        visitService = mock(VisitService.class);
        patientDomainWrapper = new PatientDomainWrapper(patient, emrProperties, mock(AdtService.class),
                visitService, mock(EncounterService.class) );
    }

    @Test
    public void shouldVerifyIfPatientIsUnknown(){

        PersonAttributeType personAttributeType = new PersonAttributeType();
        personAttributeType.setPersonAttributeTypeId(10);
        personAttributeType.setName(EmrConstants.UNKNOWN_PATIENT_PERSON_ATTRIBUTE_TYPE_NAME);
        personAttributeType.setFormat("java.lang.String");

        PersonAttribute newAttribute = new PersonAttribute(personAttributeType, "true");
        patient.addAttribute(newAttribute);

        when(emrProperties.getUnknownPatientPersonAttributeType()).thenReturn(personAttributeType);

        assertTrue(patientDomainWrapper.isUnknownPatient());

    }

    @Test
    public void shouldCreateAListOfVisitDomainWrappersBasedOnVisitListFromVisitService(){
        when(visitService.getVisitsByPatient(patient, true, false)).thenReturn(asList(new Visit(), new Visit(), new Visit()));

        List<VisitDomainWrapper> visitDomainWrappers =  patientDomainWrapper.getAllVisitsUsingWrappers();

        assertThat(visitDomainWrappers.size(), is(3));
    }

    @Test
    public void shouldReturnFormattedName(){
        patient = mock(Patient.class);

        patientDomainWrapper =  new PatientDomainWrapper(patient, emrProperties, mock(AdtService.class),
                visitService, mock(EncounterService.class) );

        Set<PersonName> personNames = new HashSet<PersonName>();

        PersonName personNamePreferred = createPreferredPersonName("John", "Dover");
        personNames.add(personNamePreferred);

        when(patient.getNames()).thenReturn(personNames);

        String formattedName = patientDomainWrapper.getFormattedName();

        assertThat(formattedName, is("Dover, John"));
    }


    @Test
    public void shouldReturnPersonNameWhenThereAreTwoNamesAndOneOfThemIsPreferred(){
        patient = mock(Patient.class);

        patientDomainWrapper =  new PatientDomainWrapper(patient, emrProperties, mock(AdtService.class),
                visitService, mock(EncounterService.class) );

        Set<PersonName> personNames = new HashSet<PersonName>();

        PersonName personNamePreferred = createPreferredPersonName("mario", "neissi");
        personNames.add(personNamePreferred);

        PersonName personNameNonPreferred = createNonPreferredPersonName("Ana", "emerson");
        personNames.add(personNameNonPreferred);

        when(patient.getNames()).thenReturn(personNames);
        PersonName returnedName = patientDomainWrapper.getPersonName();

        assertSame(personNamePreferred, returnedName);

    }

    @Test
    public void shouldReturnPersonNameWhenThereAreTwoNamesAndNoneOfThemIsPreferred(){
        patient = mock(Patient.class);

        patientDomainWrapper =  new PatientDomainWrapper(patient, emrProperties, mock(AdtService.class),
                visitService, mock(EncounterService.class) );

        Set<PersonName> personNames = new HashSet<PersonName>();

        PersonName personNamePreferred = createNonPreferredPersonName("mario", "neissi");
        personNames.add(personNamePreferred);

        PersonName personNameNonPreferred = createNonPreferredPersonName("Ana", "emerson");
        personNames.add(personNameNonPreferred);

        when(patient.getNames()).thenReturn(personNames);
        PersonName returnedName = patientDomainWrapper.getPersonName();

        assertNotNull(returnedName);

    }

    private PersonName createPreferredPersonName(String givenName, String familyName) {
        PersonName personNamePreferred = createPersonName(givenName, familyName, true);
        return personNamePreferred;
    }

    private PersonName createNonPreferredPersonName(String givenName, String familyName) {
        PersonName personNameNonPreferred = createPersonName(givenName, familyName, false);
        return personNameNonPreferred;
    }

    private PersonName createPersonName(String givenName, String familyName, boolean preferred) {
        PersonName personNameNonPreferred = new PersonName();
        personNameNonPreferred.setGivenName(givenName);
        personNameNonPreferred.setFamilyName(familyName);
        personNameNonPreferred.setPreferred(preferred);
        return personNameNonPreferred;
    }


}
