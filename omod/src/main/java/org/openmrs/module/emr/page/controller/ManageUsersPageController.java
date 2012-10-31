package org.openmrs.module.emr.page.controller;

import org.openmrs.api.ProviderService;
import org.openmrs.api.UserService;
import org.openmrs.ui.framework.annotation.SpringBean;
import org.openmrs.ui.framework.page.PageModel;

public class ManageUsersPageController {
	
	public void get(PageModel model, @SpringBean("userService") UserService userService,
	                @SpringBean("providerService") ProviderService providerService) {
		model.addAttribute("users", userService.getAllUsers());
		model.addAttribute("providers", providerService.getAllProviders());
	}
	
}
