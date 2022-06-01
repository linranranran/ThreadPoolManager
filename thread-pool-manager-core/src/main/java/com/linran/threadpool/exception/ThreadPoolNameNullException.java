package com.linran.threadpool.exception;

/**
 * 线程池名空异常
 *
 * @Author LinRan
 * @Date 2022/5/24
 */
public class ThreadPoolNameNullException extends RuntimeException{


    private static final long serialVersionUID = -7829105741602286528L;

    /**
     * Constructs a NoSuchElementException with null as its error message string.
     * */
    public ThreadPoolNameNullException(){
        super();
    }

    /**
     * Constructs a NoSuchElementException, saving a reference to the error message string s for later retrieval by the getMessage method.
     * @param : the detail message.
     * */
    public ThreadPoolNameNullException(String msg){
        super(msg);
    }
}
