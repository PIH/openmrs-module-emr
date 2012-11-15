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
import org.junit.Test;
import org.openmrs.test.BaseModuleContextSensitiveTest;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

public class PrinterServiceComponentTest extends BaseModuleContextSensitiveTest {


   @Autowired
   private PrinterService printerService;

   @Test
   public void testSavePrinter() {

       Printer printer = new Printer();
       printer.setName("Test Printer");
       printer.setIpAddress("192.1.1.8");
       printer.setType(Printer.Type.ID_CARD);

       printerService.savePrinter(printer);

       List<Printer> printers = printerService.getAllPrinters();

       Assert.assertEquals(1, printers.size());
       Assert.assertEquals("Test Printer", printers.get(0).getName());
       Assert.assertEquals("192.1.1.8", printers.get(0).getIpAddress());
       Assert.assertEquals("9100", printers.get(0).getPort());
       Assert.assertEquals(Printer.Type.ID_CARD, printers.get(0).getType());

       // make sure the audit fields have been set
       Assert.assertNotNull(printers.get(0).getDateCreated());
       Assert.assertNotNull(printers.get(0).getCreator());
       Assert.assertNotNull(printers.get(0).getUuid());
   }

    @Test
    public void testShouldReturnTrueIfAnotherPrinterAlreadyHasIpAddressAssigned() {
        Printer printer = new Printer();
        printer.setName("Test Printer");
        printer.setIpAddress("192.1.1.8");
        printer.setType(Printer.Type.ID_CARD);

        printerService.savePrinter(printer);

        Printer differentPrinter = new Printer();
        differentPrinter.setName("Another printer");
        differentPrinter.setIpAddress("192.1.1.8");
        differentPrinter.setType(Printer.Type.LABEL);

        Assert.assertTrue(printerService.isIpAddressAllocatedToAnotherPrinter(differentPrinter));

    }

    @Test
    public void testShouldReturnFalseIfAnotherPrinterDoesNotHaveIpAddressAssigned() {
        Printer printer = new Printer();
        printer.setName("Test Printer");
        printer.setIpAddress("192.1.1.6");
        printer.setType(Printer.Type.ID_CARD);

        printerService.savePrinter(printer);

        Printer differentPrinter = new Printer();
        differentPrinter.setName("Another printer");
        differentPrinter.setIpAddress("192.1.1.8");
        differentPrinter.setType(Printer.Type.LABEL);

        Assert.assertFalse(printerService.isIpAddressAllocatedToAnotherPrinter(differentPrinter));

    }

}
