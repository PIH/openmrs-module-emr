package org.openmrs.module.emr.radiology;

import org.openmrs.Concept;
import org.openmrs.Obs;
import org.openmrs.api.ConceptService;
import org.openmrs.module.emr.EmrConstants;
import org.openmrs.module.emrapi.descriptor.ConceptSetDescriptor;

public class RadiologyReportConceptSet extends ConceptSetDescriptor {

    private Concept radiologyReportSetConcept;

    private Concept accessionNumberConcept;

    private Concept reportBodyConcept;

    private Concept reportTypeConcept;

    private Concept procedureConcept;

    public RadiologyReportConceptSet(ConceptService conceptService) {

        setup(conceptService, EmrConstants.EMR_CONCEPT_SOURCE_NAME,
                "radiologyReportSetConcept", RadiologyConstants.CONCEPT_CODE_RADIOLOGY_REPORT_SET,
                "accessionNumberConcept", RadiologyConstants.CONCEPT_CODE_RADIOLOGY_ACCESSION_NUMBER,
                "reportBodyConcept", RadiologyConstants.CONCEPT_CODE_RADIOLOGY_REPORT_BODY,
                "reportTypeConcept", RadiologyConstants.CONCEPT_CODE_RADIOLOGY_REPORT_TYPE,
                "procedureConcept", RadiologyConstants.CONCEPT_CODE_RADIOLOGY_PROCEDURE);

    }

    public Obs buildRadiologyReportObsGroup(RadiologyReport radiologyReport) {

        Obs radiologyReportSet = new Obs();
        radiologyReportSet.setConcept(radiologyReportSetConcept);
        radiologyReportSet.setOrder(radiologyReport.getAssociatedRadiologyOrder());

        Obs accessionNumber = new Obs();
        accessionNumber.setConcept(accessionNumberConcept);
        accessionNumber.setValueText(radiologyReport.getAccessionNumber());
        radiologyReportSet.addGroupMember(accessionNumber);

        Obs reportBody = new Obs();
        reportBody.setConcept(reportBodyConcept);
        reportBody.setValueText(radiologyReport.getReportBody());
        radiologyReportSet.addGroupMember(reportBody);

        Obs reportType = new Obs();
        reportType.setConcept(reportTypeConcept);
        reportType.setValueCoded(radiologyReport.getReportType());
        radiologyReportSet.addGroupMember(reportType);

        Obs procedure = new Obs();
        procedure.setConcept(procedureConcept);
        procedure.setValueCoded(radiologyReport.getProcedure());
        radiologyReportSet.addGroupMember(procedure);

        return radiologyReportSet;
    }

    public Concept getRadiologyReportSetConcept() {
        return radiologyReportSetConcept;
    }

    public void setRadiologyReportSetConcept(Concept radiologyReportSetConcept) {
        this.radiologyReportSetConcept = radiologyReportSetConcept;
    }

    public Concept getAccessionNumberConcept() {
        return accessionNumberConcept;
    }

    public void setAccessionNumberConcept(Concept accessionNumberConcept) {
        this.accessionNumberConcept = accessionNumberConcept;
    }

    public Concept getReportBodyConcept() {
        return reportBodyConcept;
    }

    public void setReportBodyConcept(Concept reportBodyConcept) {
        this.reportBodyConcept = reportBodyConcept;
    }

    public Concept getReportTypeConcept() {
        return reportTypeConcept;
    }

    public void setReportTypeConcept(Concept reportTypeConcept) {
        this.reportTypeConcept = reportTypeConcept;
    }

    public Concept getProcedureConcept() {
        return procedureConcept;
    }

    public void setProcedureConcept(Concept procedureConcept) {
        this.procedureConcept = procedureConcept;
    }
}
