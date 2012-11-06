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

package org.openmrs.module.emr.page.controller;

import org.codehaus.jackson.JsonParseException;
import org.codehaus.jackson.map.ObjectMapper;
import org.openmrs.Patient;
import org.openmrs.module.emr.task.EncounterWizardTaskDescriptor;
import org.openmrs.module.emr.task.TaskService;
import org.openmrs.ui.framework.SimpleObject;
import org.openmrs.ui.framework.annotation.SpringBean;
import org.openmrs.ui.framework.page.PageModel;
import org.springframework.web.bind.annotation.RequestParam;

import java.io.IOException;

/**
 *
 */
public class EncounterWizardPageController {

    public void get(@RequestParam("patientId") Patient patient,
                    @RequestParam("task") String taskId,
                    @SpringBean TaskService taskService,
                    PageModel model) throws IOException {

        EncounterWizardTaskDescriptor task = (EncounterWizardTaskDescriptor) taskService.getTask(taskId);
        model.addAttribute("wizardConfig", new ObjectMapper().readValue(task.getConfigurationAsJson(), SimpleObject.class));
        model.addAttribute("wizardConfigString", task.getConfigurationAsJson());
    }

}
