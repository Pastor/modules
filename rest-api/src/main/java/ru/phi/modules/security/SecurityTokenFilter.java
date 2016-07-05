package ru.phi.modules.security;

import com.google.common.base.Strings;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.MDC;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.InternalAuthenticationServiceException;
import org.springframework.security.authentication.encoding.MessageDigestPasswordEncoder;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.preauth.PreAuthenticatedAuthenticationToken;
import org.springframework.web.filter.GenericFilterBean;
import ru.phi.modules.api.ExceptionService;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Optional;

import static org.slf4j.MDC.remove;

@Slf4j
final class SecurityTokenFilter extends GenericFilterBean {
    private static final String TOKEN_SESSION_KEY = "token";
    private static final String USER_SESSION_KEY = "user";

    private final AuthenticationManager manager;
    private final ExceptionService service;

    SecurityTokenFilter(AuthenticationManager manager, ExceptionService service) {
        this.manager = manager;
        this.service = service;
    }

    @SuppressWarnings("ThrowFromFinallyBlock")
    @Override
    public void doFilter(ServletRequest request,
                         ServletResponse response,
                         FilterChain chain) throws IOException, ServletException {
        final Optional<String> token = Optional.ofNullable(request.getParameter("token"));
        if (token.isPresent()) {
            try {
                processTokenAuthentication(token);
                addSessionContextToLogging();
            } catch (Exception ex) {
                service.write((HttpServletResponse) response, ex, HttpStatus.UNAUTHORIZED);
                return;
            } finally {
                remove(TOKEN_SESSION_KEY);
                remove(USER_SESSION_KEY);
            }
        }
        chain.doFilter(request, response);
    }

    @SuppressWarnings("OptionalUsedAsFieldOrParameterType")
    private void processTokenAuthentication(Optional<String> token) {
        final Authentication resultOfAuthentication = tryToAuthenticateWithToken(token);
        SecurityContextHolder.getContext().setAuthentication(resultOfAuthentication);
    }

    @SuppressWarnings("OptionalUsedAsFieldOrParameterType")
    private Authentication tryToAuthenticateWithToken(Optional<String> token) {
        PreAuthenticatedAuthenticationToken requestAuthentication = new PreAuthenticatedAuthenticationToken(token, null);
        return tryToAuthenticate(requestAuthentication);
    }

    private AuthenticationWithToken tryToAuthenticate(Authentication requestAuthentication) {
        Authentication responseAuthentication = manager.authenticate(requestAuthentication);
        if (responseAuthentication == null ||
                !responseAuthentication.isAuthenticated() ||
                !(responseAuthentication instanceof AuthenticationWithToken)) {
            throw new InternalAuthenticationServiceException("Unable to authenticate Domain User for provided credentials");
        }
        logger.debug("User successfully authenticated");
        return (AuthenticationWithToken) responseAuthentication;
    }

    private void addSessionContextToLogging() {
        final Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String tokenValue = "EMPTY";
        if (authentication != null && !Strings.isNullOrEmpty(authentication.getDetails().toString())) {
            MessageDigestPasswordEncoder encoder = new MessageDigestPasswordEncoder("SHA-1");
            tokenValue = encoder.encodePassword(authentication.getDetails().toString(), "not_so_random_salt");
        }
        MDC.put(TOKEN_SESSION_KEY, tokenValue);

        String userValue = "EMPTY";
        if (authentication != null && !Strings.isNullOrEmpty(authentication.getPrincipal().toString())) {
            userValue = authentication.getPrincipal().toString();
        }
        MDC.put(USER_SESSION_KEY, userValue);
    }
}
