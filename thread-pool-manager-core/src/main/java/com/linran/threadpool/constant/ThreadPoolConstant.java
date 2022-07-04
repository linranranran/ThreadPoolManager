package com.linran.threadpool.constant;

import java.util.concurrent.TimeUnit;

/**
 * @author: LinRan
 * @create: 2022-05-21 23:34
 **/
public class ThreadPoolConstant {

    public static final String DEFAULT_POOL_NAME = "default";
    /** 核心线程数，默认为当前服务器cpu核心数量的两倍 */
    public static final int DEFAULT_POOL_CORE_THREAD_NUMS = Runtime.getRuntime().availableProcessors() * 2;
    /** 最大线程数，默认为当前服务器cpu核心数量的三倍 */
    public static final int DEFAULT_POOL_MAX_THREAD_NUMS = Runtime.getRuntime().availableProcessors() * 3;

    public static final long DEFAULT_POOL_THREAD_KEEP_ALIVE_TIME = 30;

    public static final TimeUnit DEFAULT_POOL_THREAD_KEEP_ALIVE_TIME_UNIT = TimeUnit.SECONDS;

    public static final int DEFAULT_POOL_THREAD_QUEUE_SIZE = 64;

    /** 阻塞队列类型，0:LinkedQueue 1:ArrayQueue */
    public static final int DEFAULT_POOL_THREAD_QUEUE_TYPE = 0;

    public static final String DEFAULT_THREAD_NAME_PREFIX = "pool-";

    public static final String DEFAULT_THREAD_NAME_SUFFIX = "-thread-";

    public static final String DEFAULT_THREAD_INTERCEPTOR_ALL = "THREAD-POOL-ALL";
}
