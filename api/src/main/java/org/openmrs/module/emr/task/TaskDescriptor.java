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

import org.openmrs.module.emr.EmrContext;

import java.util.List;

/**
 * Describes a "task", vaguely defined as something displayed in the UI as an icon and label, on pages like a patient
 * dashboard that let the end user choose between multiple actions. These are typically enabled for certain roles.
 */
public interface TaskDescriptor {

    /**
     * @return a unique id for this task
     */
    String getId();

    /**
     * @return the privilege required to run this task (if any)
     */
    String getRequiredPrivilegeName();

    /**
     * @return the id of the apps that this task is applicable for
     */
    List<String> getAppIds();

    /**
     * @param context
     * @return the user-facing name for this task
     */
    String getLabel(EmrContext context);

    /**
     * @param context
     * @return the URL of an image to used for this task, e.g. for a big "action" button
     */
    String getIconUrl(EmrContext context);

    /**
     * @param context
     * @return the URL of a tiny image, e.g. to represent this task in breadcrumbs
     */
    String getTinyIconUrl(EmrContext context);

    /**
     * You should return a relative URL against OpenMRS's context-path
     * @param context
     * @return the URL of this task's first page
     */
    String getUrl(EmrContext context);

    /**
     * @param context
     * @return whether this task is available, in a given context
     */
    boolean isAvailable(EmrContext context);

    /**
     * Double.MAX_VALUE is the highest-priority, 0d is standard priority
     * @param context
     * @return how high should this task appear in the list of available tasks, in a given context
     */
    double getPriority(EmrContext context);

}
