package com.arjun.innercircle.dto;

import com.arjun.innercircle.model.Message;

import java.util.UUID;

public record MessageDto(
        UUID id,
        String content,
        UserDto sender,
        UUID roomId,
        String sentAt
) {
    public static MessageDto from(Message message) {
        return new MessageDto(
                message.getId(),
                message.getContent(),
                UserDto.from(message.getSender()),
                message.getRoom().getId(),
                message.getSentAt().toString()
        );
    }
}
