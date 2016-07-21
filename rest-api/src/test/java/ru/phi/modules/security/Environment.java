package ru.phi.modules.security;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.base.Joiner;
import com.google.common.collect.Lists;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.ClientHttpRequestInterceptor;
import org.springframework.http.client.ClientHttpResponse;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.context.SecurityContextImpl;
import org.springframework.security.crypto.codec.Base64;
import org.springframework.web.client.ResponseErrorHandler;
import org.springframework.web.client.RestTemplate;
import ru.phi.modules.entity.*;
import ru.phi.modules.entity.Error;
import ru.phi.modules.exceptions.AuthenticationException;
import ru.phi.modules.exceptions.ObjectNotFoundException;
import ru.phi.modules.exceptions.ValidationException;

import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.util.List;
import java.util.Optional;

import static junit.framework.TestCase.assertEquals;

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
                        showError(response);
                        throw new AuthenticationException();
                    } else if (statusCode == HttpStatus.NOT_FOUND) {
                        showError(response);
                        throw new ObjectNotFoundException();
                    } else if (statusCode == HttpStatus.NOT_ACCEPTABLE) {
                        showError(response);
                    } else if (statusCode == HttpStatus.BAD_REQUEST) {
                        showError(response);
                        throw new ValidationException();
                    }
                }

                protected void showError(ClientHttpResponse response) {
                    try {
                        final InputStream body = response.getBody();
                        final Error error = objectMapper.readerFor(Error.class).readValue(body);
                        log.error("{}", error);
                    } catch (Exception ex) {
//                            log.error("", ex);
                    }
                }
            });
        }
    };

    public void postUpdateIllegalBasicBase64() {
        clearDown();
        template.getInterceptors().add(illegalBasicBase64());
        template.postForEntity("http://localhost:" + port + "/rest/v1/token", "", Token.class);
    }

    public void postUpdateIllegalBasicContent() {
        clearDown();
        template.getInterceptors().add(illegalBasicContent());
        template.postForEntity("http://localhost:" + port + "/rest/v1/token", "", Token.class);
    }

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

    public Long meNewsCount(String token) {
        return template.getForObject("http://localhost:" + port + "/rest/v1/me/news/count?token={token}", Long.class, token);
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

    public String pingScope(String token) {
        final ResponseEntity<String> entity = template.getForEntity("http://localhost:" + port + "/rest/v1/ping/scope?token={token}", String.class,
                token);
        assertEquals(entity.getStatusCode(), HttpStatus.OK);
        return entity.getBody();
    }

    public String pingAuthorized(String token) {
        final ResponseEntity<String> entity = template.getForEntity("http://localhost:" + port + "/rest/v1/ping/authorized?token={token}", String.class,
                token);
        assertEquals(entity.getStatusCode(), HttpStatus.OK);
        return entity.getBody();
    }

    public String pingClear() {
        final ResponseEntity<String> entity = template.getForEntity("http://localhost:" + port + "/rest/v1/ping/clear", String.class);
        assertEquals(entity.getStatusCode(), HttpStatus.OK);
        return entity.getBody();
    }

    public Version current() {
        final ResponseEntity<Version> entity = template.getForEntity("http://localhost:" + port + "/rest/v1/version", Version.class);
        assertEquals(entity.getStatusCode(), HttpStatus.OK);
        return entity.getBody();
    }

    public String swagger() {
        return template.getForObject("http://localhost:" + port + "/rest/v1/version/swagger", String.class);
    }

    public List<News> news(String token) {
        final ResponseEntity<News[]> entity = template.getForEntity("http://localhost:" + port + "/rest/v1/news?token={token}",
                News[].class, token);
        assertEquals(entity.getStatusCode(), HttpStatus.OK);
        return Lists.newArrayList(entity.getBody());
    }

    public List<Statistic> statistics(String token) {
        final ResponseEntity<Statistic[]> entity = template.getForEntity("http://localhost:" + port + "/rest/v1/statistics?token={token}",
                Statistic[].class, token);
        assertEquals(entity.getStatusCode(), HttpStatus.OK);
        return Lists.newArrayList(entity.getBody());
    }

    public List<String> scopes(String token) {
        final ResponseEntity<String[]> entity = template.getForEntity("http://localhost:" + port + "/rest/v1/scopes?token={token}",
                String[].class, token);
        assertEquals(entity.getStatusCode(), HttpStatus.OK);
        return Lists.newArrayList(entity.getBody());
    }

    public List<String> meScopes(String token) {
        final ResponseEntity<String[]> entity = template.getForEntity("http://localhost:" + port + "/rest/v1/me/scopes?token={token}",
                String[].class, token);
        assertEquals(entity.getStatusCode(), HttpStatus.OK);
        return Lists.newArrayList(entity.getBody());
    }

    public List<AccessibilityProcess> accessibilityProcesses() {
        final ResponseEntity<AccessibilityProcess[]> entity = template.getForEntity("http://localhost:" + port + "/rest/v1/accessibility/processes?page=0&size=1000",
                AccessibilityProcess[].class);
        assertEquals(entity.getStatusCode(), HttpStatus.OK);
        return Lists.newArrayList(entity.getBody());
    }

    public List<Error> errors(String token) {
        final ResponseEntity<Error[]> entity = template.getForEntity("http://localhost:" + port + "/rest/v1/errors?token={token}",
                Error[].class, token);
        assertEquals(entity.getStatusCode(), HttpStatus.OK);
        return Lists.newArrayList(entity.getBody());
    }

    public List<Quality> qualities(String token) {
        final ResponseEntity<Quality[]> entity = template.getForEntity("http://localhost:" + port + "/rest/v1/qualities?token={token}",
                Quality[].class, token);
        assertEquals(entity.getStatusCode(), HttpStatus.OK);
        return Lists.newArrayList(entity.getBody());
    }

    public Long newsCount(String token) {
        return template.getForObject("http://localhost:" + port + "/rest/v1/news/count?token={token}", Long.class, token);
    }

    public Long statisticsCount(String token) {
        return template.getForObject("http://localhost:" + port + "/rest/v1/statistics/count?token={token}", Long.class, token);
    }

    public Long errorsCount(String token) {
        return template.getForObject("http://localhost:" + port + "/rest/v1/errors/count?token={token}", Long.class, token);
    }

    public Long categoriesCount(String token) {
        return template.getForObject("http://localhost:" + port + "/rest/v1/categories/count?token={token}", Long.class, token);
    }

    public Long elementsCount(String token) {
        return template.getForObject("http://localhost:" + port + "/rest/v1/elements/count?token={token}", Long.class, token);
    }

    public void update(String token, Long id, News news) {
        template.put("http://localhost:" + port + "/rest/v1/news/{id}?token={token}", news, id, token);
    }

    public void update(String token, Long id, Quality quality) {
        template.put("http://localhost:" + port + "/rest/v1/qualities/{id}?token={token}", quality, id, token);
    }

    public void update(String token, Long id, Element element) {
        template.put("http://localhost:" + port + "/rest/v1/elements/{id}?token={token}", element, id, token);
    }

    public void update(String token, Long id, EndPoint[] endPoints) {
        template.put("http://localhost:" + port + "/rest/v1/elements/{id}/endpoints?token={token}", endPoints, id, token);
    }

    public void update(String token, Long id, ElementCategory category) {
        template.put("http://localhost:" + port + "/rest/v1/categories/{id}?token={token}", category, id, token);
    }

    public void publish(String token, Long id) {
        template.put("http://localhost:" + port + "/rest/v1/news/{id}/publish?token={token}", "", id, token);
    }

    public void hide(String token, Long id) {
        template.put("http://localhost:" + port + "/rest/v1/news/{id}/hide?token={token}", "", id, token);
    }

    public void deleteNews(String token, Long id) {
        template.delete("http://localhost:" + port + "/rest/v1/news/{id}?token={token}", id, token);
    }

    public void deleteEndpoints(String token, Long id) {
        template.delete("http://localhost:" + port + "/rest/v1/elements/{id}/endpoints?token={token}", id, token);
    }

    public void deleteElement(String token, Long id) {
        template.delete("http://localhost:" + port + "/rest/v1/elements/{id}?token={token}", id, token);
    }

    public void deleteCategory(String token, Long id) {
        template.delete("http://localhost:" + port + "/rest/v1/categories/{id}?token={token}", id, token);
    }

    public void deleteQuality(String token, Long id) {
        template.delete("http://localhost:" + port + "/rest/v1/qualities/{id}?token={token}", id, token);
    }

    public List<News> meNews(String token) {
        final ResponseEntity<News[]> entity = template.getForEntity("http://localhost:" + port + "/rest/v1/me/news?token={token}",
                News[].class, token);
        assertEquals(entity.getStatusCode(), HttpStatus.OK);
        return Lists.newArrayList(entity.getBody());
    }

    public News createNews(String token, News news) {
        final ResponseEntity<News> entity = template.postForEntity("http://localhost:" + port + "/rest/v1/news?token={token}",
                news, News.class, token);
        assertEquals(entity.getStatusCode(), HttpStatus.OK);
        return entity.getBody();
    }

    public Element createElement(String token, Element element) {
        final ResponseEntity<Element> entity = template.postForEntity("http://localhost:" + port + "/rest/v1/elements?token={token}",
                element, Element.class, token);
        assertEquals(entity.getStatusCode(), HttpStatus.OK);
        return entity.getBody();
    }

    public ElementCategory createCategory(String token, ElementCategory category) {
        final ResponseEntity<ElementCategory> entity = template.postForEntity("http://localhost:" + port + "/rest/v1/categories?token={token}",
                category, ElementCategory.class, token);
        assertEquals(entity.getStatusCode(), HttpStatus.OK);
        return entity.getBody();
    }

    public Statistic createStatistic(String token, Statistic statistic) {
        final ResponseEntity<Statistic> entity = template.postForEntity("http://localhost:" + port + "/rest/v1/statistics?token={token}",
                statistic, Statistic.class, token);
        assertEquals(entity.getStatusCode(), HttpStatus.OK);
        return entity.getBody();
    }

    public Quality createQuality(String token, Quality quality) {
        final ResponseEntity<Quality> entity = template.postForEntity("http://localhost:" + port + "/rest/v1/qualities?token={token}",
                quality, Quality.class, token);
        assertEquals(entity.getStatusCode(), HttpStatus.OK);
        return entity.getBody();
    }

    public void putContent(String token, Long id, String content) {
        template.put("http://localhost:" + port + "/rest/v1/news/{id}/content?token={token}",
                content, id, token);
    }

    public String getContent(String token, Long id) {
        return template.getForObject("http://localhost:" + port + "/rest/v1/news/{id}/content?token={token}", String.class, id, token);
    }

    public News getNews(String token, Long id) {
        final ResponseEntity<News> entity = template.getForEntity("http://localhost:" + port + "/rest/v1/news/{id}?token={token}", News.class, id, token);
        assertEquals(entity.getStatusCode(), HttpStatus.OK);
        return entity.getBody();
    }

    public Quality getQuality(String token, Long id) {
        final ResponseEntity<Quality> entity = template.getForEntity("http://localhost:" + port + "/rest/v1/qualities/{id}?token={token}", Quality.class, id, token);
        assertEquals(entity.getStatusCode(), HttpStatus.OK);
        return entity.getBody();
    }

    public String pingAuthorizedFault() {
        final ResponseEntity<String> entity = template.getForEntity("http://localhost:" + port + "/rest/v1/ping/authorized_fault", String.class);
        assertEquals(entity.getStatusCode(), HttpStatus.OK);
        return entity.getBody();
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

    private static ClientHttpRequestInterceptor illegalBasicBase64() {
        return (request, body, execution) -> {
            request.getHeaders().add("Authorization", "Basic ------------------");
            return execution.execute(request, body);
        };
    }

    private static ClientHttpRequestInterceptor illegalBasicContent() {
        return (request, body, execution) -> {
            request.getHeaders().add("Authorization", "Basic " + new String(Base64.encode("ILLEGAL_CONTENT"
                    .getBytes("UTF-8")), "UTF-8"));
            return execution.execute(request, body);
        };
    }

    private static String basic(String username, String password) throws UnsupportedEncodingException {
        final String token = username + ":" + password;
        return "Basic " + new String(Base64.encode(token.getBytes("UTF-8")), "UTF-8");
    }

    public List<Element> elements(String token) {
        final ResponseEntity<Element[]> entity = template.getForEntity("http://localhost:" + port + "/rest/v1/elements?token={token}",
                Element[].class, token);
        assertEquals(entity.getStatusCode(), HttpStatus.OK);
        return Lists.newArrayList(entity.getBody());
    }

    public List<EndPoint> endpoints(String token, Long id) {
        final ResponseEntity<EndPoint[]> entity = template.getForEntity("http://localhost:" + port + "/rest/v1/elements/{id}/endpoints?token={token}",
                EndPoint[].class, id, token);
        assertEquals(entity.getStatusCode(), HttpStatus.OK);
        return Lists.newArrayList(entity.getBody());
    }

    public Element getElement(String token, Long id) {
        final ResponseEntity<Element> entity = template.getForEntity("http://localhost:" + port + "/rest/v1/elements/{id}?token={token}", Element.class, id, token);
        assertEquals(entity.getStatusCode(), HttpStatus.OK);
        return entity.getBody();
    }

    public List<ElementCategory> categories(String token) {
        final ResponseEntity<ElementCategory[]> entity = template.getForEntity("http://localhost:" + port + "/rest/v1/categories?token={token}",
                ElementCategory[].class, token);
        assertEquals(entity.getStatusCode(), HttpStatus.OK);
        return Lists.newArrayList(entity.getBody());
    }

    public ElementCategory getCategory(String token, Long id) {
        final ResponseEntity<ElementCategory> entity = template.getForEntity("http://localhost:" + port + "/rest/v1/categories/{id}?token={token}",
                ElementCategory.class, id, token);
        assertEquals(entity.getStatusCode(), HttpStatus.OK);
        return entity.getBody();
    }

    public static Optional<Token> registerAuthentication() {
        final SecurityContextImpl context = new SecurityContextImpl();
        SecurityContextHolder.setContext(context);
        final Token token = new Token();
        context.setAuthentication(new AuthenticationWithToken(token, null, null, null));
        return Optional.of(token);
    }

    public static void registerNullAuthentication() {
        final SecurityContextImpl context = new SecurityContextImpl();
        SecurityContextHolder.setContext(context);
        context.setAuthentication(null);
    }
}
