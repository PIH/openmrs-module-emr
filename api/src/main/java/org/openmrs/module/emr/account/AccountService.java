package org.openmrs.module.emr.account;

import java.util.List;

import org.openmrs.Provider;
import org.openmrs.User;

public interface AccountService {
	
	/**
	 * @return
	 * @should get all unique accounts
	 */
	public List<Account> getAllAccounts();
	
	/**
	 * Gets the user account associated to the provider or creates an unsaved User instance for the
	 * provider and returns it
	 * 
	 * @param provider
	 * @param createNew specifies if a new User instance should be created if none is found
	 * @return
	 * @should get the user associated to the provider
	 * @should return a new user instance if createNew is set to true and no matching user is found
	 * @should return null if createNew is set to false and no matching user is found
	 */
	public User getUserByProvider(Provider provider, boolean createNew);
	
}
