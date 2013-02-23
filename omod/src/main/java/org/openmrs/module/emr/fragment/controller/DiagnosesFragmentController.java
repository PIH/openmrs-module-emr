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

import org.apache.commons.beanutils.PropertyUtils;
import org.openmrs.Concept;
import org.openmrs.ConceptClass;
import org.openmrs.ConceptName;
import org.openmrs.ConceptSearchResult;
import org.openmrs.ConceptSource;
import org.openmrs.module.emr.EmrContext;
import org.openmrs.module.emr.EmrProperties;
import org.openmrs.module.emr.api.EmrService;
import org.openmrs.ui.framework.SimpleObject;
import org.openmrs.ui.framework.UiUtils;
import org.openmrs.ui.framework.annotation.SpringBean;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Locale;

/**
 *
 */
public class DiagnosesFragmentController {

    public List<SimpleObject> search(EmrContext context,
                                     UiUtils ui,
                                     @SpringBean("emrProperties") EmrProperties emrProperties,
                                     @SpringBean("emrService") EmrService emrService,
                                     @RequestParam("term") String query,
                                     @RequestParam(value = "start", defaultValue = "0") Integer start,
                                     @RequestParam(value = "size", defaultValue = "50") Integer size) throws Exception {

        ConceptClass diagnosisClass = emrProperties.getDiagnosisConceptClass();
        Locale locale = context.getUserContext().getLocale();

        List<ConceptSource> sources = emrProperties.getConceptSourcesForDiagnosisSearch();

        List<ConceptSearchResult> hits = emrService.conceptSearch(query, locale, Collections.singleton(diagnosisClass), sources, null);
        List<SimpleObject> ret = new ArrayList<SimpleObject>();
        for (ConceptSearchResult hit : hits) {
            ret.add(simplify(hit, ui, locale));
        }
        return ret;
    }

    private SimpleObject simplify(ConceptSearchResult result, UiUtils ui, Locale locale) throws Exception {
        SimpleObject simple = SimpleObject.fromObject(result, ui, "word", "conceptName.id", "conceptName.conceptNameType", "conceptName.name", "concept.id", "concept.conceptMappings.conceptMapType", "concept.conceptMappings.conceptReferenceTerm.code", "concept.conceptMappings.conceptReferenceTerm.name", "concept.conceptMappings.conceptReferenceTerm.conceptSource.name");

        Concept concept = result.getConcept();
        ConceptName conceptName = result.getConceptName();
        ConceptName preferredName = concept.getPreferredName(locale);
        PropertyUtils.setProperty(simple, "concept.preferredName", preferredName.getName());

        return simple;
    }

}
