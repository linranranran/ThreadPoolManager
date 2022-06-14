package com.linran.threadpool.config;

import cn.hutool.core.util.StrUtil;
import com.linran.threadpool.annotation.PoolInterceptor;
import com.linran.threadpool.constant.ThreadPoolConstant;
import com.linran.threadpool.factory.pool.DefaultThreadPoolFactory;
import com.linran.threadpool.factory.pool.ThreadPoolFactory;
import com.linran.threadpool.handler.ThreadPoolHolder;
import com.linran.threadpool.interceptor.ThreadPoolInterceptor;
import com.linran.threadpool.properties.ThreadPoolManagerProperties;
import com.linran.threadpool.util.ThreadPoolManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.BeanFactoryAware;
import org.springframework.beans.factory.config.BeanDefinitionHolder;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.boot.autoconfigure.AutoConfigurationPackages;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.*;
import org.springframework.core.type.AnnotationMetadata;
import org.springframework.core.type.filter.AnnotationTypeFilter;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

/**
 * @Author LinRan
 * @Date 2022/6/1
 */
@Configuration
@Import({ThreadPoolManagerAutoConfig.AutoConfiguredInterceptorScannerRegistrar.class})
@EnableConfigurationProperties(ThreadPoolManagerProperties.class)
public class ThreadPoolManagerAutoConfig implements BeanPostProcessor {

    private static final Logger log = LoggerFactory.getLogger(ThreadPoolManagerAutoConfig.class);

    /**
     * 扫描拦截器链
     * */
    private Map<String , List<ThreadPoolInterceptor>> interceptsRegister = new ConcurrentHashMap<>();

    @Bean
    public ThreadPoolManager getThreadPoolManager(ThreadPoolManagerProperties properties){
        //start create default pool
        String defaultName = properties.getDefaultPoolName();
        ThreadPoolConfig globalSet = properties.getGlobalSet();
        Map<String, ThreadPoolConfig> pools = properties.getPools();
        //避免只设置了默认线程池名称而没设置全局设置而报错
        if(globalSet == null){
            globalSet = new ThreadPoolConfig();
        }
        if(!StrUtil.isEmpty(defaultName)){
            globalSet.setPoolName(defaultName);
        }else{
            defaultName = ThreadPoolConstant.DEFAULT_POOL_NAME;
        }
        ThreadPoolHolder holder = null;
        //不初始化默认线程池，解析完配置文件后统一进行加载线程池
        holder = new ThreadPoolHolder(globalSet , interceptsRegister , false);
        //如果没有配置默认线程池
        if( !pools.containsKey( defaultName )){
            pools.put(defaultName , globalSet);
        }
        List<ThreadPoolFactory> factories = resolvePools(pools, defaultName);
        List<String> nameList = factories.stream().map(f -> f.getPoolName()).collect(Collectors.toList());
        //如果没有解析出工厂或者List中没有默认线程池，则补充上默认线程池。兜个低，至少有一个默认线程池可用。
        if(factories == null || factories.size() == 0 || !nameList.contains(defaultName) ){
            holder.addThreadPool(new DefaultThreadPoolFactory(globalSet));
        }
        for(ThreadPoolFactory factory : factories){
            holder.addThreadPool(factory);
        }
        ThreadPoolManager manager = new ThreadPoolManager(holder);
        return manager;
    }

//    @Bean
//    public AutoConfiguredInterceptorScannerRegistrar getScannerRegistrar(BeanDefinitionRegistry registry){
//
//    }

    @Override
    public Object postProcessAfterInitialization(Object bean, String beanName) throws BeansException {
        //拦截扫描所有的拦截器，添加到线程池实例当中
        if(bean.getClass().isAnnotationPresent( PoolInterceptor.class )){
            if(bean instanceof ThreadPoolInterceptor){
                if(interceptsRegister.containsKey(ThreadPoolConstant.DEFAULT_THREAD_INTERCEPTOR_ALL)){
                    List<ThreadPoolInterceptor> list = interceptsRegister.get(ThreadPoolConstant.DEFAULT_THREAD_INTERCEPTOR_ALL);
                    list.add((ThreadPoolInterceptor)bean);
                }else{
                    List<ThreadPoolInterceptor> list = new LinkedList<>();
                    list.add((ThreadPoolInterceptor)bean );
                    interceptsRegister.put(ThreadPoolConstant.DEFAULT_THREAD_INTERCEPTOR_ALL , list);
                }
            }
        }
        return BeanPostProcessor.super.postProcessAfterInitialization(bean, beanName);
    }

    /**
     *  解析配置返回线程池工厂
     * @param   pools
     * @param   defaultPoolName 默认线程池名称，用于解析pools.default
     * @return  线程池工厂集合
     * */
    public List<ThreadPoolFactory> resolvePools(Map<String , ThreadPoolConfig> pools , String defaultPoolName){
        List<ThreadPoolFactory> list = new ArrayList<>();
        if( pools == null || pools.size() == 0){
            return list;
        }
        Set<String> keySet = pools.keySet();
        //用于解决同时设置了default和“defaultPoolName”线程池的情况
        boolean flag = false;
        for(String name : keySet){
            //如果设置线程池名称是default或者是“defaultPoolName”则都看作是对默认线程池进行设置，只会创建一个默认线程池。
            if( ThreadPoolConstant.DEFAULT_POOL_NAME.equals(name) || defaultPoolName.equals(name)){
                if( flag ){
                    continue;
                }
                //只有一个默认线程池
                pools.get(name).setPoolName( defaultPoolName );
                list.add(new DefaultThreadPoolFactory( pools.get(name) ));

                flag = true;
            }else{
                pools.get(name).setPoolName( name );
                list.add(new DefaultThreadPoolFactory( pools.get(name) ));
            }
        }
        return list;
    }

    public static class AutoConfiguredInterceptorScannerRegistrar implements BeanFactoryAware,ImportBeanDefinitionRegistrar {

        private BeanFactory beanFactory;

        public AutoConfiguredInterceptorScannerRegistrar(){

        }

        public void registerBeanDefinitions(AnnotationMetadata importingClassMetadata, BeanDefinitionRegistry registry) {
            if (!AutoConfigurationPackages.has(this.beanFactory)) {
                log.debug("Could not determine auto-configuration package, automatic mapper scanning disabled.");
            } else {
                log.debug("Searching for mappers annotated with @ThreadPoolInterceptor");
                List<String> packages = AutoConfigurationPackages.get(this.beanFactory);
                if (log.isDebugEnabled()) {
                    packages.forEach((pkg) -> {
                        log.debug("Using auto-configuration base package '{}'", pkg);
                    });
                }
                //使用AutoConfiguredInterceptorScanner扫描类扫描@ThreadPoolInterceptor注解类
                AutoConfiguredInterceptorScanner scanner = new AutoConfiguredInterceptorScanner(registry);
                scanner.registerFilters();
                for(String str : packages){
                    scanner.doScan(str);
                }
            }
        }

        public void setBeanFactory(BeanFactory beanFactory) {
            this.beanFactory = beanFactory;
        }
    }


    public static class AutoConfiguredInterceptorScanner extends ClassPathBeanDefinitionScanner{

        public AutoConfiguredInterceptorScanner(BeanDefinitionRegistry registry) {
            super(registry ,false);
        }

        @Override
        public Set<BeanDefinitionHolder> doScan(String... basePackages) {
            Set<BeanDefinitionHolder> definitionHolders = super.doScan(basePackages );
            return definitionHolders;
        }

        /** 借鉴了Mybatis中的ClassPathMapperScanner类 */
        public void registerFilters() {
            boolean acceptAllInterfaces = true;
            // if specified, use the given annotation and / or marker interface
            addIncludeFilter(new AnnotationTypeFilter(PoolInterceptor.class));
            acceptAllInterfaces = false;
            if (acceptAllInterfaces) {
                // default include filter that accepts all classes
                addIncludeFilter((metadataReader, metadataReaderFactory) -> true);
            }
            // exclude package-info.java
            addExcludeFilter((metadataReader, metadataReaderFactory) -> {
                String className = metadataReader.getClassMetadata().getClassName();
                return className.endsWith("package-info");
            });
        }
    }
}
