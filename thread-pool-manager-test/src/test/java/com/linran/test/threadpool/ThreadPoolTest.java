package com.linran.test.threadpool;

import com.linran.threadpool.constant.ThreadPoolAddType;
import com.linran.threadpool.factory.pool.DefaultThreadPoolFactory;
import com.linran.threadpool.factory.pool.ThreadPoolFactory;
import com.linran.threadpool.handler.ThreadPoolHolder;
import com.linran.threadpool.interceptor.DefaultThreadPoolInterceptor;
import com.linran.threadpool.task.AbstractRunnable;

import com.linran.threadpool.util.ThreadPoolManager;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import javax.annotation.Resource;
import java.util.Set;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.locks.ReentrantLock;

/**
 * @Author LinRan
 * @Date 2022/6/6
 */
@SpringBootTest(classes = ThreadPoolManagerApplication.class)
@RunWith(SpringRunner.class)
public class ThreadPoolTest {

    @Resource
    ThreadPoolManager threadPoolManager;

    @Test
    public void test1(){

        threadPoolManager.addPoolInstance("order-pool" , new DefaultThreadPoolFactory("order-pool"));
        threadPoolManager.addPoolInstance("order-pool" , new DefaultThreadPoolFactory("order-pool").createBasicThreadPoolInstance());
        threadPoolManager.addPoolInstance("order-pool" , new DefaultThreadPoolFactory("order-pool").createBasicThreadPoolInstance() , ThreadPoolAddType.ABANDON_IF_EXIST);

//        AbstractRunnable runnable = new AbstractRunnable() {
//            @Override
//            public void before() {
//                //do something before
//            }
//
//            @Override
//            public void after() {
//                //do something after
//            }
//
//            @Override
//            public void task() {
//                //do job,just like run()
//            }
//        };
//        long runnableNum = threadPoolManager.execute(runnable);

        threadPoolManager.addPoolInterceptor(new DefaultThreadPoolInterceptor());
        threadPoolManager.addPoolInterceptor("pool-name",new DefaultThreadPoolInterceptor());
        for(int i = 0 ; i < 10 ; i++){
            AbstractRunnable runnable = new AbstractRunnable() {
                /**
                 * 线程池执行任务前置方法
                 */
                @Override
                public void before() {
                    System.out.println(this.getTaskNums() + "-执行前...");
                }

                /**
                 * 线程池执行任务后置方法
                 */
                @Override
                public void after() {
                    System.out.println(this.getTaskNums() + "-执行后...");
                }

                /**
                 * 实际线程需执行的任务方法，等同于Runnable的run方法
                 */
                @Override
                public void task() {
                    System.out.println(this.getTaskNums() + "-开始执行任务.....");
                    try {
                        Thread.sleep(2000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    System.out.println(this.getTaskNums() + "-任务结束.....");
                }
            };

            threadPoolManager.execute(runnable);
        }

        Set<String> nameSet = threadPoolManager.getThreadPoolNameSet();
        System.out.println(nameSet);

    }

}
