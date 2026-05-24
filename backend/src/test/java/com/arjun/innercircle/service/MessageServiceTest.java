package com.arjun.innercircle.service;

import com.arjun.innercircle.kafka.MessageProducer;
import com.arjun.innercircle.model.Message;
import com.arjun.innercircle.model.Room;
import com.arjun.innercircle.model.RoomMemberId;
import com.arjun.innercircle.model.User;
import com.arjun.innercircle.repository.MessageRepository;
import com.arjun.innercircle.repository.RoomMemberRepository;
import com.arjun.innercircle.repository.RoomRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("MessageService")
class MessageServiceTest {

    @Mock
    private RoomRepository roomRepository;

    @Mock
    private RoomMemberRepository roomMemberRepository;

    @Mock
    private MessageRepository messageRepository;

    @Mock
    private MessageProducer messageProducer;

    @InjectMocks
    private MessageService messageService;

    private User sender;
    private Room room;

    @BeforeEach
    void setUp() {
        sender = User.builder()
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
    }

    @Test
    @DisplayName("should publish message to Kafka when sender is a room member")
    void shouldPublishMessageWhenSenderIsMember() {
        when(roomRepository.findById(room.getId())).thenReturn(Optional.of(room));
        when(roomMemberRepository.existsById(new RoomMemberId(room.getId(), sender.getId())))
                .thenReturn(true);

        messageService.sendMessage(room.getId(), "Hello!", sender);

        verify(messageProducer).publish(any(Message.class));
        verify(messageRepository, never()).save(any());
    }

    @Test
    @DisplayName("should throw when sender is not a member of the room")
    void shouldThrowWhenSenderIsNotMember() {
        when(roomRepository.findById(room.getId())).thenReturn(Optional.of(room));
        when(roomMemberRepository.existsById(new RoomMemberId(room.getId(), sender.getId())))
                .thenReturn(false);

        assertThatThrownBy(() -> messageService.sendMessage(room.getId(), "Hello!", sender))
                .isInstanceOf(IllegalStateException.class)
                .hasMessage("User is not a member of this room");

        verify(messageProducer, never()).publish(any());
    }

    @Test
    @DisplayName("should throw when room does not exist")
    void shouldThrowWhenRoomNotFound() {
        UUID unknownRoomId = UUID.randomUUID();
        when(roomRepository.findById(unknownRoomId)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> messageService.sendMessage(unknownRoomId, "Hello!", sender))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Room not found");

        verify(messageProducer, never()).publish(any());
    }
}
