package com.arjun.innercircle.kafka;

import com.arjun.innercircle.model.Message;
import com.arjun.innercircle.repository.MessageRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Sinks;

@Slf4j
@Component
@RequiredArgsConstructor
public class MessageConsumer {

    private final MessageRepository messageRepository;
    private final Sinks.Many<Message> messageSink;

    @KafkaListener(topics = "${innercircle.kafka.topics.chat-messages}",
                   groupId = "${spring.kafka.consumer.group-id}")
    public void consume(Message message) {
        log.debug("Consumed message from Kafka: {}", message.getId());
        Message saved = messageRepository.save(message);
        messageSink.tryEmitNext(saved);
    }
}
