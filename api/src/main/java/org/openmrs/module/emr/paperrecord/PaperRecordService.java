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
import org.openmrs.module.emr.EmrConstants;
import org.openmrs.module.emr.paperrecord.PaperRecordRequest;

import java.util.List;

/**
 * Public API for functionality relating to paper medical records
 */
public interface PaperRecordService {

    /**
     * Fetches the Paper Record Request with the specified id
     *
     * @param id primary key of the paper record request to retrieve
     * @return the patient record request with the specified id
     */
    @Authorized(EmrConstants.PRIVILEGE_PAPER_RECORDS_MANAGE_REQUESTS)
    PaperRecordRequest getPaperRecordRequestById(Integer id);

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
     *
     * @return the list of all open paper record requests
     */
    @Authorized(EmrConstants.PRIVILEGE_PAPER_RECORDS_MANAGE_REQUESTS)
    List<PaperRecordRequest> getOpenPaperRecordRequests();

    /**
     * Sets the status to ASSIGNED and the assignee to the given value, for the given requests.
     * @param requests
     * @param assignee
     * @return the list that was passed in, but with assignees and status set
     * @throws IllegalStateException if any of the requests are not in the OPEN status
     */
    @Authorized(EmrConstants.PRIVILEGE_PAPER_RECORDS_MANAGE_REQUESTS)
    List<PaperRecordRequest> assignRequests(List<PaperRecordRequest> requests, Person assignee);

}