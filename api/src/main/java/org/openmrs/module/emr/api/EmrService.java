/**
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
package org.openmrs.module.emr.api;

import org.openmrs.Encounter;
import org.openmrs.Location;
import org.openmrs.Obs;
import org.openmrs.Order;
import org.openmrs.Patient;
import org.openmrs.Visit;
import org.openmrs.module.emr.domain.RadiologyRequisition;

import java.util.Date;
import java.util.List;

/**
 * <pre>
 * Public API for EMR-related functionality.
 *
 * Since patients frequently leave the facility without having any formal electronic check-out process, we ensure that
 * old stale visits are automatically closed, even if they are never intentionally stopped. Our business logic is built
 * on the idea of <em>active</em> visits, per #isActive(Visit, Date). A visit with stopDatetime==null is not necessarily
 * active from our perspective. Non-active visits are liable to be stopped at any time.
 *
 * Visits are only allowed to happen at locations tagged with the EmrConstants.LOCATION_TAG_SUPPORTS_VISITS tag. When
 * you pass a location without that tag to a service method, we look from that location and above in the location
 * hierarchy until we find a location with this tag. (This allows you to configure the setup such that doing a check-in
 * at Outpatient Department creates a visit at its parent, with an Encounter at the location itself.)
 * </pre>
 */
public interface EmrService {

    List<Patient> findPatients(String query, Location checkedInAt, Integer start, Integer length);

    Encounter placeRadiologyRequisition(RadiologyRequisition requisition);

    /**
     * Ensures this patient has an <em>active</em> visit at the given location.
     * If the patient has any non-stopped visits that are not active, they are stopped as a side-effect.
     * If this method creates a new Visit, it will also persist it to the database.
     * The visit's location will be a valid visit location per our business logic.
     *
     * @param patient
     * @param department
     * @return
     */
    Visit ensureActiveVisit(Patient patient, Location department);

    /**
     * Our business logic is that a visit has ended if it has no recent encounter.
     * @see org.openmrs.module.emr.EmrProperties#getVisitExpireHours()
     * @return whether we think this visit has ended, according to our business logic
     */
    boolean isActive(Visit visit);

    /**
     * If we have to guess, we say a visit ends a fixed number of hours after its last encounter.
     * @see org.openmrs.module.emr.EmrProperties#getVisitExpireHours()
     * @param visit
     * @return when, according to our business logic, this visit ends
     */
    Date guessVisitStopDatetime(Visit visit);

    /**
     * Creates a "check-in" encounter for the given patient, at the location where, and adds it to the active visit.
     * (This method calls ensureActiveVisit.)
     *
     * @param patient required
     * @param where required (must either support visits, or have an ancestor location that does)
     * @param obsForCheckInEncounter optional
     * @param ordersForCheckInEncounter optional
     * @return the encounter created (with EncounterService.saveEncounter already called on it)
     */
    Encounter checkInPatient(Patient patient, Location where, List<Obs> obsForCheckInEncounter, List<Order> ordersForCheckInEncounter);

}
