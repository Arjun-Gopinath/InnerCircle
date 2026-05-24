package com.arjun.innercircle.service;

import org.springframework.stereotype.Component;

import java.security.SecureRandom;

@Component
public class InviteKeyGenerator {

    private static final String CHARACTERS = "ABCDEFGHJKLMNPQRSTUVWXYZ23456789";
    private static final int KEY_LENGTH = 6;
    private final SecureRandom random = new SecureRandom();

    public String generate() {
        StringBuilder key = new StringBuilder(KEY_LENGTH);
        for (int i = 0; i < KEY_LENGTH; i++) {
            key.append(CHARACTERS.charAt(random.nextInt(CHARACTERS.length())));
        }
        return key.toString();
    }
}