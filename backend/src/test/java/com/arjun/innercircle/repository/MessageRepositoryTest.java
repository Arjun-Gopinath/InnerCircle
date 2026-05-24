package com.arjun.innercircle.repository;

import com.arjun.innercircle.model.Message;
import com.arjun.innercircle.model.Room;
import com.arjun.innercircle.model.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.data.domain.PageRequest;
import org.springframework.test.context.ActiveProfiles;

import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase.Replace.NONE;

@DataJpaTest
@AutoConfigureTestDatabase(replace = NONE)
@ActiveProfiles("test")
@DisplayName("MessageRepository")
class MessageRepositoryTest {

    @Autowired
    private MessageRepository messageRepository;

    @Autowired
    private RoomRepository roomRepository;

    @Autowired
    private UserRepository userRepository;

    private Room room;
    private User sender;

    @BeforeEach
    void setUp() {
        sender = userRepository.save(User.builder()
                .googleId("google-123")
                .email("arjun@example.com")
                .name("Arjun")
                .build());

        room = roomRepository.save(Room.builder()
                .name("Dev Chat")
                .inviteKey("ABC123")
                .createdBy(sender)
                .build());
    }

    @Test
    @DisplayName("should return messages for a room ordered by sentAt descending")
    void shouldReturnMessagesForRoomOrderedBySentAt() {
        messageRepository.save(Message.builder()
                .content("First message")
                .room(room)
                .sender(sender)
                .build());
        messageRepository.save(Message.builder()
                .content("Second message")
                .room(room)
                .sender(sender)
                .build());

        List<Message> messages = messageRepository
                .findByRoomIdOrderBySentAtDesc(room.getId(), PageRequest.of(0, 50));

        assertThat(messages).hasSize(2);
        assertThat(messages.get(0).getContent()).isEqualTo("Second message");
    }

    @Test
    @DisplayName("should return empty list for a room with no messages")
    void shouldReturnEmptyForRoomWithNoMessages() {
        List<Message> messages = messageRepository
                .findByRoomIdOrderBySentAtDesc(UUID.randomUUID(), PageRequest.of(0, 50));

        assertThat(messages).isEmpty();
    }
}
