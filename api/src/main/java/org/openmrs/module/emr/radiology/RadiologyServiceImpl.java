package org.openmrs.module.emr.radiology;

import java.util.Date;

import org.openmrs.Concept;
import org.openmrs.Encounter;
import org.openmrs.api.EncounterService;
import org.openmrs.module.emr.EmrContext;
import org.openmrs.module.emr.EmrProperties;
import org.openmrs.module.emr.adt.VisitSummary;
import org.springframework.transaction.annotation.Transactional;

public class RadiologyServiceImpl implements RadiologyService {

    private EmrProperties emrProperties;

    private EncounterService encounterService;

    @Transactional
    @Override
    public Encounter placeRadiologyRequisition(EmrContext emrContext, RadiologyRequisition requisition) {
        Encounter encounter = new Encounter();
        encounter.setEncounterType(emrProperties.getPlaceOrdersEncounterType());
        encounter.setProvider(emrProperties.getClinicianEncounterRole(), requisition.getRequestedBy());
        encounter.setPatient(requisition.getPatient());
        encounter.setLocation(emrContext.getSessionLocation());
        VisitSummary activeVisitSummary = emrContext.getActiveVisitSummary();
        if (activeVisitSummary != null) {
            encounter.setVisit(activeVisitSummary.getVisit());
        }

        Date currentDatetime = new Date();
        encounter.setEncounterDatetime(currentDatetime);
        encounter.setDateCreated(currentDatetime);

        for (Concept study : requisition.getStudies()) {
            RadiologyOrder order = new RadiologyOrder();
            order.setExamLocation(requisition.getExamLocation());
            order.setClinicalHistory(requisition.getClinicalHistory());
            order.setConcept(study);
            order.setUrgency(requisition.getUrgency());
            order.setStartDate(new Date());
            order.setOrderType(emrProperties.getTestOrderType());
            order.setPatient(requisition.getPatient());
            encounter.addOrder(order);
        }

        return encounterService.saveEncounter(encounter);
    }

    @Override
    public void onStartup() {
    }

    @Override
    public void onShutdown() {
    }

    public void setEmrProperties(EmrProperties emrProperties) {
        this.emrProperties = emrProperties;
    }

    public void setEncounterService(EncounterService encounterService) {
        this.encounterService = encounterService;
    }
}
