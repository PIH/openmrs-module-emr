package org.openmrs.module.emr.page.controller;

import org.openmrs.Location;
import org.openmrs.api.LocationService;
import org.openmrs.module.emr.adt.AdtService;
import org.openmrs.ui.framework.annotation.SpringBean;
import org.openmrs.ui.framework.page.PageModel;

public class ActiveVisitsPageController {
	
	public void get(PageModel model, @SpringBean AdtService service,
	                @SpringBean("locationService") LocationService locationService) {
		
		// TEMPORARY HACK UNTIL WE HAVE A SESSION LOCATION TO START FROM=
		Location visitLocation = null;
		for (Location candidate : locationService.getAllLocations(false)) {
			Location test = service.getLocationThatSupportsVisits(candidate);
			if (test != null) {
				visitLocation = test;
				break;
			}
		}
		if (visitLocation == null) {
			throw new IllegalStateException("Configuration required");
		}
		
		model.addAttribute("visitSummaries", service.getActiveVisitSummaries(visitLocation));
	}
	
}
