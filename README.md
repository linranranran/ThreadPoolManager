
# ThreadPoolManager

## 介绍
日常开发中，常常需要使用线程池来执行异步任务。随着业务复杂程度的提高，一个线程池已经不能满足需要。因此往往需要一个功能或者一个需求单独使用一个线程池处理异步任务，因而多个线程池的管理和大量Runnable的分模块执行、日志记录变得困难，而**ThreadPoolManager**就是用于快捷、简单地管理N个线程池。
**“ThreadPoolManager”** 是一个可灵活可配置的开源线程池管理工具，主要用于线程池的创建、集中管理、日志收集处理，并提供统一的异步任务可扩展点，支持和SpringBoot集成，实现项目启动时创建好对应的线程池实例，通过Spring自动注入核心类即可快捷使用线程池。

### 核心功能

- 可自动创建线程池并集中管理
  &emsp;核心类ThreadPoolManager代表着一个“管理者”，可管辖多个线程池，可调用相关方法添加线程池实例、执行Runnable任务等等。

- 线程池名称
  &emsp;线程池名称可单独设置，以线程池名称为key，线程池实例为value进行保存。以便根据线程池名称将任务交给对应的线程池实例执行。

- 线程编号
  &emsp;通过线程工厂类实现对线程Thread的编号，便于日志的打印、记录。

- 任务编号
  &emsp;通过AtomicLong工具类实现对Runnable任务的编号，便于日志的打印、记录。

- 与SpringBoot集成
  &emsp;在与SpringBoot集成后可根据配置文件自动创建对应的线程池实例，并会自动注入“管理者”到Spring容器中以供使用。不用再考虑线程池的7个参数，直接一键搞定。

## 设计

- 模块结构

  该依赖使用JDK1.8开发。

  thread-pool-manager（线程池管理父工程）  
  ├── thread-pool-manager-core（线程池核心工程）  
  │ ├── config（线程池配置类）  
  │ ├── constant（常量默认值）  
  │ ├── exception（相关异常类）  
  │ ├── factory（工厂类相关）  
  │ │	├── pool（线程池实例工厂类）  
  │ │	├── thread（线程工厂类）  
  │ ├── handler（线程池核心处理类）  
  │ ├── interceptor（拦截器）  
  │ ├── task（任务包装类）  
  │ └── util（线程池核心工具类）  
  ├── thread-pool-manager-stater（线程池管理与springboot集成的starter）  
  ├── thread-pool-manager-test（线程池管理测试模块）  
  └── README.md（文档）

  thread-pool-manager：线程池管理父工程，管理平台的依赖、构建、插件等；  
  thread-pool-manager-core：线程池管理核心工程，提供所有的核心代码，可手动调用，也可直接通过stater与spring集成使用；  
  thread-pool-manager-stater：与springboot集成的starter；
  thread-pool-manager-test：测试模块，包括用例测试等；


## 下载

- 源码仓库地址

  [https://gitee.com/linranran/thread-pool-manager](https://gitee.com/linranran/thread-pool-manager)

- 中央仓库地址

1. 目前暂未把项目上传至中央仓库

2. 客户端为springboot程序

   将项目clone到本地后，使用maven命令将项目package至本地仓库，即可引入依赖。
```xml
<!-- 需将项目打包至本地仓库才可以引用 -->
<dependency>  
 <groupId>com.linran</groupId>  
 <artifactId>thread-pool-manager-stater</artifactId>  
 <version>1.0-SNAPSHOT</version>  
</dependency>
```

- 最新版本

  1.0-SNAPSHOT

## 使用

### 初始化“线程池”

一、如果是SpringBoot环境，引入自动注入包后，可直接通过配置文件申明线程池：

1. yml配置文件示例：
   threadPoolManager:（前缀）  
   ├─ default-pool-name:（默认线程池名称，默认线程池有且仅有一个）  
   ├─ global-set:（全局线程池属性设置）  
   ├─ pools:（申明其它线程池）
   │ ├─ default:（申明默认线程池参数）  
   │ │	├─ main-size（核心线程数，注意核心线程数不能大于最大线程数）  
   │ │	├─ max-size（最大线程数）  
   │ │	├─ alive-time（线程空闲时间）  
   │ │	├─ time-util（时间单位，-2纳秒 -1微秒 0毫秒 1秒 2分钟 3小时 4天）  
   │ │	├─ queue-type（阻塞队列类型，0LinkedDeque,1ArrayQueue）  
   │ │	├─ queue-size（阻塞队列长度）  
   │ │	├─ thread-factory（线程工厂，开发中暂不支持配置）  
   │ │	├─ reject-handler（拒绝策略，开发中暂不支持配置）  
   │ ├─ xxxx:（申明“xxxx”线程池）    
   │ │	├─ ....  
   │ │	├─ ....  
   │ ├─ aaa,bbb（申明"aaa"和"bbb"两个线程池）    
   │ │	├─ ....  
   │ │	├─ ....  

2. 注入核心线程池管理类即可使用
 ``` java
	@Resource  
	ThreadPoolManager threadPoolManager;
```

二、如果是其它环境，可直接通过申明核心类和线程池工厂实现：
1. 创建核心类
``` java
	//申明Holder类，用于统一存放线程池。
	ThreadPoolHolder holder = new ThreadPoolHolder();  
	//申明线程池管理类，用于调用核心方法。
```
### 拦截器
1.	介绍：
      拦截器可以提供线程池在执行异步任务的前置、后置处理器的接口，便于在执行任务前、后进行日志记录等自定义操作。仅需实现ThreadPoolInterceptor接口即可。
2.	使用拦截器
      如果是在SpringBoot环境中，实现ThreadPoolInterceptor接口，并加上**@PoolInterceptor注解**就会在项目启动后**自动扫描注入生效**。
      申明一个拦截器有两种方式，实现ThreadPoolInterceptor接口并创建实例手动添加ThreadPoolManager中即可。添加方式请见下方Manager相关方法。



### Manager相关方法

1. 手动添加线程池
   推荐使用DefaultThreadPoolFactory来创建线程池，也可以自定义线程池工厂类。
``` java
//使用线程池工厂添加
threadPoolManager.addPoolInstance("order-pool" , new DefaultThreadPoolFactory("order-pool"));  
//将已有的线程池实例手动添加
threadPoolManager.addPoolInstance("order-pool" , new DefaultThreadPoolFactory("order-pool").createBasicThreadPoolInstance());  
//手动添加并指定添加方式，模式方式是“如果存在则抛弃”
threadPoolManager.addPoolInstance("order-pool" , new DefaultThreadPoolFactory("order-pool").createBasicThreadPoolInstance() , ThreadPoolAddType.ABANDON_IF_EXIST);
```


2. 执行任务
   推荐使用AbstractRunnable方法，可以执行自定义前置后置方法。excute方法可返回任务编号，可根据该任务编号记录日志、查询执行记录等等
``` java
AbstractRunnable runnable = new AbstractRunnable() {  
    @Override  
  public void before() {  
        //do something before  
  }  
  
    @Override  
  public void after() {  
        //do something after  
  }  
  
    @Override  
  public void task() {  
        //do job,just like run()  
  }  
};  
//返回任务编号，可根据该任务编号记录日志、查询执行记录等等
long runnableNum = threadPoolManager.execute(runnable);
```

3. 手动添加拦截器
``` java
//为所有线程池添加拦截器
threadPoolManager.addPoolInterceptor(new DefaultThreadPoolInterceptor());  
//为指定线程池添加拦截器
threadPoolManager.addPoolInterceptor("pool-name",new DefaultThreadPoolInterceptor());
```

## 期望


更多的功能正在开发中，欢迎提出更好的意见，帮助完善ThreadPoolManager

## 联系
email：386126949@qq.com
wechat：linrantop
