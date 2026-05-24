package com.arjun.innercircle.kafka;

import com.arjun.innercircle.model.Message;
import com.arjun.innercircle.model.Room;
import com.arjun.innercircle.model.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.UUID;

import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
@DisplayName("MessageProducer")
class MessageProducerTest {

    @Mock
    private KafkaTemplate<String, Message> kafkaTemplate;

    @InjectMocks
    private MessageProducer messageProducer;

    private Message message;
    private Room room;

    @BeforeEach
    void setUp() {
        ReflectionTestUtils.setField(messageProducer, "topic", "chat-messages");

        User sender = User.builder()
                .id(UUID.randomUUID())
                .googleId("google-123")
                .email("arjun@example.com")
                .name("Arjun")
                .build();

        room = Room.builder()
                .id(UUID.randomUUID())
                .name("Dev Chat")
                .inviteKey("ABC123")
                .createdBy(sender)
                .build();

        message = Message.builder()
                .id(UUID.randomUUID())
                .content("Hello!")
                .room(room)
                .sender(sender)
                .build();
    }

    @Test
    @DisplayName("should publish message to the chat-messages topic")
    void shouldPublishToCorrectTopic() {
        messageProducer.publish(message);

        verify(kafkaTemplate).send("chat-messages", room.getId().toString(), message);
    }
}
