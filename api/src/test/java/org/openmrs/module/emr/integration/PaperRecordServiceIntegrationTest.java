package org.openmrs.module.emr.integration;

import org.junit.Before;
import org.junit.Test;
import org.openmrs.Location;
import org.openmrs.Patient;
import org.openmrs.api.AdministrationService;
import org.openmrs.api.PatientService;
import org.openmrs.module.emr.EmrConstants;
import org.openmrs.module.emr.paperrecord.PaperRecordService;
import org.openmrs.module.emr.paperrecord.PaperRecordServiceImpl;
import org.openmrs.module.idgen.service.IdentifierSourceService;
import org.openmrs.test.BaseModuleContextSensitiveTest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class PaperRecordServiceIntegrationTest extends BaseModuleContextSensitiveTest {

    private AdministrationService administrationService;

    @Autowired
    private IdentifierSourceService identifierSourceService;

    @Autowired
    private PatientService patientService;

    private PaperRecordService paperRecordService;


    @Before
    public void setUp(){
        paperRecordService = new PaperRecordServiceImpl();
        administrationService = mock(AdministrationService.class);
        ((PaperRecordServiceImpl) paperRecordService).setAdministrationService(administrationService);
        ((PaperRecordServiceImpl) paperRecordService).setIdentifierSourceService(identifierSourceService);
        ((PaperRecordServiceImpl) paperRecordService).setPatientService(patientService);
    }


     @Test
    public void shouldCreateTwoDifferentDossierNumbers(){
         when(administrationService.getGlobalProperty(EmrConstants.GP_PAPER_RECORD_IDENTIFIER_TYPE)).thenReturn("e66645eb-03a8-4991-b4ce-e87318e37566");

         String paperMedicalRecordNumberFor = paperRecordService.createPaperMedicalRecordNumberFor(mock(Patient.class), new Location(15));
     }

}
