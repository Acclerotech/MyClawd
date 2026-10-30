package com.myclawd.identity.domain;

import java.security.SecureRandom;
import java.util.Random;
import java.util.function.Predicate;
import java.util.regex.Pattern;
import org.springframework.stereotype.Component;

/**
 * Generates the 8-character MyClawd user code (Unique Sign ID): 4 uppercase letters
 * followed by 4 digits, e.g. {@code JOHN3003}. Codes are checked against existing
 * users and regenerated on collision.
 */
@Component
public class UserCodeGenerator {

    public static final Pattern FORMAT = Pattern.compile("^[A-Z]{4}[0-9]{4}$");
    static final int MAX_ATTEMPTS = 20;

    private final Random random;

    public UserCodeGenerator() {
        this(new SecureRandom());
    }

    UserCodeGenerator(Random random) {
        this.random = random;
    }

    public String next(Predicate<String> alreadyTaken) {
        for (int attempt = 0; attempt < MAX_ATTEMPTS; attempt++) {
            String code = candidate();
            if (!alreadyTaken.test(code)) {
                return code;
            }
        }
        throw new IllegalStateException("Could not generate a unique user code after " + MAX_ATTEMPTS + " attempts");
    }

    private String candidate() {
        StringBuilder sb = new StringBuilder(8);
        for (int i = 0; i < 4; i++) {
            sb.append((char) ('A' + random.nextInt(26)));
        }
        for (int i = 0; i < 4; i++) {
            sb.append((char) ('0' + random.nextInt(10)));
        }
        return sb.toString();
    }
}
