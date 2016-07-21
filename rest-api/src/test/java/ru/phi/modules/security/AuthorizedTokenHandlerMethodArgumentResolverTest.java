package ru.phi.modules.security;

import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.MethodSorters;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.test.WebIntegrationTest;
import org.springframework.core.MethodParameter;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.context.jdbc.SqlConfig;
import org.springframework.test.context.jdbc.SqlGroup;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.web.bind.support.WebArgumentResolver;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import ru.phi.modules.JpaConfiguration;
import ru.phi.modules.RestMvcConfiguration;
import ru.phi.modules.entity.Scope;
import ru.phi.modules.entity.Token;

import java.lang.reflect.Method;
import java.util.Optional;

import static junit.framework.TestCase.*;

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
public final class AuthorizedTokenHandlerMethodArgumentResolverTest {

    @Autowired
    @Qualifier("authorizedToken.v1")
    private HandlerMethodArgumentResolver resolver;

    @Test
    public void supportsParameter() throws Exception {
        final MethodParameter successParameter = createParameter(0);
        assertTrue(resolver.supportsParameter(successParameter));
        final MethodParameter withIllegalClassType = createParameter(1);
        assertFalse(resolver.supportsParameter(withIllegalClassType));
        final MethodParameter withoutAnnotation = createParameter(2);
        assertFalse(resolver.supportsParameter(withoutAnnotation));
    }

    @Test
    public void resolveArgument() throws Exception {
        final MethodParameter successParameter = createParameter(0);
        final Optional<Token> token = Environment.registerAuthentication();
        final Object resolveArgument = resolver.resolveArgument(successParameter, null, null, null);
        assertEquals(token.get(), resolveArgument);
    }

    @Test(expected = BadCredentialsException.class)
    public void resolveArgumentWithNullAuthentication() throws Exception {
        final MethodParameter successParameter = createParameter(0);
        Environment.registerNullAuthentication();
        resolver.resolveArgument(successParameter, null, null, null);
    }

    @Test
    public void resolveArgumentWithIllegalParameter() throws Exception {
        final MethodParameter successParameter = createParameter(1);
        final Object resolveArgument = resolver.resolveArgument(successParameter, null, null, null);
        assertEquals(resolveArgument, WebArgumentResolver.UNRESOLVED);
    }

    @SuppressWarnings("unused")
    private void methodWithToken(@AuthorizedToken Token token, @AuthorizedToken Scope scope, Token token2) {
    }

    private static MethodParameter createParameter(int index) throws NoSuchMethodException {
        final Method method = AuthorizedTokenHandlerMethodArgumentResolverTest.class.
                getDeclaredMethod("methodWithToken", Token.class, Scope.class, Token.class);
        method.setAccessible(true);
        return new MethodParameter(method, index);
    }
}

