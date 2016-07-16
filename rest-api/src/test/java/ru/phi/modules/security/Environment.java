package ru.phi.modules.security;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.base.Joiner;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.ClientHttpRequestInterceptor;
import org.springframework.http.client.ClientHttpResponse;
import org.springframework.security.crypto.codec.Base64;
import org.springframework.web.client.ResponseErrorHandler;
import org.springframework.web.client.RestTemplate;
import ru.phi.modules.entity.Error;
import ru.phi.modules.entity.Profile;
import ru.phi.modules.entity.Settings;
import ru.phi.modules.entity.Token;
import ru.phi.modules.exceptions.AuthenticationException;

import java.io.IOException;
import java.io.UnsupportedEncodingException;

import static junit.framework.Assert.assertEquals;

@Slf4j
public final class Environment {
    private final ObjectMapper objectMapper;
    private final int port;

    public Environment(ObjectMapper objectMapper, int port) {
        this.objectMapper = objectMapper;
        this.port = port;
    }

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

    public Token postUpdate(String username, String password, String... scopes) {
        clearDown();
        successUp(username, password);
        final ResponseEntity<Token> entity;
        if (scopes == null) {
            entity = template.postForEntity("http://localhost:" + port + "/rest/v1/token", "", Token.class);
        } else {
            final String params = Joiner.on(",").join(scopes);
            entity = template.postForEntity("http://localhost:" + port + "/rest/v1/token?scopes={scopes}", "",
                    Token.class, params);
        }
        clearDown();
        assertEquals(entity.getStatusCode(), HttpStatus.OK);
        return entity.getBody();
    }

    public void postUpdate() {
        clearDown();
        template.postForEntity("http://localhost:" + port + "/rest/v1/token", "", Token.class);
        clearDown();
    }

    public Profile me(String token) {
        final ResponseEntity<Profile> userEntity = template.getForEntity("http://localhost:" + port + "/rest/v1/me?token={token}",
                Profile.class, token);
        clearDown();
        assertEquals(userEntity.getStatusCode(), HttpStatus.OK);
        return userEntity.getBody();
    }

    public void putMe(Profile profile, String token) {
        template.put("http://localhost:" + port + "/rest/v1/me?token={token}", profile, token);
    }

    public Settings meSettings(String token) {
        clearDown();
        final ResponseEntity<Settings> userEntity =
                template.getForEntity("http://localhost:" + port + "/rest/v1/me/settings?token={token}",
                        Settings.class, token);
        clearDown();
        assertEquals(userEntity.getStatusCode(), HttpStatus.OK);
        return userEntity.getBody();
    }

    public void putSettings(Settings settings, String token) {
        clearDown();
        template.put("http://localhost:" + port + "/rest/v1/me/settings?token={token}",
                settings, token);
        clearDown();
    }

    public void putUpdate(String token, String... scopes) {
        clearDown();
        if (scopes == null) {
            template.put("http://localhost:" + port + "/rest/v1/token?token={token}", "", token);
        } else {
            final String params = Joiner.on(",").join(scopes);
            template.put("http://localhost:" + port + "/rest/v1/token?scopes={scopes}&token={token}", "",
                    params, token);
        }
        clearDown();
    }

    public void successUp(String username, String password) {
        template.getInterceptors().add(basicInterceptor(username, password));
    }

    public void clearDown() {
        template.getInterceptors().clear();
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
