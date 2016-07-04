package ru.phi.modules.security;

import com.google.common.collect.Sets;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.preauth.PreAuthenticatedAuthenticationToken;
import org.springframework.stereotype.Component;
import ru.phi.modules.api.AuthenticateService;
import ru.phi.modules.entity.User;

import java.util.Optional;

@Component
final class TokenAuthenticateProvider implements AuthenticationProvider {

    private final AuthenticateService service;

    @Autowired
    public TokenAuthenticateProvider(AuthenticateService service) {
        this.service = service;
    }

    @SuppressWarnings("unchecked")
    @Override
    public Authentication authenticate(Authentication authentication) throws AuthenticationException {
        final Optional<String> token = (Optional<String>) authentication.getPrincipal();
        if (!token.isPresent())
            throw new BadCredentialsException("Empty token");
        final User user;
        final String tokenKey = token.get();
        try {
            user = service.authenticate(tokenKey);
        } catch (ru.phi.modules.exceptions.AuthenticationException e) {
            throw new BadCredentialsException("", e);
        }
        final AuthenticationWithToken withToken = new AuthenticationWithToken(user,
                user.getUsername(), user.getPassword(), Sets.newHashSet());
        withToken.setDetails(user);
        return withToken;
    }

    @Override
    public boolean supports(Class<?> authentication) {
        return authentication.equals(PreAuthenticatedAuthenticationToken.class);
    }
}
