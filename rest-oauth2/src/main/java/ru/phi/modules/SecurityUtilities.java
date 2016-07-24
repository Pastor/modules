package ru.phi.modules;

import com.google.common.hash.HashFunction;
import com.google.common.hash.Hashing;

import java.util.UUID;

public final class SecurityUtilities {
    private static final HashFunction hash = Hashing.goodFastHash(256);

    public static String generateTokenKey() {
        return hash.hashUnencodedChars(UUID.randomUUID().toString()).toString().toUpperCase();
    }
}
