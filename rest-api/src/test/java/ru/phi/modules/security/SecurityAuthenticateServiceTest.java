package ru.phi.modules.security;

import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.MethodSorters;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.WebIntegrationTest;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.context.jdbc.SqlConfig;
import org.springframework.test.context.jdbc.SqlGroup;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import ru.phi.modules.JpaConfiguration;
import ru.phi.modules.RestMvcConfiguration;
import ru.phi.modules.exceptions.AuthenticationException;

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

    @Test(expected = AuthenticationException.class)
    public void authenticateWithNullKey() throws Exception {
        service.authenticate(null);
    }

    @Configuration
    @ComponentScan({"ru.phi.modules.exceptions"})
    public static class ScannerConfiguration {

    }
}