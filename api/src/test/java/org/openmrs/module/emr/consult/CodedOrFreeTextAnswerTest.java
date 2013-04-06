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

import org.junit.Test;
import org.junit.runner.RunWith;
import org.openmrs.Concept;
import org.openmrs.ConceptMap;
import org.openmrs.ConceptMapType;
import org.openmrs.ConceptName;
import org.openmrs.ConceptReferenceTerm;
import org.openmrs.ConceptSource;
import org.openmrs.module.emr.EmrConstants;
import org.openmrs.module.emrapi.diagnosis.CodedOrFreeTextAnswer;
import org.openmrs.util.LocaleUtility;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import java.util.Collections;
import java.util.Locale;

import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertThat;

@RunWith(PowerMockRunner.class)
@PrepareForTest(LocaleUtility.class)
public class CodedOrFreeTextAnswerTest {

    @Test
    public void testFormatWithCode() throws Exception {
        PowerMockito.mockStatic(LocaleUtility.class);
        PowerMockito.when(LocaleUtility.getLocalesInOrder()).thenReturn(Collections.singleton(Locale.ENGLISH));

        String name = "Cholera due to Vibrio Cholerae";
        String code = "A00.0";

        ConceptMapType sameAs = new ConceptMapType();
        sameAs.setUuid(EmrConstants.SAME_AS_CONCEPT_MAP_TYPE_UUID);
        sameAs.setName("SAME-AS");

        ConceptSource icd10 = new ConceptSource();

        ConceptReferenceTerm term = new ConceptReferenceTerm(icd10, code, null);

        ConceptName choleraName = new ConceptName(name, Locale.ENGLISH);
        choleraName.setLocalePreferred(true);

        Concept cholera = new Concept();
        cholera.addConceptMapping(new ConceptMap(term, sameAs));
        cholera.addName(choleraName);

        CodedOrFreeTextAnswer answer = new CodedOrFreeTextAnswer(cholera);

        String formatted = answer.formatWithCode(Locale.ENGLISH, Collections.singletonList(icd10));
        assertThat(formatted, is(name + " [" + code + "]"));
    }

}
