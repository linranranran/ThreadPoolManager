package com.linran.threadpool.config;

import cn.hutool.core.util.StrUtil;
import com.linran.threadpool.handler.ThreadPoolHolder;
import com.linran.threadpool.properties.ThreadPoolManagerProperties;
import com.linran.threadpool.util.ThreadPoolManager;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.Map;

/**
 * @Author LinRan
 * @Date 2022/6/1
 */
@Configuration
@EnableConfigurationProperties(ThreadPoolManagerProperties.class)
public class ThreadPoolManagerAutoConfig {

    @Bean
    public ThreadPoolManager getThreadPoolManager(ThreadPoolManagerProperties properties){
        String defaultName = properties.getDefaultPoolName();
        Map<String, String> globalSet = properties.getGlobalSet();
        if(!StrUtil.isEmpty(defaultName)){
            globalSet.put("defaultPoolName" , defaultName);
        }
        ThreadPoolHolder holder = null;
        if(globalSet != null && globalSet.size() != 0){
            holder = new ThreadPoolHolder(globalSet);
        }else{
            holder = new ThreadPoolHolder();
        }
        ThreadPoolManager manager = new ThreadPoolManager(holder);
        return manager;
    }
}
