package com.linran.threadpool.factory.pool;

/**
 * @Author LinRan
 * @Date 2022/5/26
 */
public class ThreadPoolParameter {

    /**
     *  核心线程大小
     * */
    private int coreThreadSize;

    /**
     * 最大线程大小
     * */
    private int maxThreadSize;

    /**
     * 实际核心线程数量
     * */
    private int coreThreadNums;

    /**
     * 实际非核心线程数量
     * */
    private int otherThreadNums;

    /**
     * 阻塞队列大小
     * */
    private int queueSize;

    /**
     * 阻塞队列类型
     * 0-Linked
     * 1-Array
     * */
    private int queueType;

    /**
     * 线程池执行任务数量
     * */
    private long taskCount;

    /**
     * Atomic计数执行任务数量
     * */
    private long currentTaskNums;

    /**
     * 线程池类型
     * 0-默认
     * 1-继承AbstractThreadPoolExecutor
     * */
    private int poolType;

    public int getCoreThreadSize() {
        return coreThreadSize;
    }

    public void setCoreThreadSize(int coreThreadSize) {
        this.coreThreadSize = coreThreadSize;
    }

    public int getMaxThreadSize() {
        return maxThreadSize;
    }

    public void setMaxThreadSize(int maxThreadSize) {
        this.maxThreadSize = maxThreadSize;
    }

    public int getCoreThreadNums() {
        return coreThreadNums;
    }

    public void setCoreThreadNums(int coreThreadNums) {
        this.coreThreadNums = coreThreadNums;
    }

    public int getOtherThreadNums() {
        return otherThreadNums;
    }

    public void setOtherThreadNums(int otherThreadNums) {
        this.otherThreadNums = otherThreadNums;
    }

    public int getQueueSize() {
        return queueSize;
    }

    public void setQueueSize(int queueSize) {
        this.queueSize = queueSize;
    }

    public int getQueueType() {
        return queueType;
    }

    public void setQueueType(int queueType) {
        this.queueType = queueType;
    }

    public long getCurrentTaskNums() {
        return currentTaskNums;
    }

    public void setCurrentTaskNums(long currentTaskNums) {
        this.currentTaskNums = currentTaskNums;
    }

    public int getPoolType() {
        return poolType;
    }

    public void setPoolType(int poolType) {
        this.poolType = poolType;
    }

    public long getTaskCount() {
        return taskCount;
    }

    public void setTaskCount(long taskCount) {
        this.taskCount = taskCount;
    }
}
