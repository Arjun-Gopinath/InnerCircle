package com.arjun.innercircle.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.RepeatedTest;
import org.junit.jupiter.api.Test;

import java.util.HashSet;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("InviteKeyGenerator")
class InviteKeyGeneratorTest {

    private InviteKeyGenerator generator;

    @BeforeEach
    void setUp() {
        generator = new InviteKeyGenerator();
    }

    @Test
    @DisplayName("should generate a key of exactly 6 characters")
    void shouldGenerateKeyOfCorrectLength() {
        String key = generator.generate();
        assertThat(key).hasSize(6);
    }

    @Test
    @DisplayName("should only contain uppercase letters and digits")
    void shouldOnlyContainValidCharacters() {
        String key = generator.generate();
        assertThat(key).matches("[A-Z0-9]+");
    }

    @RepeatedTest(100)
    @DisplayName("should generate unique keys across repeated calls")
    void shouldGenerateUniqueKeys() {
        Set<String> keys = new HashSet<>();
        for (int i = 0; i < 50; i++) {
            keys.add(generator.generate());
        }
        assertThat(keys).hasSize(50);
    }
}