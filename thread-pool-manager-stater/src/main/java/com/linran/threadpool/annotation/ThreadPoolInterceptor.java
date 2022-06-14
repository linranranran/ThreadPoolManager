package com.linran.threadpool.annotation;

import java.lang.annotation.*;

/**
 * @Author LinRan
 * @Date 2022/6/13
 */
@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.TYPE})
public @interface ThreadPoolInterceptor {

    /** 需要拦截的线程池名称，默认拦截所有的 */
    String poolName() default "";

}
