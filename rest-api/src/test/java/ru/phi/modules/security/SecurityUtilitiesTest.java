package ru.phi.modules.security;

import org.junit.Test;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.context.SecurityContextImpl;
import ru.phi.modules.entity.Token;

import java.util.Optional;

import static org.junit.Assert.*;

public final class SecurityUtilitiesTest {

    @Test
    public void currentTokenWithoutAuthentication() throws Exception {
        final SecurityContextImpl context = new SecurityContextImpl();
        SecurityContextHolder.setContext(context);
        context.setAuthentication(null);
        assertEquals(Optional.empty(), SecurityUtilities.currentToken());
    }

    @Test
    public void currentTokenWithIllegalAuthentication() throws Exception {
        final SecurityContextImpl context = new SecurityContextImpl();
        SecurityContextHolder.setContext(context);
        context.setAuthentication(new UsernamePasswordAuthenticationToken(null, null));
        assertEquals(Optional.empty(), SecurityUtilities.currentToken());
    }

    @Test
    public void currentTokenWithAuthentication() throws Exception {
        final SecurityContextImpl context = new SecurityContextImpl();
        SecurityContextHolder.setContext(context);
        final Token token = new Token();
        context.setAuthentication(new AuthenticationWithToken(token, null, null, null));
        assertEquals(Optional.of(token), SecurityUtilities.currentToken());
    }

    @Test
    public void generateTokenKey() throws Exception {
        final String key = SecurityUtilities.generateTokenKey();
        assertNotNull(key);
        assertFalse(key.isEmpty());
        assertTrue(key.length() >= 256/4);
    }
}