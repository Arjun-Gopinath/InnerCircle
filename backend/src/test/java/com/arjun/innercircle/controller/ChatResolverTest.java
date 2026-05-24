package com.arjun.innercircle.controller;

import com.arjun.innercircle.dto.MessageDto;
import com.arjun.innercircle.dto.RoomDto;
import com.arjun.innercircle.model.Message;
import com.arjun.innercircle.model.Room;
import com.arjun.innercircle.model.RoomMember;
import com.arjun.innercircle.model.RoomMemberId;
import com.arjun.innercircle.model.User;
import com.arjun.innercircle.repository.RoomMemberRepository;
import com.arjun.innercircle.service.MessageService;
import com.arjun.innercircle.service.RoomService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Sinks;
import reactor.test.StepVerifier;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("ChatResolver")
class ChatResolverTest {

    @Mock
    private RoomService roomService;

    @Mock
    private MessageService messageService;

    @Mock
    private RoomMemberRepository roomMemberRepository;

    private ChatResolver chatResolver;

    private User user;
    private Room room;
    private Sinks.Many<Message> messageSink;

    @BeforeEach
    void setUp() {
        user = User.builder()
                .id(UUID.randomUUID())
                .googleId("google-123")
                .email("arjun@example.com")
                .name("Arjun")
                .createdAt(Instant.now())
                .build();

        room = Room.builder()
                .id(UUID.randomUUID())
                .name("Dev Chat")
                .inviteKey("ABC123")
                .createdBy(user)
                .createdAt(Instant.now())
                .build();

        messageSink = Sinks.many().multicast().onBackpressureBuffer();
        chatResolver = new ChatResolver(
                roomService, messageService, roomMemberRepository, messageSink);
    }

    @Test
    @DisplayName("should return rooms the user is a member of")
    void shouldReturnMyRooms() {
        RoomMember member = RoomMember.builder()
                .id(new RoomMemberId(room.getId(), user.getId()))
                .room(room)
                .user(user)
                .build();
        when(roomMemberRepository.findByUserId(user.getId())).thenReturn(List.of(member));

        List<RoomDto> result = chatResolver.myRooms(user);

        assertThat(result).hasSize(1);
        assertThat(result.get(0).name()).isEqualTo("Dev Chat");
    }

    @Test
    @DisplayName("should return message history for a room")
    void shouldReturnMessages() {
        Message message = Message.builder()
                .id(UUID.randomUUID())
                .content("Hello!")
                .room(room)
                .sender(user)
                .sentAt(Instant.now())
                .build();
        when(messageService.getMessages(room.getId(), 50)).thenReturn(List.of(message));

        List<MessageDto> result = chatResolver.messages(room.getId(), 50);

        assertThat(result).hasSize(1);
        assertThat(result.get(0).content()).isEqualTo("Hello!");
    }

    @Test
    @DisplayName("should create a room and return its DTO")
    void shouldCreateRoom() {
        when(roomService.createRoom("Dev Chat", user)).thenReturn(room);

        RoomDto result = chatResolver.createRoom("Dev Chat", user);

        assertThat(result.name()).isEqualTo("Dev Chat");
        assertThat(result.inviteKey()).isEqualTo("ABC123");
    }

    @Test
    @DisplayName("should join a room and return its DTO")
    void shouldJoinRoom() {
        when(roomService.joinRoom("ABC123", user)).thenReturn(room);

        RoomDto result = chatResolver.joinRoom("ABC123", user);

        assertThat(result.inviteKey()).isEqualTo("ABC123");
    }

    @Test
    @DisplayName("should send a message and return true")
    void shouldSendMessage() {
        boolean result = chatResolver.sendMessage(room.getId(), "Hello!", user);

        verify(messageService).sendMessage(room.getId(), "Hello!", user);
        assertThat(result).isTrue();
    }

    @Test
    @DisplayName("should stream only messages belonging to the subscribed room")
    void shouldFilterMessagesByRoomId() {
        UUID otherRoomId = UUID.randomUUID();
        Room otherRoom = Room.builder()
                .id(otherRoomId)
                .name("Other Room")
                .inviteKey("XYZ999")
                .createdBy(user)
                .createdAt(Instant.now())
                .build();

        Message correctMessage = Message.builder()
                .id(UUID.randomUUID())
                .content("For this room")
                .room(room)
                .sender(user)
                .sentAt(Instant.now())
                .build();
        Message wrongMessage = Message.builder()
                .id(UUID.randomUUID())
                .content("For other room")
                .room(otherRoom)
                .sender(user)
                .sentAt(Instant.now())
                .build();

        Flux<MessageDto> subscription = chatResolver.messageReceived(room.getId());

        messageSink.tryEmitNext(wrongMessage);
        messageSink.tryEmitNext(correctMessage);

        StepVerifier.create(subscription.take(1))
                .expectNextMatches(dto -> dto.content().equals("For this room"))
                .verifyComplete();
    }
}