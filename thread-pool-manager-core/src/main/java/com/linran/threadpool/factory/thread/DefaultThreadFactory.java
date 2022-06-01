package com.linran.threadpool.factory.thread;

import com.linran.threadpool.constant.ThreadPoolConstant;

import java.util.concurrent.ThreadFactory;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * @author: LinRan
 * @create: 2022-05-22 15:20
 **/
public class DefaultThreadFactory implements ThreadFactory {

    /** 线程编号计数器 */
    private AtomicInteger threadNums = new AtomicInteger(0);


    @Override
    public Thread newThread(Runnable r) {
        return new Thread(r , getThreadName());
    }

    /**
     * 获得线程名：
     * 默认线程名称格式：pool-‘线程池名’-thread-'线程编号'
     * @return 线程名称
     * */
    public String getThreadName(){
        StringBuffer name = new StringBuffer();
        name.append(ThreadPoolConstant.DEFAULT_THREAD_NAME_PREFIX);
        name.append(ThreadPoolConstant.DEFAULT_POOL_NAME);
        name.append(ThreadPoolConstant.DEFAULT_THREAD_NAME_SUFFIX);
        name.append(threadNums.getAndIncrement());
        return name.toString();
    }
}
