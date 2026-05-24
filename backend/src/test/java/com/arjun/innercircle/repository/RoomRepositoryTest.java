package com.arjun.innercircle.repository;

import com.arjun.innercircle.model.Room;
import com.arjun.innercircle.model.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.ActiveProfiles;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase.Replace.NONE;

@DataJpaTest
@AutoConfigureTestDatabase(replace = NONE)
@ActiveProfiles("test")
@DisplayName("RoomRepository")
class RoomRepositoryTest {

    @Autowired
    private RoomRepository roomRepository;

    @Autowired
    private UserRepository userRepository;

    private User owner;

    @BeforeEach
    void setUp() {
        owner = userRepository.save(User.builder()
                .googleId("google-123")
                .email("arjun@example.com")
                .name("Arjun")
                .build());
    }

    @Test
    @DisplayName("should find a room by its invite key")
    void shouldFindByInviteKey() {
        roomRepository.save(Room.builder()
                .name("Dev Chat")
                .inviteKey("ABC123")
                .createdBy(owner)
                .build());

        Optional<Room> found = roomRepository.findByInviteKey("ABC123");

        assertThat(found).isPresent();
        assertThat(found.get().getName()).isEqualTo("Dev Chat");
    }

    @Test
    @DisplayName("should return empty when invite key does not exist")
    void shouldReturnEmptyWhenInviteKeyNotFound() {
        Optional<Room> found = roomRepository.findByInviteKey("XXXXXX");
        assertThat(found).isEmpty();
    }
}
