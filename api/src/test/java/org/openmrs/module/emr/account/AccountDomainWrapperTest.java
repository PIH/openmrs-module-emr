package org.openmrs.module.emr.account;

import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyString;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.openmrs.util.OpenmrsConstants.USER_PROPERTY_LOCKOUT_TIMESTAMP;
import static org.openmrs.util.OpenmrsConstants.USER_PROPERTY_LOGIN_ATTEMPTS;

import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.HashSet;
import java.util.Locale;
import java.util.Set;

import junit.framework.Assert;

import org.apache.commons.lang.time.DateUtils;
import org.junit.Before;
import org.junit.Test;
import org.openmrs.Person;
import org.openmrs.PersonName;
import org.openmrs.Provider;
import org.openmrs.Role;
import org.openmrs.User;
import org.openmrs.api.PersonService;
import org.openmrs.api.ProviderService;
import org.openmrs.api.UserService;
import org.openmrs.module.emr.EmrConstants;
import org.openmrs.util.OpenmrsConstants;

public class AccountDomainWrapperTest {

    private AccountService accountService;

    private UserService userService;

    private ProviderService providerService;

    private PersonService personService;

    private Role fullPrivileges;

    private Role limitedPrivileges;

    private Role receptionApp;

    private Role archiveApp;

    private Role adminApp;

    @Before
    public void setup() {
        accountService = mock(AccountService.class);
        providerService = mock(ProviderService.class);
        userService = mock(UserService.class);
        personService = mock(PersonService.class);

        fullPrivileges  = new Role();
        fullPrivileges.setRole(EmrConstants.ROLE_PREFIX_PRIVILEGE_LEVEL + "Full");
        limitedPrivileges = new Role();
        limitedPrivileges.setRole(EmrConstants.ROLE_PREFIX_PRIVILEGE_LEVEL + "Limited");
        when(accountService.getAllPrivilegeLevels()).thenReturn(Arrays.asList(fullPrivileges, limitedPrivileges));

        receptionApp = new Role();
        receptionApp.setRole(EmrConstants.ROLE_PREFIX_CAPABILITY + "Reception");
        archiveApp = new Role();
        archiveApp.setRole(EmrConstants.ROLE_PREFIX_CAPABILITY + "Archives");
        adminApp = new Role();
        adminApp.setRole(EmrConstants.ROLE_PREFIX_CAPABILITY + "Admin");
        when(accountService.getAllCapabilities()).thenReturn(Arrays.asList(receptionApp, archiveApp, adminApp));

    }

    @Test
    public void settingAccountDomainWrapperShouldSetPerson() {

        AccountDomainWrapper account = initializeNewAccountDomainWrapper(new Person());
        account.setGivenName("Mark");
        account.setFamilyName("Jones");
        account.setGender("M");

        Person person = account.getPerson();
        Assert.assertEquals("Mark", person.getGivenName());
        Assert.assertEquals("Jones", person.getFamilyName());
        Assert.assertEquals("M", person.getGender());
    }

    @Test
    public void gettingAccountDomainWrapperShouldFetchFromPerson() {

        Person person = new Person();
        person.addName(new PersonName());
        person.getPersonName().setGivenName("Mark");
        person.getPersonName().setFamilyName("Jones");
        person.setGender("M");

        AccountDomainWrapper account = initializeNewAccountDomainWrapper(person);
        Assert.assertEquals("Mark", account.getGivenName());
        Assert.assertEquals("Jones", account.getFamilyName());
        Assert.assertEquals("M", account.getGender());
    }

    @Test
    public void settingAccountDomainWrapperShouldSetUser() {

        Person person = new Person();

        AccountDomainWrapper account = initializeNewAccountDomainWrapper(person);
        account.setUsername("mjones");
        account.setDefaultLocale(new Locale("fr"));
        account.setPrivilegeLevel(fullPrivileges);

        Set<Role> capabilities = new HashSet<Role>();
        capabilities.add(archiveApp);
        capabilities.add(receptionApp);
        account.setCapabilities(capabilities);

        User user = account.getUser();
        Assert.assertEquals("mjones", user.getUsername());
        Assert.assertEquals(person, user.getPerson());
        Assert.assertEquals("fr", user.getUserProperty(OpenmrsConstants.USER_PROPERTY_DEFAULT_LOCALE).toString());
        Assert.assertTrue(user.hasRole(fullPrivileges.toString()));
        Assert.assertTrue(user.hasRole(archiveApp.toString()));
        Assert.assertTrue(user.hasRole(receptionApp.toString()));
    }

    @Test
    public void gettingAccountDomainWrapperShouldFetchFromUser() {

        Person person = new Person();
        person.setId(1);
        User user = new User();
        user.setPerson(person);
        user.setUsername("mjones");
        user.setUserProperty(OpenmrsConstants.USER_PROPERTY_DEFAULT_LOCALE, new Locale("fr").toString());
        user.addRole(fullPrivileges);
        user.addRole(archiveApp);
        user.addRole(receptionApp);

        when(userService.getUsersByPerson(eq(person), eq(false))).thenReturn(Collections.singletonList(user));

        AccountDomainWrapper account = initializeNewAccountDomainWrapper(person);
        Assert.assertEquals("mjones", account.getUsername());
        Assert.assertEquals("fr", account.getDefaultLocale().toString());
        Assert.assertEquals(fullPrivileges, account.getPrivilegeLevel());
        Assert.assertTrue(account.getCapabilities().contains(receptionApp));
        Assert.assertTrue(account.getCapabilities().contains(archiveApp));
    }

    @Test
    public void settingAccountDomainWrapperShouldSetProvider() {
        Person person = new Person();

        AccountDomainWrapper account = initializeNewAccountDomainWrapper(person);
        account.setProviderIdentifier("ABC123");

        Provider provider = account.getProvider();
        Assert.assertEquals(person, provider.getPerson());
        Assert.assertEquals("ABC123", provider.getIdentifier());
    }

    @Test
    public void gettingAccountDomainWrapperShouldFetchFromProvider() {

        Person person = new Person();
        person.setId(1);
        Provider provider = new Provider();
        provider.setPerson(person);
        provider.setIdentifier("ABC123");

        when(providerService.getProvidersByPerson(eq(person), eq(false))).thenReturn(Collections.singletonList(provider));

        AccountDomainWrapper account = initializeNewAccountDomainWrapper(person);
        Assert.assertEquals("ABC123", account.getProviderIdentifier());
    }

    @Test
    public void testCreatingPersonWithoutCreatingProviderAndUser() {

        Person person = new Person();

        AccountDomainWrapper account = initializeNewAccountDomainWrapper(person);
        account.setGivenName("Mark");
        account.setFamilyName("Jones");
        account.setGender("M");

        // mimic spring binding blanks
        account.setCapabilities(null);
        account.setDefaultLocale(null);
        account.setPrivilegeLevel(null);
        account.setUsername("");
        account.setProviderIdentifier("");
        account.setProviderEnabled(false);

        // make sure the person has been created, but not the user or provider
        Assert.assertNotNull(account.getPerson());
        Assert.assertNull(account.getUser());
        Assert.assertNull(account.getProvider());
    }

    @Test
    public void shouldDisableExistingProviderAccount() {

        Person person = new Person();
        person.setId(1);
        Provider provider = new Provider();
        provider.setPerson(person);
        provider.setIdentifier("ABC123");

        when(providerService.getProvidersByPerson(eq(person), eq(false))).thenReturn(Collections.singletonList(provider));

        AccountDomainWrapper account = initializeNewAccountDomainWrapper(person);
        account.setProviderEnabled(false);

        Assert.assertTrue(provider.getRetired());
        // TODO: figure out how to set retired by
        //Assert.assertNotNull(provider.getRetiredBy());
        Assert.assertNotNull(provider.getRetireReason());
        Assert.assertNotNull(provider.getDateRetired());
    }

    @Test
    public void shouldEnablePreviouslyRetiredProviderAccount() {

        Person person = new Person();
        person.setId(1);
        Provider provider = new Provider();
        provider.setPerson(person);
        provider.setIdentifier("ABC123");
        provider.setRetired(true);
        // TODO: figure out how to handle retired by
        //provider.setRetiredBy();
        provider.setDateRetired(new Date());
        provider.setRetireReason("test");

        when(providerService.getProvidersByPerson(eq(person), eq(false))).thenReturn(Collections.singletonList(provider));

        AccountDomainWrapper account = initializeNewAccountDomainWrapper(person);
        account.setProviderEnabled(true);

        Assert.assertFalse(provider.getRetired());
        Assert.assertNull(provider.getRetiredBy());
        Assert.assertNull(provider.getRetireReason());
        Assert.assertNull(provider.getDateRetired());
    }

    @Test
    public void shouldEnableNewProviderAccount() {

        Person person = new Person();

        AccountDomainWrapper account = initializeNewAccountDomainWrapper(person);
        account.setProviderEnabled(true);

        Assert.assertNotNull(account.getProvider());
    }

    @Test
    public void shouldReturnFalseIfProviderRetired() {

        Person person = new Person();
        person.setId(1);
        Provider provider = new Provider();
        provider.setPerson(person);
        provider.setRetired(true);

        when(providerService.getProvidersByPerson(eq(person), eq(false))).thenReturn(Collections.singletonList(provider));

        AccountDomainWrapper account = initializeNewAccountDomainWrapper(person);
        Assert.assertFalse(account.getProviderEnabled());

    }

    @Test
    public void shouldReturnTrueIfProviderNotRetired() {

        Person person = new Person();
        person.setId(1);
        Provider provider = new Provider();
        provider.setPerson(person);

        when(providerService.getProvidersByPerson(eq(person), eq(false))).thenReturn(Collections.singletonList(provider));

        AccountDomainWrapper account = initializeNewAccountDomainWrapper(person);
        Assert.assertTrue(account.getProviderEnabled());

    }

    @Test
    public void shouldReturnNullIfNoProvider() {

        Person person = new Person();

        AccountDomainWrapper account = initializeNewAccountDomainWrapper(person);
        Assert.assertNull(account.getProviderEnabled());

    }

    @Test
    public void shouldDisableExistingUserAccount() {

        Person person = new Person();
        person.setId(1);
        User user = new User();
        user.setPerson(person);
        user.setUsername("mjones");

        when(userService.getUsersByPerson(eq(person), eq(false))).thenReturn(Collections.singletonList(user));

        AccountDomainWrapper account = initializeNewAccountDomainWrapper(person);
        account.setUserEnabled(false);

        Assert.assertTrue(user.getRetired());
        // TODO: figure out how to set retired by
        //Assert.assertNotNull(user.getRetiredBy());
        Assert.assertNotNull(user.getRetireReason());
        Assert.assertNotNull(user.getDateRetired());
    }

    @Test
    public void shouldEnablePreviouslyRetiredUserAccount() {

        Person person = new Person();
        person.setId(1);
        User user = new User();
        user.setPerson(person);
        user.setUsername("mjones");
        user.setRetired(true);
        // TODO: figure out how to handle retired by
        //provider.setRetiredBy();
        user.setDateRetired(new Date());
        user.setRetireReason("test");

        when(userService.getUsersByPerson(eq(person), eq(false))).thenReturn(Collections.singletonList(user));

        AccountDomainWrapper account = initializeNewAccountDomainWrapper(person);
        account.setUserEnabled(true);

        Assert.assertFalse(user.getRetired());
        Assert.assertNull(user.getRetiredBy());
        Assert.assertNull(user.getRetireReason());
        Assert.assertNull(user.getDateRetired());
    }

    @Test
    public void shouldEnableNewUserAccount() {

        Person person = new Person();

        AccountDomainWrapper account = initializeNewAccountDomainWrapper(person);
        account.setUserEnabled(true);

        Assert.assertNotNull(account.getUser());
    }

    @Test
    public void shouldReturnFalseIfUserRetired() {

        Person person = new Person();
        person.setId(1);
        User user = new User();
        user.setPerson(person);
        user.setRetired(true);

        when(userService.getUsersByPerson(eq(person), eq(false))).thenReturn(Collections.singletonList(user));

        AccountDomainWrapper account = initializeNewAccountDomainWrapper(person);
        Assert.assertFalse(account.getUserEnabled());

    }

    @Test
    public void shouldReturnTrueIfUserNotRetired() {

        Person person = new Person();
        person.setId(1);
        User user = new User();
        user.setPerson(person);
        user.setRetired(false);

        when(userService.getUsersByPerson(eq(person), eq(false))).thenReturn(Collections.singletonList(user));

        AccountDomainWrapper account = initializeNewAccountDomainWrapper(person);
        Assert.assertTrue(account.getUserEnabled());

    }

    @Test
    public void shouldReturnNullIfNoUser() {

        Person person = new Person();

        AccountDomainWrapper account = initializeNewAccountDomainWrapper(person);
        Assert.assertNull(account.getUserEnabled());

    }

    @Test
    public void shouldChangeExistingProviderIdentifier() {

        Person person = new Person();
        person.setId(1);
        Provider provider = new Provider();
        provider.setPerson(person);
        provider.setIdentifier("ABC123");

        when(providerService.getProvidersByPerson(eq(person), eq(false))).thenReturn(Collections.singletonList(provider));

        AccountDomainWrapper account = initializeNewAccountDomainWrapper(person);
        account.setProviderIdentifier("ZYX654");

        Assert.assertEquals("ZYX654", provider.getIdentifier());

    }

    @Test
    public void shouldSetExistingProviderIdentifierToNull() {

        Person person = new Person();
        person.setId(1);
        Provider provider = new Provider();
        provider.setPerson(person);
        provider.setIdentifier("ABC123");

        when(providerService.getProvidersByPerson(eq(person), eq(false))).thenReturn(Collections.singletonList(provider));

        AccountDomainWrapper account = initializeNewAccountDomainWrapper(person);
        account.setProviderIdentifier(null);

        Assert.assertNull(provider.getIdentifier());

    }

    @Test
    public void shouldChangeExistingUserInformation() {

        Person person = new Person();
        person.setId(1);
        User user = new User();
        user.setPerson(person);
        user.setUsername("mjones");
        user.setUserProperty(OpenmrsConstants.USER_PROPERTY_DEFAULT_LOCALE, new Locale("fr").toString());
        user.addRole(fullPrivileges);
        user.addRole(archiveApp);
        user.addRole(receptionApp);

        when(userService.getUsersByPerson(eq(person), eq(false))).thenReturn(Collections.singletonList(user));

        AccountDomainWrapper account = initializeNewAccountDomainWrapper(person);

        account.setUsername("msmith");
        account.setPrivilegeLevel(limitedPrivileges);
        Set<Role> roles = new HashSet<Role>();
        roles.add(archiveApp);
        roles.add(adminApp);
        account.setCapabilities(roles);

        Assert.assertEquals("msmith", user.getUsername());
        Assert.assertTrue(user.getRoles().contains(limitedPrivileges));
        Assert.assertTrue(user.getRoles().contains(archiveApp));
        Assert.assertTrue(user.getRoles().contains(adminApp));
        Assert.assertFalse(user.getRoles().contains(receptionApp));
        Assert.assertFalse(user.getRoles().contains(fullPrivileges));

    }

    @Test
    public void shouldRemoveAllExistingCapabilities() {

        Person person = new Person();
        person.setId(1);
        User user = new User();
        user.setPerson(person);
        user.setUsername("mjones");
        user.setUserProperty(OpenmrsConstants.USER_PROPERTY_DEFAULT_LOCALE, new Locale("fr").toString());
        user.addRole(fullPrivileges);
        user.addRole(archiveApp);
        user.addRole(receptionApp);

        when(userService.getUsersByPerson(eq(person), eq(false))).thenReturn(Collections.singletonList(user));

        AccountDomainWrapper account = initializeNewAccountDomainWrapper(person);
        account.setCapabilities(null);

        Assert.assertFalse(user.getRoles().contains(receptionApp));
        Assert.assertFalse(user.getRoles().contains(archiveApp));
    }

    @Test
    public void testThatAccountIsNotLockedWhenNeverLocked() throws Exception {
        AccountDomainWrapper account = initializeNewAccountDomainWrapper(new Person());
        account.setUserEnabled(true);
        assertFalse(account.isLocked());
    }

    @Test
    public void testThatAccountIsNotLockedWhenLockedALongTimeAgo() throws Exception {
        AccountDomainWrapper account = initializeNewAccountDomainWrapper(new Person());
        account.setUserEnabled(true);
        account.getUser().setUserProperty(USER_PROPERTY_LOCKOUT_TIMESTAMP, "" + DateUtils.addDays(new Date(), -1).getTime());
        assertFalse(account.isLocked());
    }

    @Test
    public void testThatAccountIsLockedWhenStillLocked() throws Exception {
        AccountDomainWrapper account = initializeNewAccountDomainWrapper(new Person());
        account.setUserEnabled(true);
        account.getUser().setUserProperty(USER_PROPERTY_LOCKOUT_TIMESTAMP, "" + DateUtils.addMinutes(new Date(), 5).getTime());
        assertTrue(account.isLocked());
    }

    @Test
    public void testUnlockingAccount() throws Exception {
        AccountDomainWrapper account = initializeNewAccountDomainWrapper(new Person());
        account.setUserEnabled(true);
        account.getUser().setUserProperty(USER_PROPERTY_LOCKOUT_TIMESTAMP, "" + DateUtils.addMinutes(new Date(), 5).getTime());

        account.unlock();

        assertThat(account.getUser().getUserProperty(USER_PROPERTY_LOCKOUT_TIMESTAMP), is(""));
        assertThat(account.getUser().getUserProperty(USER_PROPERTY_LOGIN_ATTEMPTS), is(""));

        verify(userService).saveUser(account.getUser(), null);
    }


    @Test
    public void testSaveAccountWithOnlyPerson() throws Exception {

        Person person = new Person();

        AccountDomainWrapper account = initializeNewAccountDomainWrapper(person);
        account.save();

        verify(personService).savePerson(person);

        verify(userService, never()).saveUser(any(User.class), anyString());
        verify(userService, never()).changeQuestionAnswer(any(User.class), anyString(), anyString());
        verify(providerService, never()).saveProvider(any(Provider.class));
    }

    @Test
    public void testSaveAccountWithNewPersonUserAndProvider() throws Exception {

        Person person = new Person();

        AccountDomainWrapper account = initializeNewAccountDomainWrapper(person);
        account.setUserEnabled(true);
        account.setProviderEnabled(true);
        account.setPassword("abc");
        account.setConfirmPassword("abc");
        account.save();

        verify(personService).savePerson(person);
        verify(userService).saveUser(account.getUser(), "abc");
        verify(providerService).saveProvider(account.getProvider());

        verify(userService, never()).changePassword(account.getUser(), "abc");
        verify(userService, never()).changeQuestionAnswer(eq(account.getUser()), anyString(), anyString());
    }

    @Test
    public void testSaveAccountWithPasswordChangeForExistingUser() throws Exception {

        Person person = new Person();

        AccountDomainWrapper account = initializeNewAccountDomainWrapper(person);
        account.setUserEnabled(true);
        account.setPassword("abc");
        account.setConfirmPassword("abc");
        account.getUser().setUserId(1);    // mimic making this user persistent
        account.save();

        verify(userService).saveUser(account.getUser(), "abc");
        verify(userService).changePassword(account.getUser(), "abc");

        verify(userService, never()).changeQuestionAnswer(eq(account.getUser()), anyString(), anyString());
    }

    @Test
    public void testSaveAccountWithSettingQuestionAndAnswer() throws Exception {

        Person person = new Person();

        AccountDomainWrapper account = initializeNewAccountDomainWrapper(person);
        account.setUserEnabled(true);
        account.setPassword("abc");
        account.setConfirmPassword("abc");
        account.setSecretQuestion("Who is my favorite person?");
        account.setSecretAnswer("Evan Waters");
        account.save();

        verify(userService).saveUser(account.getUser(), "abc");
        verify(userService).changeQuestionAnswer(account.getUser(), "Who is my favorite person?", "Evan Waters");
    }

    private AccountDomainWrapper initializeNewAccountDomainWrapper(Person person) {
        return new AccountDomainWrapper(person, accountService, userService, providerService, personService);
    }

}
