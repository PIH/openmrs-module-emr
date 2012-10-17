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

package org.openmrs.module.emr.api.impl;

import org.openmrs.Location;
import org.openmrs.Patient;
import org.openmrs.api.AdministrationService;
import org.openmrs.module.emr.api.PaperRecordService;
import org.openmrs.module.emr.api.db.PaperRecordRequestDAO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.transaction.annotation.Transactional;

public class PaperRecordServiceImpl implements PaperRecordService {

    private PaperRecordRequestDAO paperRecordRequestDAO;

    @Autowired
    @Qualifier("adminService")
    private AdministrationService adminService;

    public void setDao(PaperRecordRequestDAO paperRecordRequestDAO) {
        this.paperRecordRequestDAO = paperRecordRequestDAO;
    }

    @Override
    @Transactional
    public void requestPaperRecord(Patient patient, Location medicalRecordLocation, Location requestLocation) {

        // fetch the patient identifier we want to use

        // TODO: change this to make sure it handles getting the identifier from the right location
        //patient.getPatientIdentifier(administrationService)


        // TODO: handle null case if no patient identifier found

    }
}
