-- Users (populated from Google OAuth)
CREATE TABLE users (
    id          UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    google_id   VARCHAR(255) NOT NULL UNIQUE,
    email       VARCHAR(255) NOT NULL UNIQUE,
    name        VARCHAR(255) NOT NULL,
    avatar_url  VARCHAR(512),
    created_at  TIMESTAMP NOT NULL DEFAULT now()
);

-- Rooms
CREATE TABLE rooms (
    id          UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    name        VARCHAR(100) NOT NULL,
    invite_key  VARCHAR(6)   NOT NULL UNIQUE,
    created_by  UUID         NOT NULL REFERENCES users(id),
    created_at  TIMESTAMP    NOT NULL DEFAULT now()
);

-- Room membership (who has joined which room)
CREATE TABLE room_members (
    room_id     UUID      NOT NULL REFERENCES rooms(id) ON DELETE CASCADE,
    user_id     UUID      NOT NULL REFERENCES users(id) ON DELETE CASCADE,
    joined_at   TIMESTAMP NOT NULL DEFAULT now(),
    PRIMARY KEY (room_id, user_id)
);

-- Messages
CREATE TABLE messages (
    id          UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    content     TEXT      NOT NULL,
    room_id     UUID      NOT NULL REFERENCES rooms(id) ON DELETE CASCADE,
    sender_id   UUID      NOT NULL REFERENCES users(id),
    sent_at     TIMESTAMP NOT NULL DEFAULT now()
);

-- Indexes for common query patterns
CREATE INDEX idx_messages_room_id     ON messages(room_id, sent_at DESC);
CREATE INDEX idx_room_members_user_id ON room_members(user_id);
CREATE INDEX idx_rooms_invite_key     ON rooms(invite_key);