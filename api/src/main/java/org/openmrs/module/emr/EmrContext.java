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

package org.openmrs.module.emr;

import org.openmrs.Location;
import org.openmrs.Patient;
import org.openmrs.api.context.Context;
import org.openmrs.api.context.UserContext;

import javax.servlet.http.HttpSession;

/**
 * The state of a User's login session, as well as state of the current page view. The lifecycle of this object is
 * controlled by org.openmrs.module.emr.ui.EmrContextArgumentProvider: one of these is instantiated at the start of
 * each page request, and made available in various useful places.
 */
public class EmrContext {

    public final static String LOCATION_SESSION_ATTRIBUTE = "emrContext.sessionLocationId";
    public final static String PATIENT_SESSION_ATTRIBUTE = "emrContext.currentPatientId";

    UserContext userContext;

    Location sessionLocation;

    Patient currentPatient;

    public EmrContext() {
        try {
            userContext = Context.getUserContext();
        } catch (Exception ex) {
            // pass
        }
    }

    public EmrContext(HttpSession session) {
        this();
        String locationId = (String) session.getAttribute(LOCATION_SESSION_ATTRIBUTE);
        if (locationId != null) {
            this.sessionLocation = Context.getLocationService().getLocation(Integer.valueOf(locationId));
        }
    }

    public UserContext getUserContext() {
        return userContext;
    }

    public void setUserContext(UserContext userContext) {
        this.userContext = userContext;
    }

    public Location getSessionLocation() {
        return sessionLocation;
    }

    public void setSessionLocation(Location sessionLocation) {
        this.sessionLocation = sessionLocation;
    }

    public Patient getCurrentPatient() {
        return currentPatient;
    }

    public void setCurrentPatient(Patient currentPatient) {
        this.currentPatient = currentPatient;
    }

}
