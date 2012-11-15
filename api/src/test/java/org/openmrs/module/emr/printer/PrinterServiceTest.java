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

import junit.framework.Assert;
import org.junit.Before;
import org.junit.Test;
import org.openmrs.module.emr.printer.db.PrinterDAO;

import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class PrinterServiceTest {

    private PrinterServiceImpl printerService;

    private PrinterDAO mockPrinterDAO;


    @Before
    public void setup() {
        mockPrinterDAO = mock(PrinterDAO.class);
        printerService = new PrinterServiceImpl();
        printerService.setPrinterDAO(mockPrinterDAO);
    }

    @Test
    public void shouldReturnTrueIfAnotherPrinterAlreadyHasIpAddressAssigned() {

        Printer differentPrinter = new Printer();
        differentPrinter.setId(1);
        when(mockPrinterDAO.getPrinterByIpAddress(anyString())).thenReturn(differentPrinter);

        Printer printer = new Printer();
        printer.setId(2);
        printer.setIpAddress("10.10.10.10");

        Assert.assertTrue(printerService.isIpAddressAllocatedToAnotherPrinter(printer));
    }

    @Test
    public void shouldReturnFalseIfAnotherPrinterDoesNotHaveIpAddressAssigned() {

        when(mockPrinterDAO.getPrinterByIpAddress(anyString())).thenReturn(null);

        Printer printer = new Printer();
        printer.setId(2);
        printer.setIpAddress("10.10.10.10");

        Assert.assertFalse(printerService.isIpAddressAllocatedToAnotherPrinter(printer));
    }

    @Test
    public void shouldReturnFalseIfOnlyCurrentPrinterHasIpAddressAssigned() {

        Printer printer = new Printer();
        printer.setId(1);
        printer.setIpAddress("10.10.10.10");
        when(mockPrinterDAO.getPrinterByIpAddress(anyString())).thenReturn(printer);

        Assert.assertFalse(printerService.isIpAddressAllocatedToAnotherPrinter(printer));
    }
}
