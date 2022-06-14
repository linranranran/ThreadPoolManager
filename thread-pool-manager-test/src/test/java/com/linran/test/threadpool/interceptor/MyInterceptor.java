package com.linran.test.threadpool.interceptor;

import com.linran.threadpool.annotation.PoolInterceptor;
import com.linran.threadpool.factory.pool.AbstractThreadPoolExecutor;
import com.linran.threadpool.interceptor.ThreadPoolInterceptor;
import com.linran.threadpool.task.AbstractRunnable;

/**
 * @Author LinRan
 * @Date 2022/6/14
 */
@PoolInterceptor
public class MyInterceptor implements ThreadPoolInterceptor {
    /**
     * 执行任务前
     *
     * @param executor 线程池实例
     * @param runnable 正在执行的任务
     * @param thread   执行的线程实例
     */
    @Override
    public void beforeRun(AbstractThreadPoolExecutor executor, AbstractRunnable runnable, Thread thread) {
        System.out.println("==========beforeRun我生效了！================");
    }

    /**
     * 执行任务后
     *
     * @param executor  线程池实例
     * @param runnable  正在执行的任务
     * @param throwable 异常，the exception that caused termination, or null if
     */
    @Override
    public void afterRun(AbstractThreadPoolExecutor executor, AbstractRunnable runnable, Throwable throwable) {
        System.out.println("==========afterRun我生效了！================");
    }
}
