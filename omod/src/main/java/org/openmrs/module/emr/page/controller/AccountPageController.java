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

import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openmrs.Person;
import org.openmrs.PersonName;
import org.openmrs.Provider;
import org.openmrs.User;
import org.openmrs.api.APIException;
import org.openmrs.api.UserService;
import org.openmrs.api.context.Context;
import org.openmrs.messagesource.MessageSourceService;
import org.openmrs.module.emr.EmrConstants;
import org.openmrs.module.emr.account.Account;
import org.openmrs.module.emr.account.AccountService;
import org.openmrs.module.emr.account.AccountValidator;
import org.openmrs.ui.framework.annotation.BindParams;
import org.openmrs.ui.framework.annotation.MethodParam;
import org.openmrs.ui.framework.annotation.SpringBean;
import org.openmrs.ui.framework.page.PageModel;
import org.springframework.context.MessageSource;
import org.springframework.validation.BindingResult;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.annotation.RequestParam;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

public class AccountPageController {
	
	protected final Log log = LogFactory.getLog(getClass());

    public Account getAccount(@RequestParam(value = "personId", required = false) Person person,
                                @SpringBean("accountService") AccountService accountService,
                                @SpringBean("userService") UserService userService) {
        Account account;
        if (person == null) {
            Person newPerson = new Person();
            newPerson.addName(new PersonName());
            account = new Account(newPerson, userService);
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
                       @SpringBean("messageSource") MessageSource messageSource,
                       @SpringBean("messageSourceService") MessageSourceService messageSourceService,
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
				request.getSession().setAttribute(EmrConstants.SESSION_ATTRIBUTE_INFO_MESSAGE,
                        messageSourceService.getMessage("emr.account.saved"));
				
				return "redirect:/emr/account.page?personId=" + account.getPerson().getPersonId();
			}
			catch (Exception e) {
				log.warn("Some error occurred while saving account details:", e);
				request.getSession().setAttribute(EmrConstants.SESSION_ATTRIBUTE_ERROR_MESSAGE,
                        messageSourceService.getMessage("emr.account.error.save.fail", new Object[]{ e.getMessage() }, Context.getLocale()));
			}
		} else {
            sendErrorMessage(errors, messageSource, request);
		}
		
		model.addAttribute("account", account);
		model.addAttribute("capabilities", accountService.getAllCapabilities());
		model.addAttribute("privilegeLevels", accountService.getAllPrivilegeLevels());
		model.addAttribute("errors", errors);
		model.addAttribute("showPasswordFields",
		    StringUtils.isNotBlank(account.getPassword()) || StringUtils.isNotBlank(account.getConfirmPassword())
		            || createUserAccount);

        return "redirect:/emr/account.page" + returnParameters(account);
	}

    private String returnParameters(Account account) {
        Person person = account.getPerson();
        Integer personId = person.getPersonId();
        if (personId==null){
            return "";
        }
        return "?personId=" + personId;
    }

    private void sendErrorMessage(BindingResult errors, MessageSource messageSource, HttpServletRequest request) {
        List<ObjectError> allErrors = errors.getAllErrors();
        String message = getMessageErrors(messageSource, allErrors);
        request.getSession().setAttribute(EmrConstants.SESSION_ATTRIBUTE_ERROR_MESSAGE,
                message);
    }

    private String getMessageErrors(MessageSource messageSource, List<ObjectError> allErrors) {
        String message="";
        for (ObjectError error : allErrors) {
            Object[] arguments = error.getArguments();
            String errorMessage = messageSource.getMessage(error.getCode(), arguments, Context.getLocale());
            message = message.concat(replaceArguments(errorMessage, arguments).concat("<br>"));
        }
        return message;
    }

    private String replaceArguments(String message, Object[] arguments){
        for (int i = 0 ; i < arguments.length ; i++){
            String argument = (String) arguments[i];
            message = message.replaceAll("\\{" + i + "\\}", argument);
        }
        return message;
    }
}
