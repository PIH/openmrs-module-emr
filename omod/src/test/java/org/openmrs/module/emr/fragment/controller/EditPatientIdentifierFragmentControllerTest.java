package org.openmrs.module.emr.fragment.controller;


import org.junit.Before;
import org.junit.Test;
import org.openmrs.*;
import org.openmrs.api.PatientService;
import org.openmrs.module.emr.EmrContext;
import org.openmrs.module.emr.TestUiUtils;
import org.openmrs.ui.framework.SimpleObject;
import org.openmrs.ui.framework.UiUtils;

import java.text.SimpleDateFormat;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;



public class EditPatientIdentifierFragmentControllerTest {

    private EmrContext emrContext;
    private EditPatientIdentifierFragmentController controller;
    private UiUtils ui;
    private PatientIdentifierType patientIdentifierType = new PatientIdentifierType();
    private PatientService patientService;

    @Before
    public void setUp() {
        controller = new EditPatientIdentifierFragmentController();
        ui = new TestUiUtils();
        emrContext = mock(EmrContext.class);
        patientService =  mock(PatientService.class);
    }

    @Test
    public void testEditPatientIdentifierEditExistingValue() throws Exception {

        PersonName name = new PersonName();
        name.setGivenName("John");
        name.setFamilyName("Adam");

        PatientIdentifierType pit = new PatientIdentifierType();
        pit.setName("HIVEMR1");

        Patient patient = new Patient();
        patient.setPatientId(44);
        patient.addName(name);
        patient.setGender("M");
        patient.setBirthdate(new SimpleDateFormat("yyyy-MM-dd").parse("1961-08-04"));
        patient.addIdentifier(new PatientIdentifier("44", pit, null));

        SimpleObject simpleObject = controller.editPatientIdentifier(ui, patient, pit, "12434", null, patientService);
        assertEquals(simpleObject.get("message"),  ui.message("emr.patientDashBoard.editPatientIdentifier.successMessage"));

    }

    @Test
    public void testEditPatientIdentifierAddNewValue() throws Exception {

        PersonName name = new PersonName();
        name.setGivenName("John");
        name.setFamilyName("Adam");

        PatientIdentifierType pit = new PatientIdentifierType();
        pit.setName("HIVEMR1");

        Patient patient = new Patient();
        patient.setPatientId(44);
        patient.addName(name);
        patient.setGender("M");
        patient.setBirthdate(new SimpleDateFormat("yyyy-MM-dd").parse("1961-08-04"));

        SimpleObject simpleObject = controller.editPatientIdentifier(ui, patient, pit, "12434", null, patientService);
        assertEquals(simpleObject.get("message"),  ui.message("emr.patientDashBoard.editPatientIdentifier.successMessage"));
        assertEquals(patient.getPatientIdentifier(pit).getIdentifier(), "12434");

    }

}
