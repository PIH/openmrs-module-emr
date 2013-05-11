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

import org.hamcrest.Matcher;
import org.junit.Test;
import org.mockito.ArgumentMatcher;
import org.openmrs.module.emrapi.diagnosis.CodedOrFreeTextAnswer;
import org.openmrs.module.emrapi.diagnosis.Diagnosis;
import org.openmrs.module.emrapi.disposition.Disposition;

import java.util.HashMap;

import static org.hamcrest.core.Is.is;
import static org.hamcrest.core.IsNull.notNullValue;
import static org.junit.Assert.assertThat;

/**
 *
 */
public class ConsultNoteTest {

    @Test
    public void testBehavior() throws Exception {
        String answer1 = "A disease";
        String answer2 = "Another disease";
        String answer3 = "A third disease";

        Disposition died = new Disposition("died", "emr.disposition.died", "SNOMED CT:397709008", null, null);
        HashMap<String, String[]> map = new HashMap<String, String[]>();
        map.put("deathDate", new String[] { "2013-02-01" });

        ConsultNote consultNote = new ConsultNote();
        consultNote.addPrimaryDiagnosis(new Diagnosis(new CodedOrFreeTextAnswer(answer1)));
        consultNote.addPrimaryDiagnosis(new Diagnosis(new CodedOrFreeTextAnswer(answer2)));
        consultNote.addSecondaryDiagnosis(new Diagnosis(new CodedOrFreeTextAnswer(answer3)));

        consultNote.setDisposition(died);
        consultNote.setDispositionParameters(map);

        assertThat(consultNote.getDiagnoses().size(), is(3));

        assertThat(consultNote.getDiagnoses(Diagnosis.Order.PRIMARY).size(), is(2));
        assertThat(consultNote.getDiagnoses(Diagnosis.Order.PRIMARY).get(0), matchesDiagnosis(Diagnosis.Order.PRIMARY, Diagnosis.Certainty.PRESUMED, answer1));
        assertThat(consultNote.getDiagnoses(Diagnosis.Order.PRIMARY).get(1), matchesDiagnosis(Diagnosis.Order.PRIMARY, Diagnosis.Certainty.PRESUMED, answer2));

        assertThat(consultNote.getDiagnoses(Diagnosis.Order.SECONDARY).size(), is(1));
        assertThat(consultNote.getDiagnoses(Diagnosis.Order.SECONDARY).get(0), matchesDiagnosis(Diagnosis.Order.SECONDARY, Diagnosis.Certainty.PRESUMED, answer3));

        assertThat(consultNote.getDisposition(), notNullValue());
    }

    private Matcher<Diagnosis> matchesDiagnosis(final Diagnosis.Order order, final Diagnosis.Certainty certainty, final String nonCodedAnswer) {
        return new ArgumentMatcher<Diagnosis>() {
            @Override
            public boolean matches(Object argument) {
                Diagnosis actual = (Diagnosis) argument;
                return actual.getOrder().equals(order) &&
                        actual.getDiagnosis().getNonCodedAnswer().equals(nonCodedAnswer) &&
                        actual.getCertainty().equals(certainty);
            }
        };
    }

}
