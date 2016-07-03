package ru.phi.modules.security;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.web.authentication.preauth.PreAuthenticatedAuthenticationToken;
import ru.phi.modules.entity.User;

import java.util.Collection;

final class AuthenticationWithToken extends PreAuthenticatedAuthenticationToken {

    final User user;

    AuthenticationWithToken(User user, Object aPrincipal, Object aCredentials, Collection<? extends GrantedAuthority> anAuthorities) {
        super(aPrincipal, aCredentials, anAuthorities);
        this.user = user;
    }

    void setToken(String token) {
        setDetails(token);
    }

    public String getToken() {
        return (String) getDetails();
    }
}
