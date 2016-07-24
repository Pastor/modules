package ru.phi.modules.rest;

import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runners.MethodSorters;
import org.springframework.http.MediaType;
import ru.phi.modules.entity.Accessibility;
import ru.phi.modules.entity.Profile;

import java.lang.reflect.Field;

import static junit.framework.TestCase.assertEquals;
import static org.hamcrest.Matchers.is;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public final class MeControllerTest extends AbstractRestTest {

    @Test
    public void me() throws Exception {
        final String accessToken = newToken("read:profile");
        get("/rest/v1/me", accessToken)
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8))
                .andExpect(jsonPath("$.email", is(successProfile.getEmail())));
    }


    @Test
    public void meWithoutProfile() throws Exception {
        final String accessToken = newTokenWithoutProfile("read:profile");
        get("/rest/v1/me", accessToken)
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8))
                .andExpect(jsonPath("$.email", is(successUserWithoutProfile.getEmail())));
    }

    @Test
    public void updateMe() throws Exception {
        final String accessToken = newToken("write:profile");
        final Profile profile = profileRepository.findOne(successProfile.getId());
        profile.setMiddleName("Иванович");
        update("/rest/v1/me", accessToken, profile).andExpect(status().isNoContent());
        final Profile profile2 = profileRepository.findOne(successProfile.getId());
        assertEquals(profile.getEmail(), successProfile.getEmail());
        assertEquals(profile.getEmail(), profile2.getEmail());
        assertEquals(profile2.getMiddleName(), "Иванович");
    }

    @Test
    public void updateMeWithoutAccessibility() throws Exception {
        final String accessToken = newToken("write:profile");
        final Profile profile = profileRepository.findOne(successProfile.getId());
        final Field accessibility = Profile.class.getDeclaredField("accessibility");
        accessibility.setAccessible(true);
        accessibility.set(profile, null);
        update("/rest/v1/me", accessToken, profile).andExpect(status().isBadRequest());
    }

    @Test
    public void updateMeWithQuality() throws Exception {
        final String accessToken = newToken("write:profile");
        final Profile profile = profileRepository.findOne(successProfile.getId());
        profile.setQuality(createQuality("NAME", "TEMPLATE", Accessibility.eyeless));
        update("/rest/v1/me", accessToken, profile).andExpect(status().isNoContent());
        final Profile profile2 = profileRepository.findOne(successProfile.getId());
        assertEquals(profile2.getEmail(), successProfile.getEmail());
        assertEquals(profile2.getQuality().getName(), "NAME");
        assertEquals(profile2.getQuality().getTemplate(), "TEMPLATE");
        assertEquals(profile2.getQuality().getAccessibility(), Accessibility.eyeless);
    }

    @Test
    public void updateMeWithoutProfile() throws Exception {
        final String accessToken = newTokenWithoutProfile("write:profile");
        final Profile profile = new Profile();
        profile.setMiddleName("Иванович");
        update("/rest/v1/me", accessToken, profile).andExpect(status().isNoContent());
        final Profile profile2 = profileRepository.findOne(userRepository.findOne(successUserWithoutProfile.getId()).getProfile().getId());
        assertEquals(profile.getEmail(), successUserWithoutProfile.getEmail());
        assertEquals(profile.getEmail(), profile2.getEmail());
        assertEquals(profile2.getMiddleName(), "Иванович");
    }

    @Test
    public void updateMeWithoutProfileScope() throws Exception {
        final String accessToken = newTokenWithoutProfile("empty");
        final Profile profile = new Profile();
        profile.setMiddleName("Иванович");
        update("/rest/v1/me", accessToken, profile).andExpect(status().isForbidden());
    }

    @Test
    public void meWithoutProfileScope() throws Exception {
        final String accessToken = newTokenWithoutProfile("empty");
        get("/rest/v1/me", accessToken)
                .andExpect(status().isForbidden());
    }

    @Test
    public void getSettings() throws Exception {
        final String accessToken = newToken("read:profile", "read:settings");
        get("/rest/v1/me/settings", accessToken)
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8))
                .andExpect(jsonPath("$.route_type", is("best")));
    }

    @Test
    public void getSettingsWithoutProfileScope() throws Exception {
        final String accessToken = newToken("read:settings");
        get("/rest/v1/me/settings", accessToken)
                .andExpect(status().isForbidden());
    }

    @Test
    public void getSettingsWithoutProfile() throws Exception {
        final String accessToken = newTokenWithoutProfile("read:profile", "read:settings");
        final int length = get("/rest/v1/me/settings", accessToken)
                .andExpect(status().isOk())
                .andReturn().getResponse().getContentLength();
        assertEquals(length, 0);
    }

    @Test
    public void getSettingsWithoutSettingsScope() throws Exception {
        final String accessToken = newToken("read:profile");
        get("/rest/v1/me/settings", accessToken)
                .andExpect(status().isForbidden());
    }
//
//    @Test
//    public void updateSettings() throws Exception {
//        final Token token = newToken("profile", "settings");
//        Settings settings = environment.meSettings(token.getKey());
//        assertNotNull(settings);
//        assertNull(settings.getFilter());
//        settings.setFilter("FILTER");
//        environment.putSettings(settings, token.getKey());
//        final Settings settings2 = environment.meSettings(token.getKey());
//        assertNotNull(settings2);
//        assertNotNull(settings2.getFilter());
//        assertEquals(settings2.getFilter(), "FILTER");
//        assertEquals(settings, settings2);
//    }
//
//    @Test
//    public void updateSettingsWithQualities() throws Exception {
//        final Token token = newToken("profile", "settings");
//        Settings settings = environment.meSettings(token.getKey());
//        assertNotNull(settings);
//        assertNull(settings.getFilter());
//        settings.setQuality(createQuality("QUALITY", "TEMPLATE", Accessibility.normal));
//        environment.putSettings(settings, token.getKey());
//        final Settings settings2 = environment.meSettings(token.getKey());
//        assertNotNull(settings2);
//        assertNotNull(settings2.getQuality());
//        assertEquals(settings2.getQuality().getName(), "QUALITY");
//        assertEquals(settings2.getQuality().getAccessibility(), Accessibility.normal);
//        assertEquals(settings2.getQuality().getTemplate(), "TEMPLATE");
//    }
//
//    @Test
//    public void updateSettingsWithoutProfile() throws Exception {
//        final Token token = newTokenWithoutProfile("profile", "settings");
//        final Settings settings = new Settings();
//        settings.setQuality(createQuality("QUALITY", "TEMPLATE", Accessibility.normal));
//        environment.putSettings(settings, token.getKey());
//        final Settings settings2 = environment.meSettings(token.getKey());
//        assertNull(settings2);
//    }
//
//    @Test
//    public void updateNewSettings() throws Exception {
//        final Token token = newToken("profile", "settings");
//        final Settings settings = new Settings();
//        settings.setQuality(createQuality("QUALITY", "TEMPLATE", Accessibility.normal));
//        environment.putSettings(settings, token.getKey());
//        final Settings settings2 = environment.meSettings(token.getKey());
//        assertNotNull(settings2);
//        assertNotNull(settings2.getQuality());
//        assertEquals(settings2.getQuality().getName(), "QUALITY");
//        assertEquals(settings2.getQuality().getAccessibility(), Accessibility.normal);
//        assertEquals(settings2.getQuality().getTemplate(), "TEMPLATE");
//    }
//
//    @Test(expected = AuthenticationException.class)
//    public void updateSettingsWithoutSettingsScope() throws Exception {
//        final Token token = newToken("profile", "settings");
//        Settings settings = environment.meSettings(token.getKey());
//        assertNotNull(settings);
//        assertNull(settings.getFilter());
//        settings.setFilter("FILTER");
//        final Token tokenWithoutScope = newToken("profile");
//        environment.putSettings(settings, tokenWithoutScope.getKey());
//        assertTrue(false);
//    }
}