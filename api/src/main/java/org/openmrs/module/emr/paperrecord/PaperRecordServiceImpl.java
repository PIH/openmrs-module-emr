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

import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openmrs.Location;
import org.openmrs.Patient;
import org.openmrs.PatientIdentifier;
import org.openmrs.PatientIdentifierType;
import org.openmrs.Person;
import org.openmrs.api.AdministrationService;
import org.openmrs.api.PatientService;
import org.openmrs.api.context.Context;
import org.openmrs.api.impl.BaseOpenmrsService;
import org.openmrs.messagesource.MessageSourceService;
import org.openmrs.module.emr.EmrConstants;
import org.openmrs.module.emr.paperrecord.db.PaperRecordRequestDAO;
import org.openmrs.module.emr.printer.Printer;
import org.openmrs.module.emr.printer.PrinterService;
import org.openmrs.module.emr.utils.GeneralUtils;
import org.openmrs.module.idgen.service.IdentifierSourceService;
import org.springframework.transaction.annotation.Transactional;

import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.List;

import static org.openmrs.module.emr.paperrecord.PaperRecordRequest.Status;

public class PaperRecordServiceImpl extends BaseOpenmrsService implements PaperRecordService {

    private final Log log = LogFactory.getLog(getClass());

    private PaperRecordRequestDAO paperRecordRequestDAO;

    private AdministrationService administrationService;

    private PatientService patientService;

    private MessageSourceService messageSourceService;

    private IdentifierSourceService identifierSourceService;

    private PrinterService printerService;

    private PaperRecordLabelTemplate paperRecordLabelTemplate;


    public void setPaperRecordRequestDAO(PaperRecordRequestDAO paperRecordRequestDAO) {
        this.paperRecordRequestDAO = paperRecordRequestDAO;
    }

    public void setMessageSourceService(MessageSourceService messageSourceService) {
        this.messageSourceService = messageSourceService;
    }
    
    public void setAdministrationService(AdministrationService administrationService) {
    	this.administrationService = administrationService;
    }

    public void setIdentifierSourceService(IdentifierSourceService identifierSourceService) {
        this.identifierSourceService = identifierSourceService;
    }

    public void setPatientService(PatientService patientService) {
        this.patientService = patientService;
    }

    public void setPrinterService(PrinterService printerService) {
        this.printerService = printerService;
    }

    public void setPaperRecordLabelTemplate(PaperRecordLabelTemplate paperRecordLabelTemplate) {
        this.paperRecordLabelTemplate = paperRecordLabelTemplate;
    }

    @Override
    @Transactional(readOnly = true)
    public PaperRecordRequest getPaperRecordRequestById(Integer id) {
        return paperRecordRequestDAO.getById(id);
    }

    // TODO: these could probably be refactored into a single method

    @Override
    @Transactional
    public PaperRecordRequest requestPaperRecord(Patient patient, Location recordLocation, Location requestLocation) {

        // TODO: we will have to handle the case if there is already a request for this patient's record in the "SENT" state
        // TODO: (ie, what to do if the record is already out on the floor--right now it will just create a new request)

        if (patient == null) {
            throw new IllegalStateException("Patient cannot be null");
        }

        if (recordLocation == null) {
            throw new IllegalStateException("Record Location cannot be null");
        }

        if (requestLocation == null) {
            throw new IllegalStateException("Request Location cannot be null");
        }

        // TODO: might be valuable to switch this functionality so that if the recordLocation is not tagged as a
        // TODO: medical record location, we search up the hierarchy until we find a location with that tag (so that the calling
        // TODO: could just pass the request location, and this method would find the appropriate medical record location based on that location)

        // fetch the appropriate identifier (if it exists)
        PatientIdentifier paperRecordIdentifier = GeneralUtils.getPatientIdentifier(patient, getPaperRecordIdentifierType(), recordLocation);
        String identifier = paperRecordIdentifier != null ? paperRecordIdentifier.getIdentifier() : null;

        // see if there is an active request for this patient at this location
        List<PaperRecordRequest> requests = paperRecordRequestDAO.findPaperRecordRequests(Arrays.asList(Status.OPEN, Status.ASSIGNED_TO_PULL, Status.ASSIGNED_TO_CREATE), patient, recordLocation, null, null);

        if (requests.size() > 1) {
            // this should not be allowed, but it could possibility happen if you merge two patients that both have
            // open paper record requests for the same location; we should fix this when we handle story #186
            log.warn("Duplicate active record requests exist for patient " + patient);
        }

        // if an active record exists, simply update that request location, don't issue a new requeset
        if (requests.size() > 0) {   // TODO: change this to size() == 1 once we  implement story #186 and can guarantee that there won't be multiple requests (see comment above)
            PaperRecordRequest request = requests.get(0);
            request.setRequestLocation(requestLocation);
            paperRecordRequestDAO.saveOrUpdate(request);
            return request;
        }

        // if no active record exists, create a new request
        PaperRecordRequest request = new PaperRecordRequest();
        request.setCreator(Context.getAuthenticatedUser());
        request.setDateCreated(new Date());
        request.setIdentifier(identifier);
        request.setRecordLocation(recordLocation);
        request.setPatient(patient);
        request.setRequestLocation(requestLocation);

        paperRecordRequestDAO.saveOrUpdate(request);

        return request;
    }

    @Override
    @Transactional
    public PaperRecordRequest savePaperRecordRequest(PaperRecordRequest paperRecordRequest) {
	    PaperRecordRequest request =null;
    	if(paperRecordRequest!=null){
    		return paperRecordRequestDAO.saveOrUpdate(paperRecordRequest);
    	}
	    return request;
    }

	@Override
    @Transactional(readOnly = true)
    public List<PaperRecordRequest> getOpenPaperRecordRequestsToPull() {
        // TODO: once we have multiple medical record locations, we will need to add location as a criteria
        return paperRecordRequestDAO.findPaperRecordRequests(Collections.singletonList(PaperRecordRequest.Status.OPEN), null, null, null, true);
    }

    @Override
    @Transactional(readOnly = true)
    public List<PaperRecordRequest> getOpenPaperRecordRequestsToCreate() {
        // TODO: once we have multiple medical record locations, we will need to add location as a criteria
        return paperRecordRequestDAO.findPaperRecordRequests(Collections.singletonList(PaperRecordRequest.Status.OPEN), null, null, null, false);
    }

    @Override
    @Transactional
    public synchronized List<PaperRecordRequest> assignRequests(List<PaperRecordRequest> requests, Person assignee) {

        if (requests == null) {
            throw new IllegalStateException("Requests cannot be null");
        }

        if (assignee == null) {
            throw new IllegalStateException("Assignee cannot be null");
        }

        // first verify that all of these requests are open, or else we can't assign them
        for (PaperRecordRequest request : requests) {
            if (request.getStatus() != PaperRecordRequest.Status.OPEN) {
                throw new IllegalStateException("Cannot assign a request that is not open");
            }
        }

        for (PaperRecordRequest request : requests) {
             // first do a sanity check, in case an identifier has been created since the request was placed
            if (StringUtils.isBlank(request.getIdentifier())) {
               request.setIdentifier(getPaperMedicalRecordNumberFor(request.getPatient(), request.getRecordLocation()));
            }

            // if there is still no identifier, assign an identifier and mark it as to create, otherwise mark to pull
            if (StringUtils.isBlank(request.getIdentifier())) {
                String identifier = createPaperMedicalRecordNumberFor(request.getPatient(), request.getRecordLocation());
                request.setIdentifier(identifier);
                request.updateStatus(PaperRecordRequest.Status.ASSIGNED_TO_CREATE);
            }
            else {
                request.updateStatus(PaperRecordRequest.Status.ASSIGNED_TO_PULL);
            }

            // set the assignee and save the record
            request.setAssignee(assignee);
            paperRecordRequestDAO.saveOrUpdate(request);
        }

        return requests;
    }

    @Override
    @Transactional(readOnly = true)
    public List<PaperRecordRequest> getAssignedPaperRecordRequestsToPull() {
        // TODO: once we have multiple medical record locations, we will need to add location as a criteria
        return paperRecordRequestDAO.findPaperRecordRequests(Collections.singletonList(PaperRecordRequest.Status.ASSIGNED_TO_PULL), null, null, null, null);
    }

    @Override
    @Transactional(readOnly = true)
    public List<PaperRecordRequest> getAssignedPaperRecordRequestsToCreate() {
        // TODO: once we have multiple medical record locations, we will need to add location as a criteria
        return paperRecordRequestDAO.findPaperRecordRequests(Collections.singletonList(PaperRecordRequest.Status.ASSIGNED_TO_CREATE), null, null, null, null);
    }

    @Override
    @Transactional(readOnly = true)
    public List<PaperRecordRequest> getPaperRecordRequestsByPatient(Patient patient) {
	    return paperRecordRequestDAO.findPaperRecordRequests(null, patient, null, null, null);
    }

    @Override
    @Transactional(readOnly = true)
    public PaperRecordRequest getPendingPaperRecordRequestByIdentifier(String identifier) {
        // TODO: once we have multiple medical record locations, we will need to add location as a criteria
        List<PaperRecordRequest> requests = paperRecordRequestDAO.findPaperRecordRequests(Arrays.asList(Status.OPEN, Status.ASSIGNED_TO_PULL, Status.ASSIGNED_TO_CREATE), null, null, identifier, null);

        if (requests == null || requests.size() == 0) {
            return null;
        }
        else if (requests.size() > 1) {
            // TODO: we may run into this case until we handle merging properly
            throw new IllegalStateException("Duplicate pending record requests exist with identifier " + identifier);
        }
        else {
            return requests.get(0);
        }

    }

    @Override
    @Transactional(readOnly = true)
    public PaperRecordRequest getSentPaperRecordRequestByIdentifier(String identifier) {
        // TODO: once we have multiple medical record locations, we will need to add location as a criteria
        List<PaperRecordRequest> requests = paperRecordRequestDAO.findPaperRecordRequests(Collections.singletonList(Status.SENT), null, null, identifier, null);

        if (requests == null || requests.size() == 0) {
            return null;
        }
        else if (requests.size() > 1) {
            // TODO: we may run into this case until we handle merging properly
            throw new IllegalStateException("Duplicate sent record requests exist with identifier " + identifier);
        }
        else {
            return requests.get(0);
        }
    }

    @Override
    @Transactional
    public void markPaperRequestRequestAsSent(PaperRecordRequest request) {
        // I don't think we really need to do any verification here
        request.updateStatus(Status.SENT);
        savePaperRecordRequest(request);
    }

    @Override
    public Boolean printPaperRecordLabel(PaperRecordRequest request, Location location) {
        String data = paperRecordLabelTemplate.generateLabel(request);
        String encoding = paperRecordLabelTemplate.getEncoding();
        return printerService.printViaSocket(data, Printer.Type.LABEL, location, encoding);
    }

    // leaving this method as public so that it can be tested by integration test in mirebalais module
    public String createPaperMedicalRecordNumberFor(Patient patient, Location medicalRecordLocation) {
        if (patient == null){
            throw new IllegalArgumentException("Patient shouldn't be null");
        }

        if (StringUtils.isNotBlank(getPaperMedicalRecordNumberFor(patient, medicalRecordLocation))) {
            // TODO: we probably want to actually throw an exception here, but we should wait until this method is removed from patient registration and made protected
            //throw new IllegalStateException("Cannot create paper record number for patient.  Paper record number already exists for patient:" + patient);
            return "";
        }

        PatientIdentifierType paperRecordIdentifierType = getPaperRecordIdentifierType();
        String paperRecordId = "";

        paperRecordId = identifierSourceService.generateIdentifier(paperRecordIdentifierType, "generating a new dossier number");
        PatientIdentifier paperRecordIdentifier = new PatientIdentifier(paperRecordId, paperRecordIdentifierType, medicalRecordLocation);
        patient.addIdentifier(paperRecordIdentifier);
        patientService.savePatientIdentifier(paperRecordIdentifier);

        return paperRecordId;
    }

    protected String getPaperMedicalRecordNumberFor(Patient patient, Location medicalRecordLocation) {
        PatientIdentifier paperRecordIdentifier = GeneralUtils.getPatientIdentifier(patient, getPaperRecordIdentifierType(), medicalRecordLocation);
        return paperRecordIdentifier != null ? paperRecordIdentifier.getIdentifier() : "";
    }

    protected PatientIdentifierType getPaperRecordIdentifierType() {
        String uuid = administrationService.getGlobalProperty(EmrConstants.GP_PAPER_RECORD_IDENTIFIER_TYPE);
        PatientIdentifierType paperRecordIdentifierType = patientService.getPatientIdentifierTypeByUuid(uuid);
        if (paperRecordIdentifierType == null) {
            throw new IllegalStateException("Configuration required: " + EmrConstants.GP_PAPER_RECORD_IDENTIFIER_TYPE);
        }
        return paperRecordIdentifierType;
    }

}
