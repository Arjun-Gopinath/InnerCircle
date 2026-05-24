package com.arjun.innercircle.repository;

import com.arjun.innercircle.model.RoomMember;
import com.arjun.innercircle.model.RoomMemberId;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface RoomMemberRepository extends JpaRepository<RoomMember, RoomMemberId> {
    List<RoomMember> findByUserId(UUID userId);
}
