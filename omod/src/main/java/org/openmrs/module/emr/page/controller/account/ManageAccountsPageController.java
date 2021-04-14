package org.openmrs.module.emr.page.controller.account;

import org.apache.commons.lang.BooleanUtils;
import org.openmrs.module.emrapi.account.AccountDomainWrapper;
import org.openmrs.module.emrapi.account.AccountService;
import org.openmrs.ui.framework.annotation.SpringBean;
import org.openmrs.ui.framework.page.PageModel;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.Iterator;
import java.util.List;

public class ManageAccountsPageController {

    public void get(PageModel model, @SpringBean("accountService") AccountService accountService,
                    @RequestParam(value = "showDisabled", required = false) Boolean showDisabled) {

        if (showDisabled == null) {
            showDisabled = true;
        }

        List<AccountDomainWrapper> accounts = accountService.getAllAccounts();
        if (!showDisabled) {
            for (Iterator<AccountDomainWrapper> iter = accounts.iterator(); iter.hasNext();) {
                AccountDomainWrapper account = iter.next();
                if (BooleanUtils.isFalse(account.getUserEnabled())) {
                    iter.remove();
                }
            }
        }
        model.addAttribute("accounts", accounts);
        model.addAttribute("showDisabled", showDisabled);
    }

}
