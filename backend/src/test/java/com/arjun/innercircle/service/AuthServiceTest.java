package com.arjun.innercircle.service;

import com.arjun.innercircle.model.User;
import com.arjun.innercircle.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.oauth2.jwt.Jwt;

import java.util.Map;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("AuthService")
class AuthServiceTest {

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private AuthService authService;

    private Jwt jwt;

    @BeforeEach
    void setUp() {
        jwt = Jwt.withTokenValue("token")
                .header("alg", "RS256")
                .subject("google-123")
                .claims(claims -> claims.putAll(Map.of(
                        "email", "arjun@example.com",
                        "name", "Arjun",
                        "picture", "https://avatar.url/photo.jpg"
                )))
                .build();
    }

    @Test
    @DisplayName("should return existing user when Google ID is found")
    void shouldReturnExistingUser() {
        User existing = User.builder()
                .id(UUID.randomUUID())
                .googleId("google-123")
                .email("arjun@example.com")
                .name("Arjun")
                .build();
        when(userRepository.findByGoogleId("google-123")).thenReturn(Optional.of(existing));

        User result = authService.loadOrCreateUser(jwt);

        assertThat(result.getGoogleId()).isEqualTo("google-123");
        verify(userRepository, never()).save(any());
    }

    @Test
    @DisplayName("should create a new user when Google ID is not found")
    void shouldCreateNewUser() {
        when(userRepository.findByGoogleId("google-123")).thenReturn(Optional.empty());
        when(userRepository.save(any(User.class))).thenAnswer(inv -> inv.getArgument(0));

        User result = authService.loadOrCreateUser(jwt);

        assertThat(result.getGoogleId()).isEqualTo("google-123");
        assertThat(result.getEmail()).isEqualTo("arjun@example.com");
        assertThat(result.getName()).isEqualTo("Arjun");
        assertThat(result.getAvatarUrl()).isEqualTo("https://avatar.url/photo.jpg");
        verify(userRepository).save(any(User.class));
    }

    @Test
    @DisplayName("should map avatar URL from picture claim")
    void shouldMapAvatarFromPictureClaim() {
        when(userRepository.findByGoogleId("google-123")).thenReturn(Optional.empty());
        when(userRepository.save(any(User.class))).thenAnswer(inv -> inv.getArgument(0));

        User result = authService.loadOrCreateUser(jwt);

        assertThat(result.getAvatarUrl()).isEqualTo("https://avatar.url/photo.jpg");
    }
}
