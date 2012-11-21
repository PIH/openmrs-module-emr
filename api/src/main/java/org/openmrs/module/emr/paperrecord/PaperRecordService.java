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

package org.openmrs.module.emr.paperrecord;

import org.openmrs.Location;
import org.openmrs.Patient;
import org.openmrs.Person;
import org.openmrs.annotation.Authorized;
import org.openmrs.api.OpenmrsService;
import org.openmrs.module.emr.EmrConstants;

import java.util.List;

/**
 * Public API for functionality relating to paper medical records
 */
public interface PaperRecordService extends OpenmrsService {

    /**
     * Fetches the Paper Record Request with the specified id
     *
     * @param id primary key of the paper record request to retrieve
     * @return the patient record request with the specified id
     */
    @Authorized(EmrConstants.PRIVILEGE_PAPER_RECORDS_MANAGE_REQUESTS)
    PaperRecordRequest getPaperRecordRequestById(Integer id);
    
    /**
     * 
     * Fetches a list of Paper Record Request for the specified Patient
     * 
     * @param patient a Patient
     * @return a List<PaperRecordRequest>
     */
    @Authorized(EmrConstants.PRIVILEGE_PAPER_RECORDS_MANAGE_REQUESTS)
    List<PaperRecordRequest> getPaperRecordRequest(Patient patient);

    /**
     * Requests the paper record for the specified patient for the specified location
     *
     * @param patient the patient whose record we are requesting
     * @param recordLocation the location of the record (ie, "Mirebalais Hospital")
     * @param requestLocation the location where the record is to be sent
     */
    @Authorized(EmrConstants.PRIVILEGE_PAPER_RECORDS_REQUEST_RECORDS)
    PaperRecordRequest requestPaperRecord(Patient patient, Location recordLocation, Location requestLocation);

    /**
     * Retrieves all records that are open (ie, have yet to be assigned to an archivist for retrieval)
     * and need to be pulled (ie, already exist and just need to be pulled, not created)
     *
     * @return the list of all open paper record requests that need to be pulled
     */
    @Authorized(EmrConstants.PRIVILEGE_PAPER_RECORDS_MANAGE_REQUESTS)
    List<PaperRecordRequest> getOpenPaperRecordRequestsToPull();

    /**
     * Retrieves all records that are open (ie, have yet to be assigned to an archivist for retrieval)
     * and need to be created (ie, do not yet exist)
     *
     * @return the list of all open paper record requests that need to be created
     */
    @Authorized(EmrConstants.PRIVILEGE_PAPER_RECORDS_MANAGE_REQUESTS)
    List<PaperRecordRequest> getOpenPaperRecordRequestsToCreate();

    /**
     * 
     * Creates or updates a Paper Record Request
     * 
     * @return
     */
    @Authorized(EmrConstants.PRIVILEGE_PAPER_RECORDS_MANAGE_REQUESTS)
    PaperRecordRequest savePaperRecordRequest(PaperRecordRequest paperRecordRequest);

    /**
     * Sets the status to ASSIGNED_TO_PULL and the assignee to the given value, for the given requests.
     * @param requests
     * @param assignee
     * @return the list that was passed in, but with assignees and status set
     * @throws IllegalStateException if any of the requests are not in the OPEN status
     */
    @Authorized(EmrConstants.PRIVILEGE_PAPER_RECORDS_MANAGE_REQUESTS)
    List<PaperRecordRequest> assignRequests(List<PaperRecordRequest> requests, Person assignee);


    /**
     * Retrieves all records that have been assigned and need to be pulled
     *
     * @return the list of all assigned paper record requests that need to be pulled
     */
    @Authorized(EmrConstants.PRIVILEGE_PAPER_RECORDS_MANAGE_REQUESTS)
    List<PaperRecordRequest> getAssignedPaperRecordRequestsToPull();

    // TODO: we can probably remove this as a public method after we remove it from the Patient registration module
    @Authorized(EmrConstants.PRIVILEGE_PAPER_RECORDS_MANAGE_REQUESTS)
    String createPaperMedicalRecordNumberFor(Patient patient, Location medicalRecordLocation);

}
