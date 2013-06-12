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

package org.openmrs.module.emr.page.controller.consult;

import org.codehaus.jackson.JsonNode;
import org.codehaus.jackson.map.ObjectMapper;
import org.openmrs.Concept;
import org.openmrs.Location;
import org.openmrs.Obs;
import org.openmrs.Patient;
import org.openmrs.Provider;
import org.openmrs.api.ConceptService;
import org.openmrs.api.ProviderService;
import org.openmrs.module.appframework.domain.Extension;
import org.openmrs.module.appframework.service.AppFrameworkService;
import org.openmrs.module.emr.EmrConstants;
import org.openmrs.module.emr.consult.ConsultNote;
import org.openmrs.module.emr.consult.ConsultService;
import org.openmrs.module.emr.utils.ObservationFactory;
import org.openmrs.module.emrapi.diagnosis.CodedOrFreeTextAnswer;
import org.openmrs.module.emrapi.diagnosis.Diagnosis;
import org.openmrs.module.emrapi.disposition.DispositionFactory;
import org.openmrs.ui.framework.SimpleObject;
import org.openmrs.ui.framework.UiUtils;
import org.openmrs.ui.framework.annotation.MethodParam;
import org.openmrs.ui.framework.annotation.SpringBean;
import org.openmrs.ui.framework.page.PageModel;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.RequestParam;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

public class ConsultPageController {

    private static final String CONSULT_NOTE_CONFIG_EXTENSION = "org.openmrs.referenceapplication.consult.note.config";

    public void get(@RequestParam("patientId") Patient patient,
                    @SpringBean DispositionFactory factory,
                    @SpringBean("conceptService") ConceptService conceptService,
                    @SpringBean("providerService") ProviderService providerService,
                    @MethodParam("getConfigFromExtension") Extension config,
                    PageModel model) throws IOException {

        List<Map<String, Object>> additionalObservationsConfig = (List<Map<String, Object>>) config.getExtensionParams().get(
                "additionalObservationsConfig");

        if (additionalObservationsConfig == null) {
            additionalObservationsConfig = new LinkedList<Map<String, Object>>();
        }

        model.addAttribute("consultNoteConfig", config.getId());
        model.addAttribute("title", config.getExtensionParams().get("title"));
        model.addAttribute("dispositions", factory.getDispositions());
        model.addAttribute("additionalObservationsConfig", additionalObservationsConfig);
        model.addAttribute("providers", getProviders(providerService));
        model.addAttribute("conceptService", conceptService);
    }

    private List<SimpleObject> getProviders(ProviderService providerService) {
        List<SimpleObject> items = new ArrayList<SimpleObject>();
        List<Provider> clerks = providerService.getAllProviders(false);
        for (Provider clerk : clerks) {
            SimpleObject item = new SimpleObject();
            item.put("value", clerk.getProviderId());
            item.put("label", clerk.getName());
            items.add(item);
        }

        return items;
    }

    public Extension getConfigFromExtension(@RequestParam("config") String configExtensionId,
                                            @SpringBean("appFrameworkService") AppFrameworkService appFrameworkService) {
        Extension config = null;
        List<Extension> extensions = appFrameworkService.getExtensionsForCurrentUser(CONSULT_NOTE_CONFIG_EXTENSION);
        for (Extension extension : extensions) {
            if (extension.getId().equals(configExtensionId)) {
                config = extension;
                break;
            }
        }

        return config;
    }

    public String post(@RequestParam("patientId") Patient patient,
                       @RequestParam("diagnosis") List<String> diagnoses, // each string is json, like {"certainty":"PRESUMED","diagnosisOrder":"PRIMARY","diagnosis":"ConceptName:840"}
                       @RequestParam(required = false, value = "disposition") String disposition, // a unique key for a disposition
                       @RequestParam(required = false, value = "freeTextComments") String freeTextComments,
                       @RequestParam("consultProviderId") Provider consultProvider,
                       @RequestParam("consultLocationId") Location consultLocation,
                       @RequestParam("consultDate") Date date,
                       @MethodParam("getConfigFromExtension") Extension config,
                       @SpringBean("consultService") ConsultService consultService,
                       @SpringBean("conceptService") ConceptService conceptService,
                       @SpringBean DispositionFactory dispositionFactory,
                       HttpSession httpSession,
                       HttpServletRequest request,
                       UiUtils ui) throws IOException {

        ConsultNote consultNote = new ConsultNote();
        consultNote.setPatient(patient);
        addDiagnosis(consultNote, diagnoses, conceptService);

        List<Map<String, Object>> additionalObservationsConfig = (List<Map<String, Object>>) config.getExtensionParams().get(
                "additionalObservationsConfig");

        if (additionalObservationsConfig != null) {
            addAdditionalObs(consultNote, request, additionalObservationsConfig, conceptService);
        }

        if (StringUtils.hasText(freeTextComments)) {
            consultNote.setComments(freeTextComments);
        }

        consultNote.setClinician(consultProvider);
        consultNote.setEncounterLocation(consultLocation);
        consultNote.setEncounterDate(date);

        if (StringUtils.hasText(disposition)) {
            consultNote.setDisposition(dispositionFactory.getDispositionByUniqueId(disposition));
            consultNote.setDispositionParameters((Map<String, String[]>) request.getParameterMap());
        }

        consultService.saveConsultNote(consultNote);

        String successMessage = (String) config.getExtensionParams().get("successMessage");
        if (successMessage != null) {
            httpSession.setAttribute(EmrConstants.SESSION_ATTRIBUTE_INFO_MESSAGE,
                    ui.message(successMessage.toString(), ui.format(patient)));
            httpSession.setAttribute(EmrConstants.SESSION_ATTRIBUTE_TOAST_MESSAGE, "true");
        }

        return "redirect:" + ui.pageLink("coreapps", "patientdashboard/patientDashboard", SimpleObject.create("patientId", patient.getId()));
    }

    private void addAdditionalObs(ConsultNote consultNote, HttpServletRequest request, List<Map<String, Object>> additionalObservationsConfig, ConceptService conceptService) {
        for (Map<String, Object> config : additionalObservationsConfig) {
            String value = request.getParameter((String) config.get("formFieldName"));
            if (value != null && !value.isEmpty()) {
                consultNote.addAdditionalObs(
                        createObservation(conceptService, value, (String) config.get("concept")));
            }
        }
    }

    private Obs createObservation(ConceptService conceptService, String value, String concept1) {
        Concept concept = conceptService.getConceptByUuid(concept1);
        String datatype = concept.getDatatype().getName().toUpperCase();
        return ObservationFactory.valueOf(datatype).createObs(conceptService, concept, value);
    }

    private void addDiagnosis(ConsultNote consultNote, List<String> diagnoses, ConceptService conceptService) {
        for (String diagnosisJson : diagnoses) {
            Diagnosis diagnosis;
            try {
                diagnosis = parseDiagnosisJson(diagnosisJson, conceptService);
            } catch (Exception e) {
                throw new RuntimeException("Invalid submitted diagnosis: " + diagnosisJson, e);
            }
            if (diagnosis.getOrder().equals(Diagnosis.Order.PRIMARY)) {
                consultNote.addPrimaryDiagnosis(diagnosis);
            } else {
                consultNote.addSecondaryDiagnosis(diagnosis);
            }
        }
    }

    /**
     * @param diagnosisJson  like {"certainty":"PRESUMED","diagnosisOrder":"PRIMARY","diagnosis":"ConceptName:840"}
     * @param conceptService
     * @return
     * @throws Exception
     */
    private Diagnosis parseDiagnosisJson(String diagnosisJson, ConceptService conceptService) throws Exception {
        JsonNode node = new ObjectMapper().readTree(diagnosisJson);

        CodedOrFreeTextAnswer answer = new CodedOrFreeTextAnswer(node.get("diagnosis").getTextValue(), conceptService);
        Diagnosis.Order diagnosisOrder = Diagnosis.Order.valueOf(node.get("diagnosisOrder").getTextValue());
        Diagnosis.Certainty certainty = Diagnosis.Certainty.valueOf(node.get("certainty").getTextValue());

        Diagnosis diagnosis = new Diagnosis(answer, diagnosisOrder);
        diagnosis.setCertainty(certainty);

        return diagnosis;
    }

}
