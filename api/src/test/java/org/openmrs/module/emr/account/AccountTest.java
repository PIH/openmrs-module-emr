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

package org.openmrs.module.emr.account;

import org.apache.commons.lang.time.DateUtils;
import org.junit.Before;
import org.junit.Test;
import org.openmrs.Person;
import org.openmrs.User;
import org.openmrs.api.UserService;

import java.util.Date;

import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.openmrs.util.OpenmrsConstants.USER_PROPERTY_LOCKOUT_TIMESTAMP;
import static org.openmrs.util.OpenmrsConstants.USER_PROPERTY_LOGIN_ATTEMPTS;

/**
 *
 */
public class AccountTest {

    private UserService userService;

    @Before
    public void setUp() {
        userService = mock(UserService.class);
    }

    @Test
    public void testThatAccountIsNotLockedWhenNeverLocked() throws Exception {
        Account account = buildAccount();
        assertFalse(account.isLocked());
    }

    @Test
    public void testThatAccountIsNotLockedWhenLockedALongTimeAgo() throws Exception {
        Account account = buildAccount();
        account.getUser().setUserProperty(USER_PROPERTY_LOCKOUT_TIMESTAMP, "" + DateUtils.addDays(new Date(), -1).getTime());
        assertFalse(account.isLocked());
    }

    @Test
    public void testThatAccountIsLockedWhenStillLocked() throws Exception {
        Account account = buildAccount();
        account.getUser().setUserProperty(USER_PROPERTY_LOCKOUT_TIMESTAMP, "" + DateUtils.addMinutes(new Date(), 5).getTime());
        assertTrue(account.isLocked());
    }

    @Test
    public void testUnlockingAccount() throws Exception {
        Account account = buildAccount();
        account.getUser().setUserProperty(USER_PROPERTY_LOCKOUT_TIMESTAMP, "" + DateUtils.addMinutes(new Date(), 5).getTime());

        account.unlock();

        assertThat(account.getUser().getUserProperty(USER_PROPERTY_LOCKOUT_TIMESTAMP), is(""));
        assertThat(account.getUser().getUserProperty(USER_PROPERTY_LOGIN_ATTEMPTS), is(""));

        verify(userService).saveUser(account.getUser(), null);
    }

    private Account buildAccount() {
        Person person = new Person();
        User user = new User(person);
        return new Account(user, null, userService);
    }
}
