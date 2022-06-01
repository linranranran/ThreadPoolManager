package com.linran.threadpool.task;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @Author LinRan
 * @Date 2022/5/25
 */
public abstract class AbstractRunnable implements Runnable{

    private static final Logger log = LoggerFactory.getLogger(AbstractRunnable.class);

    /**
     * 任务编号
     * */
    private long taskNums;

    /**
     * 开始时间
     * */
    private long startTime;

    /**
     * 结束时间
     * */
    private long endTime;

    /**
     * 线程池执行任务前置方法
     * */
    public abstract void before();

    /**
     * 线程池执行任务后置方法
     * */
    public abstract void after();

    /**
     * 实际线程需执行的任务方法，等同于Runnable的run方法
     *
     * */
    public abstract void task();

    @Override
    public void run() {
        before();
        task();
        after();
    }

    public long getTaskNums() {
        return taskNums;
    }

    public void setTaskNums(long taskNums) {
        this.taskNums = taskNums;
    }

    public long getStartTime() {
        return startTime;
    }

    public void setStartTime(long startTime) {
        this.startTime = startTime;
    }

    public long getEndTime() {
        return endTime;
    }

    public void setEndTime(long endTime) {
        this.endTime = endTime;
    }
}
