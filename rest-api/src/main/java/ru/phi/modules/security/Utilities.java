package ru.phi.modules.security;

import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import ru.phi.modules.entity.User;

import java.util.Optional;

public final class Utilities {
    public static Optional<User> currentUser() {
        final SecurityContext context = SecurityContextHolder.getContext();
        if (context == null)
            return Optional.empty();
        final AuthenticationWithToken token = (AuthenticationWithToken) context.getAuthentication();
        if (token == null)
            return Optional.empty();
        return Optional.ofNullable(token.user);
    }
}
