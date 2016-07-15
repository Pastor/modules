package ru.phi.modules;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.boot.test.WebIntegrationTest;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import ru.phi.modules.entity.Scope;
import ru.phi.modules.entity.Token;
import ru.phi.modules.entity.User;
import ru.phi.modules.entity.UserRole;
import ru.phi.modules.exceptions.AuthenticationException;
import ru.phi.modules.repository.ErrorRepository;
import ru.phi.modules.repository.ScopeRepository;
import ru.phi.modules.repository.TokenRepository;
import ru.phi.modules.repository.UserRepository;
import ru.phi.modules.security.Environment;

import static junit.framework.Assert.*;

@Slf4j
@RunWith(SpringJUnit4ClassRunner.class)
@WebIntegrationTest(randomPort = true)
@SpringApplicationConfiguration(classes = Application.class)
@TestPropertySource({"classpath:application.properties"})
public class SecurityRestControllerIntegrationTest {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ErrorRepository errorRepository;

    @Autowired
    private TokenRepository tokenRepository;

    @Autowired
    private ScopeRepository scopeRepository;

    @Autowired
    private ObjectMapper objectMapper;

    @Value("${local.server.port}")
    private int port;

    private Environment environment;
    private final User successUser = new User();

    @Before
    public void setUp() {
        successUser.setPassword("123456");
        successUser.setUsername("pastor");
        userRepository.save(successUser);
        environment = new Environment(objectMapper, port);
        final Scope scope = new Scope();
        scope.setName("profile");
        scope.setRole(UserRole.USER);
        scopeRepository.save(scope);
    }

    @After
    public void tearDown() throws Exception {
        errorRepository.deleteAll();
        userRepository.deleteAll();
        tokenRepository.deleteAll();
        environment.clearDown();
    }

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
        final Token token = environment.postUpdate("pastor", "123456");
        assertNotNull(token);
        assertNotNull(token.getExpiredAt());
    }

    @Test
    public void updateToken() throws Exception {
        Token token = environment.postUpdate(successUser.getUsername(), successUser.getPassword());
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
        final Token token = environment.postUpdate(successUser.getUsername(), successUser.getPassword(), "profile");
        final User user = environment.me(token.getKey());
        assertEquals(user.getUsername(), "pastor");
    }

    @Test(expected = AuthenticationException.class)
    public void faultScopedToken() throws Exception {
        final Token token = environment.postUpdate("pastor", "123456");
        final User user = environment.me(token.getKey());
        assertNotNull(user);
    }

    @Test(expected = AuthenticationException.class)
    public void faultToken() throws Exception {
        final User user = environment.me("000000000000000000000000000");
        assertNotNull(user);
    }

    @Test(expected = AuthenticationException.class)
    public void emptyToken() throws Exception {
        final User user = environment.me("");
        assertNotNull(user);
    }
}
