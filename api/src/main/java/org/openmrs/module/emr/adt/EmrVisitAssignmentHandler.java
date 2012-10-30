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

package org.openmrs.module.emr.adt;

import org.openmrs.Encounter;
import org.openmrs.Patient;
import org.openmrs.Visit;
import org.openmrs.api.VisitService;
import org.openmrs.api.context.Context;
import org.openmrs.api.handler.BaseEncounterVisitHandler;
import org.openmrs.api.handler.EncounterVisitHandler;

import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Locale;

/**
 * Ensures that encounters are assigned to visits based on the EMR module's business logic.
 *
 * For now, we require that a compatible visit exist before you're allowed to create an encounter.
 */
public class EmrVisitAssignmentHandler extends BaseEncounterVisitHandler implements EncounterVisitHandler {

    private VisitService visitService;

    private AdtService adtService;

    /**
     * Since the OpenMRS core doesn't load this bean via Spring, do some hacky setup here.
     * @see https://tickets.openmrs.org/browse/TRUNK-3772
     */
    public EmrVisitAssignmentHandler() {
        try {
            // in production, set the fields this way
            visitService = Context.getVisitService();
            adtService = Context.getService(AdtService.class);
        }
        catch (Exception ex) {
            // unit tests will set the fields manually
        }
    }

    @Override
    public String getDisplayName(Locale locale) {
        return "Default EMR Visit Assignment Handler";
    }

    @Override
    public void beforeCreateEncounter(Encounter encounter) {
        //Do nothing if the encounter already belongs to a visit.
        if (encounter.getVisit() != null)
            return;

        // Eventually allow some encounters to be visit-free, probably via a GP defining a list of EncounterTypes.

        Date when = encounter.getEncounterDatetime();
        if (when == null) {
            when = new Date();
        }

        List<Patient> patient = Collections.singletonList(encounter.getPatient());

        // visits that have not ended by the encounter date.
        List<Visit> candidates = visitService.getVisits(null, patient, null, null, null,
                when, null, null, null, true, false);

        if (candidates != null) {
            for (Visit candidate : candidates) {
                if (adtService.isSuitableVisit(candidate, encounter.getLocation(), when)) {
                    candidate.addEncounter(encounter);
                    return;
                }
            }
        }

        throw new IllegalStateException("Cannot create an encounter outside of a visit");
    }

    public void setVisitService(VisitService visitService) {
        this.visitService = visitService;
    }

    public void setAdtService(AdtService adtService) {
        this.adtService = adtService;
    }
}
