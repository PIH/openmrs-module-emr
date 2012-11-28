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

import org.apache.commons.lang.StringUtils;
import org.openmrs.api.context.Context;
import org.openmrs.customdatatype.InvalidCustomValueException;
import org.openmrs.customdatatype.SerializingCustomDatatype;
import org.springframework.stereotype.Component;

/**
 * Datatype for serializing the Printer class
 */
@Component
public class PrinterDatatype extends SerializingCustomDatatype<Printer> {

    @Override
    public Printer deserialize(String serializedValue) {

        if (StringUtils.isBlank(serializedValue)) {
            return null;
        }
        try {
            // TODO: we should autowire the printer service here once TRUNK-3823 is fixed
            return Context.getService(PrinterService.class).getPrinterById(Integer.parseInt(serializedValue));
        }
        catch (Exception ex) {
            throw new InvalidCustomValueException("Invalid Printer: " + serializedValue);
        }
    }

    @Override
    public String serialize(Printer typedValue) {
        return typedValue.getId().toString();
    }


}
