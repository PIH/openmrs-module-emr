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

package org.openmrs.module.emr.fragment.controller;

import org.openmrs.module.emr.api.EmrService;
import org.openmrs.module.emr.domain.RadiologyRequisition;
import org.openmrs.module.emr.radiology.RadiologyService;
import org.openmrs.ui.framework.annotation.BindParams;
import org.openmrs.ui.framework.annotation.SpringBean;
import org.openmrs.ui.framework.fragment.action.FragmentActionResult;
import org.openmrs.ui.framework.fragment.action.SuccessResult;
import org.springframework.web.bind.annotation.RequestParam;

public class RadiologyRequisitionFragmentController {

    public FragmentActionResult orderXray(@BindParams RadiologyRequisition requisition,
                                          @SpringBean RadiologyService radiologyService) {
        if (requisition.getStudies().size() == 0) {
            throw new IllegalArgumentException("No studies");
        }

        radiologyService.placeRadiologyRequisition(requisition, null);
        return new SuccessResult();
    }

}
