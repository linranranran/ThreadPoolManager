package com.linran.threadpool.log;

import cn.hutool.core.util.StrUtil;

import java.lang.ref.SoftReference;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @Author LinRan
 * @Date 2022/5/26
 */
public class SoftReferenceTaskLog {

    /**
     * 使用软引用保存日志在内存当中，当GC空间不足时会将其清理
     * */
    private Map< String , List<SoftReference<Object>>> log = new HashMap<>();

    public void push(String poolName , Object obj){
        if(StrUtil.isEmpty(poolName) || obj == null){
            return;
        }
        if( !log.containsKey(poolName) ){
            SoftReference<Object> soft = new SoftReference<Object>(obj);
            List<SoftReference<Object>> list = new ArrayList<>();
            list.add(soft);
            log.put(poolName , list);
        }else{
            log.get(poolName).add(new SoftReference<Object>(obj));
        }
    }

    public List<SoftReference<Object>> get(String poolName){
        return log.getOrDefault(poolName , null);
    }

}
