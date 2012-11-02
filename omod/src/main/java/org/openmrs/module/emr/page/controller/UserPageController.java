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

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.openmrs.Person;
import org.openmrs.PersonName;
import org.openmrs.Provider;
import org.openmrs.Role;
import org.openmrs.User;
import org.openmrs.api.APIException;
import org.openmrs.api.PersonService;
import org.openmrs.api.ProviderService;
import org.openmrs.api.UserService;
import org.openmrs.module.emr.EmrConstants;
import org.openmrs.module.emr.account.Account;
import org.openmrs.module.emr.account.AccountService;
import org.openmrs.ui.framework.annotation.BindParams;
import org.openmrs.ui.framework.annotation.SpringBean;
import org.openmrs.ui.framework.page.PageModel;
import org.springframework.web.bind.annotation.RequestParam;

public class UserPageController {
	
	public void get(PageModel model, @RequestParam(value = "userId", required = false) User user,
	                @RequestParam(value = "providerId", required = false) Provider provider,
	                @SpringBean("userService") UserService userService,
	                @SpringBean("providerService") ProviderService providerService,
	                @SpringBean("personService") PersonService personService) {
		
		if (user == null) {
			if (provider == null)
				throw new IllegalArgumentException("userId  or providerId id required");
			
			//If the associated person has a user account, use that
			if (provider.getPerson() != null) {
				List<User> users = userService.getUsersByPerson(provider.getPerson(), false);
				if (users.size() == 1)
					user = users.get(0);
				else if (users.size() > 1)
					throw new APIException("Found multiple users for person with id: " + provider.getPerson().getPersonId());
				else {
					//This provider has no user account, create one
					user = new User();
					user.setPerson(provider.getPerson());
				}
			} else if (StringUtils.isNotBlank(provider.getName())) {
				//Create a Person object for the provider since they have none
				PersonName personName = personService.parsePersonName(provider.getName());
				Person person = new Person();
				person.addName(personName);
				user = new User();
				user.setPerson(person);
			} else {
				throw new APIException("Provider has no name and is not associated to a person");
			}
		}
		
		model.addAttribute("account", new Account(user));
		List<Role> candidateRoles = userService.getAllRoles();
		List<Role> roles = new ArrayList<Role>();
		for (Role candidate : candidateRoles) {
			if (candidate.getName().startsWith(EmrConstants.ROLE_PREFIX_CAPABILITY))
				roles.add(candidate);
		}
		model.addAttribute("roles", roles);
	}
	
	public String post(@RequestParam("userId") @BindParams Account account, @SpringBean("accountService") AccountService accountService) {
		accountService.saveAccount(account);
		return "redirect:/emr/user.page?userId=" + account.getUser().getUserId(); // "redirect:/emr/manageUsers.page";
	}
	
}
