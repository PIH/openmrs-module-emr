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

package org.openmrs.module.emr.paperrecord;

// we made this a RuntimeException so that it forces a transaction rollback when it is thrown
public class UnableToPrintPaperRecordLabelException extends RuntimeException {

    private static final long serialVersionUID = 1L;

    public UnableToPrintPaperRecordLabelException() {
        super();
    }

    public UnableToPrintPaperRecordLabelException(String message) {
        super(message);
    }

    public UnableToPrintPaperRecordLabelException(String message, Throwable throwable) {
        super(message, throwable);
    }
}