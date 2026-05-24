package com.arjun.innercircle.dto;

import com.arjun.innercircle.model.Room;

import java.util.UUID;

public record RoomDto(
        UUID id,
        String name,
        String inviteKey,
        UserDto createdBy,
        String createdAt
) {
    public static RoomDto from(Room room) {
        return new RoomDto(
                room.getId(),
                room.getName(),
                room.getInviteKey(),
                UserDto.from(room.getCreatedBy()),
                room.getCreatedAt().toString()
        );
    }
}
