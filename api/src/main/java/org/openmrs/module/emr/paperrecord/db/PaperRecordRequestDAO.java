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

package org.openmrs.module.emr.paperrecord.db;

import org.openmrs.Location;
import org.openmrs.Patient;
import org.openmrs.module.emr.api.db.SingleClassDAO;
import org.openmrs.module.emr.paperrecord.PaperRecordRequest;

import java.util.List;

public interface PaperRecordRequestDAO extends SingleClassDAO<PaperRecordRequest> {

    // TODO: these could probably be refactored into a single method

    /**
     * Returns all the paper record requests with the specified status
     *
     * @param status
     * @return the paper record requests with the specified status
     */
    List<PaperRecordRequest> findPaperRecordRequests(PaperRecordRequest.Status status);
    
    /**
     * 
     * Returns all paper record requests with the specified Patient
     * 
     * @param patient
     * @return
     */
    List<PaperRecordRequest> findPaperRecordRequests(Patient patient);

    /**
     * Returns all paper record requests with the specified status and specified identifier
     *
     * @param statusList
     * @param identifier
     * @return
     */
    List<PaperRecordRequest> findPaperRecordRequests(List<PaperRecordRequest.Status> statusList, String identifier);

    /**
     * Returns all the paper record requests with the specified status that either have or don't have
     * an identifier, based on the hasIdentifier boolean
     *
     * @param status
     * @param hasIdentifier
     * @return the paper record requests with the specified status that either have or don't have an identifier
     */
    List<PaperRecordRequest> findPaperRecordRequests(PaperRecordRequest.Status status, boolean hasIdentifier);

    /**
     * Returns all the paper record requests for the given patient and given location with ANY of the specified statuses
     *
     * @param statusList
     * @param patient
     * @param recordLocation
     * @return the paper record requests for the given patient and given location with ANY of the specified statuses
     */
    List<PaperRecordRequest> findPaperRecordRequests(List<PaperRecordRequest.Status> statusList, Patient patient, Location recordLocation);

}
