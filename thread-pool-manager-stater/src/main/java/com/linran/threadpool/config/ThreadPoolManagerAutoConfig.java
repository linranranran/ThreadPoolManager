package com.linran.threadpool.config;

import com.linran.threadpool.handler.ThreadPoolHolder;
import com.linran.threadpool.properties.ThreadPoolManagerProperties;
import com.linran.threadpool.util.ThreadPoolManager;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @Author LinRan
 * @Date 2022/6/1
 */
@Configuration
@EnableConfigurationProperties(ThreadPoolManagerProperties.class)
public class ThreadPoolManagerAutoConfig {

    @Bean
    public ThreadPoolManager getThreadPoolManager(ThreadPoolManagerProperties properties){
        ThreadPoolHolder holder = new ThreadPoolHolder();
        ThreadPoolManager manager = new ThreadPoolManager(holder);
        return manager;
    }
}
