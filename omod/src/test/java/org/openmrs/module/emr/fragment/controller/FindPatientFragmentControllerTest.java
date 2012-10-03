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
package org.openmrs.module.emr.fragment.controller;

import org.apache.commons.beanutils.PropertyUtils;
import org.junit.Test;
import org.openmrs.Patient;
import org.openmrs.PersonName;
import org.openmrs.ui.framework.BasicUiUtils;
import org.openmrs.ui.framework.FormatterImpl;
import org.openmrs.ui.framework.SimpleObject;

import java.text.SimpleDateFormat;

import static org.junit.Assert.assertEquals;

public class FindPatientFragmentControllerTest {

    @Test
    public void testSimplify() throws Exception {
        PersonName name = new PersonName();
        name.setGivenName("Barack");
        name.setFamilyName("Obama");

        Patient patient = new Patient();
        patient.setPatientId(44);
        patient.addName(name);
        patient.setGender("M");
        patient.setBirthdate(new SimpleDateFormat("yyyy-MM-dd").parse("1961-08-04"));

        TestUiUtils ui = new TestUiUtils();
        SimpleObject o = new FindPatientFragmentController().simplify(ui, patient);

        assertEquals("Barack", PropertyUtils.getProperty(o, "preferredName.givenName"));
        assertEquals("", PropertyUtils.getProperty(o, "preferredName.middleName"));
        assertEquals("Obama", PropertyUtils.getProperty(o, "preferredName.familyName"));
        assertEquals("Barack Obama", PropertyUtils.getProperty(o, "preferredName.fullName"));
        assertEquals("04-Aug-1961", PropertyUtils.getProperty(o, "birthdate"));
        assertEquals("false", PropertyUtils.getProperty(o, "birthdateEstimated"));
        assertEquals("M", PropertyUtils.getProperty(o, "gender"));
    }

    private class TestUiUtils extends BasicUiUtils {
        public TestUiUtils() {
            this.formatter = new FormatterImpl();
        }
    }
}