package org.openmrs.module.emr.account;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.openmrs.Person;
import org.openmrs.PersonName;
import org.openmrs.Role;
import org.openmrs.User;
import org.openmrs.messagesource.MessageSourceService;
import org.openmrs.module.emr.EmrConstants;
import org.openmrs.util.OpenmrsUtil;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;
import org.springframework.validation.BindException;
import org.springframework.validation.Errors;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.powermock.api.mockito.PowerMockito.mockStatic;

@RunWith(PowerMockRunner.class)
@PrepareForTest(OpenmrsUtil.class)
public class AccountValidatorTest {
	
	private AccountValidator validator;
    private Account account;

    @Before
	public void setValidator() {
		validator = new AccountValidator();
		validator.setMessageSourceService(Mockito.mock(MessageSourceService.class));

        Person person = new Person();
        person.addName(new PersonName());
        account = new Account(person);
	}
	
	/**
	 * @see AccountValidator#validate(Object,Errors)
	 * @verifies reject an empty givenname
	 */
	@Test
	public void validate_shouldRejectAnEmptyGivenname() throws Exception {
		Errors errors = new BindException(account, "account");
		validator.validate(account, errors);
		assertTrue(errors.hasFieldErrors("givenName"));
	}
	
	/**
	 * @see AccountValidator#validate(Object,Errors)
	 * @verifies reject an empty familyname
	 */
	@Test
	public void validate_shouldRejectAnEmptyFamilyname() throws Exception {
		account.setGivenName("give name");
		
		Errors errors = new BindException(account, "account");
		validator.validate(account, errors);
		assertTrue(errors.hasFieldErrors("familyName"));
	}
	
	/**
	 * @see AccountValidator#validate(Object,Errors)
	 * @verifies reject an empty privilegeLevel if user is not null
	 */
	@Test
	public void validate_shouldRejectAnEmptyPrivilegeLevelIfUserIsNotNull() throws Exception {
		account.setGivenName("give name");
		account.setFamilyName("family name");
		account.setUser(new User());
		account.setUsername("username");
		
		Errors errors = new BindException(account, "account");
		validator.validate(account, errors);
		assertTrue(errors.hasFieldErrors("privilegeLevel"));
	}
	
	/**
	 * @see AccountValidator#validate(Object,Errors)
	 * @verifies reject an empty username if user is not null
	 */
	@Test
	public void validate_shouldRejectAnEmptyUsernameIfUserIsNotNull() throws Exception {
		account.setGivenName("give name");
		account.setFamilyName("family name");
		account.setUser(new User());
		account.setPrivilegeLevel(new Role(EmrConstants.PRIVILEGE_LEVEL_FULL_ROLE));
		
		Errors errors = new BindException(account, "account");
		validator.validate(account, errors);
		assertTrue(errors.hasFieldErrors("username"));
	}
	
	/**
	 * @see AccountValidator#validate(Object,Errors)
	 * @verifies reject password and confirm password if they dont match
	 */
	@Test
	public void validate_shouldRejectPasswordAndConfirmPasswordIfTheyDontMatch() throws Exception {
		account.setGivenName("give name");
		account.setFamilyName("family name");
		account.setUser(new User());
		account.setUsername("username");
		account.setPassword("password");
		account.setConfirmPassword("confirm password");
		
		Errors errors = new BindException(account, "account");
		validator.validate(account, errors);
		assertTrue(errors.hasFieldErrors("password"));
		assertTrue(errors.hasFieldErrors("confirmPassword"));
	}
	
	/**
	 * @see AccountValidator#validate(Object,Errors)
	 * @verifies require confirm password if password is provided
	 */
	@Test
	public void validate_shouldRequireConfirmPasswordIfPasswordIsProvided() throws Exception {
		account.setGivenName("give name");
		account.setFamilyName("family name");
		account.setUser(new User());
		account.setUsername("username");
		account.setPassword("password");
		
		Errors errors = new BindException(account, "account");
		validator.validate(account, errors);
		assertFalse(errors.hasFieldErrors("password"));
		assertTrue(errors.hasFieldErrors("confirmPassword"));
	}
	
	/**
	 * @see AccountValidator#validate(Object,Errors)
	 * @verifies require password if conform password is provided
	 */
	@Test
	public void validate_shouldRequirePasswordIfConformPasswordIsProvided() throws Exception {
		account.setGivenName("give name");
		account.setFamilyName("family name");
		account.setUser(new User());
		account.setUsername("username");
		account.setConfirmPassword("confirm password");
		
		Errors errors = new BindException(account, "account");
		validator.validate(account, errors);
		assertFalse(errors.hasFieldErrors("confirmPassword"));
		assertTrue(errors.hasFieldErrors("password"));
	}
	
	/**
	 * @see AccountValidator#validate(Object,Errors)
	 * @verifies pass for a valid account
	 */
	@Test
	public void validate_shouldPassForAValidAccount() throws Exception {
		account.setUser(new User());
		account.setUsername("username");
		account.setGivenName("give name");
		account.setFamilyName("family name");
		account.setPassword("Password123");
		account.setConfirmPassword("Password123");
		account.setPrivilegeLevel(new Role(EmrConstants.PRIVILEGE_LEVEL_FULL_ROLE));
		
		Errors errors = new BindException(account, "account");
		validator.validate(account, errors);
		assertFalse(errors.hasErrors());
	}
	
	/**
	 * @see AccountValidator#validate(Object,Errors)
	 * @verifies pass for a valid account with only person property
	 */
	@Test
	public void validate_shouldPassForAValidAccountWithOnlyPersonProperty() throws Exception {
		account.setGivenName("give name");
		account.setFamilyName("family name");
		
		Errors errors = new BindException(account, "account");
		validator.validate(account, errors);
		assertFalse(errors.hasErrors());
	}
	
	/**
	 * @see AccountValidator#validate(Object,Errors)
	 * @verifies require passwords for a new a user account
	 */
	@Test
	public void validate_shouldRequirePasswordsForANewAUserAccount() throws Exception {
		account.setGivenName("give name");
		account.setFamilyName("family name");
		account.setUser(new User());
		account.setUsername("username");
		account.setPrivilegeLevel(new Role(EmrConstants.PRIVILEGE_LEVEL_FULL_ROLE));
		
		Errors errors = new BindException(account, "account");
		validator.validate(account, errors);
		assertTrue(errors.hasFieldErrors("password"));
		assertTrue(errors.hasFieldErrors("confirmPassword"));
	}

    @Test
    public void shouldValidateShortPassword() {
        mockStatic(OpenmrsUtil.class);

        account.setUsername("username");
        account.setPassword("password");
        User user = new User();
        user.setSystemId("systemId");
        account.setUser(user);

        Errors errors = new BindException(account, "account");
        validator.validate(account, errors);

//        PowerMockito.verifyStatic(OpenmrsUtil.validatePassword("username", "password", "systemId"));
    }
}
