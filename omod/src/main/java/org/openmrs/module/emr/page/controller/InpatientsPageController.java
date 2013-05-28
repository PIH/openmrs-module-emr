package org.openmrs.module.emr.page.controller;

import org.openmrs.Location;
import org.openmrs.api.LocationService;
import org.openmrs.module.emr.EmrContext;
import org.openmrs.module.emrapi.adt.AdtService;
import org.openmrs.ui.framework.annotation.SpringBean;
import org.openmrs.ui.framework.page.PageModel;
import org.springframework.web.bind.annotation.RequestParam;

public class InpatientsPageController {
    public void get(EmrContext emrContext, PageModel model,
                    @RequestParam(value = "ward", required = false) Location ward,
                    @SpringBean AdtService service,
                    @SpringBean("locationService") LocationService locationService) {

        Location sessionLocation = emrContext.getSessionLocation();
        Location visitLocation=null;
        if(sessionLocation!=null){
            visitLocation= service.getLocationThatSupportsVisits(sessionLocation);
        }
        if (visitLocation == null) {
            throw new IllegalStateException("Configuration required: no visit location found based on session location");
        }

        model.addAttribute("ward", ward);
        model.addAttribute("visitSummaries", service.getInpatientVisits(visitLocation, ward));
    }
}
