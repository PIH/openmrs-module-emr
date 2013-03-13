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

import org.mockito.ArgumentMatcher;
import org.openmrs.module.reporting.query.IdSet;

import java.util.Set;

import static org.hamcrest.collection.IsIterableContainingInAnyOrder.containsInAnyOrder;

/**
 * TODO move this to the reporting module
 */
public class ReportingMatchers {

    public static ArgumentMatcher<IdSet<?>> hasExactlyIds(final Integer... expectedMemberIds) {
        return new ArgumentMatcher<IdSet<?>>() {
            @Override
            public boolean matches(Object o) {
                Set<Integer> actual = ((IdSet<?>) o).getMemberIds();
                return (actual.size() == expectedMemberIds.length) && containsInAnyOrder(expectedMemberIds).matches(actual);
            }
        };
    }
}
