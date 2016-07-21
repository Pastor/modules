package ru.phi.modules.rest;

import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runners.MethodSorters;
import ru.phi.modules.AbstractRestTest;
import ru.phi.modules.entity.Scope;
import ru.phi.modules.entity.Token;

import java.util.List;

import static org.junit.Assert.assertEquals;

@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public final class ScopesControllerTest extends AbstractRestTest {

    @Test
    public void list() throws Exception {
        final Token token = newToken();
        final List<String> scopes = environment.scopes(token.getKey());
        final List<Scope> byRole = scp.findByRole(successUser.getRole());
        assertEquals(scopes.size(), byRole.size());
    }
}