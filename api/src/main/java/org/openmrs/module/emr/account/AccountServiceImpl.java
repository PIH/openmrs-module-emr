package org.openmrs.module.emr.account;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.openmrs.Person;
import org.openmrs.PersonName;
import org.openmrs.Provider;
import org.openmrs.User;
import org.openmrs.api.APIException;
import org.openmrs.api.PersonService;
import org.openmrs.api.ProviderService;
import org.openmrs.api.UserService;
import org.openmrs.api.impl.BaseOpenmrsService;
import org.springframework.beans.factory.annotation.Autowired;

public class AccountServiceImpl extends BaseOpenmrsService implements AccountService {
	
	@Autowired
	private UserService userService;
	
	@Autowired
	private ProviderService providerService;
	
	@Autowired
	private PersonService personService;
	
	/**
	 * @param userService the userService to set
	 */
	public void setUserService(UserService userService) {
		this.userService = userService;
	}
	
	/**
	 * @param providerService the providerService to set
	 */
	public void setProviderService(ProviderService providerService) {
		this.providerService = providerService;
	}
	
	/**
	 * @param personService the personService to set
	 */
	public void setPersonService(PersonService personService) {
		this.personService = personService;
	}
	
	/**
	 * @see org.openmrs.module.emr.account.AccountService#getAllAccounts()
	 */
	@Override
	public List<Account> getAllAccounts() {
		List<Account> accounts = new ArrayList<Account>();
		for (User user : userService.getAllUsers()) {
			accounts.add(new Account(user));
		}
		for (Provider provider : providerService.getAllProviders()) {
			accounts.add(new Account(provider));
		}
		
		return accounts;
	}
	
	/**
	 * @see org.openmrs.module.emr.account.AccountService#getUserByProvider(org.openmrs.Provider,
	 *      boolean)
	 */
	@Override
	public User getUserByProvider(Provider provider, boolean createNew) {
		User user = null;
		if (provider.getPerson() != null) {
			List<User> users = userService.getUsersByPerson(provider.getPerson(), false);
			if (users.size() == 0)
				users = userService.getUsersByPerson(provider.getPerson(), true);
			
			if (users.size() == 1)
				user = users.get(0);
			else if (users.size() > 1)
				throw new APIException("Found multiple users associated to the provider with id: "
				        + provider.getProviderId());
		}
		if (user == null && createNew) {
			//This provider has no user account, create one
			user = new User();
			if (provider.getPerson() != null) {
				user.setPerson(provider.getPerson());
			} else if (StringUtils.isNotBlank(provider.getName())) {
				//Create a Person object with the name matching provider.name
				PersonName personName = personService.parsePersonName(provider.getName());
				Person person = new Person();
				person.addName(personName);
				user = new User();
				user.setPerson(person);
			}
		}
		
		return user;
	}
	
	public Account saveAccount(Account account) {
		account.syncProperties();
		account.setUser(userService.saveUser(account.getUser(), null));
		account.setPerson(personService.savePerson(account.getPerson()));
		
		if (account.getProvider() != null) {
			account.setProvider(providerService.saveProvider(account.getProvider()));
		}
		
		return account;
	}
}
