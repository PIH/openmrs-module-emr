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

package org.openmrs.module.emr.fragment.controller.account;

import org.openmrs.Person;
import org.openmrs.module.emr.account.Account;
import org.openmrs.module.emr.account.AccountService;
import org.openmrs.ui.framework.UiUtils;
import org.openmrs.ui.framework.annotation.SpringBean;
import org.openmrs.ui.framework.fragment.action.FragmentActionResult;
import org.openmrs.ui.framework.fragment.action.SuccessResult;
import org.springframework.web.bind.annotation.RequestParam;

/**
 *
 */
public class AccountFragmentController {

    public FragmentActionResult unlock(@RequestParam("personId") Person person,
                                       @SpringBean("accountService") AccountService accountService,
                                       UiUtils ui) {
        Account account = accountService.getAccountByPerson(person);
        account.unlock();
        return new SuccessResult(ui.message("emr.account.unlocked.successMessage"));
    }

}
