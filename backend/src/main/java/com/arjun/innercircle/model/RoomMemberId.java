package com.arjun.innercircle.model;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.UUID;

@Embeddable
@Getter
@EqualsAndHashCode
@NoArgsConstructor
@AllArgsConstructor
public class RoomMemberId implements Serializable {

    @Column(name = "room_id")
    private UUID roomId;

    @Column(name = "user_id")
    private UUID userId;
}
