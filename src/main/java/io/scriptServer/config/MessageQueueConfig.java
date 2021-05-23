package io.scriptServer.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import io.scriptServer.model.POJO.Message;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

@Configuration
public class MessageQueueConfig {
    @Bean
    public BlockingQueue<Message> blockingQueue() {
        return new LinkedBlockingQueue<Message>();
    }
}
