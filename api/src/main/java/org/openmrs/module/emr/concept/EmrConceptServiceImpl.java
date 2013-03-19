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

package org.openmrs.module.emr.concept;

import org.openmrs.Concept;
import org.openmrs.ConceptReferenceTerm;
import org.openmrs.api.impl.BaseOpenmrsService;
import org.openmrs.module.emr.EmrProperties;

import java.util.Arrays;
import java.util.List;

/**
 *
 */
public class EmrConceptServiceImpl extends BaseOpenmrsService implements EmrConceptService {

    private EmrConceptDAO dao;

    EmrProperties emrProperties;

    public void setDao(EmrConceptDAO dao) {
        this.dao = dao;
    }

    public void setEmrProperties(EmrProperties emrProperties) {
        this.emrProperties = emrProperties;
    }

    @Override
    public List<Concept> getConceptsSameOrNarrowerThan(ConceptReferenceTerm term) {
        if (term == null) {
            throw new IllegalArgumentException("term is required");
        }
        return dao.getConceptsMappedTo(Arrays.asList(emrProperties.getSameAsConceptMapType(), emrProperties.getNarrowerThanConceptMapType()), term);
    }

}
