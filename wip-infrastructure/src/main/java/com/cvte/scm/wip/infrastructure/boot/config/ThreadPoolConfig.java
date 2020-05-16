package com.cvte.scm.wip.infrastructure.boot.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.task.TaskExecutor;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import java.util.concurrent.ThreadPoolExecutor;

/**
  * 线程池配置，结合@Async注解使用
  * @author  : xueyuting
  * @since    : 2020/2/26 17:23
  * @version : 1.0
  * email   : xueyuting@cvte.com
  */
@Configuration
public class ThreadPoolConfig {
    @Bean("taskExecutor")
    public TaskExecutor taskExecutor() {
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        // 核心线程数
        executor.setCorePoolSize(10);
        // 最大线程数
        executor.setMaxPoolSize(20);
        // 队列容量
        executor.setQueueCapacity(256);
        // 存活时间
        executor.setKeepAliveSeconds(60);
        // 默认线程名称
        executor.setThreadNamePrefix("taskExecutor-");
        // 若满，直接拒绝
        executor.setRejectedExecutionHandler(new ThreadPoolExecutor.CallerRunsPolicy());
        return executor;
    }
}