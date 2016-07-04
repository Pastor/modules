package ru.phi.modules;

import lombok.extern.slf4j.Slf4j;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.boot.test.WebIntegrationTest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.ClientHttpResponse;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.web.client.ResponseErrorHandler;
import org.springframework.web.client.RestTemplate;
import ru.phi.modules.entity.Token;
import ru.phi.modules.entity.User;
import ru.phi.modules.exceptions.AuthenticationException;
import ru.phi.modules.repository.UserRepository;

import java.io.IOException;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertNotNull;

@Slf4j
@RunWith(SpringJUnit4ClassRunner.class)
@WebIntegrationTest(randomPort = true)
@SpringApplicationConfiguration(classes = Application.class)
@TestPropertySource({"classpath:application.properties"})
public class SecurityRestControllerIntegrationTest {

    @Autowired
    private UserRepository userRepository;

    @Value("${local.server.port}")
    private int port;

    private final RestTemplate template = new RestTemplate() {
        {
            setErrorHandler(new ResponseErrorHandler() {
                @Override
                public boolean hasError(ClientHttpResponse response) throws IOException {
                    return true;
                }

                @Override
                public void handleError(ClientHttpResponse response) throws IOException {
                    final HttpStatus statusCode = response.getStatusCode();
                    if (statusCode == HttpStatus.UNAUTHORIZED ||
                            statusCode == HttpStatus.FORBIDDEN)
                        throw new AuthenticationException();
                }
            });
        }
    };

    @Before
    public void setUp() {
        final User user = new User();
        user.setPassword("123456");
        user.setUsername("pastor");
        userRepository.save(user);
    }

    @After
    public void tearDown() throws Exception {
        userRepository.deleteAll();
    }

    @Test(expected = AuthenticationException.class)
    public void faultLogin() throws Exception {
        final ResponseEntity<Token> entity = template.postForEntity("http://localhost:" + port + "/rest/v1/login?login={login}&password={password}",
                "", Token.class, "username", "password");
        assertEquals(entity.getStatusCode().value(), 401);
    }

    @Test(expected = AuthenticationException.class)
    public void emptyLogin() throws Exception {
        final ResponseEntity<Token> entity = template.postForEntity("http://localhost:" + port + "/rest/v1/login",
                "", Token.class);
        assertEquals(entity.getStatusCode().value(), 401);
    }

    @Test
    public void successLogin() throws Exception {
        final ResponseEntity<Token> entity = template.postForEntity("http://localhost:" + port + "/rest/v1/login?login={login}&password={password}",
                "", Token.class, "pastor", "123456");
        assertEquals(entity.getStatusCode(), HttpStatus.OK);
        final Token token = entity.getBody();
        assertNotNull(token);
        assertNotNull(token.getExpiredAt());
    }

    @Test
    public void successToken() throws Exception {
        final ResponseEntity<Token> entity = template.postForEntity("http://localhost:" + port + "/rest/v1/login?login={login}&password={password}",
                "", Token.class, "pastor", "123456");
        final Token token = entity.getBody();
        final ResponseEntity<User> userEntity = template.getForEntity("http://localhost:" + port + "/rest/v1/user?token={token}",
                User.class, token.getKey());
        assertEquals(userEntity.getStatusCode(), HttpStatus.OK);
        final User user = userEntity.getBody();
        assertEquals(user.getUsername(), "pastor");
    }

    @Test(expected = AuthenticationException.class)
    public void faultToken() throws Exception {
        final ResponseEntity<User> userEntity = template.getForEntity("http://localhost:" + port + "/rest/v1/user?token={token}",
                User.class, "000000000000000000000000000000000000");
        assertEquals(userEntity.getStatusCode(), HttpStatus.UNAUTHORIZED);
    }

    @Test(expected = AuthenticationException.class)
    public void emptyToken() throws Exception {
        final ResponseEntity<User> userEntity = template.getForEntity("http://localhost:" + port + "/rest/v1/user",
                User.class);
        assertEquals(userEntity.getStatusCode(), HttpStatus.UNAUTHORIZED);
    }
}
