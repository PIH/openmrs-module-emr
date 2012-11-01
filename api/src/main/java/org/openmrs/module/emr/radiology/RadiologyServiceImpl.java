package org.openmrs.module.emr.radiology;

import org.openmrs.Concept;
import org.openmrs.Encounter;
import org.openmrs.api.EncounterService;
import org.openmrs.module.emr.EmrContext;
import org.openmrs.module.emr.EmrProperties;
import org.openmrs.module.emr.domain.RadiologyRequisition;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;

public class RadiologyServiceImpl implements RadiologyService {

    @Autowired
    @Qualifier("emrProperties")
    private EmrProperties emrProperties;

    @Autowired
    @Qualifier("encounterService")
    private EncounterService encounterService;

    @Transactional
    @Override
    public Encounter placeRadiologyRequisition(EmrContext emrContext, RadiologyRequisition requisition) {
        Encounter encounter = new Encounter();
        encounter.setEncounterType(emrProperties.getPlaceOrdersEncounterType());
        encounter.setProvider(emrProperties.getClinicianEncounterRole(), requisition.getRequestedBy());
        encounter.setPatient(requisition.getPatient());
        encounter.setLocation(emrContext.getSessionLocation());
        encounter.setVisit(emrContext.getActiveVisitSummary().getVisit());

        Date currentDatetime = new Date();
        encounter.setEncounterDatetime(currentDatetime);
        encounter.setDateCreated(currentDatetime);

        for (Concept study : requisition.getStudies()) {
            RadiologyOrder order = new RadiologyOrder();
            order.setExamLocation(requisition.getExamLocation());
            order.setClinicalHistory(requisition.getClinicalHistory());
            order.setConcept(study);
            order.setUrgency(requisition.getUrgency());
            order.setStartDate(requisition.getEncounterDatetime());
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

    protected void setEmrProperties(EmrProperties emrProperties) {
        this.emrProperties = emrProperties;
    }

    protected void setEncounterService(EncounterService encounterService) {
        this.encounterService = encounterService;
    }
}
