package org.openmrs.module.emr.account;

import java.util.HashSet;

import org.openmrs.Person;
import org.openmrs.Provider;
import org.openmrs.Role;
import org.openmrs.User;

/**
 * Acts a wrapper for a Person, User and Provider
 */
public class Account {
	
	private Person person;
	
	private User user;
	
	private Provider provider;
	
	private String givenName;
	
	private String familyName;
	
	private String gender;
	
	private boolean retired;
	
	private String username;
	
	private String password;
	
	private String confirmPassword;
	
	private HashSet<String> capabilities;
	
	private String privilegeLevel;
	
	private boolean interactsWithPatients;
	
	private String providerIdentifier;
	
	private String type;
	
	public Account(Person person) {
		this.person = person;
		setPersonDetails();
	}
	
	public Account(User user) {
		this.user = user;
		person = user.getPerson();
		setUserDetails();
		setPersonDetails();
	}
	
	public Account(Provider provider) {
		this.provider = provider;
		person = provider.getPerson();
		setProviderDetails();
		setPersonDetails();
	}
	
	/**
	 * @return the givenName
	 */
	public String getGivenName() {
		return givenName;
	}
	
	/**
	 * @param givenName the givenName to set
	 */
	public void setGivenName(String givenName) {
		this.givenName = givenName;
	}
	
	/**
	 * @return the familyName
	 */
	public String getFamilyName() {
		return familyName;
	}
	
	/**
	 * @param familyName the familyName to set
	 */
	public void setFamilyName(String familyName) {
		this.familyName = familyName;
	}
	
	/**
	 * @return the gender
	 */
	public String getGender() {
		return gender;
	}
	
	/**
	 * @param gender the gender to set
	 */
	public void setGender(String gender) {
		this.gender = gender;
	}
	
	/**
	 * @return the person
	 */
	public Person getPerson() {
		return person;
	}
	
	/**
	 * @param person the person to set
	 */
	public void setPerson(Person person) {
		this.person = person;
	}
	
	/**
	 * @return the user
	 */
	public User getUser() {
		return user;
	}
	
	/**
	 * @param user the user to set
	 */
	public void setUser(User user) {
		this.user = user;
	}
	
	/**
	 * @return the provider
	 */
	public Provider getProvider() {
		return provider;
	}
	
	/**
	 * @param provider the provider to set
	 */
	public void setProvider(Provider provider) {
		this.provider = provider;
	}
	
	/**
	 * @return the disabled
	 */
	public boolean getRetired() {
		return retired;
	}
	
	/**
	 * @param retired
	 */
	public void setRetired(boolean retired) {
		this.retired = retired;
	}
	
	/**
	 * @return the username
	 */
	public String getUsername() {
		return username;
	}
	
	/**
	 * @param username the username to set
	 */
	public void setUsername(String username) {
		this.username = username;
	}
	
	/**
	 * @return the password
	 */
	public String getPassword() {
		return password;
	}
	
	/**
	 * @param password the password to set
	 */
	public void setPassword(String password) {
		this.password = password;
	}
	
	/**
	 * @return the confirmPassword
	 */
	public String getConfirmPassword() {
		return confirmPassword;
	}
	
	/**
	 * @param confirmPassword the confirmPassword to set
	 */
	public void setConfirmPassword(String confirmPassword) {
		this.confirmPassword = confirmPassword;
	}
	
	/**
	 * @return the capabilities
	 */
	public HashSet<String> getCapabilities() {
		return capabilities;
	}
	
	/**
	 * @param capabilities the capabilities to set
	 */
	public void setCapabilities(HashSet<String> capabilities) {
		this.capabilities = capabilities;
	}
	
	/**
	 * @return the privilegeLevel
	 */
	public String getPrivilegeLevel() {
		return privilegeLevel;
	}
	
	/**
	 * @param privilegeLevel the privilegeLevel to set
	 */
	public void setPrivilegeLevel(String privilegeLevel) {
		this.privilegeLevel = privilegeLevel;
	}
	
	/**
	 * @return the interactsWithPatients
	 */
	public boolean isInteractsWithPatients() {
		return interactsWithPatients;
	}
	
	/**
	 * @param interactsWithPatients the interactsWithPatients to set
	 */
	public void setInteractsWithPatients(boolean interactsWithPatients) {
		this.interactsWithPatients = interactsWithPatients;
	}
	
	/**
	 * @return the providerIdentifier
	 */
	public String getProviderIdentifier() {
		return providerIdentifier;
	}
	
	/**
	 * @param providerIdentifier the providerIdentifier to set
	 */
	public void setProviderIdentifier(String providerIdentifier) {
		this.providerIdentifier = providerIdentifier;
	}
	
	/**
	 * @return the type
	 */
	public String getType() {
		return type;
	}
	
	/**
	 * @param type the type to set
	 */
	public void setType(String type) {
		this.type = type;
	}
	
	private void setPersonDetails() {
		if (person != null) {
			givenName = person.getGivenName();
			familyName = person.getFamilyName();
			gender = person.getGender();
		}
	}
	
	private void setUserDetails() {
		if (user != null) {
			setUsername(user.getUsername());
			setRetired(user.isRetired());
			
			//TODO set privilege level
			
			setCapabilities(new HashSet<String>());
			for (Role role : user.getAllRoles()) {
				getCapabilities().add(role.getName());
			}
		}
	}
	
	private void setProviderDetails() {
		if (provider != null) {
			//TODO set interacts with users and type
			
			setProviderIdentifier(provider.getIdentifier());
		}
	}
	
	/**
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	@Override
	public boolean equals(Object obj) {
		// TODO Add some smarter code that will prevent duplicate 
		//accounts in case the provider has a user account
		return super.equals(obj);
	}
	
	/**
	 * @see java.lang.Object#hashCode()
	 */
	@Override
	public int hashCode() {
		// TODO implement this as per equals() method above
		return super.hashCode();
	}
	
}
