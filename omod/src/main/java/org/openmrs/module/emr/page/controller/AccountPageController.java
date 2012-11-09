/**
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
package org.openmrs.module.emr.page.controller;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openmrs.Person;
import org.openmrs.PersonName;
import org.openmrs.Provider;
import org.openmrs.User;
import org.openmrs.api.APIException;
import org.openmrs.module.emr.EmrConstants;
import org.openmrs.module.emr.account.Account;
import org.openmrs.module.emr.account.AccountService;
import org.openmrs.module.emr.account.AccountValidator;
import org.openmrs.ui.framework.annotation.BindParams;
import org.openmrs.ui.framework.annotation.MethodParam;
import org.openmrs.ui.framework.annotation.SpringBean;
import org.openmrs.ui.framework.page.PageModel;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.RequestParam;

public class AccountPageController {
	
	protected final Log log = LogFactory.getLog(getClass());

    public Account getAccount(@RequestParam(value = "personId", required = false) Person person,
                                @SpringBean("accountService") AccountService accountService) {
        Account account;
        if (person == null) {
            Person newPerson = new Person();
            person.addName(new PersonName());
            account = new Account(newPerson);
        }
        else {
            account = accountService.getAccountByPerson(person);
            if (account == null)
                throw new APIException("Failed to find user account matching person with id:" + person.getPersonId());
        }

        return account;
    }

	public void get(PageModel model, @MethodParam("getAccount") Account account,
	                @SpringBean("accountService") AccountService accountService) {

		model.addAttribute("account", account);
		model.addAttribute("capabilities", accountService.getAllCapabilities());
		model.addAttribute("privilegeLevels", accountService.getAllPrivilegeLevels());
        model.addAttribute("rolePrefix", EmrConstants.ROLE_PREFIX_CAPABILITY);
		model.addAttribute("showPasswordFields", false);
	}
	
	public String post(@MethodParam("getAccount") @BindParams Account account, BindingResult errors,
                       @RequestParam(value = "enabled", defaultValue = "false") boolean enabled,
                       @RequestParam(value = "interactsWithPatients", defaultValue = "false") boolean interactsWithPatients,
	                   @RequestParam(value = "createProviderAccount") boolean createProviderAccount,
	                   @RequestParam(value = "createUserAccount") boolean createUserAccount,
	                   @SpringBean("accountService") AccountService accountService,
	                   @SpringBean("accountValidator") AccountValidator accountValidator, PageModel model,
	                   HttpServletRequest request) {
		
		if (createUserAccount && account.getUser() == null) {
			User user = new User();
			account.setUser(user);
		}

		if (createProviderAccount && account.getProvider() == null) {
			Provider provider = new Provider();
			account.setProvider(provider);
		}

        account.setEnabled(enabled);
        account.setInteractsWithPatients(interactsWithPatients);

		accountValidator.validate(account, errors);
		
		if (!errors.hasErrors()) {
			
			try {
				accountService.saveAccount(account);
				request.getSession().setAttribute(EmrConstants.SESSION_ATTRIBUTE_INFO_MESSAGE, "emr.account.saved");
				
				return "redirect:/emr/account.page?personId=" + account.getPerson().getPersonId();
			}
			catch (Exception e) {
				log.warn("Some error occured while saving account details:", e);
				request.getSession().setAttribute(EmrConstants.SESSION_ATTRIBUTE_ERROR_MESSAGE,
				    "emr.account.error.save.fail");
			}
		} else {
			request.getSession().setAttribute(EmrConstants.SESSION_ATTRIBUTE_ERROR_MESSAGE,
			    "emr.error.foundValidationErrors");
		}
		
		model.addAttribute("account", account);
		model.addAttribute("capabilities", accountService.getAllCapabilities());
		model.addAttribute("privilegeLevels", accountService.getAllPrivilegeLevels());
		model.addAttribute("errors", errors);
		model.addAttribute("showPasswordFields",
		    StringUtils.isNotBlank(account.getPassword()) || StringUtils.isNotBlank(account.getConfirmPassword())
		            || createUserAccount);
		//redisplay the form
		return null;
	}
}
