package ru.phi.modules.api;

import ru.phi.modules.entity.Token;
import ru.phi.modules.exceptions.AuthenticationException;

import java.util.Set;

public interface AuthenticateService {

    Token authenticate(String key) throws AuthenticationException;

    Token authenticate(String username, String password, boolean update)
            throws AuthenticationException;

    Token authenticate(String username, String password, boolean update, Set<String> scopes)
            throws AuthenticationException;
}
