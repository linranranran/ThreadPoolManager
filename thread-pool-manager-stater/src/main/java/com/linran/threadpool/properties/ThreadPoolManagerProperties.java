package com.linran.threadpool.properties;

import com.linran.threadpool.config.ThreadPoolConfig;
import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

import java.util.Map;

/**
 * linran:
 *
 *  --设置默认线程池name，如果手动设置了则默认线程池名称为该设置，但是参数仍可通过“default:”来设置。
 *  default-pool-name:xxxx
 *
 *  -- 全局设置，即默认值，未手动设置的参数的实例优先使用该设置。
 *  global-set:
 *
 *  -- 线程池
 *  pools:
 *    -- default线程池是默认的，设不设置都会创建该线程池实例。
 *    default:
 *
 *      -- true表示开启自动设置，如果开启，其它没有手动设置的参数均为默认设置，手动设置优先级更高。
 *      auto-set:true/false
 *
 *      main-size:
 *      max-size:
 *      alive-time:
 *      time-util:
 *      queue-type:
 *      queue-size:
 *      thread-factory:
 *      reject-handler:
 *
 *    -- 申明"xxx"线程池
 *    xxxx:
 *      ....
 *      ....
 *
 *    -- 申明"aaa"和"bbb"两个线程池，并且共用同一个参数设置。
 *    aaa,bbb:
 *      ....
 *      ....
 * @Author LinRan
 * @Date 2022/6/1
 */
@ConfigurationProperties(prefix = "linran")
@Data
public class ThreadPoolManagerProperties {

    /**
     * 默认线程池名称
     * */
    private String defaultPoolName;

    /**
     *  全局线程池参数设置
     *      main-size:
     *      max-size:
     *      alive-time:
     *      time-util:
     *      queue-type:
     *      queue-size:
     *      thread-factory:
     *      reject-handler:
     * */
    private ThreadPoolConfig globalSet;

    /**
     * 申明其它线程池，default为关键字，指默认线程池，而默认线程池的线程池名可以通过defaultPoolName设置。
     * -- 申明"xxx"线程池
     *    xxxx:
     *      ....
     *      ....
     *
     *    -- 申明"aaa"和"bbb"两个线程池，并且共用同一个参数设置。
     *    aaa,bbb:
     *      ....
     *      ....
     * */
    private Map<String , ThreadPoolConfig> pools;

}
