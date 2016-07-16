package ru.phi.modules;

import org.junit.Test;
import ru.phi.modules.entity.Profile;
import ru.phi.modules.entity.Token;
import ru.phi.modules.exceptions.AuthenticationException;

import static junit.framework.TestCase.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;

public final class SecurityControllerIntegrationTest extends AbstractRestTest {

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
        final Token token = newToken();
        assertNotNull(token);
        assertNotNull(token.getExpiredAt());
    }

    @Test
    public void updateToken() throws Exception {
        Token token = newToken();
        assertNotNull(token);
        assertNull(token.getScopes());
        environment.putUpdate(token.getKey(), "profile");
        environment.me(token.getKey());
    }

    @Test
    public void successToken() throws Exception {
        final Token token = newToken("profile");
        final Profile profile = environment.me(token.getKey());
        assertEquals(profile.getEmail(), successProfile.getEmail());
    }

    @Test(expected = AuthenticationException.class)
    public void faultScopedToken() throws Exception {
        final Token token = newToken();
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

    public void createToken() throws Exception {
        final Token token = newToken();
        assertEquals(token.getScopes().size(), 0);
        final Token token2 = newToken("profile");
        assertEquals(token2.getScopes().size(), 1);
        assertEquals(token.getScopes().size(), 0);
        final Token token3 = newToken();
        assertEquals(token3.getScopes().size(), 0);
        assertEquals(token2.getScopes().size(), 1);
    }
}
