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

/**
 * Helper class representing either a coded or non-coded answer to a question. If the answer is coded, this may specify
 * a specific ConceptName that was used when the answer was chosen.
 */
public abstract class CodedOrFreeTextAnswer {

    Concept codedAnswer;

    ConceptName specificCodedAnswer;

    String nonCodedAnswer;

    public Concept getCodedAnswer() {
        return codedAnswer;
    }

    public void setCodedAnswer(Concept codedAnswer) {
        this.codedAnswer = codedAnswer;
    }

    public ConceptName getSpecificCodedAnswer() {
        return specificCodedAnswer;
    }

    public void setSpecificCodedAnswer(ConceptName specificCodedAnswer) {
        this.specificCodedAnswer = specificCodedAnswer;
    }

    public String getNonCodedAnswer() {
        return nonCodedAnswer;
    }

    public void setNonCodedAnswer(String nonCodedAnswer) {
        this.nonCodedAnswer = nonCodedAnswer;
    }

}
