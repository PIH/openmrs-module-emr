package org.openmrs.module.emr.visit;


import org.openmrs.Encounter;
import org.openmrs.Obs;
import org.openmrs.Order;
import org.openmrs.module.emr.EmrConstants;
import org.openmrs.module.emr.EmrProperties;
import org.openmrs.module.emr.consult.Diagnosis;
import org.openmrs.module.emr.consult.DiagnosisMetadata;
import org.openmrs.ui.framework.SimpleObject;
import org.openmrs.ui.framework.UiUtils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Locale;
import java.util.Set;


public class ParserEncounterIntoSimpleObjects {

    private Encounter encounter;
    private UiUtils uiUtils;
    private EmrProperties emrProperties;

    public ParserEncounterIntoSimpleObjects(Encounter encounter, UiUtils uiUtils, EmrProperties emrProperties){
        this.encounter = encounter;
        this.uiUtils = uiUtils;
        this.emrProperties = emrProperties;
    }

    public List<SimpleObject> parseOrders() {
        List<SimpleObject> orders = new ArrayList<SimpleObject>();

        for (Order order : encounter.getOrders()) {
            orders.add(SimpleObject.create("concept", uiUtils.format(order.getConcept())));
        }
        return orders;
    }

    public List<SimpleObject> parseObservations(){
        List<SimpleObject> observations = new ArrayList<SimpleObject>();

        if (!encounter.getEncounterType().getUuid().equals(EmrConstants.CONSULTATION_TYPE_UUID)){
            observations = createSimpleObjectWithObservations(encounter.getObs(), uiUtils.getLocale());
        }

        return observations;
    }

    private List<SimpleObject> createSimpleObjectWithObservations(Set<Obs> obs, Locale locale) {
        List<SimpleObject> encounterDetails = new ArrayList<SimpleObject>();

        for (Obs ob : obs) {

            SimpleObject simpleObject = SimpleObject.fromObject(ob, uiUtils, "obsId");
            simpleObject.put("question", ob.getConcept().getName(locale).getName());
            simpleObject.put("answer", ob.getValueAsString(locale));

            encounterDetails.add(simpleObject);
        }
        return encounterDetails;
    }

    public List<SimpleObject> parseDiagnoses() {
        List<SimpleObject> diagnoses = new ArrayList<SimpleObject>();

        if (encounter.getEncounterType().getUuid().equals(EmrConstants.CONSULTATION_TYPE_UUID)){

            diagnoses =  parseDiagnosesInformation(emrProperties.getDiagnosisMetadata());

        }
        return diagnoses;
    }


    private List<SimpleObject> parseDiagnosesInformation(DiagnosisMetadata diagnosisMetadata) {

        Set<Obs> groupedObservations = encounter.getObsAtTopLevel(false);

        return parseDiagnosesFromObservations(diagnosisMetadata, groupedObservations);
    }

    private List<SimpleObject> parseDiagnosesFromObservations(DiagnosisMetadata diagnosisMetadata, Set<Obs> groupedObservations) {
        List<SimpleObject> diagnoses = new ArrayList<SimpleObject>();

        for (Obs observation : groupedObservations) {

            if (diagnosisMetadata.isDiagnosis(observation)) {
                diagnoses.add(createDiagnosis(diagnosisMetadata, observation));
            } else {
                diagnoses.add(createDiagnosisComment(observation));
            }

        }

        Collections.sort(diagnoses, new Comparator<SimpleObject>() {
            @Override
            public int compare(SimpleObject o1, SimpleObject o2) {
                Integer order1 = (Integer) o1.get("order");
                Integer order2 = (Integer) o2.get("order");
                return order1- order2;
            }
        });

        return diagnoses;
    }


    private SimpleObject createDiagnosisComment(Obs observation) {
        String name = observation.getConcept().getName(uiUtils.getLocale()).getName();

        SimpleObject simpleObject = SimpleObject.fromObject(observation, uiUtils, "obsId");
        simpleObject.put("question", capitalizeString(name));
        simpleObject.put("answer", observation.getValueAsString(uiUtils.getLocale()));
        simpleObject.put("order", 99);
        return simpleObject;
    }

    private SimpleObject createDiagnosis(DiagnosisMetadata diagnosisMetadata, Obs observation) {

        SimpleObject simpleObject = SimpleObject.fromObject(observation, uiUtils, "obsId");
        Diagnosis diagnosis = diagnosisMetadata.toDiagnosis(observation);

        simpleObject.put("question", getDiagnosisQuestion(diagnosis.getOrder().name()));

        String answer = diagnosis.getDiagnosis().formatWithCode(uiUtils.getLocale(), emrProperties.getConceptSourcesForDiagnosisSearch());
        simpleObject.put("answer", answer);

        simpleObject.put("order", diagnosis.getOrder().ordinal());
        return simpleObject;
    }

    private String getDiagnosisQuestion(String name) {

        return capitalizeString(name) + " " + uiUtils.message("emr.patientDashboard.diagnosis");
    }

    private String capitalizeString(String name) {
        return name.substring(0, 1).toUpperCase() + name.substring(1).toLowerCase();
    }

}
