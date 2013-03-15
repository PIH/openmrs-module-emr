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

package org.openmrs.module.emr.reporting.library;

import org.openmrs.annotation.Handler;
import org.openmrs.module.emr.reporting.BaseDefinitionLibrary;
import org.openmrs.module.emr.reporting.DocumentedDefinition;
import org.openmrs.module.reporting.cohort.definition.AgeCohortDefinition;
import org.openmrs.module.reporting.cohort.definition.CohortDefinition;
import org.openmrs.module.reporting.cohort.definition.GenderCohortDefinition;
import org.openmrs.module.reporting.evaluation.parameter.Parameter;

import java.util.Date;

/**
 *
 */
@Handler(supports = CohortDefinition.class)
public class BasicCohortDefinitionLibrary extends BaseDefinitionLibrary<CohortDefinition> {

    public static final String PREFIX = "emr.cohortDefinition.";

    @Override
    public String getUuidPrefix() {
        return PREFIX;
    }

    @DocumentedDefinition(value = "males", definition = "Patients whose gender is M")
    public GenderCohortDefinition getMales() {
        GenderCohortDefinition males = new GenderCohortDefinition();
        males.setMaleIncluded(true);
        return males;
    }

    @DocumentedDefinition(value = "females", definition = "Patients whose gender is F")
    public GenderCohortDefinition getFemales() {
        GenderCohortDefinition females = new GenderCohortDefinition();
        females.setFemaleIncluded(true);
        return females;
    }

    @DocumentedDefinition(value = "unknown gender", definition = "Patients whose gender is neither M or F")
    public GenderCohortDefinition getUnknownGender() {
        GenderCohortDefinition unknownGender = new GenderCohortDefinition();
        unknownGender.setUnknownGenderIncluded(true);
        return unknownGender;
    }

    @DocumentedDefinition(value = "up to age on date", definition = "Patients whose age is <= $maxAge years on $effectiveDate")
    public AgeCohortDefinition getUpToAgeOnDate() {
        AgeCohortDefinition cd = new AgeCohortDefinition();
        cd.addParameter(new Parameter("effectiveDate", "Effective Date", Date.class));
        cd.addParameter(new Parameter("maxAge", "Max Age (years)", Integer.class));
        return cd;
    }

    @DocumentedDefinition(value = "at least age on date", definition = "Patients whose age is >= $minAge years on $effectiveDate")
    public AgeCohortDefinition getAtLeastAgeOnDate() {
        AgeCohortDefinition cd = new AgeCohortDefinition();
        cd.addParameter(new Parameter("effectiveDate", "Effective Date", Date.class));
        cd.addParameter(new Parameter("minAge", "Min Age (years)", Integer.class));
        return cd;
    }

}
