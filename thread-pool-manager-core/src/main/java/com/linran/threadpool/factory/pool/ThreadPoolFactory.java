package com.linran.threadpool.factory.pool;

import java.util.concurrent.ThreadPoolExecutor;

/**
 * @author: LinRan
 * @create: 2022-05-22 15:04
 **/
public interface ThreadPoolFactory {

    /**
     * 创建线程池实例
     * @return  ThreadPoolExecutor
     * */
    public ThreadPoolExecutor createBasicThreadPoolInstance();

    /**
     * 返回要创建的线程池名称
     * @return  poolName
     * */
    public String getPoolName();

}
