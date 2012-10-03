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

import org.openmrs.Location;
import org.openmrs.Patient;
import org.openmrs.PersonName;
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
                                     @RequestParam(value = "checkedInAt", required = false) Location checkedInAt,
                                     @SpringBean EmrService service,
                                     UiUtils ui) {
        List<Patient> results = service.findPatients(query, checkedInAt, 0, 100);
        return simplify(ui, results);
    }

    List<SimpleObject> simplify(UiUtils ui, List<Patient> results) {
        List<SimpleObject> ret = new ArrayList<SimpleObject>(results.size());
        for (Patient p : results) {
            ret.add(simplify(ui, p));
        }
        return ret;
    }

    SimpleObject simplify(UiUtils ui, Patient p) {
        PersonName name = p.getPersonName();
        SimpleObject preferredName = SimpleObject.fromObject(name, ui, "givenName", "middleName", "familyName", "familyName2");
        preferredName.put("fullName", name.getFullName());

        SimpleObject o = SimpleObject.fromObject(p, ui, "patientId", "gender", "age", "birthdate", "birthdateEstimated");
        o.put("preferredName", preferredName);

        return o;
    }

}
