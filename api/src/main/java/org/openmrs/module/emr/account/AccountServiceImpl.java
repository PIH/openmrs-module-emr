package org.openmrs.module.emr.account;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.openmrs.Person;
import org.openmrs.Provider;
import org.openmrs.Role;
import org.openmrs.User;
import org.openmrs.api.APIException;
import org.openmrs.api.PersonService;
import org.openmrs.api.ProviderService;
import org.openmrs.api.UserService;
import org.openmrs.api.context.Context;
import org.openmrs.api.impl.BaseOpenmrsService;
import org.openmrs.module.emr.EmrConstants;
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
		Map<Person, Account> byPerson = new LinkedHashMap<Person, Account>();
		for (User user : userService.getAllUsers()) {
			//exclude daemon user
			if (EmrConstants.DAEMON_USER_UUID.equals(user.getUuid()))
				continue;
			
			Provider provider = getProviderByPerson(user.getPerson());
			byPerson.put(user.getPerson(), new Account(user, provider));
		}
		for (Provider provider : providerService.getAllProviders()) {
			if (provider.getPerson() == null)
				throw new APIException("Providers not associated to a person are not supported");
			
			Account account = byPerson.get(provider.getPerson());
			if (account == null) {
				User user = getUserByPerson(provider.getPerson());
				byPerson.put(provider.getPerson(), new Account(user, provider));
			}
		}
		
		List<Account> accounts = new ArrayList<Account>();
		for (Account account : byPerson.values()) {
			accounts.add(account);
		}
		
		return accounts;
	}
	
	public Account saveAccount(Account account) {
		account.syncProperties();
		account.setPerson(personService.savePerson(account.getPerson()));
		
		if (account.getUser() != null) {
			User user = account.getUser();
			//only include capabilities and privilege level set on the account
			if (user.getRoles() != null) {
				//TODO Figure out how to unset inherited roles
				user.getRoles().removeAll(getAllPrivilegeLevels());
				user.getRoles().removeAll(getAllCapabilities());
			}
			if (account.getPrivilegeLevel() != null && !user.hasRole(account.getPrivilegeLevel().getRole()))
				user.addRole(account.getPrivilegeLevel());
			
			for (Role capability : account.getCapabilities()) {
				user.addRole(capability);
			}
			
			if (user.isRetired())
				user.setRetireReason(Context.getMessageSourceService().getMessage("general.default.retireReason"));
			
			account.setUser(userService.saveUser(user, account.getPassword()));
		}
		
		if (account.getProvider() != null) {
			Provider provider = account.getProvider();
			if (provider.isRetired())
				provider.setRetireReason(Context.getMessageSourceService().getMessage("general.default.retireReason"));
			
			account.setProvider(providerService.saveProvider(provider));
		}
		
		return account;
	}
	
	/**
	 * @see org.openmrs.module.emr.account.AccountService#getAccount(java.lang.Integer)
	 */
	@Override
	public Account getAccount(Integer personId) {
		return getAccountByPerson(personService.getPerson(personId));
	}
	
	/**
	 * @see org.openmrs.module.emr.account.AccountService#getAccountByPerson(org.openmrs.Person)
	 */
	@Override
	public Account getAccountByPerson(Person person) {
		return new Account(getUserByPerson(person), getProviderByPerson(person));
	}
	
	/**
	 * @see org.openmrs.module.emr.account.AccountService#getAllCapabilities()
	 */
	@Override
	public List<Role> getAllCapabilities() {
		List<Role> capabilities = new ArrayList<Role>();
		for (Role candidate : userService.getAllRoles()) {
			if (candidate.getName().startsWith(EmrConstants.ROLE_PREFIX_CAPABILITY))
				capabilities.add(candidate);
		}
		return capabilities;
	}
	
	/**
	 * @see org.openmrs.module.emr.account.AccountService#getAllPrivilegeLevels()
	 */
	@Override
	public List<Role> getAllPrivilegeLevels() {
		List<Role> privilegeLevels = new ArrayList<Role>();
		for (Role candidate : userService.getAllRoles()) {
			if (candidate.getName().startsWith(EmrConstants.ROLE_PREFIX_PRIVILEGE_LEVEL))
				privilegeLevels.add(candidate);
		}
		return privilegeLevels;
	}
	
	private User getUserByPerson(Person person) {
		User user = null;
		List<User> users = userService.getUsersByPerson(person, false);
		//exclude daemon user
		for (Iterator<User> i = users.iterator(); i.hasNext();) {
			User candidate = i.next();
			if (EmrConstants.DAEMON_USER_UUID.equals(candidate.getUuid())) {
				i.remove();
				break;
			}
		}
		//return a retired account if they have none
		if (users.size() == 0)
			users = userService.getUsersByPerson(person, true);
		
		if (users.size() == 1)
			user = users.get(0);
		else if (users.size() > 1)
			throw new APIException("Found multiple users associated to the person with id: " + person.getPersonId());
		
		return user;
	}
	
	private Provider getProviderByPerson(Person person) {
		Provider provider = null;
		Collection<Provider> providers = providerService.getProvidersByPerson(person, false);
		for (Iterator<Provider> i = providers.iterator(); i.hasNext();) {
			Provider candidate = i.next();
			if (EmrConstants.DAEMON_USER_UUID.equals(candidate.getUuid())) {
				i.remove();
				break;
			}
		}
		//see if they have a retired account
		if (providers.size() == 0)
			providers = providerService.getProvidersByPerson(person, true);
		
		if (providers.size() == 1)
			provider = providers.iterator().next();
		else if (providers.size() > 1)
			throw new APIException("Found multiple providers associated to the person with id: " + person.getPersonId());
		
		return provider;
	}
}
