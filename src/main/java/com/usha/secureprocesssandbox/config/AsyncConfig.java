package com.usha.secureprocesssandbox.config;



import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import java.util.concurrent.Executor;

@Configuration
@EnableAsync
public class AsyncConfig {

    @Bean(name = "sandboxExecutor")
    public Executor sandboxExecutor() {
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        executor.setCorePoolSize(10);       // Maintain up to 10 active baseline worker loops
        executor.setMaxPoolSize(25);        // Burst allowance for busy traffic windows
        executor.setQueueCapacity(100);     // Buffer size before requests queue up
        executor.setThreadNamePrefix("SandboxWorker-");
        executor.initialize();
        return executor;
    }
}
