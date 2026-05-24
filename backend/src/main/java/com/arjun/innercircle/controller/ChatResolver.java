package com.arjun.innercircle.controller;

import com.arjun.innercircle.dto.MessageDto;
import com.arjun.innercircle.dto.RoomDto;
import com.arjun.innercircle.model.Message;
import com.arjun.innercircle.model.User;
import com.arjun.innercircle.repository.RoomMemberRepository;
import com.arjun.innercircle.service.MessageService;
import com.arjun.innercircle.service.RoomService;
import lombok.RequiredArgsConstructor;
import org.springframework.graphql.data.method.annotation.Argument;
import org.springframework.graphql.data.method.annotation.MutationMapping;
import org.springframework.graphql.data.method.annotation.QueryMapping;
import org.springframework.graphql.data.method.annotation.SubscriptionMapping;
import org.springframework.stereotype.Controller;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Sinks;

import java.util.List;
import java.util.UUID;

@Controller
@RequiredArgsConstructor
public class ChatResolver {

    private final RoomService roomService;
    private final MessageService messageService;
    private final RoomMemberRepository roomMemberRepository;
    private final Sinks.Many<Message> messageSink;

    @QueryMapping
    public List<RoomDto> myRooms(User currentUser) {
        return roomMemberRepository.findByUserId(currentUser.getId())
                .stream()
                .map(member -> RoomDto.from(member.getRoom()))
                .toList();
    }

    @QueryMapping
    public List<MessageDto> messages(@Argument UUID roomId, @Argument int limit) {
        return messageService.getMessages(roomId, limit)
                .stream()
                .map(MessageDto::from)
                .toList();
    }

    @MutationMapping
    public RoomDto createRoom(@Argument String name, User currentUser) {
        return RoomDto.from(roomService.createRoom(name, currentUser));
    }

    @MutationMapping
    public RoomDto joinRoom(@Argument String inviteKey, User currentUser) {
        return RoomDto.from(roomService.joinRoom(inviteKey, currentUser));
    }

    @MutationMapping
    public boolean sendMessage(@Argument UUID roomId, @Argument String content, User currentUser) {
        messageService.sendMessage(roomId, content, currentUser);
        return true;
    }

    @SubscriptionMapping
    public Flux<MessageDto> messageReceived(@Argument UUID roomId) {
        return messageSink.asFlux()
                .filter(message -> message.getRoom().getId().equals(roomId))
                .map(MessageDto::from);
    }
}
