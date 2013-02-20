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

package org.openmrs.module.emr.consult;

import org.apache.commons.beanutils.PropertyUtils;
import org.openmrs.Concept;
import org.openmrs.ConceptAnswer;
import org.openmrs.ConceptMap;
import org.openmrs.ConceptReferenceTerm;
import org.openmrs.Obs;
import org.openmrs.api.ConceptService;
import org.openmrs.module.emr.EmrConstants;

/**
 * Metadata describing how a diagnosis is represented as an Obs group.
 * TODO refactor to pull more of this functionality into a base class, e.g. ConceptSetDescriptor
 */
public class DiagnosisConceptSet {

    private Concept diagnosisSetConcept;
    private Concept codedDiagnosisConcept;
    private Concept nonCodedDiagnosisConcept;
    private Concept diagnosisOrderConcept;

    public DiagnosisConceptSet(ConceptService conceptService) {
        setup(conceptService, EmrConstants.EMR_CONCEPT_SOURCE_NAME, "diagnosisSetConcept", EmrConstants.CONCEPT_CODE_DIAGNOSIS_CONCEPT_SET,
                "codedDiagnosisConcept", EmrConstants.CONCEPT_CODE_CODED_DIAGNOSIS,
                "nonCodedDiagnosisConcept", EmrConstants.CONCEPT_CODE_NON_CODED_DIAGNOSIS,
                "diagnosisOrderConcept", EmrConstants.CONCEPT_CODE_DIAGNOSIS_ORDER);
    }

    /**
     * Used for testing -- in production you'll typically use the constructor that takes ConceptService
     */
    public DiagnosisConceptSet() {
    }

    private void setup(ConceptService conceptService, String conceptSourceName, String... fieldsAndConceptCodes) {
        try {
            String primaryConceptCode = fieldsAndConceptCodes[1];
            Concept primaryConcept = conceptService.getConceptByMapping(primaryConceptCode, conceptSourceName);
            if (primaryConcept == null) {
                throw new IllegalStateException("Couldn't find primary concept for " + getClass().getSimpleName() + " which should be mapped as " + conceptSourceName + ":" + primaryConceptCode);
            }
            PropertyUtils.setProperty(this, fieldsAndConceptCodes[0], primaryConcept);
            for (int i = 2; i < fieldsAndConceptCodes.length; i += 2) {
                String propertyName = fieldsAndConceptCodes[i];
                String mappingCode = fieldsAndConceptCodes[i + 1];
                Concept childConcept = conceptService.getConceptByMapping(mappingCode, conceptSourceName);
                if (childConcept == null) {
                    throw new IllegalStateException("Couldn't find " + propertyName + " concept for " + getClass().getSimpleName() + " which should be mapped as " + conceptSourceName + ":" + mappingCode);
                }
                if (!primaryConcept.getSetMembers().contains(childConcept)) {
                    throw new IllegalStateException("Concept mapped as " + conceptSourceName + ":" + mappingCode + " needs to be a set member of concept " + primaryConcept.getConceptId() + " which is mapped as " + conceptSourceName + ":" + primaryConceptCode);
                }
                PropertyUtils.setProperty(this, propertyName, childConcept);
            }
        } catch (Exception ex) {
            if (ex instanceof RuntimeException) {
                throw (RuntimeException) ex;
            } else {
                throw new IllegalStateException(ex);
            }
        }
    }

    public Concept getDiagnosisSetConcept() {
        return diagnosisSetConcept;
    }

    public Concept getCodedDiagnosisConcept() {
        return codedDiagnosisConcept;
    }

    public Concept getNonCodedDiagnosisConcept() {
        return nonCodedDiagnosisConcept;
    }

    public Concept getDiagnosisOrderConcept() {
        return diagnosisOrderConcept;
    }

    public void setDiagnosisSetConcept(Concept diagnosisSetConcept) {
        this.diagnosisSetConcept = diagnosisSetConcept;
    }

    public void setCodedDiagnosisConcept(Concept codedDiagnosisConcept) {
        this.codedDiagnosisConcept = codedDiagnosisConcept;
    }

    public void setNonCodedDiagnosisConcept(Concept nonCodedDiagnosisConcept) {
        this.nonCodedDiagnosisConcept = nonCodedDiagnosisConcept;
    }

    public void setDiagnosisOrderConcept(Concept diagnosisOrderConcept) {
        this.diagnosisOrderConcept = diagnosisOrderConcept;
    }

    public Obs buildDiagnosisObsGroup(Diagnosis diagnosis, String codeForOrder) {
        Obs order = new Obs();
        order.setConcept(diagnosisOrderConcept);
        order.setValueCoded(findAnswer(diagnosisOrderConcept, codeForOrder));

        Obs diagnosisObs = new Obs();
        if (diagnosis.getNonCodedAnswer() != null) {
            diagnosisObs.setConcept(nonCodedDiagnosisConcept);
            diagnosisObs.setValueText(diagnosis.getNonCodedAnswer());
        } else {
            diagnosisObs.setConcept(codedDiagnosisConcept);
            diagnosisObs.setValueCoded(diagnosis.getCodedAnswer());
            diagnosisObs.setValueCodedName(diagnosis.getSpecificCodedAnswer());
        }

        Obs obs = new Obs();
        obs.setConcept(diagnosisSetConcept);
        obs.addGroupMember(order);
        obs.addGroupMember(diagnosisObs);
        return obs;
    }

    private Concept findAnswer(Concept concept, String codeForAnswer) {
        for (ConceptAnswer conceptAnswer : concept.getAnswers()) {
            Concept answerConcept = conceptAnswer.getAnswerConcept();
            if (answerConcept != null) {
                if (hasConceptMapping(answerConcept, EmrConstants.EMR_CONCEPT_SOURCE_NAME, codeForAnswer)) {
                    return answerConcept;
                }
            }
        }
        throw new IllegalStateException("Cannot find answer mapped with " + EmrConstants.EMR_CONCEPT_SOURCE_NAME + ":" + codeForAnswer + " in the concept " + concept.getName());
    }

    private boolean hasConceptMapping(Concept concept, String sourceName, String codeToLookFor) {
        for (ConceptMap conceptMap : concept.getConceptMappings()) {
            ConceptReferenceTerm conceptReferenceTerm = conceptMap.getConceptReferenceTerm();
            if (sourceName.equals(conceptReferenceTerm.getConceptSource().getName()) && codeToLookFor.equals(conceptReferenceTerm.getCode())) {
                return true;
            }
        }
        return false;
    }

}
