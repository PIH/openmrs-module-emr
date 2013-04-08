/*
 * The contents of this file are subject to the OpenMRS Public License
 * Version 1.0 (the "License"); you may not use this file except in
 * compliance with the License. You may obtain a copy of the License at
 * http://license.openmrs.org
 *
 * Software distributed under the License is distributed on an "AS IS"
 * basis, WITHOUT WARRANTY OF ANY KIND, either express or implied. See the
 * License for the specific language governing rights and limitations
 * under the License.
 *
 * Copyright (C) OpenMRS, LLC.  All Rights Reserved.
 */

package org.openmrs.module.emr;

import org.openmrs.Concept;
import org.openmrs.ConceptMapType;
import org.openmrs.EncounterRole;
import org.openmrs.EncounterType;
import org.openmrs.Location;
import org.openmrs.Provider;
import org.openmrs.module.emr.api.EmrService;
import org.openmrs.module.emrapi.utils.ModuleProperties;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

@Component("emrProperties")
public class EmrProperties extends ModuleProperties {

    @Autowired
    @Qualifier("emrService")
    protected EmrService emrService;

    /**
     * For testing. In production this will be autowired
     *
     * @param emrService
     */
    public void setEmrService(EmrService emrService) {
        this.emrService = emrService;
    }

    public EncounterType getCheckInEncounterType() {
        return getEncounterTypeByGlobalProperty(EmrConstants.GP_CHECK_IN_ENCOUNTER_TYPE);
    }

    public EncounterType getTriageEncounterType() {
        return getEncounterTypeByGlobalProperty(EmrConstants.GP_TRIAGE_ENCOUNTER_TYPE);
    }

    public EncounterRole getOrderingProviderEncounterRole() {
        return getEncounterRoleByGlobalProperty(EmrConstants.GP_ORDERING_PROVIDER_ENCOUNTER_ROLE);
    }

    public Location getUnknownLocation() {
        return getLocationByGlobalProperty(EmrConstants.GP_UNKNOWN_LOCATION);
    }

    public Provider getUnknownProvider() {
        return getProviderByGlobalProperty(EmrConstants.GP_UNKNOWN_PROVIDER);
    }

    public Concept getPaymentConstructConcept() {
        return getConceptByGlobalProperty(EmrConstants.PAYMENT_CONSTRUCT_CONCEPT);
    }

    public Concept getPaymentAmountConcept() {
        return getConceptByGlobalProperty(EmrConstants.PAYMENT_AMOUNT_CONCEPT);
    }

    public Concept getPaymentReasonsConcept() {
        return getConceptByGlobalProperty(EmrConstants.PAYMENT_REASON_CONCEPT);
    }

    public Concept getPaymentReceiptNumberConcept() {
        return getConceptByGlobalProperty(EmrConstants.PAYMENT_RECEIPT_NUMBER_CONCEPT);
    }

    public ConceptMapType getSameAsConceptMapType() {
        return conceptService.getConceptMapTypeByUuid(EmrConstants.SAME_AS_CONCEPT_MAP_TYPE_UUID);
    }

    public ConceptMapType getNarrowerThanConceptMapType() {
        return conceptService.getConceptMapTypeByUuid(EmrConstants.NARROWER_THAN_CONCEPT_MAP_TYPE_UUID);
    }

}
