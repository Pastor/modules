package ru.phi.modules.rest;

import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runners.MethodSorters;
import ru.phi.modules.AbstractRestTest;
import ru.phi.modules.entity.Profile;
import ru.phi.modules.entity.Settings;
import ru.phi.modules.entity.Token;
import ru.phi.modules.exceptions.AuthenticationException;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertNull;
import static junit.framework.TestCase.assertNotNull;
import static junit.framework.TestCase.assertTrue;

@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public final class MeRestControllerTest extends AbstractRestTest {

    @Test
    public void me() throws Exception {
        final Token token = newToken("profile");
        final Profile profile = environment.me(token.getKey());
        assertEquals(profile.getEmail(), successProfile.getEmail());
    }

    @Test(expected = AuthenticationException.class)
    public void meWithoutProfileScope() throws Exception {
        final Token token = newToken();
        environment.me(token.getKey());
        assertTrue(false);
    }

    @Test
    public void getSettings() throws Exception {
        final Token token = newToken("profile", "settings");
        final Settings settings = environment.meSettings(token.getKey());
        assertNotNull(settings);
    }

    @Test(expected = AuthenticationException.class)
    public void getSettingsWithoutProfileScope() throws Exception {
        final Token token = newToken("settings");
        environment.meSettings(token.getKey());
        assertTrue(false);
    }

    @Test(expected = AuthenticationException.class)
    public void getSettingsWithoutSettingsScope() throws Exception {
        final Token token = newToken("profile");
        environment.meSettings(token.getKey());
        assertTrue(false);
    }

    @Test
    public void putSettings() throws Exception {
        final Token token = newToken("profile", "settings");
        Settings settings = environment.meSettings(token.getKey());
        assertNotNull(settings);
        assertNull(settings.getFilter());
        settings.setFilter("FILTER");
        environment.putSettings(settings, token.getKey());
        final Settings settings2 = environment.meSettings(token.getKey());
        assertNotNull(settings2);
        assertNotNull(settings2.getFilter());
        assertEquals(settings2.getFilter(), "FILTER");

        assertEquals(settings, settings2);
    }

    @Test(expected = AuthenticationException.class)
    public void putSettingsWithoutSettingsScope() throws Exception {
        final Token token = newToken("profile", "settings");
        Settings settings = environment.meSettings(token.getKey());
        assertNotNull(settings);
        assertNull(settings.getFilter());
        settings.setFilter("FILTER");
        final Token tokenWithoutScope = newToken("profile");
        environment.putSettings(settings, tokenWithoutScope.getKey());
        assertTrue(false);
    }
}