package com.example.kafkasender;

import org.apache.camel.CamelContext;
import org.apache.camel.spi.ThreadPoolProfile;
import org.apache.camel.util.concurrent.ThreadPoolRejectedPolicy;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.concurrent.ExecutorService;

@Configuration
public class CamelThreadPoolConfig  {
    @Bean(name = "customExecutorService")
    public ExecutorService customExecutorService(CamelContext camelContext) {
        ThreadPoolProfile profile = new ThreadPoolProfile("custom");
        profile.setPoolSize(50);
        profile.setMaxPoolSize(1000);
        profile.setMaxQueueSize(100000);
        profile.setKeepAliveTime(60L);
        profile.setRejectedPolicy(ThreadPoolRejectedPolicy.CallerRuns);
        camelContext.getExecutorServiceManager().registerThreadPoolProfile(profile);
        return camelContext.getExecutorServiceManager()
                .newThreadPool(this, "customExecutorService", profile);
    }
}
