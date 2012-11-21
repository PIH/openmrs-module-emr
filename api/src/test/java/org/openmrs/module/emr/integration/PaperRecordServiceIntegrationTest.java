package org.openmrs.module.emr.integration;

import org.junit.Before;
import org.junit.Test;
import org.openmrs.Location;
import org.openmrs.Patient;
import org.openmrs.PatientIdentifier;
import org.openmrs.api.AdministrationService;
import org.openmrs.api.PatientService;
import org.openmrs.module.emr.EmrConstants;
import org.openmrs.module.emr.paperrecord.PaperRecordService;
import org.openmrs.module.emr.paperrecord.PaperRecordServiceImpl;
import org.openmrs.module.idgen.service.IdentifierSourceService;
import org.openmrs.test.BaseModuleContextSensitiveTest;
import org.openmrs.test.SkipBaseSetup;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.annotation.NotTransactional;

import javax.annotation.Resource;

import static org.mockito.Matchers.any;
import static org.mockito.Matchers.notNull;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.when;

@SkipBaseSetup
public class PaperRecordServiceIntegrationTest extends BaseModuleContextSensitiveTest {

    @Autowired
    private IdentifierSourceService identifierSourceService;

    @Autowired
    @Qualifier("patientServiceTest")
    private PatientService patientServiceStub;

    private AdministrationService administrationService;

    private PaperRecordService paperRecordService;


    @Before
    public void setUp(){
        patientServiceStub = spy(patientServiceStub);
        paperRecordService = new PaperRecordServiceImpl();
        administrationService = mock(AdministrationService.class);
        ((PaperRecordServiceImpl) paperRecordService).setAdministrationService(administrationService);
        ((PaperRecordServiceImpl) paperRecordService).setIdentifierSourceService(identifierSourceService);
        ((PaperRecordServiceImpl) paperRecordService).setPatientService(patientServiceStub);
    }

    @Override
    public Boolean useInMemoryDatabase() {
        return false;
    }

    @Override
    public String getWebappName() {
        return "mirebalais";
    }


    @Test
    @DirtiesContext
    @NotTransactional
    public void shouldCreateTwoDifferentDossierNumbers() throws Exception {
        authenticate();
        when(administrationService.getGlobalProperty(EmrConstants.GP_PAPER_RECORD_IDENTIFIER_TYPE)).thenReturn("e66645eb-03a8-4991-b4ce-e87318e37566");

        doNothing().when(patientServiceStub).savePatientIdentifier(any(PatientIdentifier.class));

        String paperMedicalRecordNumberFor = paperRecordService.createPaperMedicalRecordNumberFor(new Patient(), new Location(15));
     }

}
