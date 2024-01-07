package com.banmao.yuquedownloader.config;

import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import java.util.concurrent.RejectedExecutionHandler;
import java.util.concurrent.ThreadPoolExecutor;

@Configuration
@EnableAsync
@Slf4j
public class ThreadPoolConfig {

    /**
     * 创建线程池 Doc处理线程
     */
    @Bean(name = "asyncDocExecutor")
    public ThreadPoolTaskExecutor asyncExecutorConsumerKafka() {
        ThreadPoolExecutor.CallerRunsPolicy callerRunsPolicy = new ThreadPoolExecutor.CallerRunsPolicy();
        return initExcutor("DocExecutor", callerRunsPolicy);
    }

    /**
     * 创建线程池 下载图片线程池
     */
    @Bean(name = "asyncImageExecutor")
    public ThreadPoolTaskExecutor asyncExecutorFailFile() {
        ThreadPoolExecutor.CallerRunsPolicy callerRunsPolicy = new ThreadPoolExecutor.CallerRunsPolicy();
        return initExcutor("ImageExecutor", callerRunsPolicy);
    }

    private ThreadPoolTaskExecutor initExcutor(String threadName, RejectedExecutionHandler rejectedExecutionHandler){
        ThreadPoolTaskExecutor threadPool = new ThreadPoolTaskExecutor();
        threadPool.setCorePoolSize(10);
        threadPool.setMaxPoolSize(20);
        threadPool.setKeepAliveSeconds(10);
        threadPool.setQueueCapacity(5);
        threadPool.setThreadNamePrefix(threadName);
        threadPool.setRejectedExecutionHandler(rejectedExecutionHandler);
        return threadPool;
    }
}

