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

package org.openmrs.module.emr.test;

/**
 * Helper class for printing out some timing statistics from tests.
 */
public class TestTimer {

    long startTimestamp = System.currentTimeMillis();

    long lastTimestamp = startTimestamp;

    public void println(String message) {
        System.out.println(formatTimestamps() + ": " + message);
        lastTimestamp = System.currentTimeMillis();
    }

    private String formatTimestamps() {
        long now = System.currentTimeMillis();
        long cumulative = now - startTimestamp;
        long sinceLast = now - lastTimestamp;
        return sinceLast + "ms (" + cumulative + "ms total)";
    }

}
