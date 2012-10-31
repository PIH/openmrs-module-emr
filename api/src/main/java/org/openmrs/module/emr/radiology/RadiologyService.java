package org.openmrs.module.emr.radiology;

import org.openmrs.Encounter;
import org.openmrs.Provider;
import org.openmrs.api.EncounterService;
import org.openmrs.api.OpenmrsService;
import org.openmrs.module.emr.EmrContext;
import org.openmrs.module.emr.EmrProperties;
import org.openmrs.module.emr.domain.RadiologyRequisition;

public interface RadiologyService extends OpenmrsService {

    Encounter placeRadiologyRequisition(RadiologyRequisition requisition, Provider clinician);

    void setEmrProperties(EmrProperties emrProperties);

    void setEncounterService(EncounterService encounterService);

    void setEmrContext(EmrContext emrContext);
}
