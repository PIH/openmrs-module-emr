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

package org.openmrs.module.emr.order;

import org.junit.Before;
import org.junit.Test;
import org.openmrs.Order;

import java.util.HashSet;
import java.util.Set;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.junit.Assert.assertThat;

public class EmrOrderServiceTest {

    EmrOrderService emrOrderService;

    @Before
    public void setup() {
        emrOrderService = new EmrOrderServiceImpl();
    }

    @Test
    public void testAssignAccessionNumberTo() throws Exception {
        Order order = new Order();
        order.setOrderId(1);
        emrOrderService.ensureAccessionNumberAssignedTo(order);
        assertThat(order.getAccessionNumber(), is(notNullValue()));
    }

    @Test
    public void testAssignAccessionNumberToGeneratesUniqueNumbers() throws Exception {
        int TIMES = 1000;

        Set accessionNumbers = new HashSet();
        for(int i=0; i < TIMES; i++) {
            Order order = new Order();
            order.setOrderId(i);
            emrOrderService.ensureAccessionNumberAssignedTo(order);
            accessionNumbers.add(order.getAccessionNumber());
        }
        assertThat(accessionNumbers.size(), is(TIMES));
    }

}
