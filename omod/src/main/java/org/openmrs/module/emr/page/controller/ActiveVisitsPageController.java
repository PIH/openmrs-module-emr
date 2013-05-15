package org.openmrs.module.emr.page.controller;

import org.openmrs.Location;
import org.openmrs.api.LocationService;
import org.openmrs.module.emr.EmrContext;
import org.openmrs.module.emrapi.adt.AdtService;
import org.openmrs.ui.framework.annotation.SpringBean;
import org.openmrs.ui.framework.page.PageModel;

public class ActiveVisitsPageController {
	
	public void get(EmrContext emrContext, PageModel model, @SpringBean AdtService service,
	                @SpringBean("locationService") LocationService locationService) {
		
		Location sessionLocation = emrContext.getSessionLocation();
		Location visitLocation=null;
		if(sessionLocation!=null){
			visitLocation= service.getLocationThatSupportsVisits(sessionLocation);
		}
		if (visitLocation == null) {
			throw new IllegalStateException("Configuration required: no visit location found based on session location");
		}
		
		model.addAttribute("visitSummaries", service.getActiveVisits(visitLocation));
	}
	
}
