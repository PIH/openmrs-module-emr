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
 * Implementations of this interface can produce multiple {@link TaskDescriptor}s, and will be queried on each call
 * to {@link TaskService#getAvailableTasks(org.openmrs.module.emr.EmrContext)}
 */
public interface TaskFactory {

    /**
     * The framework will still call {@link TaskDescriptor#isAvailable(org.openmrs.module.emr.EmrContext)} on returned
     * tasks, so (1) implementation are allowed to return a superset of suitable task descriptors; (2) the returned
     * TaskDescriptors should implement isAvailable in a way that doesn't depend on privileges that wouldn't already
     * have been assignable.
     * @param emrContext
     * @return tasks suitable for the current context
     */
    List<TaskDescriptor> getTaskDescriptors(EmrContext emrContext);

}
