package com.linran.threadpool.handler;

import cn.hutool.core.util.StrUtil;
import com.linran.threadpool.config.ThreadPoolConfig;
import com.linran.threadpool.constant.ThreadPoolAddType;
import com.linran.threadpool.constant.ThreadPoolConstant;
import com.linran.threadpool.exception.ThreadPoolNameNullException;
import com.linran.threadpool.exception.ThreadPoolNotFoundException;
import com.linran.threadpool.factory.pool.AbstractThreadPoolExecutor;
import com.linran.threadpool.factory.pool.DefaultThreadPoolFactory;
import com.linran.threadpool.factory.pool.ThreadPoolFactory;
import com.linran.threadpool.factory.pool.ThreadPoolParameter;
import com.linran.threadpool.interceptor.ThreadPoolInterceptor;
import com.linran.threadpool.task.AbstractRunnable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.atomic.AtomicLong;

/**
 *  记录任务编号，在记录任务编号这个功能上有两种方式：
 *
 *  1、把Atomic放在AbstractThreadPoolExecutor对象中
 *  2、把Atomic放在Holder的Map中
 *
 *  第一种方式封装性更强，但是所有放入线程池都必须继承AbstractThreadPoolExecutor。
 *  第二中方式可用性更强，用户但凡有个线程池都能放入Holder进行管理。
 *
 *  权衡之下，目标是想实现一个能和Spring集成的通用的线程池管理工具，因此两种方式都选择使用，如果实现了AbstractThreadPoolExecutor则优先使用pool对象中的计数器，否则使用Map中的计数器。
 *  请大佬予以批评指正:
 *  wechat: linrantop
 *  mail:   386126949@qq.com
 *
 *  @author: LinRan
 *  @create: 2022-05-21 23:29
 **/
public class ThreadPoolHolder {

    private static final Logger log = LoggerFactory.getLogger(ThreadPoolHolder.class);

    /** 保存线程池实例 */
    private Map<String , ThreadPoolExecutor> threadPoolMap = null;

    /** 全局设置，如果找不到对应的设置则取ThreadPoolConstant中的默认值 */
    private ThreadPoolConfig globalSet = null;

    /** 全局默认线程池名称，如有设置则优先使用 */
    private String defaultPoolName = ThreadPoolConstant.DEFAULT_POOL_NAME;

    /** 拦截器链 */
    private Map<String , List<ThreadPoolInterceptor>> intercepts = null;

    /** 工作模式，false:全默认工作模式，true:自定义全局设置模式 */
    private boolean workMode = false;

    public ThreadPoolHolder(){
        //统一使用init初始化方法
        init(null , null ,true);
    }

    /**
     * 传入自定义全局设置Map，并会修改工作模式为ture，会优先获取自定义设置。
     * */
    public ThreadPoolHolder(ThreadPoolConfig globalSet){
        init(globalSet ,null ,true);
    }

    /**
     * 传入自定义全局设置Map，并会修改工作模式为ture，会优先获取自定义设置。
     * */
    public ThreadPoolHolder(ThreadPoolConfig globalSet , Map<String , List<ThreadPoolInterceptor>> intercepts){
        init(globalSet ,intercepts ,true);
    }


    /**
     * 传入自定义全局设置Map，并会修改工作模式为ture，会优先获取自定义设置。
     * @param globalSet 全局设置
     * @param isCreateDefault 是否初始化默认线程池,true表示默认创建，false表示不需要
     * */
    public ThreadPoolHolder(ThreadPoolConfig globalSet , Map<String , List<ThreadPoolInterceptor>> intercepts , boolean isCreateDefault){
        init(globalSet ,intercepts , isCreateDefault);
    }

    /**
     * 初始化方法
     * @param   globalSet
     * @param   isCreateDefault 是否初始化默认子流程，用于stater批量创建线程池
     * */
    public void init(ThreadPoolConfig globalSet , Map<String , List<ThreadPoolInterceptor>> intercepts , boolean isCreateDefault){
        this.intercepts = intercepts;
        this.globalSet = globalSet;
        workMode = true;
        threadPoolMap = new ConcurrentHashMap<>();
        String defaultName = ThreadPoolConstant.DEFAULT_POOL_NAME;
        String setDefaultName = globalSet.getPoolName();
        if( setDefaultName != null && !"".equals(setDefaultName)){
            defaultName = setDefaultName;
        }
        //defaultName最起码也是一个默认值
        this.defaultPoolName = defaultName;
        if(isCreateDefault){
            ThreadPoolFactory factory = new DefaultThreadPoolFactory(this.defaultPoolName);
            addThreadPool( defaultName , factory);
        }
    }

    /**
     *  将线程池放入Map中，默认以如果name已存在则抛弃实例的方式添加
     *  factory中poolName参数不能为空
     * @param   factory     线程池工厂
     * */
    public void addThreadPool(ThreadPoolFactory factory){
        if(factory == null || StrUtil.isEmpty(factory.getPoolName())){
            throw new ThreadPoolNameNullException();
        }
        addThreadPool(factory.getPoolName() , factory.createBasicThreadPoolInstance() , ThreadPoolAddType.ABANDON_IF_EXIST);
    }

    /**
     *  将线程池放入Map中，默认以如果name已存在则抛弃实例的方式添加
     * @param poolName  线程池名称
     * @param factory   线程池工厂
     * */
    public void addThreadPool(String poolName , ThreadPoolFactory factory){
        if(StrUtil.isEmpty(poolName)){
            throw new ThreadPoolNameNullException();
        }
        addThreadPool(poolName , factory.createBasicThreadPoolInstance() , ThreadPoolAddType.ABANDON_IF_EXIST);
    }

    /**
     *  将线程池放入Map中，默认以如果name已存在则抛弃实例的方式添加
     * @param poolName  线程池名称
     * @param pool      线程池实例
     * */
    public void addThreadPool(String poolName , ThreadPoolExecutor pool){
        if(StrUtil.isEmpty(poolName)){
            throw new ThreadPoolNameNullException();
        }
        addThreadPool(poolName , pool , ThreadPoolAddType.ABANDON_IF_EXIST);
    }

    /**
     *  将线程池放入Map中
     * @param poolName  线程池名称
     * @param pool      线程池实例
     * @param type      添加方式
     * */
    public void addThreadPool(String poolName , ThreadPoolExecutor pool , ThreadPoolAddType type){
        switch (type){
            case ABANDON_IF_EXIST:
                if(!threadPoolMap.containsKey(poolName)){
                    doAddThreadPoolToMap(poolName , pool);
                }
                break;
            case REPLACE_AND_SHUTDOWN_IF_EXIST:
                removeAndShutDownThreadPool(poolName);
                doAddThreadPoolToMap(poolName , pool);
                break;
            case REPLACE_AND_SHUTDOWN_NOW_IF_EXIST:
                removeAndShutDownNowThreadPool(poolName);
                doAddThreadPoolToMap(poolName , pool);
                break;
        }
    }

    /**
     * poolName不能为空，将其放入map中
     * @param poolName
     * @param pool
     * */
    private void doAddThreadPoolToMap(String poolName , ThreadPoolExecutor pool){
        if(StrUtil.isEmpty(poolName)){
            throw new ThreadPoolNameNullException();
        }
        threadPoolMap.put(poolName , pool);
        resetThreadPoolTaskNums(poolName);
        if(pool instanceof AbstractThreadPoolExecutor){
            AbstractThreadPoolExecutor executor = (AbstractThreadPoolExecutor)pool;
            executor.setInterceptors(this.intercepts);
        }
    }

    /**
     * 移除线程池，并shutdown
     * @param poolName
     * */
    public void removeAndShutDownThreadPool(String poolName){
        if(threadPoolMap.containsKey(poolName) && threadPoolMap.get(poolName) != null){
            threadPoolMap.get(poolName).shutdown();
            threadPoolMap.remove(poolName);
        }
        resetThreadPoolTaskNums(poolName);
    }

    /**
     * 移除线程池，并shutdownNow
     * @param poolName
     * */
    public void removeAndShutDownNowThreadPool(String poolName){
        if(threadPoolMap.containsKey(poolName) && threadPoolMap.get(poolName) != null){
            threadPoolMap.get(poolName).shutdownNow();
            threadPoolMap.remove(poolName);
        }
        resetThreadPoolTaskNums(poolName);
    }

    /**
     * 重置任务编号
     * @param poolName
     * */
    public void resetThreadPoolTaskNums(String poolName){
        if(threadPoolMap.containsKey(poolName) && threadPoolMap.get(poolName) != null){
            ThreadPoolExecutor executor = threadPoolMap.get(poolName);
            if(executor instanceof AbstractThreadPoolExecutor){
                AbstractThreadPoolExecutor poolExecutor = (AbstractThreadPoolExecutor)executor;
                poolExecutor.resetTaskNums();
            }else{

            }
        }
    }

    /**
     * 添加任务
     * @param runnable 任务
     * @return 任务编号
     * */
    public long executeTask(Runnable runnable){
        return executeTask(this.defaultPoolName , runnable);
    }

    /**
     * 添加任务至指定线程池，如果poolName为空则抛出ThreadPoolNameNullException异常
     * @param poolName  线程池名称
     * @param runnable  任务
     * @return 任务编号
     * */
    public long executeTask(String poolName , Runnable runnable){
        if( poolName == null || StrUtil.isEmpty(poolName )){
            throw new ThreadPoolNameNullException("ThreadPoolName can not be null!");
        }
        if(threadPoolMap == null || !threadPoolMap.containsKey(poolName) ){
            throw new ThreadPoolNotFoundException("ThreadPool not found!");
        }
        threadPoolMap.get(poolName).execute(runnable);
//        return taskNums.get(poolName).getAndIncrement();
        long taskNums = ((AbstractThreadPoolExecutor) threadPoolMap.get(poolName)).getTaskNums().getAndIncrement();
        //记录任务编号至Runnable对象中，如果返回false表示非AbstractRunnable类型，记录日志
        if( !recordTaskNums(taskNums , runnable) ){
            log.info(poolName + "-" + taskNums + "is not AbstractRunnable, can not record task nums.");
        }
        return taskNums;
    }

    /**
     *  为AbstractRunnable类型记录任务编号到对象中
     *  @return false表示参数runnable对象不是AbstractRunnable类型，true表示记录成功
     * */
    public boolean recordTaskNums(long taskNums ,Runnable runnable){
        if(runnable instanceof AbstractRunnable){
            AbstractRunnable abstractRunnable = (AbstractRunnable)runnable;
            abstractRunnable.setTaskNums(taskNums);
            return true;
        }else{
            return false;
        }
    }

    /**
     *  获取所有的线程池名称
     *  @return set
     * */
    public Set<String> getThreadPoolNameSet(){
        if(threadPoolMap == null || threadPoolMap.size() == 0){
            throw new ThreadPoolNotFoundException("No ThreadPool !");
        }
        return threadPoolMap.keySet();
    }

    /**
     *  获取线程池的相关信息
     *  如果poolName字符串空或者找不到对应的线程池则返回null
     *  @param poolName
     *  @return ThreadPoolParameter
     * */
    public ThreadPoolParameter getThreadPoolParameter(String poolName){
        if(StrUtil.isEmpty(poolName) || !threadPoolMap.containsKey(poolName)){
            return null;
        }
        return getThreadPoolParameter(threadPoolMap.get(poolName));
    }

    /**
     *  获取线程池的相关信息
     *  如果pool空则返回null,
     *  @param pool
     *  @param taskNums
     *  @return ThreadPoolParameter
     * */
    public ThreadPoolParameter getThreadPoolParameter(ThreadPoolExecutor pool , AtomicLong taskNums){
        ThreadPoolParameter parameter = getThreadPoolParameter(pool);
        parameter.setCurrentTaskNums( taskNums != null ? taskNums.get() : 0);
        return parameter;
    }

    /**
     *  获取线程池的相关信息
     *  如果pool空则返回null
     *  @param pool
     *  @return ThreadPoolParameter
     * */
    public ThreadPoolParameter getThreadPoolParameter(ThreadPoolExecutor pool){
        if(pool == null){
            return null;
        }
        ThreadPoolParameter parameter = new ThreadPoolParameter();
        //设置相关
        parameter.setCoreThreadSize(pool.getCorePoolSize());
        parameter.setCoreThreadSize(pool.getMaximumPoolSize());
        //实际数量相关
        parameter.setCoreThreadNums(pool.getPoolSize());
        parameter.setTaskCount(pool.getTaskCount());
        //类型相关
        parameter.setPoolType( pool instanceof AbstractThreadPoolExecutor ? 1 : 0);
        parameter.setQueueType( pool.getQueue() instanceof ArrayBlockingQueue ? 1 : 0);
        return parameter;
    }

    /**
     *  关闭线程池
     * @param poolName
     * */
    public void shutDownThreadPool(String poolName){
        log.info(poolName + " shutdown , current active Thread : "+threadPoolMap.get(poolName).getActiveCount()+"");
        threadPoolMap.get(poolName).shutdown();
    }

    /**
     * 初始化线程池添加拦截器
     * @param intercepts
     * */
    public void initPoolInterceptor(Map<String , List<ThreadPoolInterceptor>> intercepts){
        this.intercepts = intercepts;
    }


    /**
     * 默认为所有线程池添加拦截器
     * @param interceptor
     * */
    public void addPoolInterceptor(ThreadPoolInterceptor interceptor){
        if(interceptor == null){
            return;
        }
        List<ThreadPoolInterceptor> list = new ArrayList<>();
        list.add(interceptor);
        addPoolInterceptor(list);
    }

    /**
     * 默认为所有线程池批量添加拦截器
     * @param   intercepts
     * */
    public void addPoolInterceptor(List<ThreadPoolInterceptor> intercepts){
        if(intercepts == null || intercepts.size() == 0){
            return;
        }
        addPoolInterceptor(ThreadPoolConstant.DEFAULT_THREAD_INTERCEPTOR_ALL , intercepts);
    }

    /**
     * 为指定线程池添加拦截器
     * @param   interceptor
     * */
    public void addPoolInterceptor(String poolName , ThreadPoolInterceptor interceptor){
        if(interceptor == null){
            return;
        }
        List<ThreadPoolInterceptor> list = new ArrayList<>();
        list.add(interceptor);
        addPoolInterceptor(poolName , list);
    }

    /**
     * 为指定线程池批量添加拦截器
     * @param   intercepts
     * */
    public void addPoolInterceptor(String poolName , List<ThreadPoolInterceptor> intercepts){
        if(StrUtil.isEmpty(poolName) || intercepts == null || intercepts.size() == 0){
            return;
        }
        Set<String> poolNameSet = getThreadPoolNameSet();
        for(String key : poolNameSet){
            ThreadPoolExecutor executor = threadPoolMap.get(key);
            doAddPoolIntercepts(poolName , executor , intercepts);
        }
    }

    /**
     * 将拦截器添加到线程池实例List中
     *
     * @param   poolName
     * @param   executor
     * @param   intercepts
     * */
    private void doAddPoolIntercepts(String poolName , ThreadPoolExecutor executor , List<ThreadPoolInterceptor> intercepts){
        if(executor == null || intercepts == null){
            return;
        }
        if(executor instanceof AbstractThreadPoolExecutor){
            AbstractThreadPoolExecutor poolExecutor = (AbstractThreadPoolExecutor)executor;
            Map<String , List<ThreadPoolInterceptor>> interceptors = poolExecutor.getInterceptors();
            //如果已经存在key-value则直接addAll
            if( interceptors.containsKey(poolName) ){
                interceptors.get(poolName).addAll(intercepts);
            }else{
                interceptors.put(poolName , intercepts);
            }
        }else{

        }
    }
}

