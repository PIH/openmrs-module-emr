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
import org.openmrs.LocationAttributeType;
import org.openmrs.LocationTag;
import org.openmrs.OrderType;
import org.openmrs.PatientIdentifierType;
import org.openmrs.PersonAttributeType;
import org.openmrs.Role;
import org.openmrs.VisitType;
import org.openmrs.module.emr.utils.ModuleProperties;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

import static org.openmrs.module.emr.EmrConstants.LOCATION_ATTRIBUTE_TYPE_NAME_TO_PRINT_ON_ID_CARD;
import static org.openmrs.module.emr.EmrConstants.UNKNOWN_PATIENT_PERSON_ATTRIBUTE_TYPE_NAME;

@Component("emrProperties")
public class EmrProperties extends ModuleProperties {

    public int getVisitExpireHours() {
        return 12;
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

    public EncounterType getTriageEncounterType() {
        return getEncounterTypeByGlobalProperty(EmrConstants.GP_TRIAGE_ENCOUNTER_TYPE);
    }

    public EncounterRole getClinicianEncounterRole() {
        return getEncounterRoleByGlobalProperty(EmrConstants.GP_CLINICIAN_ENCOUNTER_ROLE);
    }

    public EncounterRole getCheckInClerkEncounterRole() {
        return getEncounterRoleByGlobalProperty(EmrConstants.GP_CHECK_IN_CLERK_ENCOUNTER_ROLE);
    }

	public Role getFullPrivilegeLevel() {
		return userService.getRole(EmrConstants.PRIVILEGE_LEVEL_FULL_ROLE);
	}

    public OrderType getTestOrderType() {
        return getOrderTypeByGlobalProperty(EmrConstants.GP_TEST_ORDER_TYPE);
    }

    public VisitType getAtFacilityVisitType() {
        return getVisitTypeByGlobalProperty(EmrConstants.GP_AT_FACILITY_VISIT_TYPE);
    }

    public LocationTag getSupportsVisitsLocationTag() {
        return locationService.getLocationTagByName(EmrConstants.LOCATION_TAG_SUPPORTS_VISITS);
    }

    public LocationTag getSupportsLoginLocationTag() {
        return locationService.getLocationTagByName(EmrConstants.LOCATION_TAG_SUPPORTS_LOGIN);
    }

    public LocationTag getMedicalRecordLocationLocationTag() {
        return locationService.getLocationTagByName(EmrConstants.LOCATION_TAG_MEDICAL_RECORD_LOCATION);
    }

    public PatientIdentifierType getPrimaryIdentifierType() {
        return getPatientIdentifierTypeByGlobalProperty(EmrConstants.PRIMARY_IDENTIFIER_TYPE, true);
    }

    public PatientIdentifierType getPaperRecordIdentifierType() {
        return getPatientIdentifierTypeByGlobalProperty(EmrConstants.GP_PAPER_RECORD_IDENTIFIER_TYPE, false);
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

    public PersonAttributeType getUnknownPatientPersonAttributeType(){
        PersonAttributeType type = null;
        type = personService.getPersonAttributeTypeByName(UNKNOWN_PATIENT_PERSON_ATTRIBUTE_TYPE_NAME);
        if (type == null) {
            throw new IllegalStateException("Configuration required: " + UNKNOWN_PATIENT_PERSON_ATTRIBUTE_TYPE_NAME);
        }
        return type;
    }

    public LocationAttributeType getLocationAttributeTypeNameToPrintOnIdCard() {
        LocationAttributeType type = null;
        type = locationService.getLocationAttributeTypeByUuid(LOCATION_ATTRIBUTE_TYPE_NAME_TO_PRINT_ON_ID_CARD);
        if (type == null) {
            throw new IllegalStateException("Configuration required: " + LOCATION_ATTRIBUTE_TYPE_NAME_TO_PRINT_ON_ID_CARD);
        }
        return type;
    }

    public List<PatientIdentifierType> getIdentifierTypesToSearch() {
        ArrayList<PatientIdentifierType> types = new ArrayList<PatientIdentifierType>();
        types.add(getPrimaryIdentifierType());
        PatientIdentifierType paperRecordIdentifierType = getPaperRecordIdentifierType();
        if (paperRecordIdentifierType != null) {
            types.add(paperRecordIdentifierType);
        }
        return types;
    }

}
