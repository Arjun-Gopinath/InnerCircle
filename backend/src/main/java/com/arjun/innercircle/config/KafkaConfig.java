package com.arjun.innercircle.config;

import com.arjun.innercircle.model.Message;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import reactor.core.publisher.Sinks;

@Configuration
public class KafkaConfig {

    @Bean
    public Sinks.Many<Message> messageSink() {
        return Sinks.many().multicast().onBackpressureBuffer();
    }
}
