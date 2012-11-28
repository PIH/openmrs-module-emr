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

import org.openmrs.Location;
import org.openmrs.LocationAttribute;
import org.openmrs.LocationAttributeType;
import org.openmrs.api.LocationService;
import org.openmrs.api.impl.BaseOpenmrsService;
import org.openmrs.module.emr.printer.db.PrinterDAO;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static org.openmrs.module.emr.EmrConstants.LOCATION_ATTRIBUTE_TYPE_DEFAULT_PRINTER;

public class PrinterServiceImpl extends BaseOpenmrsService implements PrinterService  {

    private PrinterDAO printerDAO;

    private LocationService locationService;

    public void setPrinterDAO(PrinterDAO printerDAO) {
        this.printerDAO = printerDAO;
    }

    public void setLocationService(LocationService locationService) {
        this.locationService = locationService;
    }

    @Override
    @Transactional(readOnly = true)
    public Printer getPrinterById(Integer id) {
        return printerDAO.getById(id);
    }

    @Override
    @Transactional(readOnly = true)
    public Printer getPrinterByName(String name) {
        return printerDAO.getPrinterByName(name);
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
    public void setDefaultPrinter(Location location, Printer printer) {

        LocationAttribute defaultPrinter = new LocationAttribute();
        defaultPrinter.setAttributeType(getLocationAttributeTypeDefaultPrinter(printer.getType()));
        defaultPrinter.setValue(printer);

        location.setAttribute(defaultPrinter);
        locationService.saveLocation(location);
    }

    @Override
    public Printer getDefaultPrinter(Location location, Printer.Type type) {

        List<LocationAttribute> defaultPrinters = location.getActiveAttributes(getLocationAttributeTypeDefaultPrinter(type));

        if (defaultPrinters == null || defaultPrinters.size() == 0)  {
            return null;
        }

        if (defaultPrinters.size() > 1) {
            throw new IllegalStateException("Multiple default printer of type " + type + " defined for " + location);
        }

        return (Printer) defaultPrinters.get(0).getValue();
    }

    @Override
    @Transactional(readOnly = true)
    public boolean isIpAddressAllocatedToAnotherPrinter(Printer printer) {
        return printerDAO.isIpAddressAllocatedToAnotherPrinter(printer);
    }

    @Override
    @Transactional(readOnly = true)
    public boolean isNameAllocatedToAnotherPrinter(Printer printer) {
        return printerDAO.isNameAllocatedToAnotherPrinter(printer);
    }

    private LocationAttributeType getLocationAttributeTypeDefaultPrinter(Printer.Type type) {

        String locationAttributeTypeUuid = LOCATION_ATTRIBUTE_TYPE_DEFAULT_PRINTER.get(type.name());
        LocationAttributeType locationAttributeType = locationService.getLocationAttributeTypeByUuid(locationAttributeTypeUuid);

        if (locationAttributeType == null) {
            throw new IllegalStateException("Unable to fetch location attribute type for default " + type + " printer");
        }

        return locationAttributeType;
    }
}
