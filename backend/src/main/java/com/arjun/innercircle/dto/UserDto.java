package com.arjun.innercircle.dto;

import com.arjun.innercircle.model.User;

import java.util.UUID;

public record UserDto(
        UUID id,
        String name,
        String email,
        String avatarUrl
) {
    public static UserDto from(User user) {
        return new UserDto(
                user.getId(),
                user.getName(),
                user.getEmail(),
                user.getAvatarUrl()
        );
    }
}
