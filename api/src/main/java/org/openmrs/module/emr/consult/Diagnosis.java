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

import org.openmrs.Concept;
import org.openmrs.ConceptName;
import org.openmrs.api.ConceptService;

/**
 * Represents a recorded diagnosis
 */
public class Diagnosis extends CodedOrFreeTextAnswer {

    /**
     * @param spec should be "ConceptName:1234", "Concept:123", or "Non-Coded:Some text"
     * @return
     */
    static Diagnosis parse(String spec, ConceptService conceptService) {
        if (spec.startsWith("ConceptName:")) {
            String conceptNameId = spec.substring("ConceptName:".length());
            return new Diagnosis(conceptService.getConceptName(Integer.valueOf(conceptNameId)));
        } else if (spec.startsWith("Concept:")) {
            String conceptId = spec.substring("Concept:".length());
            return new Diagnosis(conceptService.getConcept(conceptId));
        } else if (spec.startsWith("Non-Coded:")) {
            return new Diagnosis(spec.substring("Non-Coded:".length()));
        } else {
            throw new IllegalArgumentException("Unknown diagnosis format: " + spec);
        }
    }

    public Diagnosis(ConceptName specificCodedAnswer) {
        this.specificCodedAnswer = specificCodedAnswer;
        this.codedAnswer = specificCodedAnswer.getConcept();
    }

    public Diagnosis(Concept codedAnswer) {
        this.codedAnswer = codedAnswer;
    }

    public Diagnosis(String nonCodedAnswer) {
        this.nonCodedAnswer = nonCodedAnswer;
    }

}
