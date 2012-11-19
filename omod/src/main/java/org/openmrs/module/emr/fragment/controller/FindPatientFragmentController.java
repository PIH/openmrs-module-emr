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
package org.openmrs.module.emr.fragment.controller;

import org.apache.commons.lang.StringUtils;
import org.openmrs.Location;
import org.openmrs.Patient;
import org.openmrs.PatientIdentifier;
import org.openmrs.PatientIdentifierType;
import org.openmrs.PersonName;
import org.openmrs.module.emr.EmrProperties;
import org.openmrs.module.emr.api.EmrService;
import org.openmrs.ui.framework.SimpleObject;
import org.openmrs.ui.framework.UiUtils;
import org.openmrs.ui.framework.annotation.SpringBean;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.ArrayList;
import java.util.List;

/**
 * AJAX ssearch methods for patients
 */
public class FindPatientFragmentController {

    public List<SimpleObject> search(@RequestParam(value = "q", required = false) String query,
                                     @RequestParam(value = "term", required = false) String term,
                                     @RequestParam(value = "checkedInAt", required = false) Location checkedInAt,
                                     @SpringBean EmrService service,
                                     @SpringBean EmrProperties emrProperties,
                                     UiUtils ui) {
        if (StringUtils.isBlank(query)) {
            query = term;
        }
        List<Patient> results = service.findPatients(query, checkedInAt, 0, 100);
        return simplify(ui, emrProperties, results);
    }

    List<SimpleObject> simplify(UiUtils ui, EmrProperties emrProperties, List<Patient> results) {
        List<SimpleObject> ret = new ArrayList<SimpleObject>(results.size());
        for (Patient p : results) {
            ret.add(simplify(ui, emrProperties, p));
        }
        return ret;
    }

    SimpleObject simplify(UiUtils ui, EmrProperties emrProperties, Patient p) {
        PersonName name = p.getPersonName();
        SimpleObject preferredName = SimpleObject.fromObject(name, ui, "givenName", "middleName", "familyName", "familyName2");
        preferredName.put("fullName", name.getFullName());

        PatientIdentifierType primaryIdentifierType = emrProperties.getPrimaryIdentifierType();
        List<PatientIdentifier> primaryIdentifiers = p.getPatientIdentifiers(primaryIdentifierType);

        SimpleObject o = SimpleObject.fromObject(p, ui, "patientId", "gender", "age", "birthdate", "birthdateEstimated");
        o.put("preferredName", preferredName);
        o.put("primaryIdentifiers", SimpleObject.fromCollection(primaryIdentifiers, ui, "identifier"));

        return o;
    }

}
