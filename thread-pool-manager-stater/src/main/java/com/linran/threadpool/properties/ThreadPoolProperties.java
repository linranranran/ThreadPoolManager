package com.linran.threadpool.properties;

import lombok.Data;


/**
 * @Author LinRan
 * @Date 2022/6/6
 */
@Data
public class ThreadPoolProperties {

    private Integer mainSize;

    private Integer maxSize;

    private Integer aliveTime;

    /** 时间单位 0毫秒，1秒 */
    private Integer timeType;

    /** 队列类型 0LinkedQueue 1ArrayQueue*/
    private Integer queueType;

    private Integer queueSize;

    private String threadFactory;

    private String rejectHandler;
}
