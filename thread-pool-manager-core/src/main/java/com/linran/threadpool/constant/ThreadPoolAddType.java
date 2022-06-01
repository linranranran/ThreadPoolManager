package com.linran.threadpool.constant;

/**
 * @Author LinRan
 * @Date 2022/5/24
 */
public enum ThreadPoolAddType {

    ABANDON_IF_EXIST(0),
    REPLACE_AND_SHUTDOWN_IF_EXIST(1),
    REPLACE_AND_SHUTDOWN_NOW_IF_EXIST(2)
    ;

    private int type;

    ThreadPoolAddType(int type) {
        this.type = type;
    }

}
