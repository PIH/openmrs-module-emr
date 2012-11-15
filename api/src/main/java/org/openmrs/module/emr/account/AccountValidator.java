package org.openmrs.module.emr.account;

import org.apache.commons.lang.StringUtils;
import org.openmrs.User;
import org.openmrs.annotation.Handler;
import org.openmrs.api.PasswordException;
import org.openmrs.messagesource.MessageSourceService;
import org.openmrs.util.OpenmrsUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;

@Handler(supports = { Account.class }, order = 50)
public class AccountValidator implements Validator {
	
	@Autowired
	@Qualifier("messageSourceService")
	private MessageSourceService messageSourceService;
	
	/**
	 * @param messageSourceService the messageSourceService to set
	 */
	public void setMessageSourceService(MessageSourceService messageSourceService) {
		this.messageSourceService = messageSourceService;
	}
	
	/**
	 * @see org.springframework.validation.Validator#supports(java.lang.Class)
	 */
	@Override
	public boolean supports(Class<?> clazz) {
		return Account.class.isAssignableFrom(clazz);
	}
	
	/**
	 * @see org.springframework.validation.Validator#validate(java.lang.Object,
	 *      org.springframework.validation.Errors)
	 * @should reject an empty username if user is not null
	 * @should reject an empty givenname
	 * @should reject an empty familyname
	 * @should reject an empty privilegeLevel if user is not null
	 * @should require password if conform password is provided
	 * @should require confirm password if password is provided
	 * @should reject password and confirm password if they dont match
	 * @should pass for a valid account
	 * @should pass for a valid account with only person property
	 * @should require passwords for a new a user account
	 */
	@Override
	public void validate(Object obj, Errors errors) {
		if (obj == null || !(obj instanceof Account))
			throw new IllegalArgumentException("The parameter obj should not be null and must be of type" + Account.class);
		
		Account account = (Account) obj;
		account.syncProperties();

        User user = account.getUser();

        checkIfGivenAndFamilyNameAreNotNull(errors, account);

        if (checkIfUserWasCreated(user)) {
            checkIfUserNameIsCorrect(errors, account.getUsername());
            checkIfPasswordIsCorrect(errors, account);
            checkIfPrivilegeLevelIsCorrect(errors, account);
        }
    }

    private void checkIfPrivilegeLevelIsCorrect(Errors errors, Account account) {
        if (account.getPrivilegeLevel() == null) {
            errors.rejectValue("privilegeLevel", "error.required",
                new Object[] { messageSourceService.getMessage("emr.user.privilegeLevel") }, null);
        }
    }

    private void checkIfPasswordIsCorrect(Errors errors, Account account) {
        String password = account.getPassword();
        String confirmPassword = account.getConfirmPassword();

        if (checkIfPasswordWasCreated(password, confirmPassword)) {
            validatePassword(errors, account, password, confirmPassword);
        } else {
            errors.rejectValue("password", "error.required",
                    new Object[] { messageSourceService.getMessage("emr.user.password") }, null);
            errors.rejectValue("confirmPassword", "error.required",
                    new Object[] { messageSourceService.getMessage("emr.user.confirmPassword") }, null);
        }

     }

    private void validatePassword(Errors errors, Account account, String password, String confirmPassword) {
        if (password.equals(confirmPassword)) {
            getErrorInPassword(errors, account);
        } else {
            errors.rejectValue("password", "emr.account.error.passwordDontMatch",
                    new Object[] { messageSourceService.getMessage("emr.user.password") }, null);
        }
    }

    private void getErrorInPassword(Errors errors, Account account) {
        try{
            OpenmrsUtil.validatePassword(account.getUsername(), account.getPassword(), account.getUser().getSystemId());
        }catch (PasswordException e){
            errors.rejectValue("password", "emr.account.error.passwordError",
                    new Object[]{messageSourceService.getMessage("emr.account.error.passwordError")}, null);
        }
    }


    private boolean checkIfPasswordWasCreated(String password, String confirmPassword) {
        return (StringUtils.isNotBlank(password) && StringUtils.isNotBlank(confirmPassword));
    }

    private boolean checkIfUserWasCreated(User user) {
        return (user != null && user.getUserId() == null);
    }

    private void checkIfUserNameIsCorrect(Errors errors, String username) {
       String min = "2";
       String max = "50";
       if(StringUtils.isNotBlank(username)) {
             if(!username.matches("[A-Za-z0-9\\._\\-]{" + min + "," + max + "}")){
                 errors.rejectValue("username", "error.required",
                         new Object[] { messageSourceService.getMessage("emr.user.username") }, null);
             }
       } else{
           errors.rejectValue("username", "error.required",
                   new Object[] { messageSourceService.getMessage("emr.user.username") }, null);
       }

    }

    private void checkIfGivenAndFamilyNameAreNotNull(Errors errors, Account account) {
        if (StringUtils.isBlank(account.getGivenName())) {
            errors.rejectValue("givenName", "error.required",
                new Object[] { messageSourceService.getMessage("emr.person.givenName") }, null);
        }
        if (StringUtils.isBlank(account.getFamilyName())) {
            errors.rejectValue("familyName", "error.required",
                new Object[] { messageSourceService.getMessage("emr.person.familyName") }, null);
        }
    }

}
