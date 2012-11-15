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

import org.openmrs.api.impl.BaseOpenmrsService;
import org.openmrs.module.emr.printer.db.PrinterDAO;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

public class PrinterServiceImpl extends BaseOpenmrsService implements PrinterService  {

    private PrinterDAO printerDAO;

    public void setPrinterDAO(PrinterDAO printerDAO) {
        this.printerDAO = printerDAO;
    }

    @Override
    public Printer getPrinterById(Integer id) {
        return printerDAO.getById(id);
    }

    @Override
    @Transactional
    public void savePrinter(Printer printer) {
        printerDAO.saveOrUpdate(printer);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Printer> getAllPrinters() {
       return printerDAO.getAll();
    }

    @Override
    public boolean isIpAddressAllocatedToAnotherPrinter(Printer printer) {
        if (printer.getIpAddress() == null)  {
            throw new IllegalStateException("Printer IP is null");
        }

        Printer existingPrinter = printerDAO.getPrinterByIpAddress(printer.getIpAddress());

        if (existingPrinter != null && !existingPrinter.equals(printer)) {
            return true;
        }
        else {
            return false;
        }
    }
}
