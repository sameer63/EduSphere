
package com.edusphere.util;

import jakarta.servlet.http.HttpSession;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.SecureRandom;
import java.util.Base64;

public final class CsrfTokenUtil {

    public static final String SESSION_ATTRIBUTE =
            "csrfToken";

    private static final SecureRandom SECURE_RANDOM =
            new SecureRandom();

    private CsrfTokenUtil() {
        // Utility class; do not instantiate.
    }

    public static String getOrCreateToken(
            HttpSession session) {

        if (session == null) {
            throw new IllegalArgumentException(
                    "Session is required."
            );
        }

        Object existing =
                session.getAttribute(SESSION_ATTRIBUTE);

        if (existing instanceof String token
                && !token.isBlank()) {
            return token;
        }

        byte[] randomBytes = new byte[32];
        SECURE_RANDOM.nextBytes(randomBytes);

        String token = Base64.getUrlEncoder()
                .withoutPadding()
                .encodeToString(randomBytes);

        session.setAttribute(SESSION_ATTRIBUTE, token);

        return token;
    }

    public static boolean isValid(
            HttpSession session,
            String submittedToken) {

        if (session == null
                || submittedToken == null
                || submittedToken.isBlank()) {
            return false;
        }

        Object stored =
                session.getAttribute(SESSION_ATTRIBUTE);

        if (!(stored instanceof String storedToken)
                || storedToken.isBlank()) {
            return false;
        }

        return MessageDigest.isEqual(
                storedToken.getBytes(StandardCharsets.UTF_8),
                submittedToken.getBytes(StandardCharsets.UTF_8)
        );
    }
}
