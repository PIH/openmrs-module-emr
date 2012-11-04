package org.openmrs.module.emr.account;

import java.util.List;

import org.openmrs.Person;
import org.openmrs.Role;
import org.openmrs.module.emr.EmrConstants;

public interface AccountService {
	
	/**
	 * @return
	 * @should get all unique accounts
	 */
	public List<Account> getAllAccounts();
	
	/**
	 * Save the account details to the database
	 * 
	 * @param account
	 * @return
	 */
	public Account saveAccount(Account account);
	
	/**
	 * Gets an account for the person with the specified personId
	 * 
	 * @return
	 * @should return the account for the person with the specified personId
	 */
	public Account getAccount(Integer personId);
	
	/**
	 * Gets an account for the Specified person object
	 * 
	 * @return
	 * @should return the account for the specified person if they are associated to a user
	 * @should return the account for the specified person if they are associated to a provider
	 */
	public Account getAccountByPerson(Person person);
	
	/**
	 * Gets all Capabilities, i.e roles with the {@link EmrConstants#ROLE_PREFIX_CAPABILITY} prefix
	 * 
	 * @return a list of Roles
	 * @should return all roles with the capability prefix
	 */
	public List<Role> getAllCapabilities();
	
	/**
	 * Gets all Privilege Levels, i.e roles with the
	 * {@link EmrConstants#ROLE_PREFIX_PRIVILEGE_LEVEL} prefix
	 * 
	 * @return a list of Roles
	 * @should return all roles with the privilege level prefix
	 */
	public List<Role> getAllPrivilegeLevels();
}
