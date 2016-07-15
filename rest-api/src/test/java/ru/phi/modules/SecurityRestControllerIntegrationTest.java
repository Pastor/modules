package ru.phi.modules;

import org.junit.Test;
import ru.phi.modules.entity.Profile;
import ru.phi.modules.entity.Token;
import ru.phi.modules.exceptions.AuthenticationException;

import static junit.framework.TestCase.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;

public final class SecurityRestControllerIntegrationTest extends AbstractRestTest {

    @Test(expected = AuthenticationException.class)
    public void faultUpdate() throws Exception {
        environment.postUpdate("username", "password");
    }

    @Test(expected = AuthenticationException.class)
    public void emptyUpdate() throws Exception {
        environment.postUpdate();
    }

    @Test
    public void successUpdate() throws Exception {
        final Token token = token();
        assertNotNull(token);
        assertNotNull(token.getExpiredAt());
    }

    @Test
    public void updateToken() throws Exception {
        Token token = token();
        assertNotNull(token);
        assertNull(token.getScopes());
        environment.putUpdate(successUser.getUsername(), successUser.getPassword(), "profile");
        token = environment.postUpdate(successUser.getUsername(), successUser.getPassword());
        assertNotNull(token);
        assertNotNull(token.getScopes());
        assertEquals(token.getScopes().size(), 1);
        assertEquals(token.getScopes().iterator().next().getName(), "profile");
    }

    @Test
    public void successToken() throws Exception {
        final Token token = token("profile");
        final Profile profile = environment.me(token.getKey());
        assertEquals(profile.getEmail(), successProfile.getEmail());
    }

    @Test(expected = AuthenticationException.class)
    public void faultScopedToken() throws Exception {
        final Token token = token();
        final Profile profile = environment.me(token.getKey());
        assertNotNull(profile);
    }

    @Test(expected = AuthenticationException.class)
    public void faultToken() throws Exception {
        final Profile profile = environment.me("000000000000000000000000000");
        assertNotNull(profile);
    }

    @Test(expected = AuthenticationException.class)
    public void emptyToken() throws Exception {
        final Profile profile = environment.me("");
        assertNotNull(profile);
    }
}
