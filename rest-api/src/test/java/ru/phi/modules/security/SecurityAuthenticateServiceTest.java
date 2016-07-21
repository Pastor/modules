package ru.phi.modules.security;

import com.google.common.collect.Sets;
import org.junit.After;
import org.junit.Before;
import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.MethodSorters;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.WebIntegrationTest;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.context.jdbc.SqlConfig;
import org.springframework.test.context.jdbc.SqlGroup;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import ru.phi.modules.JpaConfiguration;
import ru.phi.modules.RestMvcConfiguration;
import ru.phi.modules.entity.Token;
import ru.phi.modules.entity.User;
import ru.phi.modules.entity.UserRole;
import ru.phi.modules.exceptions.AuthenticationException;
import ru.phi.modules.repository.TokenRepository;
import ru.phi.modules.repository.UserRepository;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;

@RunWith(SpringJUnit4ClassRunner.class)
@WebIntegrationTest(randomPort = true)
@ContextConfiguration(classes = {
        RestSecurityConfiguration.class,
        JpaConfiguration.class,
        RestMvcConfiguration.class,
        SecurityAuthenticateServiceTest.ScannerConfiguration.class
})
@TestPropertySource({"classpath:application.properties"})
@SqlGroup({
        @Sql(
                scripts = {"/sql/data/drop.data.sql"},
                executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD,
                config = @SqlConfig(
                        encoding = "UTF-8",
                        errorMode = SqlConfig.ErrorMode.CONTINUE_ON_ERROR
                )
        ),
        @Sql(
                scripts = {"/sql/data/drop.data.sql"},
                executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD,
                config = @SqlConfig(
                        encoding = "UTF-8",
                        errorMode = SqlConfig.ErrorMode.CONTINUE_ON_ERROR
                )
        )
})
@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public final class SecurityAuthenticateServiceTest {

    @Autowired
    private SecurityAuthenticateService service;

    @Autowired
    private UserRepository usr;

    @Autowired
    private TokenRepository tor;

    @Before
    public void setUp() throws Exception {
        tor.deleteAll();
        usr.deleteAll();
    }

    @After
    public void tearDown() throws Exception {
        tor.deleteAll();
        usr.deleteAll();
    }

    @Test(expected = AuthenticationException.class)
    public void authenticateWithNullKey() throws Exception {
        service.authenticate(null);
    }

    @Test
    public void updateTokenWithNullToken() throws Exception {
        service.updateToken(null, Sets.newHashSet());
    }

    @Test
    public void updateTokenWithNullScopes() throws Exception {
        final Token token = createToken();
        service.updateToken(token, null);
    }

    @Test
    public void updateTokenWithEmptyScopes() throws Exception {
        final Token token = createToken();
        service.updateToken(token, Sets.newHashSet());
    }

    protected final Token createToken() {
        final Token token = new Token();
        final User user = new User();
        user.setUsername("username");
        user.setPassword("password");
        user.setRole(UserRole.admin);
        usr.save(user);
        token.setUser(user);
        token.setKey(SecurityUtilities.generateTokenKey());
        token.setExpiredAt(LocalDateTime.now().plus(1, ChronoUnit.HOURS));
        return token;
    }

    @Configuration
    @ComponentScan({"ru.phi.modules.exceptions"})
    public static class ScannerConfiguration {

    }
}