package ru.phi.modules.rest;

import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runners.MethodSorters;
import ru.phi.modules.AbstractRestTest;
import ru.phi.modules.entity.Accessibility;
import ru.phi.modules.entity.Profile;
import ru.phi.modules.entity.Settings;
import ru.phi.modules.entity.Token;
import ru.phi.modules.exceptions.AuthenticationException;

import static junit.framework.TestCase.*;

@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public final class MeControllerTest extends AbstractRestTest {


    @Test
    public void me() throws Exception {
        final Token token = newToken("profile");
        final Profile profile = environment.me(token.getKey());
        assertEquals(profile.getEmail(), successProfile.getEmail());
    }

    @Test
    public void meWithoutProfile() throws Exception {
        final Token token = newTokenWithoutProfile("profile");
        final Profile profile = environment.me(token.getKey());
        assertEquals(profile.getEmail(), successUserWithoutProfile.getEmail());
    }

    @Test
    public void updateMe() throws Exception {
        final Token token = newToken("profile");
        final Profile profile = environment.me(token.getKey());
        profile.setMiddleName("Иванович");
        environment.putMe(profile, token.getKey());
        final Profile profile2 = environment.me(token.getKey());
        assertEquals(profile.getEmail(), successProfile.getEmail());
        assertEquals(profile.getEmail(), profile2.getEmail());
        assertEquals(profile2.getMiddleName(), "Иванович");
    }

    @Test
    public void updateMeWithQuality() throws Exception {
        final Token token = newToken("profile");
        final Profile profile = environment.me(token.getKey());
        profile.setQuality(createQuality("NAME", "TEMPLATE", Accessibility.EYELESS));
        environment.putMe(profile, token.getKey());
        final Profile profile2 = environment.me(token.getKey());
        assertEquals(profile2.getEmail(), successProfile.getEmail());
        assertEquals(profile2.getQuality().getName(), "NAME");
        assertEquals(profile2.getQuality().getTemplate(), "TEMPLATE");
        assertEquals(profile2.getQuality().getAccessibility(), Accessibility.EYELESS);
    }

    @Test
    public void updateMeWithoutProfile() throws Exception {
        final Token token = newTokenWithoutProfile("profile");
        final Profile profile = new Profile();
        profile.setMiddleName("Иванович");
        environment.putMe(profile, token.getKey());
        final Profile profile2 = environment.me(token.getKey());
        assertEquals(profile.getEmail(), successUserWithoutProfile.getEmail());
        assertEquals(profile.getEmail(), profile2.getEmail());
        assertEquals(profile2.getMiddleName(), "Иванович");
    }

    @Test(expected = AuthenticationException.class)
    public void updateMeWithoutProfileScope() throws Exception {
        final Token token = newToken("profile");
        final Profile profile = environment.me(token.getKey());
        profile.setMiddleName("Иванович");
        final Token withoutScope = newToken();
        environment.putMe(profile, withoutScope.getKey());
        assertTrue(false);
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

    @Test
    public void getSettingsWithoutProfile() throws Exception {
        final Token token = newTokenWithoutProfile("settings", "profile");
        final Settings settings = environment.meSettings(token.getKey());
        assertNull(settings);
    }

    @Test(expected = AuthenticationException.class)
    public void getSettingsWithoutSettingsScope() throws Exception {
        final Token token = newToken("profile");
        environment.meSettings(token.getKey());
        assertTrue(false);
    }

    @Test
    public void updateSettings() throws Exception {
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

    @Test
    public void updateSettingsWithQualities() throws Exception {
        final Token token = newToken("profile", "settings");
        Settings settings = environment.meSettings(token.getKey());
        assertNotNull(settings);
        assertNull(settings.getFilter());
        settings.setQuality(createQuality("QUALITY", "TEMPLATE", Accessibility.NORMAL));
        environment.putSettings(settings, token.getKey());
        final Settings settings2 = environment.meSettings(token.getKey());
        assertNotNull(settings2);
        assertNotNull(settings2.getQuality());
        assertEquals(settings2.getQuality().getName(), "QUALITY");
        assertEquals(settings2.getQuality().getAccessibility(), Accessibility.NORMAL);
        assertEquals(settings2.getQuality().getTemplate(), "TEMPLATE");
    }

    @Test
    public void updateNewSettings() throws Exception {
        final Token token = newToken("profile", "settings");
        final Settings settings = new Settings();
        settings.setQuality(createQuality("QUALITY", "TEMPLATE", Accessibility.NORMAL));
        environment.putSettings(settings, token.getKey());
        final Settings settings2 = environment.meSettings(token.getKey());
        assertNotNull(settings2);
        assertNotNull(settings2.getQuality());
        assertEquals(settings2.getQuality().getName(), "QUALITY");
        assertEquals(settings2.getQuality().getAccessibility(), Accessibility.NORMAL);
        assertEquals(settings2.getQuality().getTemplate(), "TEMPLATE");
    }

    @Test(expected = AuthenticationException.class)
    public void updateSettingsWithoutSettingsScope() throws Exception {
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