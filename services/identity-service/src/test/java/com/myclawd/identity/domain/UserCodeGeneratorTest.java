package com.myclawd.identity.domain;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.util.HashSet;
import java.util.Random;
import java.util.Set;
import org.junit.jupiter.api.Test;

class UserCodeGeneratorTest {

    @Test
    void codeIsFourLettersThenFourDigits() {
        UserCodeGenerator generator = new UserCodeGenerator(new Random(42));
        for (int i = 0; i < 1_000; i++) {
            assertThat(generator.next(code -> false)).matches(UserCodeGenerator.FORMAT);
        }
    }

    @Test
    void skipsCodesThatAreAlreadyTaken() {
        Set<String> taken = new HashSet<>();
        String first = new UserCodeGenerator(new Random(7)).next(code -> false);
        taken.add(first);

        // Same seed would produce `first` again; the generator must move past it.
        String second = new UserCodeGenerator(new Random(7)).next(taken::contains);

        assertThat(second).isNotEqualTo(first).matches(UserCodeGenerator.FORMAT);
    }

    @Test
    void failsAfterTooManyCollisions() {
        UserCodeGenerator generator = new UserCodeGenerator(new Random(1));

        assertThatThrownBy(() -> generator.next(code -> true))
                .isInstanceOf(IllegalStateException.class);
    }
}
