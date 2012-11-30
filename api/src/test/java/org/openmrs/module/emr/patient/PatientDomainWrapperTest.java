package org.openmrs.module.emr.patient;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.openmrs.Patient;
import org.openmrs.PersonAttribute;
import org.openmrs.PersonAttributeType;
import org.openmrs.api.EncounterService;
import org.openmrs.api.VisitService;
import org.openmrs.module.emr.EmrConstants;
import org.openmrs.module.emr.EmrProperties;
import org.openmrs.module.emr.adt.AdtService;

import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class PatientDomainWrapperTest {

    private PatientDomainWrapper patientDomainWrapper;
    private EmrProperties emrProperties;
    private Patient patient;

    @Before
    public void setUp() throws Exception {
        patient = new Patient();
        emrProperties = mock(EmrProperties.class);
        patientDomainWrapper = new PatientDomainWrapper(patient, emrProperties, mock(AdtService.class),
                                                        mock(VisitService.class), mock(EncounterService.class) );
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


}
