package org.openmrs.module.emr.page.controller;


import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openmrs.Location;
import org.openmrs.module.emr.EmrContext;
import org.openmrs.module.emr.api.EmrService;
import org.openmrs.module.emrapi.adt.AdtService;
import org.openmrs.ui.framework.annotation.SpringBean;
import org.openmrs.ui.framework.page.PageModel;

import java.util.ArrayList;
import java.util.List;

public class InpatientsPageController {

    private final Log log = LogFactory.getLog(getClass());

    public void get(EmrContext emrContext, PageModel model,
                    @SpringBean AdtService service,
                    @SpringBean EmrService emrService ) {

        Location sessionLocation = emrContext.getSessionLocation();
        Location visitLocation=null;
        if(sessionLocation!=null){
            visitLocation= service.getLocationThatSupportsVisits(sessionLocation);
        }
        if (visitLocation == null) {
            throw new IllegalStateException("Configuration required: no visit location found based on session location");
        }
        List<Object[]> inpatientsList = emrService.getInpatientsList(visitLocation);
        model.addAttribute("inpatientsList", inpatientsList);
    }
}
