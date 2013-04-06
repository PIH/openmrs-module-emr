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

package org.openmrs.module.emr.page.controller.printer;

import org.openmrs.Location;
import org.openmrs.module.emr.api.EmrService;
import org.openmrs.module.emrapi.printer.Printer;
import org.openmrs.module.emrapi.printer.PrinterService;
import org.openmrs.ui.framework.annotation.SpringBean;
import org.openmrs.ui.framework.page.PageModel;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class DefaultPrintersPageController {

    public void get(PageModel model, @SpringBean("printerService") PrinterService printerService,
                    @SpringBean("emrService") EmrService emrService) {

        List<Location> locations = emrService.getLoginLocations();

        Map<Location,Map<String,Printer>> locationsToPrintersMap = new HashMap<Location, Map<String,Printer>>();

        for (Location location : locations) {
            Map<String,Printer> printersForLocation = new HashMap<String, Printer>();
            printersForLocation.put("idCardPrinter", printerService.getDefaultPrinter(location, Printer.Type.ID_CARD));
            printersForLocation.put("labelPrinter", printerService.getDefaultPrinter(location, Printer.Type.LABEL));
            locationsToPrintersMap.put(location, printersForLocation);
        }

        model.put("locationsToPrintersMap", locationsToPrintersMap);
        model.addAttribute("idCardPrinters", printerService.getPrintersByType(Printer.Type.ID_CARD));
        model.addAttribute("labelPrinters", printerService.getPrintersByType(Printer.Type.LABEL));
    }
}
