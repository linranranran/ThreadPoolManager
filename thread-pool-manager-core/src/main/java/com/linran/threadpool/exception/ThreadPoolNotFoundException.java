package com.linran.threadpool.exception;

/**
 * 找不到对应的线程池异常
 *
 * @Author LinRan
 * @Date 2022/5/24
 */
public class ThreadPoolNotFoundException extends RuntimeException{

    private static final long serialVersionUID = -3585188024653897848L;

    /**
     * Constructs a NoSuchElementException with null as its error message string.
     * */
    public ThreadPoolNotFoundException(){
        super();
    }

    /**
     * Constructs a NoSuchElementException, saving a reference to the error message string s for later retrieval by the getMessage method.
     * @param : the detail message.
     * */
    public ThreadPoolNotFoundException(String msg){
        super(msg);
    }
}
