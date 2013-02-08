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

import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

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
import org.openmrs.module.emr.EmrProperties;
import org.openmrs.module.emr.paperrecord.db.PaperRecordMergeRequestDAO;
import org.openmrs.module.emr.paperrecord.db.PaperRecordRequestDAO;
import org.openmrs.module.emr.printer.Printer;
import org.openmrs.module.emr.printer.PrinterService;
import org.openmrs.module.emr.utils.GeneralUtils;
import org.openmrs.module.idgen.service.IdentifierSourceService;
import org.springframework.transaction.annotation.Transactional;

import static org.openmrs.module.emr.paperrecord.PaperRecordRequest.PENDING_STATUSES;
import static org.openmrs.module.emr.paperrecord.PaperRecordRequest.Status;

public class PaperRecordServiceImpl extends BaseOpenmrsService implements PaperRecordService {



    private PaperRecordRequestDAO paperRecordRequestDAO;

    private PaperRecordMergeRequestDAO paperRecordMergeRequestDAO;

    private AdministrationService administrationService;

    private PatientService patientService;

    private MessageSourceService messageSourceService;

    private IdentifierSourceService identifierSourceService;

    private PrinterService printerService;

    private EmrProperties emrProperties;

    private PaperRecordLabelTemplate paperRecordLabelTemplate;


    public void setPaperRecordRequestDAO(PaperRecordRequestDAO paperRecordRequestDAO) {
        this.paperRecordRequestDAO = paperRecordRequestDAO;
    }

    public void setPaperRecordMergeRequestDAO(PaperRecordMergeRequestDAO paperRecordMergeRequestDAO) {
        this.paperRecordMergeRequestDAO = paperRecordMergeRequestDAO;
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

    public void setEmrProperties(EmrProperties emrProperties) {
        this.emrProperties = emrProperties;
    }

    public void setPaperRecordLabelTemplate(PaperRecordLabelTemplate paperRecordLabelTemplate) {
        this.paperRecordLabelTemplate = paperRecordLabelTemplate;
    }

    @Override
    public void setPrinterService(PrinterService printerService) {
        this.printerService = printerService;
    }


    @Override
    public boolean paperRecordExists(String identifier, Location location) {

        List<PatientIdentifier> identifiers = patientService.getPatientIdentifiers(identifier,
            Collections.singletonList(emrProperties.getPaperRecordIdentifierType()),
            Collections.singletonList(getMedicalRecordLocationAssociatedWith(location)), null, null);

        return identifiers != null && identifiers.size() > 0 ? true : false;
    }

    @Override
    @Transactional(readOnly = true)
    public PaperRecordRequest getPaperRecordRequestById(Integer id) {
        return paperRecordRequestDAO.getById(id);
    }

    @Override
    public PaperRecordMergeRequest getPaperRecordMergeRequestById(Integer id) {
        return paperRecordMergeRequestDAO.getById(id);
    }

    @Override
    @Transactional
    public PaperRecordRequest requestPaperRecord(Patient patient, Location location, Location requestLocation) {

        // TODO: we will have to handle the case if there is already a request for this patient's record in the "SENT" state
        // TODO: (ie, what to do if the record is already out on the floor--right now it will just create a new request)

        if (patient == null) {
            throw new IllegalStateException("Patient cannot be null");
        }

        if (location == null) {
            throw new IllegalStateException("Record Location cannot be null");
        }

        if (requestLocation == null) {
            throw new IllegalStateException("Request Location cannot be null");
        }

        // fetch the nearest medical record location (or just return the given location if it is a valid
        // medical record location)
        Location recordLocation = getMedicalRecordLocationAssociatedWith(location);

        // fetch any pending request for this patient at this location
        List<PaperRecordRequest> requests = paperRecordRequestDAO.findPaperRecordRequests(PENDING_STATUSES, patient,
            recordLocation, null, null);

        // if pending records exists, simply update that request location, don't issue a new request
        // (there should rarely be more than one pending record for a single patient, but this *may* happen if two
        // patients with pending records are merged)
        for (PaperRecordRequest request : requests) {
            request.setRequestLocation(requestLocation);
            paperRecordRequestDAO.saveOrUpdate(request);
            return request;
        }

        // if no pending record exists, create a new request
        // fetch the appropriate identifier (if it exists)
        PatientIdentifier paperRecordIdentifier = GeneralUtils.getPatientIdentifier(patient,
            emrProperties.getPaperRecordIdentifierType(), recordLocation);
        String identifier = paperRecordIdentifier != null ? paperRecordIdentifier.getIdentifier() : null;

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
        PaperRecordRequest request = null;
        if (paperRecordRequest != null) {
            return paperRecordRequestDAO.saveOrUpdate(paperRecordRequest);
        }
        return request;
    }

    @Override
    @Transactional(readOnly = true)
    public List<PaperRecordRequest> getOpenPaperRecordRequestsToPull() {
        // TODO: once we have multiple medical record locations, we will need to add location as a criteria
        return paperRecordRequestDAO.findPaperRecordRequests(Collections.singletonList(PaperRecordRequest.Status.OPEN),
            null, null, null, true);
    }

    @Override
    @Transactional(readOnly = true)
    public List<PaperRecordRequest> getOpenPaperRecordRequestsToCreate() {
        // TODO: once we have multiple medical record locations, we will need to add location as a criteria
        return paperRecordRequestDAO.findPaperRecordRequests(Collections.singletonList(PaperRecordRequest.Status.OPEN),
            null, null, null, false);
    }

    // we break this out into an external public and internal private method because we want the transaction to
    // occur within the synchronized block

    @Override
    public synchronized Map<String, List<String>> assignRequests(List<PaperRecordRequest> requests, Person assignee, Location location) throws UnableToPrintPaperRecordLabelException {

        if (requests == null) {
            throw new IllegalStateException("Requests cannot be null");
        }

        if (assignee == null) {
            throw new IllegalStateException("Assignee cannot be null");
        }

        // HACK: we need to reference the service here because an internal call won't pick up the @Transactional on the
        // internal method; we could potentially wire the bean into itself, but are unsure of that
        // see PaperRecordService.assignRequestsInternal(...  for more information
        return Context.getService(PaperRecordService.class).assignRequestsInternal(requests, assignee, location);
    }


    // HACK; note that this method must be public in order for Spring to pick up the @Transactional annotation;
    // see PaperRecordService.assignRequestsInternal(...  for more information
    @Transactional(rollbackFor = UnableToPrintPaperRecordLabelException.class)
    public  Map<String, List<String>>  assignRequestsInternal(List<PaperRecordRequest> requests, Person assignee, Location location) throws UnableToPrintPaperRecordLabelException {

        Map<String, List<String>> response = new HashMap<String, List<String>>();
        response.put("success", new LinkedList<String>());
        response.put("error", new LinkedList<String>());

        for (PaperRecordRequest request : requests) {
            // first do a sanity check, in case an identifier has been created since the request was placed
            // and verify that this request is open, or else we can't assign it
            if (patientHasPaperRecordIdentifier(request) || request.getStatus() != Status.OPEN) {
                response.get("error").add(request.getPatient().getPatientIdentifier().getIdentifier());
            }
            else {
                String identifier = request.getIdentifier();
                if (StringUtils.isBlank(identifier)) {
                    identifier = createPaperMedicalRecordNumberFor(request.getPatient(),
                            request.getRecordLocation());
                    request.setIdentifier(identifier);
                    request.updateStatus(Status.ASSIGNED_TO_CREATE);

                    // we print two labels if we are creating a new record
                    printPaperRecordLabels(request, location, 2);
                }
                else {
                    request.updateStatus(PaperRecordRequest.Status.ASSIGNED_TO_PULL);

                    // we print one label if we are pulling a record
                    printPaperRecordLabel(request, location);
                }

                request.setAssignee(assignee);
                paperRecordRequestDAO.saveOrUpdate(request);

                response.get("success").add(identifier);
            }
        }

        return response;
    }

    private boolean patientHasPaperRecordIdentifier(PaperRecordRequest request) {
        return StringUtils.isBlank(request.getIdentifier()) &&
            getPaperMedicalRecordNumberFor(request.getPatient(), request.getRecordLocation()) != null;
    }

    @Override
    @Transactional(readOnly = true)
    public List<PaperRecordRequest> getAssignedPaperRecordRequestsToPull() {
        // TODO: once we have multiple medical record locations, we will need to add location as a criteria
        return paperRecordRequestDAO.findPaperRecordRequests(
            Collections.singletonList(PaperRecordRequest.Status.ASSIGNED_TO_PULL), null, null, null, null);
    }

    @Override
    @Transactional(readOnly = true)
    public List<PaperRecordRequest> getAssignedPaperRecordRequestsToCreate() {
        // TODO: once we have multiple medical record locations, we will need to add location as a criteria
        return paperRecordRequestDAO.findPaperRecordRequests(
            Collections.singletonList(PaperRecordRequest.Status.ASSIGNED_TO_CREATE), null, null, null, null);
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
        List<PaperRecordRequest> requests = paperRecordRequestDAO.findPaperRecordRequests(PENDING_STATUSES, null, null,
            identifier, null);

        if (requests == null || requests.size() == 0) {
            return null;
        } else if (requests.size() > 1) {
            throw new IllegalStateException("Duplicate pending record requests exist with identifier " + identifier);
        } else {
            return requests.get(0);
        }

    }

    @Override
    @Transactional(readOnly = true)
    public PaperRecordRequest getSentPaperRecordRequestByIdentifier(String identifier) {
        // TODO: once we have multiple medical record locations, we will need to add location as a criteria
        List<PaperRecordRequest> requests = paperRecordRequestDAO.findPaperRecordRequests(
            Collections.singletonList(Status.SENT), null, null, identifier, null);

        if (requests == null || requests.size() == 0) {
            return null;
        } else if (requests.size() > 1) {
            throw new IllegalStateException("Duplicate sent record requests exist with identifier " + identifier);
        } else {
            return requests.get(0);
        }
    }

    @Override
    @Transactional
    public void markPaperRecordRequestAsSent(PaperRecordRequest request) {
        // I don't think we really need to do any verification here
        request.updateStatus(Status.SENT);
        savePaperRecordRequest(request);
    }

    @Override
    @Transactional
    public void markPaperRecordRequestAsCancelled(PaperRecordRequest request) {
        request.updateStatus(Status.CANCELLED);
        savePaperRecordRequest(request);
    }

    @Override
    @Transactional
    public void markPaperRecordRequestsAsReturned(String identifier)
        throws NoMatchingPaperMedicalRequestException {
        // TODO: once we have multiple medical record locations, we will need to add location as a criteria

        List<PaperRecordRequest> requests = paperRecordRequestDAO.findPaperRecordRequests(
            Collections.singletonList(Status.SENT),
            null, null, identifier, null);

        if (requests.size() == 0) {
            throw new NoMatchingPaperMedicalRequestException();
        }

        // we should never have more than one request in the sent state for the same record, but there
        // shouldn't be any harm in marking them all as closed if we do
        for (PaperRecordRequest request : requests) {
            request.updateStatus(Status.RETURNED);
            savePaperRecordRequest(request);
        }
    }

    @Override
    public void printPaperRecordLabel(PaperRecordRequest request, Location location) throws UnableToPrintPaperRecordLabelException {
       printPaperRecordLabels(request, location, 1);
    }


    @Override
    public void printPaperRecordLabels(PaperRecordRequest request, Location location, Integer count) throws UnableToPrintPaperRecordLabelException {
      printPaperRecordLabels(request.getPatient(), request.getIdentifier(), location, count);

    }

    @Override
    public void printPaperRecordLabels(Patient patient, Location location, Integer count) throws UnableToPrintPaperRecordLabelException {
        PatientIdentifier paperRecordIdentifier = GeneralUtils.getPatientIdentifier(patient, emrProperties.getPaperRecordIdentifierType(), getMedicalRecordLocationAssociatedWith(location));
        printPaperRecordLabels(patient, paperRecordIdentifier != null ? paperRecordIdentifier.getIdentifier() : null, location, count);
    }


    private void printPaperRecordLabels(Patient patient, String identifier, Location location, Integer count) throws UnableToPrintPaperRecordLabelException {
        if (count == null || count == 0) {
            return;  // just do nothing if we don't have a count
        }

        String data = paperRecordLabelTemplate.generateLabel(patient, identifier);
        String encoding = paperRecordLabelTemplate.getEncoding();

        // just duplicate the data if we are printing multiple labels
        StringBuffer dataBuffer = new StringBuffer();
        dataBuffer.append(data);

        while (count > 1) {
            dataBuffer.append(data);
            count--;
        }

        try {
            printerService.printViaSocket(dataBuffer.toString(), Printer.Type.LABEL, location, encoding);
        }
        catch (Exception e) {
            throw new UnableToPrintPaperRecordLabelException("Unable to print paper record label for patient " + patient, e);
        }
    }

    @Override
    @Transactional
    public void markPaperRecordsForMerge(PatientIdentifier preferredIdentifier, PatientIdentifier notPreferredIdentifier) {

        if (!preferredIdentifier.getIdentifierType().equals(emrProperties.getPaperRecordIdentifierType())
            || !notPreferredIdentifier.getIdentifierType().equals(emrProperties.getPaperRecordIdentifierType())) {
            throw new IllegalArgumentException("One of the passed identifiers is not a paper record identifier: "
                + preferredIdentifier + ", " + notPreferredIdentifier);
        }

        if (!preferredIdentifier.getLocation().equals(notPreferredIdentifier.getLocation())) {
            throw new IllegalArgumentException("Cannot merge two records from different locations: "
                + preferredIdentifier + ", " + notPreferredIdentifier);
        }

        // create the request
        PaperRecordMergeRequest mergeRequest = new PaperRecordMergeRequest();
        mergeRequest.setStatus(PaperRecordMergeRequest.Status.OPEN);
        mergeRequest.setPreferredPatient(preferredIdentifier.getPatient());
        mergeRequest.setNotPreferredPatient(notPreferredIdentifier.getPatient());
        mergeRequest.setPreferredIdentifier(preferredIdentifier.getIdentifier());
        mergeRequest.setNotPreferredIdentifier(notPreferredIdentifier.getIdentifier());
        mergeRequest.setRecordLocation(preferredIdentifier.getLocation());
        mergeRequest.setCreator(Context.getAuthenticatedUser());
        mergeRequest.setDateCreated(new Date());

        paperRecordMergeRequestDAO.saveOrUpdate(mergeRequest);

        // void the non-preferred identifier; we do this now (instead of when the merge is confirmed)
        // so that all new requests for records for this patient use the right identifier
        patientService.voidPatientIdentifier(notPreferredIdentifier, "voided during paper record merge");
    }

    @Override
    @Transactional
    public void markPaperRecordsAsMerged(PaperRecordMergeRequest mergeRequest) {

        // merge any pending paper record requests associated with the two records we are merging
        mergePendingPaperRecordRequests(mergeRequest);

        // if the archivist has just merged the records, we should be able to safely close out
        // any request for the not preferred record, as this record should no longer exist
        closeOutSentPaperRecordRequestsForNotPreferredRecord(mergeRequest);

        // the just mark the request as merged
        mergeRequest.setStatus(PaperRecordMergeRequest.Status.MERGED);
        paperRecordMergeRequestDAO.saveOrUpdate(mergeRequest);
    }

    @Override
    public List<PaperRecordMergeRequest> getOpenPaperRecordMergeRequests() {
        return paperRecordMergeRequestDAO.findPaperRecordMergeRequest(
            Collections.singletonList(PaperRecordMergeRequest.Status.OPEN));
    }

    // leaving this method as public so that it can be tested by integration test in mirebalais module
    public String createPaperMedicalRecordNumberFor(Patient patient, Location medicalRecordLocation) {
        if (patient == null) {
            throw new IllegalArgumentException("Patient shouldn't be null");
        }

        PatientIdentifierType paperRecordIdentifierType = emrProperties.getPaperRecordIdentifierType();
        String paperRecordId = "";

        paperRecordId = identifierSourceService.generateIdentifier(paperRecordIdentifierType,
            "generating a new dossier number");
        PatientIdentifier paperRecordIdentifier = new PatientIdentifier(paperRecordId, paperRecordIdentifierType,
            medicalRecordLocation);
        patient.addIdentifier(paperRecordIdentifier);
        patientService.savePatientIdentifier(paperRecordIdentifier);

        return paperRecordId;
    }


    protected Location getMedicalRecordLocationAssociatedWith(Location location) {

        if (location != null) {
            if (location.hasTag(emrProperties.getMedicalRecordLocationLocationTag().toString())) {
                return location;
            } else {
                return getMedicalRecordLocationAssociatedWith(location.getParentLocation());
            }
        }

        throw new IllegalStateException(
            "There is no matching location with the tag: " + emrProperties.getMedicalRecordLocationLocationTag().toString());
    }


    private String getPaperMedicalRecordNumberFor(Patient patient, Location medicalRecordLocation) {
        PatientIdentifier paperRecordIdentifier = GeneralUtils.getPatientIdentifier(patient,
            emrProperties.getPaperRecordIdentifierType(), medicalRecordLocation);
        return paperRecordIdentifier != null ? paperRecordIdentifier.getIdentifier() : null;
    }

    private void mergePendingPaperRecordRequests(PaperRecordMergeRequest mergeRequest) {

        // (note that we are not searching by patient here because the patient may have been changed during the merge)
        List<PaperRecordRequest> preferredRequests = paperRecordRequestDAO.findPaperRecordRequests(PENDING_STATUSES,
            null, mergeRequest.getRecordLocation(), mergeRequest.getPreferredIdentifier(), null);

        if (preferredRequests.size() > 1) {
            throw new IllegalStateException(
                "Duplicate pending record requests exist with identifier " + mergeRequest.getPreferredIdentifier());
        }

        List<PaperRecordRequest> notPreferredRequests = paperRecordRequestDAO.findPaperRecordRequests(PENDING_STATUSES,
            null, mergeRequest.getRecordLocation(), mergeRequest.getNotPreferredIdentifier(), null);

        if (notPreferredRequests.size() > 1) {
            throw new IllegalStateException(
                "Duplicate pending record requests exist with identifier " + mergeRequest.getNotPreferredIdentifier());
        }

        PaperRecordRequest preferredRequest = null;
        PaperRecordRequest notPreferredRequest = null;

        if (preferredRequests.size() == 1) {
            preferredRequest = preferredRequests.get(0);
        }

        if (notPreferredRequests.size() == 1) {
            notPreferredRequest = notPreferredRequests.get(0);
        }

        // if both the preferred and not-preferred records have a request, we need to
        // cancel on of them
        if (preferredRequest != null && notPreferredRequest != null) {
            // update the request location if the non-preferred  is more recent
            if (notPreferredRequest.getDateCreated().after(preferredRequest.getDateCreated())) {
                preferredRequest.setRequestLocation(notPreferredRequest.getRequestLocation());
            }

            notPreferredRequest.updateStatus(Status.CANCELLED);
            paperRecordRequestDAO.saveOrUpdate(preferredRequest);
            paperRecordRequestDAO.saveOrUpdate(notPreferredRequest);
        }

        // if there is only a non-preferred request, we need to update it with the right identifier
        if (preferredRequest == null && notPreferredRequest != null) {
            notPreferredRequest.setIdentifier(mergeRequest.getPreferredIdentifier());
            paperRecordRequestDAO.saveOrUpdate(notPreferredRequest);
        }

    }

    private void closeOutSentPaperRecordRequestsForNotPreferredRecord(PaperRecordMergeRequest mergeRequest) {
        List<PaperRecordRequest> notPreferredRequests = paperRecordRequestDAO.findPaperRecordRequests(
            Collections.singletonList(Status.SENT), null,
            mergeRequest.getRecordLocation(), mergeRequest.getNotPreferredIdentifier(), null);

        for (PaperRecordRequest notPreferredRequest : notPreferredRequests) {
            notPreferredRequest.updateStatus(Status.RETURNED);
            paperRecordRequestDAO.saveOrUpdate(notPreferredRequest);
        }
    }

}
