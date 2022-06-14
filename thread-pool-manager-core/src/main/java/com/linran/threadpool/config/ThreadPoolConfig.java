package com.linran.threadpool.config;

import lombok.Data;


/**
 * @Author LinRan
 * @Date 2022/6/6
 */
@Data
public class ThreadPoolConfig {

    private String poolName;

    private Integer coreSize;

    private Integer maxSize;

    private Long aliveTime;

    /** 时间单位 -2纳秒 -1微秒 0毫秒 1秒 2分钟 3小时 4天 */
    private Integer timeType;

    /** 队列类型 0LinkedDeque 1ArrayQueue*/
    private Integer queueType;

    private Integer queueSize;

    private String threadFactory;

    private String rejectHandler;
}
