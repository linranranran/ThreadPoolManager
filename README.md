
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

1. 客户端为普通Java程序

```xml
<!-- https://mvnrepository.com/artifact/com.gitee.pifeng/phoenix-client-core -->
<dependency>
  <groupId>com.gitee.pifeng</groupId>
  <artifactId>phoenix-client-core</artifactId>
  <version>${最新稳定版本}</version>
</dependency>
```

2. 客户端为springboot程序

```xml
<!-- https://mvnrepository.com/artifact/com.gitee.pifeng/phoenix-client-spring-boot-starter -->
<dependency>
  <groupId>com.gitee.pifeng</groupId>
  <artifactId>phoenix-client-spring-boot-starter</artifactId>
  <version>${最新稳定版本}</version>
</dependency>
```

3. 客户端为springmvc程序

```xml
<!-- https://mvnrepository.com/artifact/com.gitee.pifeng/phoenix-client-spring-mvc-integrator -->
<dependency>
  <groupId>com.gitee.pifeng</groupId>
  <artifactId>phoenix-client-spring-mvc-integrator</artifactId>
  <version>${最新稳定版本}</version>
</dependency>
```

- 最新稳定版本

  1.2.2.RELEASE

## 使用

### 初始化“监控数据库”

下载项目源码并解压，进入目录：**/phoenix/doc/数据库设计/sql/mysql** ，找到SQL脚本并执行即可。

```
phoenix.sql
```

### 编译源码

解压源码,按照maven格式将源码导入IDE, 使用maven进行编译即可。

### 配置

#### 监控配置

> 监控配置文件为： **monitoring.properties** ，放在 **classpath:/** 下会自动加载，UI端、服务端、代理端、客户端都需要有这个配置文件。如果是springboot项目也可以分环境配置，示例配置代码如下：

```java
/**
* 开发环境监控配置
*/
@Configuration
@Profile("dev")
@EnableMonitoring(configFileName = "monitoring-dev.properties")
public class MonitoringUiDevConfig {
}

/**
* 生产环境监控配置
*/
@Configuration
@Profile("prod")
@EnableMonitoring(configFileName = "monitoring-prod.properties")
public class MonitoringUiProdConfig {
}
```

> 监控配置项说明：

|配置项                                          |含义                                                                   |必须项       |默认值| 
  |-----------------------------------------------|-----------------------------------------------------------------------|-------------|-----|
|monitoring.server.url                          |监控服务端（代理端）url                                                  |是           |      |
|monitoring.server.connect-timeout              |连接超时时间（毫秒）                                                     |否           |15000 |
|monitoring.server.socket-timeout               |等待数据超时时间（毫秒）                                                  |否           |15000|
|monitoring.server.connection-request-timeout   |从连接池获取连接的等待超时时间（毫秒）                                      |否           |15000|
|monitoring.own.instance.order                  |实例次序（整数），用于在集群中区分应用实例，配置“1”就代表集群中的第一个应用实例  |否           |1     |
|monitoring.own.instance.endpoint               |实例端点类型（server、agent、client、ui）                                 |否           |client|
|monitoring.own.instance.name                   |实例名称，一般为项目名                                                    |是           |      |
|monitoring.own.instance.desc                   |实例描述                                                                |否           |      |
|monitoring.own.instance.language               |程序语言                                                                |否           |Java  |
|monitoring.heartbeat.rate                      |与服务端或者代理端发心跳包的频率（秒），最小不能小于30秒                      |否           |30    |
|monitoring.server-info.enable                  |是否采集服务器信息                                                        |否           |false |
|monitoring.server-info.rate                    |与服务端或者代理端发服务器信息包的频率（秒），最小不能小于30秒                 |否           |60    |
|monitoring.server-info.ip                      |被监控服务器本机ip地址                                                    |否（自动获取） |      |
|monitoring.server-info.user-sigar-enable       |是否使用Sigar采集服务器信息，要求Jdk1.8(1.8.0_131到1.8.0_241)              |否            |true  |
|monitoring.jvm-info.enable                     |是否采集Java虚拟机信息                                                    |否           |false |
|monitoring.jvm-info.rate                       |与服务端或者代理端发送Java虚拟机信息的频率（秒），最小不能小于30秒             |否           |60    |

1. 监控UI端

   除了在 **monitoring-{profile}.properties** 文件修改监控配置外，还需要在 **application-{profile}.yml** 文件修改数据库配置。

2. 监控服务端

   需要在 **application-{profile}.yml** 文件修改数据库配置和邮箱配置。

3. 监控代理端

   只需在 **monitoring-{profile}.properties** 文件修改监控配置。

4. 监控客户端

   只需添加监控配置。

#### 加解密配置

除了监控配置文件外，还可以在 **classpath:/** 下加入 **monitoring-secure.properties** 加解密配置文件，用来修改监控平台的加解密方式。但是注意各监控端加解密配置参数必须相同。这个配置不是必须的，没有此配置文件将使用默认加解密配置，加入此配置文件则必须正确配置配置项。

> 加解密配置项说明：

|配置项                 |含义                                        |必须项                         |默认值| 
  |----------------------|-------------------------------------------|------------------------------|-----|
|secret.type           |加解密类型，值只能是 des、aes、sm4 之一         |否，为空则不进行加解密           |      |
|secret.key.des        |DES密钥                                     |否，secret.type=des时，需要配置     |      |
|secret.key.aes        |AES密钥                                     |否，secret.type=aes时，需要配置     |      |
|secret.key.sm4        |国密SM4密钥                                  |否，secret.type=ms4时，需要配置      |      |

秘钥可通过 **com.gitee.pifeng.monitoring.common.util.secure.SecureUtilsTest#testGenerateKey** 方法生成，然后填入配置文件。

#### 第三方登录认证配置

监控UI端除了支持直接登录认证外，还支持第三方登录认证，只需在application.yml（或者application-{profile}.yml）配置文件中增加对应配置项即可使用。

> 第三方登录认证配置说明：

|配置项                 |含义                                        |必须项                         |默认值| 
  |----------------------|--------------------------------------------|-------------------------------|-----|
|third-auth.enable     |是否开启第三方认证                            |否                             |false |
|third-auth.type       |第三方认证类型（CAS）                         |否                             |      |

> apereo cas登录认证配置说明：

如果 third-auth.enable=true && third-auth.type=cas ，则需要进行cas配置。

|配置项                     |含义                                        |必须项                         |默认值 | 
  |--------------------------|--------------------------------------------|------------------------------|-------|
|cas.key                   |秘钥                                        |否                             |phoenix|
|cas.server-url-prefix     |cas服务端地址                                |是                             |       |
|cas.server-login-url      |cas登录地址                                  |是                             |      |
|cas.server-logout-url     |cas登出地址                                  |是                             |      |
|cas.client-host-url       |cas客户端地址                                |是                             |      |
|cas.validation-type       |CAS协议验证类型（CAS、CAS3）                  |否                             |CAS3  |

### 客户端开启监控

- 普通Java程序

  在 **main** 方法中，调用方法 **Monitor.start()** 来开启监控功能，或者调用重载的方法 **Monitor.start(configPath, configName)** 指定监控配置文件的路径和名字来开启监控功能，如果未指定配置文件路径和名字，则配置文件需要放在 **classpath:/** 下，名字必须为 **monitoring.properties** 。

- springboot程序

  在启动类上加上注解 **@EnableMonitoring** 来开启监控功能，或者通过注解的两个参数来指定配置文件的路径和名字，如果未指定配置文件路径和名字，则配置文件需要放在 **classpath:/** 下，名字必须为 **monitoring.properties** 。

- springmvc程序

  在 **web.xml** 文件中配置一个监听器，来开启监控功能：

```xml
<!-- 开启监控功能 -->
<web-app>
<context-param>
    <param-name>configLocation</param-name>
    <param-value>classpath:monitoring.properties</param-value>
</context-param>
<listener>
  <listener-class>
    com.gitee.pifeng.monitoring.integrator.listener.MonitoringPlugInitializeListener
  </listener-class>
</listener>
</web-app>
```

### 业务埋点

Java应用程序只要集成了监控客户端，就具有业务埋点监控的能力，通过 **Monitor.buryingPoint()** 方法定时监控业务运行情况，通过 **Monitor.sendAlarm()** 发送告警。具体使用示例如下：

```
// 业务埋点监控
ScheduledExecutorService service = Monitor.buryingPoint(() -> {
  // 假如发现了业务异常，用下面的代码发送告警
  Alarm alarm = new Alarm();
  alarm.setAlarmLevel(AlarmLevelEnums.ERROR);
  alarm.setTitle("业务埋点监控");
  alarm.setTest(false);
  alarm.setCharset(Charsets.UTF_8);
  alarm.setMsg("测试普通maven程序业务埋点监控！");
  // alarm.setCode("001");
  alarm.setMonitorType(MonitorTypeEnums.CUSTOM);
  Result result = Monitor.sendAlarm(alarm);
  System.out.println("发送业务告警结果：" + result.toJsonString());
}, 0, 1, TimeUnit.HOURS, ThreadTypeEnums.IO_INTENSIVE_THREAD);
```

### 时钟同步

部署监控程序（监控UI端、监控服务端、监控代理端、监控客户端）的服务器集群需要进行时钟同步（NTP），保证时间的一致性！。

### 打包部署运行

#### Jar包部署
1. 打包  
   **监控UI端、监控服务端、监控代理端** 直接打成可执行jar。

```shell script
mvn -Dmaven.test.skip=true clean package
```

2. 上传jar、脚本

   a.jar路径：**phoenix/target**

   b.脚本路径：**phoenix/doc/脚本/**

3. 运行，脚本说明如下表：

    <table>
    <thead>
    <tr>
        <th>程序</th>
        <th>脚本</th>
        <th>命令</th>
        <th>含义</th>
    </tr>
    </thead>
    <tbody>
    <tr>
        <td rowspan="4">监控UI端</td>
        <td rowspan="4">phoenix_ui.sh</td>
        <td>./phoenix_ui.sh start</td>
        <td>启动</td>
    </tr>
    <tr>
        <td>./phoenix_ui.sh stop</td>
        <td>停止</td>
    </tr>
    <tr>
        <td>./phoenix_ui.sh restart</td>
        <td>重启</td>
    </tr>
    <tr>
        <td>./phoenix_ui.sh status</td>
        <td>检查状态</td>
    </tr>
    <tr>
        <td rowspan="4">监控服务端</td>
        <td rowspan="4">phoenix_server.sh</td>
        <td>./phoenix_ui.sh start</td>
        <td>启动</td>
    </tr>
    <tr>
        <td>./phoenix_server.sh stop</td>
        <td>停止</td>
    </tr>
    <tr>
        <td>./phoenix_server.sh restart</td>
        <td>重启</td>
    </tr>
    <tr>
        <td>./phoenix_server.sh status</td>
        <td>检查状态</td>
    </tr>
    <tr>
        <td rowspan="4">监控代理端</td>
        <td rowspan="4">phoenix_agent.sh</td>
        <td>./phoenix_agent.sh start</td>
        <td>启动</td>
    </tr>
    <tr>
        <td>./phoenix_agent.sh stop</td>
        <td>停止</td>
    </tr>
    <tr>
        <td>./phoenix_agent.sh restart</td>
        <td>重启</td>
    </tr>
    <tr>
        <td>./phoenix_agent.sh status</td>
        <td>检查状态</td>
    </tr>
    </tbody>
    </table> 

#### Docker部署

- 方式一：Maven打包远程部署

1. 有一台已经安装好docker环境的服务器，并且允许远程连接（以centos7下的yum方式安装的docker且使用service方式运行为例开启远程连接）：

```shell script
vi /usr/lib/systemd/system/docker.service  
#确保：ExecStart 的后面有： -H tcp://0.0.0.0:2375  
#修改完成后保存退出，刷新并重启docker服务   
systemctl daemon-reload  
systemctl restart docker  
```  

2. 在系统环境变量中添DOCKER_HOST，如下图所示：

![docker_host_config](https://gitee.com/monitoring-platform/phoenix/raw/master/doc/%E5%85%B6%E5%AE%83/docker_host_config.png "docker_host_config")

3. 编译项目打包项目并构建镜像

```shell script
 mvn -Dmaven.test.skip=true clean package docker:build  
```

4. 运行
>监控UI端：

```shell script
docker run -itd -v /tmp:/tmp -v /liblog4phoenix:/liblog4phoenix -v /etc/localtime:/etc/localtime:ro -p 443:443 --pid host --net host --name phoenix-ui phoenix/phoenix-ui /bin/bash
```

>监控服务端：

```shell script
docker run -itd -v /tmp:/tmp -v /liblog4phoenix:/liblog4phoenix -v /etc/localtime:/etc/localtime:ro -p 16000:16000 --pid host --net host --name phoenix-server phoenix/phoenix-server /bin/bash
```

>监控代理端：

```shell script
docker run -itd -v /tmp:/tmp -v /liblog4phoenix:/liblog4phoenix -v /etc/localtime:/etc/localtime:ro -p 12000:12000 --pid host --net host --name phoenix-agent phoenix/phoenix-agent /bin/bash
```

- 方式二：服务器本地构建docker镜像

1. 打包  
   **监控UI端、监控服务端、监控代理端** 直接打成可执行jar。

```shell script
mvn -Dmaven.test.skip=true clean package
```

2. 上传jar、Dockerfile

   a.jar路径：**phoenix/target**

   b.Dockerfile路径：**phoenix/phoenix-ui/src/main/docker/Dockerfile、  
   phoenix/phoenix-agent/src/main/docker/Dockerfile、  
   phoenix/phoenix-server/src/main/docker/Dockerfile**，  
   **Dockerfile** 要与对应的jar包放在同一目录下；

3. 构建docker镜像
>监控UI端：

```shell script
docker build -t phoenix/phoenix-ui .
```

>监控服务端：

```shell script
docker build -t phoenix/phoenix-server .
```

>监控代理端：

```shell script
docker build -t phoenix/phoenix-agent .
```

4. 运行
>监控UI端：

```shell script
docker run -itd -v /tmp:/tmp -v /liblog4phoenix:/liblog4phoenix -v /etc/localtime:/etc/localtime:ro -p 443:443 --pid host --net host --name phoenix-ui phoenix/phoenix-ui /bin/bash
```

>监控服务端：

```shell script
docker run -itd -v /tmp:/tmp -v /liblog4phoenix:/liblog4phoenix -v /etc/localtime:/etc/localtime:ro -p 16000:16000 --pid host --net host --name phoenix-server phoenix/phoenix-server /bin/bash
```

>监控代理端：

```shell script
docker run -itd -v /tmp:/tmp -v /liblog4phoenix:/liblog4phoenix -v /etc/localtime:/etc/localtime:ro -p 12000:12000 --pid host --net host --name phoenix-agent phoenix/phoenix-agent /bin/bash
```

### 集群部署

**监控服务端、监控UI端**支持集群部署，提升系统的容灾和可用性。

集群部署时的几点要求和建议：
1. DB配置保持一致；
2. 集群机器时钟保持一致（单机集群忽视）；
3. 建议：推荐通过nginx为集群做负载均衡。监控服务端、监控UI端均通过nginx进行访问。

![集群部署架构](https://gitee.com/monitoring-platform/phoenix/raw/master/doc/%E9%9B%86%E7%BE%A4%E9%83%A8%E7%BD%B2%E6%9E%B6%E6%9E%84.png "集群部署架构")

### 访问
**监控UI端** 访问URL：**https://localhost/phoenix-ui/index** ，初始账号/密码：**admin/admin123**，**guest/guest123**。

## 功能截图

![首页1](https://gitee.com/monitoring-platform/phoenix/raw/master/doc/%E6%88%AA%E5%9B%BE/%E9%A6%96%E9%A1%B51.png "首页1")

![首页2](https://gitee.com/monitoring-platform/phoenix/raw/master/doc/%E6%88%AA%E5%9B%BE/%E9%A6%96%E9%A1%B52.png "首页2")

![服务器1](https://gitee.com/monitoring-platform/phoenix/raw/master/doc/%E6%88%AA%E5%9B%BE/%E6%9C%8D%E5%8A%A1%E5%99%A81.png "服务器1")

![服务器2](https://gitee.com/monitoring-platform/phoenix/raw/master/doc/%E6%88%AA%E5%9B%BE/%E6%9C%8D%E5%8A%A1%E5%99%A82.png "服务器2")

![应用程序1](https://gitee.com/monitoring-platform/phoenix/raw/master/doc/%E6%88%AA%E5%9B%BE/%E5%BA%94%E7%94%A8%E7%A8%8B%E5%BA%8F1.png "应用程序1")

![应用程序2](https://gitee.com/monitoring-platform/phoenix/raw/master/doc/%E6%88%AA%E5%9B%BE/%E5%BA%94%E7%94%A8%E7%A8%8B%E5%BA%8F2.png "应用程序2")

![数据库1](https://gitee.com/monitoring-platform/phoenix/raw/master/doc/%E6%88%AA%E5%9B%BE/%E6%95%B0%E6%8D%AE%E5%BA%931.png "数据库1")

![数据库2](https://gitee.com/monitoring-platform/phoenix/raw/master/doc/%E6%88%AA%E5%9B%BE/%E6%95%B0%E6%8D%AE%E5%BA%932.png "数据库2")

![数据库3](https://gitee.com/monitoring-platform/phoenix/raw/master/doc/%E6%88%AA%E5%9B%BE/%E6%95%B0%E6%8D%AE%E5%BA%933.png "数据库3")

![数据库4](https://gitee.com/monitoring-platform/phoenix/raw/master/doc/%E6%88%AA%E5%9B%BE/%E6%95%B0%E6%8D%AE%E5%BA%934.png "数据库4")

![网络1](https://gitee.com/monitoring-platform/phoenix/raw/master/doc/%E6%88%AA%E5%9B%BE/%E7%BD%91%E7%BB%9C1.png "网络1")

![网络2](https://gitee.com/monitoring-platform/phoenix/raw/master/doc/%E6%88%AA%E5%9B%BE/%E7%BD%91%E7%BB%9C2.png "网络2")

![TCP1](https://gitee.com/monitoring-platform/phoenix/raw/master/doc/%E6%88%AA%E5%9B%BE/TCP1.png "TCP1")

![TCP2](https://gitee.com/monitoring-platform/phoenix/raw/master/doc/%E6%88%AA%E5%9B%BE/TCP2.png "TCP2")

![HTTP1](https://gitee.com/monitoring-platform/phoenix/raw/master/doc/%E6%88%AA%E5%9B%BE/HTTP1.png "HTTP1")

![HTTP2](https://gitee.com/monitoring-platform/phoenix/raw/master/doc/%E6%88%AA%E5%9B%BE/HTTP2.png "HTTP2")

![告警定义](https://gitee.com/monitoring-platform/phoenix/raw/master/doc/%E6%88%AA%E5%9B%BE/%E5%91%8A%E8%AD%A6%E5%AE%9A%E4%B9%89.png "告警定义")

![告警记录](https://gitee.com/monitoring-platform/phoenix/raw/master/doc/%E6%88%AA%E5%9B%BE/%E5%91%8A%E8%AD%A6%E8%AE%B0%E5%BD%95.png "告警记录")

![用户管理](https://gitee.com/monitoring-platform/phoenix/raw/master/doc/%E6%88%AA%E5%9B%BE/%E7%94%A8%E6%88%B7%E7%AE%A1%E7%90%86.png "用户管理")

![操作日志1](https://gitee.com/monitoring-platform/phoenix/raw/master/doc/%E6%88%AA%E5%9B%BE/%E6%93%8D%E4%BD%9C%E6%97%A5%E5%BF%971.png "操作日志1")

![操作日志2](https://gitee.com/monitoring-platform/phoenix/raw/master/doc/%E6%88%AA%E5%9B%BE/%E6%93%8D%E4%BD%9C%E6%97%A5%E5%BF%972.png "操作日志2")

![异常日志1](https://gitee.com/monitoring-platform/phoenix/raw/master/doc/%E6%88%AA%E5%9B%BE/%E5%BC%82%E5%B8%B8%E6%97%A5%E5%BF%971.png "异常日志1")

![异常日志2](https://gitee.com/monitoring-platform/phoenix/raw/master/doc/%E6%88%AA%E5%9B%BE/%E5%BC%82%E5%B8%B8%E6%97%A5%E5%BF%972.png "异常日志2")

![监控设置](https://gitee.com/monitoring-platform/phoenix/raw/master/doc/%E6%88%AA%E5%9B%BE/%E7%9B%91%E6%8E%A7%E8%AE%BE%E7%BD%AE.png "监控设置")

## 常见问题

[https://gitee.com/monitoring-platform/phoenix/wikis/pages?sort_id=4438763&doc_id=935794](https://gitee.com/monitoring-platform/phoenix/wikis/pages?sort_id=4438763&doc_id=935794)

## 升级日志

[https://gitee.com/monitoring-platform/phoenix/wikis/pages?sort_id=4420016&doc_id=935794](https://gitee.com/monitoring-platform/phoenix/wikis/pages?sort_id=4420016&doc_id=935794)

## 期望

欢迎提出更好的意见，帮助完善 phoenix

## 版权

[GNU General Public License v3.0](https://www.gnu.org/licenses/gpl-3.0.txt)

## 联系

QQ群：[773127639](https://qm.qq.com/cgi-bin/qm/qr?k=a0yY8EZMVTwvt8Tc1uWuk2hGpvhnyp3C&authKey=nvLNq0pw1yo32ZxbW8rxkYa6yyDn4Vc7f4R65CiifQ+RAgyWXuhszxIKSCB+eb5q&noverify=0)

## 捐赠

> [捐赠记录,感谢你们的支持！](https://gitee.com/monitoring-platform/phoenix/wikis/pages?sort_id=5547585&doc_id=935794)

![捐赠](https://gitee.com/monitoring-platform/phoenix/raw/master/doc/%E5%85%B6%E5%AE%83/donate.jpg "捐赠")
1111
> Written with [StackEdit](https://stackedit.io/).