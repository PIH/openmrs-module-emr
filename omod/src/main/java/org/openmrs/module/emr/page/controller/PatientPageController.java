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

import org.openmrs.Patient;
import org.openmrs.api.OrderService;
import org.openmrs.module.emr.EmrContext;
import org.openmrs.module.emr.patient.PatientDomainWrapper;
import org.openmrs.module.emr.task.ExtensionPoint;
import org.openmrs.module.emr.task.TaskService;
import org.openmrs.module.emr.utils.GeneralUtils;
import org.openmrs.ui.framework.annotation.InjectBeans;
import org.openmrs.ui.framework.annotation.SpringBean;
import org.openmrs.ui.framework.page.PageModel;
import org.springframework.web.bind.annotation.RequestParam;

import static org.openmrs.module.emr.task.ExtensionPoint.ACTIVE_VISITS;
import static org.openmrs.module.emr.task.ExtensionPoint.GLOBAL_ACTIONS;


/**
 *
 */
public class PatientPageController {

	public void controller(@RequestParam("patientId") Patient patient,
                           @RequestParam(value = "tab", defaultValue = "visits") String selectedTab,
                           EmrContext emrContext,
	                       PageModel model,
                           @InjectBeans PatientDomainWrapper patientDomainWrapper,
                           @SpringBean("orderService") OrderService orderService,
                           @SpringBean("taskService") TaskService taskService) {

        patientDomainWrapper.setPatient(patient);
        model.addAttribute("patient", patientDomainWrapper);
        model.addAttribute("orders", orderService.getOrdersByPatient(patient));
        model.addAttribute("availableTasks", taskService.getAvailableTasksByExtensionPoint(emrContext, GLOBAL_ACTIONS.getValue()));
        model.addAttribute("activeVisitTasks", taskService.getAvailableTasksByExtensionPoint(emrContext, ACTIVE_VISITS.getValue()));
        model.addAttribute("selectedTab", selectedTab);
        model.addAttribute("addressHierarchyLevels", GeneralUtils.getAddressHierarchyLevels());
    }

}
