/**
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
package org.openmrs.module.emr.page.controller;

import org.openmrs.Location;
import org.openmrs.module.emr.adt.AdtService;
import org.openmrs.ui.framework.annotation.SpringBean;
import org.openmrs.ui.framework.page.PageModel;
import org.springframework.web.bind.annotation.RequestParam;


/**
 *
 */
public class FindPatientPageController {
	
	public void controller(PageModel model,
                           @RequestParam(value = "checkedInAt", required = false) Location checkedInAt,
                           @SpringBean AdtService adtService) {
        model.addAttribute("checkedInAt", checkedInAt);
        model.addAttribute("locationsThatSupportVisits", adtService.getAllLocationsThatSupportVisits());
	}
	
}
