package ru.phi.modules.rest;

import org.junit.FixMethodOrder;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runners.MethodSorters;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.test.web.servlet.ResultActions;

import static junit.framework.TestCase.assertEquals;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public final class PingControllerTest extends AbstractRestTest {

    @Test
    public void scoped() throws Exception {
        final String accessToken = newToken("read:ping");
        final String content = get("/rest/v1/ping/scope", accessToken)
                .andExpect(status().isOk())
                .andReturn().getResponse().getContentAsString();
        assertEquals(content, "pong");
    }

    @Test
    public void cleared() throws Exception {
        final String content = get("/rest/v1/ping/clear")
                .andExpect(status().isOk())
                .andReturn().getResponse().getContentAsString();
        assertEquals(content, "pong");
    }

    @Test
    public void authorized() throws Exception {
        final String accessToken = newToken("empty");
        final String content = get("/rest/v1/ping/authorized", accessToken)
                .andExpect(status().isOk())
                .andReturn().getResponse().getContentAsString();
        assertEquals(content, "pong");
    }

    @Ignore
    @Test
    public void authorizedFault() throws Exception {
        final String accessToken = newToken("read:ping");
        final ResultActions actions = get("/rest/v1/ping/authorized_fault", accessToken);
        actions
                .andExpect(status().isUnauthorized());
    }
}