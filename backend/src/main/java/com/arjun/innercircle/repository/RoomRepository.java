package com.arjun.innercircle.repository;

import com.arjun.innercircle.model.Room;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface RoomRepository extends JpaRepository<Room, UUID> {
    Optional<Room> findByInviteKey(String inviteKey);
}
