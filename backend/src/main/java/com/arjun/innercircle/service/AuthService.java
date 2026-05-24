package com.arjun.innercircle.service;

import com.arjun.innercircle.model.User;
import com.arjun.innercircle.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserRepository userRepository;

    @Transactional
    public User loadOrCreateUser(Jwt jwt) {
        String googleId = jwt.getSubject();
        return userRepository.findByGoogleId(googleId)
                .orElseGet(() -> createUserFromJwt(jwt, googleId));
    }

    private User createUserFromJwt(Jwt jwt, String googleId) {
        log.info("Creating new user for Google ID: {}", googleId);
        User user = User.builder()
                .googleId(googleId)
                .email(jwt.getClaimAsString("email"))
                .name(jwt.getClaimAsString("name"))
                .avatarUrl(jwt.getClaimAsString("picture"))
                .build();
        return userRepository.save(user);
    }
}
