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
import org.openmrs.ConceptSource;
import org.openmrs.EncounterRole;
import org.openmrs.EncounterType;
import org.openmrs.Location;
import org.openmrs.LocationAttributeType;
import org.openmrs.LocationTag;
import org.openmrs.OrderType;
import org.openmrs.PatientIdentifierType;
import org.openmrs.PersonAttributeType;
import org.openmrs.Provider;
import org.openmrs.VisitType;
import org.openmrs.module.emr.api.EmrService;
import org.openmrs.module.emr.consult.DiagnosisMetadata;
import org.openmrs.module.emr.radiology.RadiologyConstants;
import org.openmrs.module.emrapi.utils.ModuleProperties;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

import static org.openmrs.module.emr.EmrConstants.LOCATION_ATTRIBUTE_TYPE_NAME_TO_PRINT_ON_ID_CARD;
import static org.openmrs.module.emr.EmrConstants.TELEPHONE_ATTRIBUTE_TYPE_NAME;
import static org.openmrs.module.emr.EmrConstants.TEST_PATIENT_ATTRIBUTE_UUID;
import static org.openmrs.module.emr.EmrConstants.UNKNOWN_PATIENT_PERSON_ATTRIBUTE_TYPE_NAME;

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

    public int getVisitExpireHours() {
        return 12;
    }

    public EncounterType getCheckInEncounterType() {
        return getEncounterTypeByGlobalProperty(EmrConstants.GP_CHECK_IN_ENCOUNTER_TYPE);
    }

    public EncounterType getTriageEncounterType() {
        return getEncounterTypeByGlobalProperty(EmrConstants.GP_TRIAGE_ENCOUNTER_TYPE);
    }

    public EncounterType getConsultEncounterType() {
        return getEncounterTypeByGlobalProperty(EmrConstants.GP_CONSULT_ENCOUNTER_TYPE);
    }

    public EncounterRole getClinicianEncounterRole() {
        return getEncounterRoleByGlobalProperty(EmrConstants.GP_CLINICIAN_ENCOUNTER_ROLE);
    }

    public EncounterRole getOrderingProviderEncounterRole() {
        return getEncounterRoleByGlobalProperty(EmrConstants.GP_ORDERING_PROVIDER_ENCOUNTER_ROLE);
    }

    public EncounterRole getCheckInClerkEncounterRole() {
        return getEncounterRoleByGlobalProperty(EmrConstants.GP_CHECK_IN_CLERK_ENCOUNTER_ROLE);
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

    public Location getUnknownLocation() {
        return getLocationByGlobalProperty(EmrConstants.GP_UNKNOWN_LOCATION);
    }

    public Provider getUnknownProvider() {
        return getProviderByGlobalProperty(EmrConstants.GP_UNKNOWN_PROVIDER);
    }

    public PatientIdentifierType getPrimaryIdentifierType() {
        return getPatientIdentifierTypeByGlobalProperty(EmrConstants.PRIMARY_IDENTIFIER_TYPE, true);
    }

    public PatientIdentifierType getPaperRecordIdentifierType() {
        return getPatientIdentifierTypeByGlobalProperty(EmrConstants.GP_PAPER_RECORD_IDENTIFIER_TYPE, false);
    }

    public List<PatientIdentifierType> getExtraPatientIdentifierTypes() {
        return getPatientIdentifierTypesByGlobalProperty(EmrConstants.GP_EXTRA_PATIENT_IDENTIFIER_TYPES, false);
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

    public DiagnosisMetadata getDiagnosisMetadata() {
        return new DiagnosisMetadata(conceptService, getEmrConceptSource());
    }

    protected Concept getEmrConceptByMapping(String code) {
        return getSingleConceptByMapping(getEmrConceptSource(), code);
    }

    public Concept getConsultFreeTextCommentsConcept() {
        return getEmrConceptByMapping(EmrConstants.CONCEPT_CODE_CONSULT_FREE_TEXT_COMMENT);
    }

    public PersonAttributeType getUnknownPatientPersonAttributeType() {
        PersonAttributeType type = null;
        type = personService.getPersonAttributeTypeByName(UNKNOWN_PATIENT_PERSON_ATTRIBUTE_TYPE_NAME);
        if (type == null) {
            throw new IllegalStateException("Configuration required: " + UNKNOWN_PATIENT_PERSON_ATTRIBUTE_TYPE_NAME);
        }
        return type;
    }

    public PersonAttributeType getTestPatientPersonAttributeType() {
        PersonAttributeType type = null;
        type = personService.getPersonAttributeTypeByUuid(TEST_PATIENT_ATTRIBUTE_UUID);
        if (type == null) {
            throw new IllegalStateException("Configuration required: Test Patient Attribute UUID");
        }
        return type;
    }

    public PersonAttributeType getTelephoneAttributeType() {
        PersonAttributeType type = null;
        type = personService.getPersonAttributeTypeByName(TELEPHONE_ATTRIBUTE_TYPE_NAME);
        if (type == null) {
            throw new IllegalStateException("Configuration required: " + TELEPHONE_ATTRIBUTE_TYPE_NAME);
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
        List<PatientIdentifierType> extraPatientIdentifierTypes = getExtraPatientIdentifierTypes();
        if (extraPatientIdentifierTypes != null && extraPatientIdentifierTypes.size() > 0) {
            types.addAll(extraPatientIdentifierTypes);
        }
        return types;
    }

    public ConceptSource getEmrConceptSource() {
        return conceptService.getConceptSourceByName(EmrConstants.EMR_CONCEPT_SOURCE_NAME);
    }

    public List<ConceptSource> getConceptSourcesForDiagnosisSearch() {
        ConceptSource icd10 = conceptService.getConceptSourceByName("ICD-10-WHO");
        if (icd10 != null) {
            return Arrays.asList(icd10);
        } else {
            return null;
        }
    }

    /**
     * Expects there to be a GP configured to point to a concept set, which is a set of other concept sets.
     * E.g. "HUM Diagnosis Sets" contains "HUM Outpatient Diagnosis Set", "HUM ER Diagnosis Set", etc.
     *
     * @return
     */
    public Collection<Concept> getDiagnosisSets() {
        String gp = getGlobalProperty(EmrConstants.GP_DIAGNOSIS_SET_OF_SETS, false);
        if (StringUtils.hasText(gp)) {
            Concept setOfSets = conceptService.getConceptByUuid(gp);
            if (setOfSets == null) {
                throw new IllegalStateException("Configuration required: " + EmrConstants.GP_DIAGNOSIS_SET_OF_SETS);
            }
            return setOfSets.getSetMembers();
        } else {
            return null;
        }
    }

    public ConceptMapType getSameAsConceptMapType() {
        return conceptService.getConceptMapTypeByUuid(EmrConstants.SAME_AS_CONCEPT_MAP_TYPE_UUID);
    }

    public ConceptMapType getNarrowerThanConceptMapType() {
        return conceptService.getConceptMapTypeByUuid(EmrConstants.NARROWER_THAN_CONCEPT_MAP_TYPE_UUID);
    }

    public List<Location> getAllAvailableLocations() {
        return emrService.getLoginLocations();
    }

}
