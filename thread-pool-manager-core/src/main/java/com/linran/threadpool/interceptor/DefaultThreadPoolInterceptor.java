package com.linran.threadpool.interceptor;

import com.linran.threadpool.factory.pool.AbstractThreadPoolExecutor;
import com.linran.threadpool.task.AbstractRunnable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @Author LinRan
 * @Date 2022/5/26
 */
public class DefaultThreadPoolInterceptor implements ThreadPoolInterceptor{

    private static final Logger log = LoggerFactory.getLogger(DefaultThreadPoolInterceptor.class);

    /**
     * 执行任务前
     *
     * @param executor  线程池实例
     * @param runnable 正在执行的任务
     * @param thread   执行的线程实例
     */
    @Override
    public void beforeRun(AbstractThreadPoolExecutor executor , AbstractRunnable runnable, Thread thread) {
        log.info("before run executor taskNums:" + executor.getTaskNums().get());
        log.info("before run executor name:" + executor.getName());
        log.info("before run runnable taskNums:" + runnable.getTaskNums());
        log.info("before run runnable start:" + runnable.getStartTime());
        log.info("before run runnable end:" + runnable.getEndTime());
    }

    /**
     * 执行任务后
     *
     * @param executor  线程池实例
     * @param runnable  正在执行的任务
     * @param throwable 异常，the exception that caused termination, or null if
     */
    @Override
    public void afterRun(AbstractThreadPoolExecutor executor , AbstractRunnable runnable, Throwable throwable) {
        log.info("after run executor taskNums:" + executor.getTaskNums().get());
        log.info("after run executor name:" + executor.getName());
        log.info("after run runnable taskNums:" + runnable.getTaskNums());
        log.info("after run runnable start:" + runnable.getStartTime());
        log.info("after run runnable end:" + runnable.getEndTime());
    }
}
