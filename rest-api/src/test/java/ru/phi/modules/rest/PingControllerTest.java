package ru.phi.modules.rest;

import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runners.MethodSorters;
import ru.phi.modules.AbstractRestTest;
import ru.phi.modules.entity.Token;
import ru.phi.modules.exceptions.AuthenticationException;

import static org.junit.Assert.assertEquals;

@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public final class PingControllerTest extends AbstractRestTest {

    @Test
    public void scoped() throws Exception {
        final Token token = newToken("ping");
        final String result = environment.pingScope(token.getKey());
        assertEquals(result, "pong");
    }

    @Test
    public void cleared() throws Exception {
        final String result = environment.pingClear();
        assertEquals(result, "pong");
    }

    @Test
    public void authorized() throws Exception {
        final Token token = newToken();
        final String result = environment.pingAuthorized(token.getKey());
        assertEquals(result, "pong");
    }

    @Test(expected = AuthenticationException.class)
    public void authorizedFault() throws Exception {
        final String result = environment.pingAuthorizedFault();
        assertEquals(result, "pong");
    }
}