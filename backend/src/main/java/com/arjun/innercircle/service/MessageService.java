package com.arjun.innercircle.service;

import com.arjun.innercircle.kafka.MessageProducer;
import com.arjun.innercircle.model.Message;
import com.arjun.innercircle.model.Room;
import com.arjun.innercircle.model.RoomMemberId;
import com.arjun.innercircle.model.User;
import com.arjun.innercircle.repository.MessageRepository;
import com.arjun.innercircle.repository.RoomMemberRepository;
import com.arjun.innercircle.repository.RoomRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

import org.springframework.data.domain.PageRequest;

@Service
@RequiredArgsConstructor
public class MessageService {

    private final RoomRepository roomRepository;
    private final RoomMemberRepository roomMemberRepository;
    private final MessageRepository messageRepository;
    private final MessageProducer messageProducer;

    public void sendMessage(UUID roomId, String content, User sender) {
        Room room = roomRepository.findById(roomId)
                .orElseThrow(() -> new IllegalArgumentException("Room not found"));

        boolean isMember = roomMemberRepository
                .existsById(new RoomMemberId(roomId, sender.getId()));
        if (!isMember) {
            throw new IllegalStateException("User is not a member of this room");
        }

        Message message = Message.builder()
                .content(content)
                .room(room)
                .sender(sender)
                .build();

        messageProducer.publish(message);
    }

    public List<Message> getMessages(UUID roomId, int limit) {
        return messageRepository.findByRoomIdOrderBySentAtDesc(
                roomId, PageRequest.of(0, limit));
    }
}
