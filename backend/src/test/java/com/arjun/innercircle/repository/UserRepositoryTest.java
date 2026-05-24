package com.arjun.innercircle.repository;

import com.arjun.innercircle.model.User;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.ActiveProfiles;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@ActiveProfiles("test")
@DisplayName("UserRepository")
class UserRepositoryTest {

    @Autowired
    private UserRepository userRepository;

    @Test
    @DisplayName("should find a user by their Google ID")
    void shouldFindByGoogleId() {
        User user = User.builder()
                .googleId("google-123")
                .email("arjun@example.com")
                .name("Arjun")
                .build();
        userRepository.save(user);

        Optional<User> found = userRepository.findByGoogleId("google-123");

        assertThat(found).isPresent();
        assertThat(found.get().getEmail()).isEqualTo("arjun@example.com");
    }

    @Test
    @DisplayName("should find a user by their email")
    void shouldFindByEmail() {
        User user = User.builder()
                .googleId("google-456")
                .email("test@example.com")
                .name("Test User")
                .build();
        userRepository.save(user);

        Optional<User> found = userRepository.findByEmail("test@example.com");

        assertThat(found).isPresent();
        assertThat(found.get().getGoogleId()).isEqualTo("google-456");
    }

    @Test
    @DisplayName("should return empty when Google ID does not exist")
    void shouldReturnEmptyWhenGoogleIdNotFound() {
        Optional<User> found = userRepository.findByGoogleId("nonexistent");
        assertThat(found).isEmpty();
    }
}