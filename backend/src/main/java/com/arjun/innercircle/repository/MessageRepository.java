package com.arjun.innercircle.repository;

import com.arjun.innercircle.model.Message;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface MessageRepository extends JpaRepository<Message, UUID> {
    List<Message> findByRoomIdOrderBySentAtDesc(UUID roomId, Pageable pageable);
}
