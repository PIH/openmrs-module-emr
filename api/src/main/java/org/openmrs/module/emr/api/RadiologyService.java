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
 */package org.openmrs.module.emr.api;

import org.openmrs.Concept;
import org.openmrs.Order;
import org.openmrs.Patient;
import org.openmrs.api.OpenmrsService;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Transactional
public interface RadiologyService extends OpenmrsService {

    /**
     * @return list of orderable radiology tests, as defined by a global property
     */
    List<Concept> getRadiologyOrderables();

    /**
     * Creates an order for the given patient and radiology orderable
     *
     * @param p
     * @param cxr
     * @return the created order
     */
    Order placeRadiologyOrder(Patient p, Concept cxr);

}
