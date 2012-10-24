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
import org.openmrs.EncounterRole;
import org.openmrs.EncounterType;
import org.openmrs.OrderType;
import org.openmrs.VisitType;
import org.openmrs.module.emr.utils.ModuleProperties;
import org.springframework.stereotype.Component;

@Component("emrProperties")
public class EmrProperties extends ModuleProperties {

    public int getVisitExpireHours() {
        return 10;
    }

    public Concept getXrayOrderablesConcept() {
        return getConceptByGlobalProperty(EmrConstants.GP_XRAY_ORDERABLES_CONCEPT);
    }

    public EncounterType getPlaceOrdersEncounterType() {
        return getEncounterTypeByGlobalProperty(EmrConstants.GP_PLACE_ORDERS_ENCOUNTER_TYPE);
    }

    public EncounterType getCheckInEncounterType() {
        return getEncounterTypeByGlobalProperty(EmrConstants.GP_CHECK_IN_ENCOUNTER_TYPE);
    }

    public EncounterRole getClinicianEncounterRole() {
        return getEncounterRoleByGlobalProperty(EmrConstants.GP_CLINICIAN_ENCOUNTER_ROLE);
    }

    public OrderType getTestOrderType() {
        return getOrderTypeByGlobalProperty(EmrConstants.GP_TEST_ORDER_TYPE);
    }

    public VisitType getAtFacilityVisitType() {
        return getVisitTypeByGlobalProperty(EmrConstants.GP_AT_FACILITY_VISIT_TYPE);
    }

}
