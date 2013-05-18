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

package org.openmrs.module.emr.fragment.controller.printer;

import org.junit.Test;
import org.openmrs.Location;
import org.openmrs.module.emr.test.TestUiUtils;
import org.openmrs.module.emrapi.printer.Printer;
import org.openmrs.module.emrapi.printer.PrinterService;
import org.openmrs.ui.framework.UiUtils;
import org.openmrs.ui.framework.fragment.action.FragmentActionResult;
import org.openmrs.ui.framework.fragment.action.SuccessResult;

import static org.hamcrest.CoreMatchers.instanceOf;
import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

/**
 *
 */
public class DefaultPrintersFragmentControllerTest {

    @Test
    public void testSaveDefaultPrinter() throws Exception {
        DefaultPrintersFragmentController controller = new DefaultPrintersFragmentController();

        Printer.Type type = Printer.Type.ID_CARD;
        Printer printer = new Printer();
        UiUtils ui = new TestUiUtils();
        Location location = new Location();
        location.setName("Location");

        PrinterService printerService = mock(PrinterService.class);

        FragmentActionResult result = controller.saveDefaultPrinter(location, type, printer, printerService, ui);

        verify(printerService).setDefaultPrinter(location, type, printer);
        assertThat(result, instanceOf(SuccessResult.class));
        SuccessResult success = (SuccessResult) result;
        assertThat(success.getMessage(), is("emr.printer.defaultUpdate:emr.printer." + type + "," + location.getName()));
    }

}
