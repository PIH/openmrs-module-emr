package org.openmrs.module.emr.page.controller;

import org.openmrs.module.emr.account.AccountService;
import org.openmrs.ui.framework.annotation.SpringBean;
import org.openmrs.ui.framework.page.PageModel;

public class ManageAccountsPageController {
	
	public void get(PageModel model, @SpringBean("accountService") AccountService accountService) {
		model.addAttribute("accounts", accountService.getAllAccounts());
	}
	
}
