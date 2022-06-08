package com.linran.threadpool.factory.pool;

import java.util.concurrent.ThreadPoolExecutor;

/**
 * @author: LinRan
 * @create: 2022-05-22 15:04
 **/
public interface ThreadPoolFactory {

    public ThreadPoolExecutor createBasicThreadPoolInstance();

}
