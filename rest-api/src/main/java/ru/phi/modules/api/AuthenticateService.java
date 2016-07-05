package ru.phi.modules.api;

import ru.phi.modules.entity.Token;
import ru.phi.modules.entity.User;
import ru.phi.modules.exceptions.AuthenticationException;

public interface AuthenticateService {

    Token authenticate(String key) throws AuthenticationException;

    Token authenticate(String username, String password) throws AuthenticationException;
}
