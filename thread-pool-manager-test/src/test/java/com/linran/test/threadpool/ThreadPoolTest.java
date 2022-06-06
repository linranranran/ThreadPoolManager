package com.linran.test.threadpool;

import com.linran.threadpool.task.AbstractRunnable;

import com.linran.threadpool.util.ThreadPoolManager;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import javax.annotation.Resource;

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

    }

}
