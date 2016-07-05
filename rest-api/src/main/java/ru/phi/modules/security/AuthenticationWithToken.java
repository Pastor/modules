package ru.phi.modules.security;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.web.authentication.preauth.PreAuthenticatedAuthenticationToken;
import ru.phi.modules.entity.Token;

import java.util.Collection;

final class AuthenticationWithToken extends PreAuthenticatedAuthenticationToken {

    final Token token;

    AuthenticationWithToken(Token token, Object aPrincipal, Object aCredentials, Collection<? extends GrantedAuthority> anAuthorities) {
        super(aPrincipal, aCredentials, anAuthorities);
        this.token = token;
    }

    void setToken(String token) {
        setDetails(token);
    }

    public String getToken() {
        return (String) getDetails();
    }
}
