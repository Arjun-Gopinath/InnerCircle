package com.arjun.innercircle.kafka;

import com.arjun.innercircle.model.Message;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class MessageProducer {

    private final KafkaTemplate<String, Message> kafkaTemplate;

    @Value("${innercircle.kafka.topics.chat-messages}")
    private String topic;

    public void publish(Message message) {
        log.debug("Publishing message to topic {}: {}", topic, message.getId());
        kafkaTemplate.send(topic, message.getRoom().getId().toString(), message);
    }
}
