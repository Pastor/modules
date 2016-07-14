package ru.phi.modules;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.collect.Sets;
import lombok.extern.slf4j.Slf4j;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.boot.test.WebIntegrationTest;
import org.springframework.http.HttpRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.ClientHttpRequestExecution;
import org.springframework.http.client.ClientHttpRequestInterceptor;
import org.springframework.http.client.ClientHttpResponse;
import org.springframework.security.crypto.codec.Base64;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.web.client.ResponseErrorHandler;
import org.springframework.web.client.RestTemplate;
import ru.phi.modules.entity.Error;
import ru.phi.modules.entity.Scope;
import ru.phi.modules.entity.Token;
import ru.phi.modules.entity.User;
import ru.phi.modules.exceptions.AuthenticationException;
import ru.phi.modules.repository.ErrorRepository;
import ru.phi.modules.repository.ScopeRepository;
import ru.phi.modules.repository.TokenRepository;
import ru.phi.modules.repository.UserRepository;

import java.io.IOException;
import java.io.UnsupportedEncodingException;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertNotNull;

@Slf4j
@RunWith(SpringJUnit4ClassRunner.class)
@WebIntegrationTest(randomPort = true)
@SpringApplicationConfiguration(classes = Application.class)
@TestPropertySource({"classpath:application.properties"})
public class SecurityRestControllerIntegrationTest {

    private static ClientHttpRequestInterceptor basicSuccess;
    private static ClientHttpRequestInterceptor basicFault;

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

    private final RestTemplate template = new RestTemplate() {
        {
            setErrorHandler(new ResponseErrorHandler() {
                @Override
                public boolean hasError(ClientHttpResponse response) throws IOException {
                    final HttpStatus statusCode = response.getStatusCode();
                    return statusCode.is4xxClientError() ||
                            statusCode.is5xxServerError();
                }

                @Override
                public void handleError(ClientHttpResponse response) throws IOException {
                    final HttpStatus statusCode = response.getStatusCode();
                    if (statusCode == HttpStatus.UNAUTHORIZED ||
                            statusCode == HttpStatus.FORBIDDEN) {
                        try {
                            final Error error = objectMapper.readerFor(Error.class).readValue(response.getBody());
                            log.error("{}", error);
                        } catch (Exception ex) {
                            //Nothing
                        }
                        throw new AuthenticationException();
                    }

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
        basicSuccess = basicInterceptor("pastor", "123456");
        basicFault = basicInterceptor("username", "password");
    }

    @After
    public void tearDown() throws Exception {
        errorRepository.deleteAll();
        userRepository.deleteAll();
        tokenRepository.deleteAll();
        template.getInterceptors().remove(basicFault);
        template.getInterceptors().remove(basicSuccess);
    }

    @Test(expected = AuthenticationException.class)
    public void faultUpdate() throws Exception {
        template.getInterceptors().add(basicFault);
        final ResponseEntity<Token> entity = template.postForEntity("http://localhost:" + port + "/rest/v1/update",
                "", Token.class);
        assertEquals(entity.getStatusCode().value(), 401);
    }

    @Test(expected = AuthenticationException.class)
    public void emptyUpdate() throws Exception {
        final ResponseEntity<Token> entity = template.postForEntity("http://localhost:" + port + "/rest/v1/update",
                "", Token.class);
        assertEquals(entity.getStatusCode().value(), 401);
    }

    @Test
    public void successUpdate() throws Exception {
        template.getInterceptors().add(basicSuccess);
        final ResponseEntity<Token> entity = template.postForEntity("http://localhost:" + port + "/rest/v1/update",
                "", Token.class);
        assertEquals(entity.getStatusCode(), HttpStatus.OK);
        final Token token = entity.getBody();
        assertNotNull(token);
        assertNotNull(token.getExpiredAt());
    }

    @Test
    public void successToken() throws Exception {
        template.getInterceptors().add(basicSuccess);
        final ResponseEntity<Token> entity = template.postForEntity("http://localhost:" + port + "/rest/v1/update",
                "", Token.class);
        final Token token = tokenRepository.findByKey(entity.getBody().getKey());
        final Scope scope = new Scope();
        scope.setName("profile");
        scopeRepository.save(scope);
        token.setScopes(Sets.newHashSet(scope));
        tokenRepository.save(token);
        final ResponseEntity<User> userEntity = template.getForEntity("http://localhost:" + port + "/rest/v1/user?token={token}",
                User.class, token.getKey());
        assertEquals(userEntity.getStatusCode(), HttpStatus.OK);
        final User user = userEntity.getBody();
        assertEquals(user.getUsername(), "pastor");
    }

    @Test(expected = AuthenticationException.class)
    public void faultScopedToken() throws Exception {
        final ResponseEntity<Token> entity = template.postForEntity("http://localhost:" + port + "/rest/v1/update",
                "", Token.class);
        final ResponseEntity<User> userEntity = template.getForEntity("http://localhost:" + port + "/rest/v1/user?token={token}",
                User.class, entity.getBody().getKey());
        assertEquals(userEntity.getStatusCode(), HttpStatus.OK);
    }

    @Test(expected = AuthenticationException.class)
    public void faultToken() throws Exception {
        final ResponseEntity<Error> userEntity = template.getForEntity("http://localhost:" + port + "/rest/v1/user?token={token}",
                Error.class, "000000000000000000000000000000000000");
        assertEquals(userEntity.getStatusCode(), HttpStatus.UNAUTHORIZED);
    }

    @Test(expected = AuthenticationException.class)
    public void emptyToken() throws Exception {
        final ResponseEntity<User> userEntity = template.getForEntity("http://localhost:" + port + "/rest/v1/user",
                User.class);
        assertEquals(userEntity.getStatusCode(), HttpStatus.UNAUTHORIZED);
    }

    private static ClientHttpRequestInterceptor basicInterceptor(String username, String password) {
        return (request, body, execution) -> {
            request.getHeaders().add("Authorization", basic(username, password));
            return execution.execute(request, body);
        };
    }

    private static String basic(String username, String password) throws UnsupportedEncodingException {
        final String token = username + ":" + password;
        return "Basic " + new String(Base64.encode(token.getBytes("UTF-8")), "UTF-8");
    }
}
