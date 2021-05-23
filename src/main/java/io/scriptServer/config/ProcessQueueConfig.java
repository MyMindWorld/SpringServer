package io.scriptServer.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import io.scriptServer.model.POJO.RunningScript;

import java.util.HashMap;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

@Configuration
public class ProcessQueueConfig {
    @Bean
    public BlockingQueue<HashMap<RunningScript, Process>> processBlockingQueue() {
        return new LinkedBlockingQueue<>();
    }
}
