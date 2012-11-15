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

package org.openmrs.module.emr.printer;

import org.openmrs.annotation.Authorized;
import org.openmrs.api.OpenmrsService;
import org.openmrs.module.emr.EmrConstants;

import java.util.List;

public interface PrinterService extends OpenmrsService {


    /**
     * Fetches a printer by id
     *
     * @param id
     */
    @Authorized(EmrConstants.PRIVILEGE_PRINTERS_MANAGE_PRINTERS)
    Printer getPrinterById(Integer id);

    /**
     * Saves a printer
     *
     * @param printer
     */
    @Authorized(EmrConstants.PRIVILEGE_PRINTERS_MANAGE_PRINTERS)
    void savePrinter(Printer printer);

    /**
     * Fetches all printers in the system
     *
     * @return all printers in the systesm
     */
    @Authorized(EmrConstants.PRIVILEGE_PRINTERS_MANAGE_PRINTERS)
    List<Printer> getAllPrinters();

    /**
     * Given a printer, returns true/false if that ip address is in use
     * by *another* printer
     * @return
     */
    @Authorized(EmrConstants.PRIVILEGE_PRINTERS_MANAGE_PRINTERS)
    boolean isIpAddressAllocatedToAnotherPrinter(Printer printer);

}
