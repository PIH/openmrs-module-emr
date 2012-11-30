package org.openmrs.module.emr.page.controller;

import org.openmrs.Concept;
import org.openmrs.ConceptAnswer;
import org.openmrs.ConceptName;
import org.openmrs.Location;
import org.openmrs.api.ConceptService;
import org.openmrs.api.LocationService;
import org.openmrs.api.context.Context;
import org.openmrs.ui.framework.SimpleObject;
import org.openmrs.ui.framework.UiUtils;
import org.openmrs.ui.framework.annotation.SpringBean;
import org.openmrs.ui.framework.page.PageModel;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class RetrospectiveCheckinPageController {

    public void controller(@SpringBean("locationService") LocationService locationService,
                           @SpringBean("conceptService") ConceptService conceptService,
                           UiUtils ui,
                           PageModel model) {

        Concept amountPaidConcept = conceptService.getConceptByUuid("5d1bc5de-6a35-4195-8631-7322941fe528");
        model.addAttribute("locations", ui.toJson(getLocations(locationService)));
        model.addAttribute("paymentReasons", ui.toJson(getPaymentReasons(conceptService)));
        model.addAttribute("paymentAmounts", ui.toJson(getPossiblePaymentAmounts()));
    }

    private List<SimpleObject> getPossiblePaymentAmounts() {
        List<SimpleObject> items = new ArrayList<SimpleObject>();
        SimpleObject exempt = new SimpleObject();
        exempt.put("value", 0);
        exempt.put("label", "Exempt");

        SimpleObject fiftyGourdes = new SimpleObject();
        fiftyGourdes.put("value", 50);
        fiftyGourdes.put("label", "50 Gourdes");

        SimpleObject hundredGourdes = new SimpleObject();
        hundredGourdes.put("value", 100);
        hundredGourdes.put("label", "100 Gourdes");

        items.add(hundredGourdes); items.add(fiftyGourdes); items.add(exempt);
        return items;
    }

    private List<SimpleObject> getPaymentReasons(ConceptService conceptService) {
        List<SimpleObject> items = new ArrayList<SimpleObject>();
        Concept paymentReason = conceptService.getConceptByUuid("36ba7721-fae0-4da4-aef2-7e476cc04bdf");
        Collection<ConceptAnswer> paymentReasonAnswers = paymentReason.getAnswers();
        for(ConceptAnswer reason : paymentReasonAnswers) {
            Concept answerConcept = reason.getAnswerConcept();
            SimpleObject item = new SimpleObject();
            item.put("value", answerConcept.getConceptId());
            item.put("label", answerConcept.getName().getName());
            items.add(item);
        }
        return items;
    }

    private List<SimpleObject> getLocations(LocationService locationService) {
        List<SimpleObject> items = new ArrayList<SimpleObject>();
        List<Location> locations = locationService.getAllLocations(false);
        for (Location location: locations) {
            SimpleObject item = new SimpleObject();
            item.put("value", location.getLocationId());
            item.put("label", location.getName());
            items.add(item);
        }
        return items;

    }
}
