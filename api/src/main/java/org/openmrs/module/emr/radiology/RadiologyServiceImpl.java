package org.openmrs.module.emr.radiology;

import org.openmrs.Encounter;
import org.openmrs.Provider;
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
    @Qualifier("emrContext")
    private EmrContext emrContext;

    @Autowired
    @Qualifier("encounterService")
    private EncounterService encounterService;

    @Transactional
    @Override
    public Encounter placeRadiologyRequisition(RadiologyRequisition requisition, Provider clinician) {
        Encounter encounter = new Encounter();
        encounter.setEncounterType(emrProperties.getPlaceOrdersEncounterType());
        encounter.setProvider(emrProperties.getClinicianEncounterRole(), clinician);
        encounter.setPatient(requisition.getPatient());
        encounter.setLocation(emrContext.getSessionLocation());
        encounter.setVisit(emrContext.getActiveVisitSummary().getVisit());

        Date currentDatetime = new Date();
        encounter.setEncounterDatetime(currentDatetime);
        encounter.setDateCreated(currentDatetime);

        encounter.addOrder(requisition);

        return encounterService.saveEncounter(encounter);
    }

    @Override
    public void onStartup() {
    }

    @Override
    public void onShutdown() {
    }

    @Override
    public void setEmrProperties(EmrProperties emrProperties) {
        this.emrProperties = emrProperties;
    }

    @Override
    public void setEmrContext(EmrContext emrContext) {
        this.emrContext = emrContext;
    }

    @Override
    public void setEncounterService(EncounterService encounterService) {
        this.encounterService = encounterService;
    }
}
