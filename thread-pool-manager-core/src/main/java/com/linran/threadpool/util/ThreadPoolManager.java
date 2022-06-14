package com.linran.threadpool.util;

import com.linran.threadpool.constant.ThreadPoolAddType;
import com.linran.threadpool.factory.pool.ThreadPoolFactory;
import com.linran.threadpool.handler.ThreadPoolHolder;
import com.linran.threadpool.interceptor.ThreadPoolInterceptor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ThreadPoolExecutor;

/**
 * 用于管理ThreadPoolHolder管理类，可用于Spring注入
 * @author: LinRan
 * @create: 2022-05-21 21:13
 **/
public class ThreadPoolManager {

    private static final Logger log = LoggerFactory.getLogger(ThreadPoolManager.class);

    /** 线程池缓存池 */
    private ThreadPoolHolder holder;

    public ThreadPoolManager(ThreadPoolHolder holder){
        this.holder = holder;
    }

    public ThreadPoolManager(){

    }
    /**
     * 使用默认线程池执行任务
     * @param runnable
     * */
    public long execute(Runnable runnable){
        return holder.executeTask(runnable);
    }

    /**
     * 使用指定线程池执行任务
     * @param runnable
     * */
    public long execute(String poolName , Runnable runnable){
        return holder.executeTask(poolName , runnable);
    }

    /**
     * 使用线程池工程创建线程池，并添加至缓存池
     * @param poolName
     * @param factory
     * */
    public void addPoolInstance(String poolName , ThreadPoolFactory factory){
        holder.addThreadPool(poolName ,factory.createBasicThreadPoolInstance());
    }

    /**
     * 获得当前线程池数量
     * @return 线程池数量
     * */
    public int getThreadPoolCount(){
        return holder.getThreadPoolNameSet().size();
    }

    /**
     * 获得当前线程池集合
     * @return set
     * */
    public Set<String> getThreadPoolNameSet(){
        return holder.getThreadPoolNameSet();
    }

    /**
     * 使用默认的方式添加线程池实例
     * @param poolName
     * @param executor
     * */
    public void addPoolInstance(String poolName , ThreadPoolExecutor executor){
        holder.addThreadPool(poolName ,executor);
    }

    /**
     * 使用指定的方式添加线程池实例
     * @param poolName
     * @param executor
     * */
    public void addPoolInstance(String poolName , ThreadPoolExecutor executor , ThreadPoolAddType type){
        holder.addThreadPool(poolName ,executor , type);
    }

    /**
     * 默认为所有线程池添加拦截器
     * @param interceptor
     * */
    public void addPoolInterceptor(ThreadPoolInterceptor interceptor){
        holder.addPoolInterceptor(interceptor);
    }

    /**
     * 默认为所有线程池批量添加拦截器
     * @param intercepts
     * */
    public void addPoolInterceptor(List<ThreadPoolInterceptor> intercepts){
        holder.addPoolInterceptor(intercepts);
    }

    /**
     * 为指定线程池添加拦截器
     * @param interceptor
     * */
    public void addPoolInterceptor(String poolName , ThreadPoolInterceptor interceptor){
        holder.addPoolInterceptor(poolName , interceptor);
    }

    /**
     * 为指定线程池批量添加拦截器
     * @param intercepts
     * */
    public void addPoolInterceptor(String poolName , List<ThreadPoolInterceptor> intercepts){
        holder.addPoolInterceptor(poolName , intercepts);
    }

    public ThreadPoolHolder getHolder() {
        return holder;
    }

    public void setHolder(ThreadPoolHolder holder) {
        this.holder = holder;
    }
}
