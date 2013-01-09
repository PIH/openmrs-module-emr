package org.openmrs.module.emr.utils;

import junit.framework.Assert;
import org.junit.Test;
import org.openmrs.User;
import org.openmrs.util.OpenmrsConstants;

import static org.openmrs.module.emr.utils.GeneralUtils.getDefaultLocale;

public class GeneralUtilsTest {

    @Test
    public void shouldGetDefaultLocaleForUser() {
        User user = new User();
        user.setUserProperty(OpenmrsConstants.USER_PROPERTY_DEFAULT_LOCALE, "ht");
        Assert.assertEquals("ht", getDefaultLocale(user).toString());
    }

}
