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

import junit.framework.Assert;
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
        verifyAccessionNumberFormat(order.getAccessionNumber(), order.getId());
        Assert.assertTrue(verifyCheckDigit(order.getAccessionNumber()));
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
            verifyAccessionNumberFormat(order.getAccessionNumber(), order.getId());
            Assert.assertTrue(verifyCheckDigit(order.getAccessionNumber()));
            accessionNumbers.add(order.getAccessionNumber());
        }
        assertThat(accessionNumbers.size(), is(TIMES));
    }

    /**
     * Checks that the accession number is in the proper format
     */
    private static void verifyAccessionNumberFormat(String accessionNumber, Integer orderId) {
        assertThat(accessionNumber.length(), is(10));
        Assert.assertTrue(accessionNumber.contains(orderId.toString()));
    }


    /**
     * Checks that the specified accessionNumber has a valid check digit assigned to it
     * @param accessionNumber
     * @param orderId
     * @return
     */
    private static boolean verifyCheckDigit(String accessionNumber) {

		char[] charArray = accessionNumber.toCharArray();
		int[] numbers = new int[charArray.length];
		int total = 0;

		for (int i = 0; i < charArray.length; i++) {
			numbers[i] = Character.getNumericValue(charArray[i]);
		}

		for (int i = numbers.length - 2; i > -1; i -= 2) {
			numbers[i] *= 2;

			if (numbers[i] > 9)
				numbers[i] -= 9;
		}

		for (int i = 0; i < numbers.length; i++)
			total += numbers[i];

		if (total % 10 != 0)
			return false;

		return true;
	}

}
