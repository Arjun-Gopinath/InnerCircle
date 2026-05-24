package com.arjun.innercircle.service;

import com.arjun.innercircle.model.Room;
import com.arjun.innercircle.model.RoomMember;
import com.arjun.innercircle.model.RoomMemberId;
import com.arjun.innercircle.model.User;
import com.arjun.innercircle.repository.RoomMemberRepository;
import com.arjun.innercircle.repository.RoomRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class RoomService {

    private final RoomRepository roomRepository;
    private final RoomMemberRepository roomMemberRepository;
    private final InviteKeyGenerator inviteKeyGenerator;

    @Transactional
    public Room createRoom(String name, User creator) {
        Room room = Room.builder()
                .name(name)
                .inviteKey(inviteKeyGenerator.generate())
                .createdBy(creator)
                .build();
        Room savedRoom = roomRepository.save(room);
        addMember(savedRoom, creator);
        return savedRoom;
    }

    @Transactional
    public Room joinRoom(String inviteKey, User user) {
        Room room = roomRepository.findByInviteKey(inviteKey)
                .orElseThrow(() -> new IllegalArgumentException("Invalid invite key"));
        addMember(room, user);
        return room;
    }

    private void addMember(Room room, User user) {
        RoomMemberId memberId = new RoomMemberId(room.getId(), user.getId());
        if (!roomMemberRepository.existsById(memberId)) {
            roomMemberRepository.save(RoomMember.builder()
                    .id(memberId)
                    .room(room)
                    .user(user)
                    .build());
        }
    }
}
