import org.junit.After;
import org.junit.Before;
import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runners.MethodSorters;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.web.FilterChainProxy;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;
import ru.phi.modules.entity.User;
import ru.phi.modules.entity.UserRole;
import ru.phi.modules.repository.UserRepository;
import ru.phi.modules.rest.AbstractRestTest;

import static junit.framework.TestCase.assertNotNull;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public final class AccessTokenTest extends AbstractRestTest {

    @Test
    public void test() throws Exception {
        final String accessToken = newToken("read:user");
        assertNotNull(accessToken);
    }

    @Test
    public void greetingUnauthorized() throws Exception {
        // @formatter:off
        get("/greeting")
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.error", is("unauthorized")));
        // @formatter:on
    }

    @Test
    public void meAuthorized() throws Exception {
        String accessToken = newToken("read:user");
        // @formatter:off
        get("/me", accessToken)
                .andExpect(status().isNoContent());
        // @formatter:on
    }

    @Test
    public void greetingAuthorized() throws Exception {
        String accessToken = newToken("read:user");

        // @formatter:off
        get("/greeting", accessToken)
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(1)))
                .andExpect(jsonPath("$.content", is("Hello, pastor!")));
        // @formatter:on

        // @formatter:off
        get("/greeting", accessToken)
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(2)))
                .andExpect(jsonPath("$.content", is("Hello, pastor!")));
        // @formatter:on

        // @formatter:off
        get("/greeting", accessToken)
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(3)))
                .andExpect(jsonPath("$.content", is("Hello, pastor!")));
        // @formatter:on
    }

    @Test
    public void usersEndpointAuthorized() throws Exception {
        // @formatter:off
        final String accessToken = newToken("read:user");
        get("/users", accessToken)
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(2)));
        // @formatter:on
    }
}
