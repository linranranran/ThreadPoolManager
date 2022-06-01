package com.linran.threadpool.factory.pool;

import com.linran.threadpool.constant.ThreadPoolConstant;
import com.linran.threadpool.factory.thread.DefaultThreadFactory;

import java.util.concurrent.LinkedBlockingDeque;
import java.util.concurrent.ThreadPoolExecutor;

/**
 * @author: LinRan
 * @create: 2022-05-22 15:06
 **/
public class DefaultThreadPoolFactory implements ThreadPoolFactory{

    @Override
    public ThreadPoolExecutor createBasicThreadPoolInstance(String poolName) {
        //创建自定义的默认线程池
        DefaultThreadPoolExecutor defaultPool = new DefaultThreadPoolExecutor(
                ThreadPoolConstant.DEFAULT_POOL_CORE_THREAD_NUMS ,
                ThreadPoolConstant.DEFAULT_POOL_MAX_THREAD_NUMS ,
                ThreadPoolConstant.DEFAULT_POOL_THREAD_KEEP_ALIVE_TIME,
                ThreadPoolConstant.DEFAULT_POOL_THREAD_KEEP_ALIVE_TIME_UNIT,
                new LinkedBlockingDeque<>(ThreadPoolConstant.DEFAULT_POOL_THREAD_QUEUE_SIZE),
                new DefaultThreadFactory());
        defaultPool.setName(poolName);
        return defaultPool;
    }

}
