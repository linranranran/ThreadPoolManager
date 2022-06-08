package com.linran.threadpool.factory.pool;

import com.linran.threadpool.config.ThreadPoolConfig;
import com.linran.threadpool.constant.ThreadPoolConstant;
import com.linran.threadpool.exception.ThreadPoolConfigException;
import com.linran.threadpool.exception.ThreadPoolNameNullException;
import com.linran.threadpool.factory.thread.DefaultThreadFactory;

import java.util.concurrent.*;

/**
 * @author: LinRan
 * @create: 2022-05-22 15:06
 **/
public class DefaultThreadPoolFactory implements ThreadPoolFactory{

    /** 线程池名称 */
    private String poolName = ThreadPoolConstant.DEFAULT_POOL_NAME;

    /** 线程池相关参数设置 */
    private ThreadPoolConfig config;

    public DefaultThreadPoolFactory(String name){
        if(name == null || "".equals(name)){
            throw new ThreadPoolNameNullException("ThreadPool name can not be null!");
        }
        this.poolName = name;
    }

    public DefaultThreadPoolFactory(ThreadPoolConfig set){
        if(set == null || set.getPoolName() == null || "".equals(set.getPoolName())){
            throw new ThreadPoolNameNullException("ThreadPool name or config can not be null!");
        }
        this.poolName = set.getPoolName();
        this.config = set;
    }

    @Override
    public ThreadPoolExecutor createBasicThreadPoolInstance() {

        int coreSize = config.getCoreSize() == null ? ThreadPoolConstant.DEFAULT_POOL_CORE_THREAD_NUMS : config.getCoreSize();
        int maxSize = config.getMaxSize() == null ? ThreadPoolConstant.DEFAULT_POOL_MAX_THREAD_NUMS : config.getMaxSize();
        long aliveTime = ThreadPoolConstant.DEFAULT_POOL_THREAD_KEEP_ALIVE_TIME;
        TimeUnit timeUnit = ThreadPoolConstant.DEFAULT_POOL_THREAD_KEEP_ALIVE_TIME_UNIT;
        //如果有一个为空都使用默认的空闲时间
        if( config.getAliveTime() != null && config.getTimeType() != null ){
            aliveTime = config.getAliveTime();
            timeUnit = getTimeUnitByTimeType(config.getTimeType());
        }
        //如果未设置queueType则默认Linked，如果未设置size则取默认大小
        BlockingQueue queue = getDequeByType(config.getQueueType() == null ? 0 : config.getQueueType() , config.getQueueSize() == null ? ThreadPoolConstant.DEFAULT_POOL_THREAD_QUEUE_SIZE : config.getQueueSize());
        ThreadFactory factory = new DefaultThreadFactory(this.poolName);
        if(config.getThreadFactory() != null){
            //TODO
        }
        //还需处理拒绝策略

        //创建自定义的默认线程池
        DefaultThreadPoolExecutor defaultPool = new DefaultThreadPoolExecutor(
                coreSize ,
                maxSize ,
                aliveTime,
                timeUnit,
                queue,
                factory );
        defaultPool.setName(this.poolName);
        return defaultPool;
    }



    public String getPoolName() {
        return poolName;
    }

    public void setPoolName(String poolName) {
        this.poolName = poolName;
    }

    public ThreadPoolConfig getConfig() {
        return config;
    }

    public void setConfig(ThreadPoolConfig config) {
        this.config = config;
    }

    /**
     * 解析time-type设置，这段代码有点垃圾，封装成一个方法放最下面
     * @param type
     * @return TimeUnit
     * */
    public TimeUnit getTimeUnitByTimeType(int type){
        TimeUnit timeUnit = null;
        if(type == -2){
            //纳秒，十亿分之一秒
            timeUnit = TimeUnit.NANOSECONDS;
        }else if( type == -1 ){
            //微秒，百万分支一秒
            timeUnit = TimeUnit.MICROSECONDS;
        }else if( type == 0){
            //毫秒，千分之一秒
            timeUnit = TimeUnit.MILLISECONDS;
        }else if( type == 1){
            timeUnit = TimeUnit.SECONDS;
        }else if( type == 2 ){
            timeUnit = TimeUnit.MINUTES;
        }else if( type == 3){
            timeUnit = TimeUnit.HOURS;
        }else if( type == 4){
            timeUnit = TimeUnit.DAYS;
        }else{
            throw new ThreadPoolConfigException("ThreadPool time-type is not allow");
        }
        return timeUnit;
    }

    /**
     * 解析deque-type和deque-size参数
     * @param type
     * @param size
     * @return BlockingDeque
     * */
    public BlockingQueue getDequeByType(int type , int size){
        if( type == 0){
            return new LinkedBlockingDeque(size);
        }else if( type == 1){
            return new ArrayBlockingQueue<>(size);
        }else{
            throw new ThreadPoolConfigException("ThreadPool queue-type is not allow");
        }
    }
}
