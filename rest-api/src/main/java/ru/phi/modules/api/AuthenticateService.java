package ru.phi.modules.api;

import ru.phi.modules.entity.Token;
import ru.phi.modules.entity.User;
import ru.phi.modules.exceptions.AuthenticationException;

import java.util.Optional;

public interface AuthenticateService {

    User authenticate(Optional<String> key) throws AuthenticationException;

    Token authenticate(String username, String password) throws AuthenticationException;
}
