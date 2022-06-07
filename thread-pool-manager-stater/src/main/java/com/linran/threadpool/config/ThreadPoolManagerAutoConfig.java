package com.linran.threadpool.config;

import cn.hutool.core.util.StrUtil;
import com.linran.threadpool.handler.ThreadPoolHolder;
import com.linran.threadpool.properties.ThreadPoolManagerProperties;
import com.linran.threadpool.util.ThreadPoolManager;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.HashMap;
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
        //start create default pool
        String defaultName = properties.getDefaultPoolName();
        ThreadPoolProperties globalSet = properties.getGlobalSet();
        //避免只设置了默认线程池名称而没设置全局设置而报错
        if(globalSet == null){
            globalSet = new ThreadPoolProperties();
        }
        if(!StrUtil.isEmpty(defaultName)){
            globalSet.setPoolName(defaultName);
        }
        ThreadPoolHolder holder = null;
        if(globalSet != null){
            holder = new ThreadPoolHolder(globalSet);
        }else{
            holder = new ThreadPoolHolder();
        }
        ThreadPoolManager manager = new ThreadPoolManager(holder);
        return manager;
    }
}
