package com.arjun.innercircle.kafka;

import com.arjun.innercircle.model.Message;
import com.arjun.innercircle.model.Room;
import com.arjun.innercircle.model.User;
import com.arjun.innercircle.repository.MessageRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import reactor.core.publisher.Sinks;
import reactor.test.StepVerifier;

import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("MessageConsumer")
class MessageConsumerTest {

    @Mock
    private MessageRepository messageRepository;

    @Mock
    private Sinks.Many<Message> messageSink;

    @InjectMocks
    private MessageConsumer messageConsumer;

    private Message message;

    @BeforeEach
    void setUp() {
        User sender = User.builder()
                .id(UUID.randomUUID())
                .googleId("google-123")
                .email("arjun@example.com")
                .name("Arjun")
                .build();

        Room room = Room.builder()
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
    @DisplayName("should save message to database when consumed from Kafka")
    void shouldSaveMessageToDatabase() {
        when(messageRepository.save(any(Message.class))).thenReturn(message);

        messageConsumer.consume(message);

        verify(messageRepository).save(message);
    }

    @Test
    @DisplayName("should emit message to sink after saving")
    void shouldEmitMessageToSink() {
        when(messageRepository.save(any(Message.class))).thenReturn(message);

        messageConsumer.consume(message);

        verify(messageSink).tryEmitNext(message);
    }

    @Test
    @DisplayName("should emit saved message not original — so ID is populated")
    void shouldEmitSavedMessage() {
        Message savedMessage = Message.builder()
                .id(UUID.randomUUID())
                .content(message.getContent())
                .room(message.getRoom())
                .sender(message.getSender())
                .build();
        when(messageRepository.save(any(Message.class))).thenReturn(savedMessage);

        messageConsumer.consume(message);

        verify(messageSink).tryEmitNext(savedMessage);
        verify(messageSink, never()).tryEmitNext(message);
    }
}
