package com.linran.threadpool.config;

import cn.hutool.core.util.StrUtil;
import com.linran.threadpool.constant.ThreadPoolConstant;
import com.linran.threadpool.factory.pool.ThreadPoolFactory;
import com.linran.threadpool.handler.ThreadPoolHolder;
import com.linran.threadpool.properties.ThreadPoolManagerProperties;
import com.linran.threadpool.util.ThreadPoolManager;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.*;

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
        ThreadPoolConfig globalSet = properties.getGlobalSet();
        //避免只设置了默认线程池名称而没设置全局设置而报错
        if(globalSet == null){
            globalSet = new ThreadPoolConfig();
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

    /**
     *  解析配置返回线程池工厂
     * @param pools
     * @param defaultPoolName 默认线程池名称，用于解析pools.default
     * @return 线程池工厂集合
     * */
    public List<ThreadPoolFactory> resolvePools(Map<String , ThreadPoolConfig> pools , String defaultPoolName){
        List<ThreadPoolFactory> list = new ArrayList<>();
        if( pools == null || pools.size() == 0){
            return list;
        }
        Set<String> keySet = pools.keySet();
        //用于解决同时设置了default和“defaultPoolName”线程池的情况
        boolean flag = false;
        for(String name : keySet){
            //如果设置线程池名称是default或者是“defaultPoolName”则都看作是对默认线程池进行设置，只会创建一个默认线程池。
            if( ThreadPoolConstant.DEFAULT_POOL_NAME.equals(name) || defaultPoolName.equals(name)){
                if( !flag ){
                    break;
                }

                flag = true;
            }else{

            }
        }
        return list;
    }
}
