package com.truncon.web.authentication;

import at.favre.lib.crypto.bcrypt.BCrypt;

import java.nio.charset.StandardCharsets;
import java.security.SecureRandom;
import java.util.Base64;

public final class Passwords {
    private Passwords() {
    }

    public static String newSalt() {
        SecureRandom random = new SecureRandom();
        byte[] saltBytes = random.generateSeed(16);
        return Base64.getEncoder().encodeToString(saltBytes);
    }

    public static String hash(String value, String salt) {
        byte[] saltBytes = Base64.getDecoder().decode(salt);
        byte[] passwordBytes = value.getBytes(StandardCharsets.UTF_8);
        byte[] hash = BCrypt.with(BCrypt.Version.VERSION_2Y).hash(6, saltBytes, passwordBytes);
        return Base64.getEncoder().encodeToString(hash);
    }
}
