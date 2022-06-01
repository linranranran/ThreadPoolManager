package com.linran.threadpool.interceptor;

import com.linran.threadpool.factory.pool.AbstractThreadPoolExecutor;
import com.linran.threadpool.task.AbstractRunnable;

/**
 *
 * 线程池执行任务钩子函数
 *
 * @Author LinRan
 * @Date 2022/5/26
 */
public interface ThreadPoolInterceptor {

    /**
     * 执行任务前
     * @param executor  线程池实例
     * @param runnable  正在执行的任务
     * @param thread    执行的线程实例
     * */
    public void beforeRun(AbstractThreadPoolExecutor executor, AbstractRunnable runnable, Thread thread);

    /**
     * 执行任务后
     * @param executor  线程池实例
     * @param runnable  正在执行的任务
     * @param throwable 异常，the exception that caused termination, or null if
     * */
    public void afterRun(AbstractThreadPoolExecutor executor, AbstractRunnable runnable, Throwable throwable);

}
