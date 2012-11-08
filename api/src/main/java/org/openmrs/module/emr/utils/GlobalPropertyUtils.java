/**
 *  The contents of this file are subject to the OpenMRS Public License
 *  Version 1.0 (the "License"); you may not use this file except in
 *  compliance with the License. You may obtain a copy of the License at
 *  http://license.openmrs.org
 *
 *  Software distributed under the License is distributed on an "AS IS"
 *  basis, WITHOUT WARRANTY OF ANY KIND, either express or implied. See the
 *  License for the specific language governing rights and limitations
 *  under the License.
 *
 *  Copyright (C) OpenMRS, LLC.  All Rights Reserved.
 *
 */

package org.openmrs.module.emr.utils;

import org.openmrs.Concept;
import org.openmrs.OrderType;
import org.openmrs.api.context.Context;

import java.util.ArrayList;
import java.util.List;

public class GlobalPropertyUtils {

    /**
     * Global Property related utilities
     */


    /**
     * Gets the list of concepts specified in a global property
     *
     * @param propertyName
     * @return
     */
    public static List<Concept> getGlobalPropertyAsConceptList(String propertyName) {

        String gp = Context.getAdministrationService().getGlobalProperty(propertyName);

        if (gp == null) {
            throw new RuntimeException("Module not yet configured");
        }

        List<Concept> ret = new ArrayList<Concept>();

        for (String concept : gp.split(",")) {
            ret.add(GeneralUtils.getConcept(concept));
        }
        return ret;
    }

    public static OrderType getGlobalPropertyAsOrderType(String propertyName) {

        String gp = Context.getAdministrationService().getGlobalProperty(propertyName);

        if (gp == null) {
            throw new RuntimeException("Module not yet configured");
        }

        // TODO: add GeneralUtils utility method for fetching order type by uuid or id?
        return Context.getOrderService().getOrderTypeByUuid(gp);
    }

}
