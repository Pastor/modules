package ru.phi.modules.security;

import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.MethodSorters;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.WebIntegrationTest;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.web.authentication.preauth.PreAuthenticatedAuthenticationToken;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.context.jdbc.SqlConfig;
import org.springframework.test.context.jdbc.SqlGroup;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import ru.phi.modules.JpaConfiguration;
import ru.phi.modules.RestMvcConfiguration;

import java.util.Optional;

import static org.junit.Assert.*;

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
public final class TokenAuthenticateProviderTest {

    @Autowired
    private TokenAuthenticateProvider provider;

    @Test(expected = BadCredentialsException.class)
    public void authenticateWithoutPrincipal() throws Exception {
        provider.authenticate(new AuthenticationWithToken(null, Optional.empty(), null, null));
    }

    @Test
    public void supports() throws Exception {
        assertTrue(provider.supports(PreAuthenticatedAuthenticationToken.class));
    }

    @Test
    public void withNotSupport() throws Exception {
        assertFalse(provider.supports(TokenAuthenticateProviderTest.class));
    }
}