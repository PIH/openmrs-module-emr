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

import org.openmrs.ConceptClass;
import org.openmrs.ConceptSearchResult;
import org.openmrs.ConceptSource;
import org.openmrs.Location;
import org.openmrs.Patient;
import org.openmrs.api.OpenmrsService;

import java.util.Collection;
import java.util.List;
import java.util.Locale;

/**
 * Public API for EMR-related functionality.
 */
public interface EmrService extends OpenmrsService {

    List<Patient> findPatients(String query, Location checkedInAt, Integer start, Integer length);

    /**
     * If any locations are tagged as supporting logins, we return only those. If no locations have this tag, we
     * return all non-retired ones.
     * @see org.openmrs.module.emr.EmrConstants#LOCATION_TAG_SUPPORTS_LOGIN
     * @return all locations that you can choose as a sessionLocation when logging in
     */
    List<Location> getLoginLocations();

    Patient findPatientByPrimaryId(String primaryId);

    /**
     * Searches for concepts by a fuzzy name match, or an exact match on a concept mapping
     * @param query
     * @param locale
     * @param classes if specified, only search among concepts with this class
     * @param sources if specified, search for exact matches on mappings in this source
     * @param limit return up to this many results (defaults to 100)
     * @return
     */
    List<ConceptSearchResult> conceptSearch(String query, Locale locale, Collection<ConceptClass> classes, Collection<ConceptSource> sources, Integer limit);

}
