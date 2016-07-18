package ru.phi.modules.security;

import com.google.common.hash.HashFunction;
import com.google.common.hash.Hashing;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import ru.phi.modules.entity.Token;

import java.util.Optional;
import java.util.UUID;

public final class Utilities {
    private static final HashFunction hash = Hashing.goodFastHash(256);

    public static Optional<Token> currentToken() {
        final SecurityContext context = SecurityContextHolder.getContext();
        if (context == null)
            return Optional.empty();
        final AuthenticationWithToken token = (AuthenticationWithToken) context.getAuthentication();
        if (token == null)
            return Optional.empty();
        return Optional.ofNullable(token.token);
    }

    public static String generateTokenKey() {
        return hash.hashUnencodedChars(UUID.randomUUID().toString()).toString().toUpperCase();
    }
}
