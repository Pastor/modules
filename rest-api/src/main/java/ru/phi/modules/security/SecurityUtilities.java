package ru.phi.modules.security;

import com.google.common.hash.HashFunction;
import com.google.common.hash.Hashing;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import ru.phi.modules.entity.Token;

import java.util.Optional;
import java.util.UUID;

public final class SecurityUtilities {
    private static final HashFunction hash = Hashing.goodFastHash(256);

    public static Optional<Token> currentToken() {
        final SecurityContext context = SecurityContextHolder.getContext();
        final Authentication authentication = context.getAuthentication();
        if (authentication == null)
            return Optional.empty();
        if (!(authentication instanceof AuthenticationWithToken))
            return Optional.empty();
        final AuthenticationWithToken token = (AuthenticationWithToken) authentication;
        return Optional.ofNullable(token.token);
    }

    public static String generateTokenKey() {
        return hash.hashUnencodedChars(UUID.randomUUID().toString()).toString().toUpperCase();
    }
}
