package org.openmrs.module.emr.radiology;

import org.junit.Before;
import org.junit.Test;
import org.openmrs.Concept;
import org.openmrs.ConceptAnswer;
import org.openmrs.ConceptMapType;
import org.openmrs.ConceptSource;
import org.openmrs.Obs;
import org.openmrs.api.ConceptService;
import org.openmrs.module.emr.EmrConstants;

import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.mock;

public class RadiologyReportConceptSetTest extends BaseConceptSetTest {

    private Concept radiologyReportSetConcept;
    private Concept accessionNumberConcept;
    private Concept reportBodyConcept;
    private Concept reportTypeConcept;
    private Concept procedureConcept;

    private Concept reportTypeFinalConcept = new Concept();

    private ConceptService conceptService;

    @Before
    public void setup() throws Exception {

        conceptService = mock(ConceptService.class);

        sameAs = new ConceptMapType();
        emrConceptSource = new ConceptSource();
        emrConceptSource.setName(EmrConstants.EMR_CONCEPT_SOURCE_NAME);

        radiologyReportSetConcept = setupConcept(conceptService, "Radiology Report Set", RadiologyConstants.CONCEPT_CODE_RADIOLOGY_REPORT_SET);
        accessionNumberConcept = setupConcept(conceptService, "Accession Number", RadiologyConstants.CONCEPT_CODE_RADIOLOGY_ACCESSION_NUMBER);
        reportBodyConcept = setupConcept(conceptService, "Report Body", RadiologyConstants.CONCEPT_CODE_RADIOLOGY_REPORT_BODY);
        reportTypeConcept = setupConcept(conceptService, "Report Type", RadiologyConstants.CONCEPT_CODE_RADIOLOGY_REPORT_TYPE);;
        procedureConcept = setupConcept(conceptService, "Procedure", RadiologyConstants.CONCEPT_CODE_RADIOLOGY_PROCEDURE);

        radiologyReportSetConcept.addSetMember(accessionNumberConcept);
        radiologyReportSetConcept.addSetMember(reportBodyConcept);
        radiologyReportSetConcept.addSetMember(reportTypeConcept);
        radiologyReportSetConcept.addSetMember(procedureConcept);

    }

    @Test
    public void testConstructor() throws Exception {

        RadiologyReportConceptSet radiologyReportConceptSet = new RadiologyReportConceptSet(conceptService);
        assertThat(radiologyReportConceptSet.getRadiologyReportSetConcept(), is(radiologyReportSetConcept));
        assertThat(radiologyReportConceptSet.getAccessionNumberConcept(), is(accessionNumberConcept));
        assertThat(radiologyReportConceptSet.getReportBodyConcept(), is(reportBodyConcept));
        assertThat(radiologyReportConceptSet.getReportTypeConcept(), is(reportTypeConcept));
        assertThat(radiologyReportConceptSet.getProcedureConcept(), is(procedureConcept));

    }

    @Test
    public void shouldCreateObsGroupFromRadiologyReport() {

        RadiologyOrder radiologyOrder = new RadiologyOrder();
        String accessionNumber = "12345";
        radiologyOrder.setAccessionNumber(accessionNumber);
        Concept procedure = new Concept();

        RadiologyReport radiologyReport = new RadiologyReport();
        radiologyReport.setAssociatedRadiologyOrder(radiologyOrder);
        radiologyReport.setAccessionNumber(accessionNumber);
        radiologyReport.setReportBody("Some report body");
        radiologyReport.setReportType(reportTypeFinalConcept);
        radiologyReport.setProcedure(procedure);

        RadiologyReportConceptSet radiologyReportConceptSet = new RadiologyReportConceptSet(conceptService);
        Obs radiologyReportObsSet = radiologyReportConceptSet.buildRadiologyReportObsGroup(radiologyReport);

        assertThat(radiologyReportObsSet.getGroupMembers().size(), is(4));
        assertThat(radiologyReportObsSet.getOrder().getAccessionNumber(), is(accessionNumber));

        Obs accessionNumberObs = null;
        Obs procedureObs = null;
        Obs reportBodyObs = null;
        Obs reportTypeObs = null;

        for (Obs obs : radiologyReportObsSet.getGroupMembers()) {
            if (obs.getConcept().equals(accessionNumberConcept)) {
                accessionNumberObs = obs;
            }
            if (obs.getConcept().equals(procedureConcept)) {
                procedureObs  = obs;
            }
            if (obs.getConcept().equals(reportBodyConcept)) {
                reportBodyObs = obs;
            }
            if (obs.getConcept().equals(reportTypeConcept))
                reportTypeObs = obs;
        }

        assertNotNull(accessionNumberObs);
        assertNotNull(procedureObs);
        assertNotNull(reportBodyObs);
        assertNotNull(reportTypeObs);

        assertThat(accessionNumberObs.getValueText(), is("12345"));
        assertThat(procedureObs.getValueCoded(), is(procedure));
        assertThat(reportBodyObs.getValueText(), is("Some report body"));
        assertThat(reportTypeObs.getValueCoded(), is(reportTypeFinalConcept));

    }

    @Test
    public void shouldNotCreateObsForAccessionNumberAndTypeIfNotSpecified() {

        Concept procedure = new Concept();

        RadiologyReport radiologyReport = new RadiologyReport();
        radiologyReport.setReportBody("Some report body");
        radiologyReport.setProcedure(procedure);

        RadiologyReportConceptSet radiologyReportConceptSet = new RadiologyReportConceptSet(conceptService);
        Obs radiologyReportObsSet = radiologyReportConceptSet.buildRadiologyReportObsGroup(radiologyReport);

        assertThat(radiologyReportObsSet.getGroupMembers().size(), is(2));

        Obs procedureObs = null;
        Obs reportBodyObs = null;

        for (Obs obs : radiologyReportObsSet.getGroupMembers()) {
            if (obs.getConcept().equals(procedureConcept)) {
                procedureObs  = obs;
            }
            if (obs.getConcept().equals(reportBodyConcept)) {
                reportBodyObs = obs;
            }
        }

        assertNotNull(procedureObs);
        assertNotNull(reportBodyObs);

        assertThat(procedureObs.getValueCoded(), is(procedure));
        assertThat(reportBodyObs.getValueText(), is("Some report body"));

    }


}
