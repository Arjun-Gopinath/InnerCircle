package com.arjun.innercircle.service;

import com.arjun.innercircle.model.Room;
import com.arjun.innercircle.model.RoomMember;
import com.arjun.innercircle.model.RoomMemberId;
import com.arjun.innercircle.model.User;
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

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("RoomService")
class RoomServiceTest {

    @Mock
    private RoomRepository roomRepository;

    @Mock
    private RoomMemberRepository roomMemberRepository;

    @Mock
    private InviteKeyGenerator inviteKeyGenerator;

    @InjectMocks
    private RoomService roomService;

    private User user;

    @BeforeEach
    void setUp() {
        user = User.builder()
                .id(UUID.randomUUID())
                .googleId("google-123")
                .email("arjun@example.com")
                .name("Arjun")
                .build();
    }

    @Test
    @DisplayName("should create a room with a generated invite key and add creator as member")
    void shouldCreateRoomAndAddCreatorAsMember() {
        when(inviteKeyGenerator.generate()).thenReturn("ABC123");
        Room savedRoom = Room.builder()
                .id(UUID.randomUUID())
                .name("Dev Chat")
                .inviteKey("ABC123")
                .createdBy(user)
                .build();
        when(roomRepository.save(any(Room.class))).thenReturn(savedRoom);

        Room result = roomService.createRoom("Dev Chat", user);

        assertThat(result.getName()).isEqualTo("Dev Chat");
        assertThat(result.getInviteKey()).isEqualTo("ABC123");
        verify(roomMemberRepository).save(any(RoomMember.class));
    }

    @Test
    @DisplayName("should join a room with a valid invite key")
    void shouldJoinRoomWithValidInviteKey() {
        Room room = Room.builder()
                .id(UUID.randomUUID())
                .name("Dev Chat")
                .inviteKey("ABC123")
                .createdBy(user)
                .build();
        when(roomRepository.findByInviteKey("ABC123")).thenReturn(Optional.of(room));
        when(roomMemberRepository.existsById(any(RoomMemberId.class))).thenReturn(false);

        Room result = roomService.joinRoom("ABC123", user);

        assertThat(result.getInviteKey()).isEqualTo("ABC123");
        verify(roomMemberRepository).save(any(RoomMember.class));
    }

    @Test
    @DisplayName("should throw when invite key does not exist")
    void shouldThrowWhenInviteKeyNotFound() {
        when(roomRepository.findByInviteKey("XXXXXX")).thenReturn(Optional.empty());

        assertThatThrownBy(() -> roomService.joinRoom("XXXXXX", user))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Invalid invite key");
    }

    @Test
    @DisplayName("should not add duplicate member when user already in room")
    void shouldNotAddDuplicateMember() {
        Room room = Room.builder()
                .id(UUID.randomUUID())
                .name("Dev Chat")
                .inviteKey("ABC123")
                .createdBy(user)
                .build();
        when(roomRepository.findByInviteKey("ABC123")).thenReturn(Optional.of(room));
        when(roomMemberRepository.existsById(any(RoomMemberId.class))).thenReturn(true);

        roomService.joinRoom("ABC123", user);

        verify(roomMemberRepository, never()).save(any(RoomMember.class));
    }
}
