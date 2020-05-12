package ru.protei.scriptServer.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import ru.protei.scriptServer.model.POJO.Message;

import java.util.HashMap;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

@Configuration
public class ProcessQueueConfig {
    @Bean
    public BlockingQueue<HashMap<String,Process>> processBlockingQueue() {
        return new LinkedBlockingQueue<>();
    }
}
