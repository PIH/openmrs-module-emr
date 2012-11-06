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

package org.openmrs.module.emr.task;

import org.openmrs.messagesource.MessageSourceService;
import org.openmrs.module.emr.EmrContext;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * Base implementation of TaskDescriptor
 */
public abstract class BaseTaskDescriptor implements TaskDescriptor {

    private String id;
    @Autowired
    MessageSourceService messageSourceService;
    private String labelCode;

    /**
     * @param context
     * @return Base implementation returns true if the authenticated user has the privilege given by getRequiredPrivilegeName()
     */
    @Override
    public boolean isAvailable(EmrContext context) {
        String requiredPrivilege = getRequiredPrivilegeName();
        if (requiredPrivilege != null) {
            return context.getUserContext().hasPrivilege(requiredPrivilege);
        } else {
            return true;
        }
    }

    /**
     * @return Base implementation returns "Task: ${ this.id }"
     */
    @Override
    public String getRequiredPrivilegeName() {
        return "Task: " + getId();
    }

    /**
     * @param context
     * @return Base implementation returns 0d
     */
    @Override
    public double getPriority(EmrContext context) {
        return 0d;
    }

    @Override
    public String toString() {
        return "Task: " + getId();
    }

    @Override
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    @Override
    public String getLabel(EmrContext context) {
        return messageSourceService.getMessage(labelCode);
    }

    /**
     * @param labelCode code a messages.properties file for this task's label
     */
    public void setLabelCode(String labelCode) {
        this.labelCode = labelCode;
    }
}
