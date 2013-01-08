package org.openmrs.module.emr.account;

import org.openmrs.Person;
import org.openmrs.Provider;
import org.openmrs.Role;
import org.openmrs.User;
import org.openmrs.api.UserService;
import org.openmrs.module.emr.EmrConstants;
import org.openmrs.module.emr.utils.GeneralUtils;
import org.openmrs.util.OpenmrsConstants;
import org.openmrs.util.OpenmrsUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;

import java.util.HashSet;
import java.util.Locale;
import java.util.Set;

/**
 * Acts a wrapper for a Person, User and Provider
 */
public class Account {

    @Qualifier("userService")
    @Autowired
    UserService userService;
	
	private Person person;
	
	private User user;
	
	private Provider provider;
	
	private String givenName;
	
	private String familyName;
	
	private String gender;
	
	private boolean enabled;
	
	private String username;
	
	private String password;
	
	private String confirmPassword;

    private Locale defaultLocale;
	
	private Set<Role> capabilities;
	
	private Role privilegeLevel;
	
	private String secretQuestion;
	
	private String secretAnswer;
	
	private boolean interactsWithPatients;
	
	private String providerIdentifier;
	
	public Account(Person person, UserService userService) {
		this.person = person;
        this.userService = userService;
		setPersonDetails();
	}
	
	public Account(User user, Provider provider, UserService userService) {
        this.userService = userService;

		if (user == null && provider == null)
			throw new IllegalArgumentException("Both user and provider cannot be null");
		else if (user != null && provider != null) {
			if (!OpenmrsUtil.nullSafeEquals(user.getPerson(), provider.getPerson()))
				throw new IllegalArgumentException("The person objects for user and provider should match");
		}
		
		person = (user != null) ? user.getPerson() : provider.getPerson();
		setPersonDetails();
		if (user != null) {
			this.user = user;
			setUsername(user.getUsername());
			setEnabled(!user.isRetired());
			setSecretQuestion(user.getSecretQuestion());
            setDefaultLocale(GeneralUtils.getDefaultLocale(user));
			
			setCapabilities(new HashSet<Role>());
			for (Role role : user.getAllRoles()) {
				if (role.getName().startsWith(EmrConstants.ROLE_PREFIX_CAPABILITY))
					getCapabilities().add(role);
				else if (role.getName().startsWith(EmrConstants.ROLE_PREFIX_PRIVILEGE_LEVEL))
					setPrivilegeLevel(role);
			}
		}
		if (provider != null) {
			this.provider = provider;
			interactsWithPatients = !provider.isRetired();
			providerIdentifier = provider.getIdentifier();
		}
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
	 * @return the enabled
	 */
	public boolean getEnabled() {
		return enabled;
	}
	
	/**
	 * @param enabled the enabled to set
	 */
	public void setEnabled(boolean enabled) {
		this.enabled = enabled;
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
     * @param defaultLocale
     */
    public void setDefaultLocale(Locale defaultLocale) {
        this.defaultLocale = defaultLocale;
    }

    /**
     * @return defaultLocale
     */
    public Locale getDefaultLocale() {
        return defaultLocale;
    }

    /**
	 * @return the capabilities
	 */
	public Set<Role> getCapabilities() {
		if (capabilities == null)
			capabilities = new HashSet<Role>();
		return capabilities;
	}
	
	/**
	 * @param capabilities the capabilities to set
	 */
	public void setCapabilities(Set<Role> capabilities) {
		this.capabilities = capabilities;
	}
	
	/**
	 * @return the privilegeLevel
	 */
	public Role getPrivilegeLevel() {
		return privilegeLevel;
	}
	
	/**
	 * @param privilegeLevel the privilegeLevel to set
	 */
	public void setPrivilegeLevel(Role privilegeLevel) {
		this.privilegeLevel = privilegeLevel;
	}
	
	/**
	 * @return the secretQuestion
	 */
	public String getSecretQuestion() {
		return secretQuestion;
	}
	
	/**
	 * @param secretQuestion the secretQuestion to set
	 */
	public void setSecretQuestion(String secretQuestion) {
		this.secretQuestion = secretQuestion;
	}
	
	/**
	 * @return the secretAnswer
	 */
	public String getSecretAnswer() {
		return secretAnswer;
	}
	
	/**
	 * @param secretAnswer the secretAnswer to set
	 */
	public void setSecretAnswer(String secretAnswer) {
		this.secretAnswer = secretAnswer;
	}
	
	/**
	 * @return the interactsWithPatients
	 */
	public boolean getInteractsWithPatients() {
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
	 * Synchronizes the account properties with their corresponding ones on the underlying Provider,
	 * Person and User instances except the capabilities
	 */
	public void syncProperties() {
		person.getPersonName().setFamilyName(familyName);
		person.getPersonName().setGivenName(givenName);
		person.setGender(gender);
		
		if (user != null) {
			if (user.getPerson() == null)
				user.setPerson(person);
			user.setUsername(username);
			user.setRetired(!enabled);
			user.setSecretQuestion(secretQuestion);
		}
		
		if (provider != null) {
			if (provider.getPerson() == null)
				provider.setPerson(person);
			provider.setIdentifier(providerIdentifier);
			provider.setRetired(!interactsWithPatients);
		}
	}
	
	private void setPersonDetails() {
		givenName = person.getGivenName();
		familyName = person.getFamilyName();
		gender = person.getGender();
	}
	
	/**
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	@Override
	public boolean equals(Object obj) {
		if (obj == null)
			return false;
		return OpenmrsUtil.nullSafeEquals(this.getPerson(), ((Account) obj).getPerson());
	}
	
	/**
	 * @see java.lang.Object#hashCode()
	 */
	@Override
	public int hashCode() {
		if (getPerson() == null)
			return super.hashCode();
		return getPerson().hashCode();
	}


    public boolean isLocked() {
        if (user == null) {
            return false;
        }
        String lockoutTimeProperty = user.getUserProperty(OpenmrsConstants.USER_PROPERTY_LOCKOUT_TIMESTAMP);
        if (lockoutTimeProperty != null) {
            try {
                Long lockedOutUntil = Long.valueOf(lockoutTimeProperty) + 300000;
                return System.currentTimeMillis() < lockedOutUntil;
            } catch (NumberFormatException ex) {
                return false;
            }
        }
        return false;
    }

    /**
     * Unlocks this account (in case it has been locked for getting the password wrong too many times), and saves that
     * to the database.
     */
    public void unlock() {
        if (user == null) {
            throw new IllegalStateException("Cannot unlock an account that doesn't have a user");
        }
        user.removeUserProperty(OpenmrsConstants.USER_PROPERTY_LOCKOUT_TIMESTAMP);
        user.removeUserProperty(OpenmrsConstants.USER_PROPERTY_LOGIN_ATTEMPTS);
        userService.saveUser(user, null);
    }

}
