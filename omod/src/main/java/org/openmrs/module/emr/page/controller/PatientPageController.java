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

import org.openmrs.Order;
import org.openmrs.Patient;
import org.openmrs.api.OrderService;
import org.openmrs.ui.framework.annotation.SpringBean;
import org.openmrs.ui.framework.page.PageModel;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.Collections;
import java.util.List;


/**
 *
 */
public class PatientPageController {

	public void controller(@RequestParam("patientId") Patient patient,
	                       PageModel model,
                           @SpringBean("orderService") OrderService orderService) {
        List<Order> orders = orderService.getOrdersByPatient(patient);
        model.addAttribute("patient", patient);
        model.addAttribute("orders", orders);
    }

}
