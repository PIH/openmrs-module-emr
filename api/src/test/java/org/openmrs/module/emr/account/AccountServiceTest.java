package org.openmrs.module.emr.account;

import static org.mockito.Matchers.any;
import static org.mockito.Mockito.when;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.openmrs.Person;
import org.openmrs.Provider;
import org.openmrs.User;
import org.openmrs.api.ProviderService;
import org.openmrs.api.UserService;
import org.openmrs.api.impl.PersonServiceImpl;
import org.powermock.modules.junit4.PowerMockRunner;

@RunWith(PowerMockRunner.class)
public class AccountServiceTest {
	
	@InjectMocks
	private AccountServiceImpl accountService;
	
	@Mock
	private UserService userService;
	
	@Mock
	private ProviderService providerService;
	
	/**
	 * @see AccountService#getAllAccounts()
	 * @verifies get all unique accounts
	 */
	@Test
	public void getAllAccounts_shouldGetAllUniqueAccounts() throws Exception {
		when(userService.getAllUsers()).thenReturn(Arrays.asList(new User(), new User()));
		when(providerService.getAllProviders()).thenReturn(Arrays.asList(new Provider()));
		
		List<Account> accounts = accountService.getAllAccounts();
		Assert.assertEquals(3, accounts.size());
	}
	
	/**
	 * @see AccountService#getUserByProvider(Provider,boolean)
	 * @verifies get the user associated to the provider
	 */
	@Test
	public void getUserByProvider_shouldGetTheUserAssociatedToTheProvider() throws Exception {
		User expectedUser = new User();
		Provider provider = new Provider();
		provider.setPerson(new Person());
		when(userService.getUsersByPerson(any(Person.class), any(Boolean.class))).thenReturn(Arrays.asList(expectedUser));
		User actualUser = accountService.getUserByProvider(provider, false);
		Assert.assertNotNull(actualUser);
		Assert.assertEquals(expectedUser, actualUser);
	}
	
	/**
	 * @see AccountService#getUserByProvider(Provider,boolean)
	 * @verifies return a new user instance if createNew is set to true and no matching user is
	 *           found
	 */
	@SuppressWarnings("unchecked")
	@Test
	public void getUserByProvider_shouldReturnANewUserInstanceIfCreateNewIsSetToTrueAndNoMatchingUserIsFound()
	    throws Exception {
		Provider provider = new Provider();
		provider.setName("New  Developer");
		when(userService.getUsersByPerson(any(Person.class), any(Boolean.class))).thenReturn(Collections.EMPTY_LIST);
		accountService.setPersonService(new PersonServiceImpl());
		User user = accountService.getUserByProvider(provider, true);
		Assert.assertNotNull(user);
		Assert.assertNull(user.getUserId());
		Assert.assertEquals("New", user.getPerson().getGivenName());
		Assert.assertEquals("Developer", user.getPerson().getFamilyName());
	}
	
	/**
	 * @see AccountService#getUserByProvider(Provider,boolean)
	 * @verifies return null if createNew is set to false and no matching user is found
	 */
	@SuppressWarnings("unchecked")
	@Test
	public void getUserByProvider_shouldReturnNullIfCreateNewIsSetToFalseAndNoMatchingUserIsFound() throws Exception {
		when(userService.getUsersByPerson(any(Person.class), any(Boolean.class))).thenReturn(Collections.EMPTY_LIST);
		Assert.assertNull(accountService.getUserByProvider(new Provider(), false));
	}
}
