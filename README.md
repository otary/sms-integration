# sms-integration
基于SMGP3.4协议、CMPP协议的短信发送框架

![jdk](https://img.shields.io/badge/jdk-1.6-brightgreen.svg)
![spring](https://img.shields.io/badge/spring-4.3.22.RELEASE-brightgreen.svg)


## SMGP协议

#### 核心类

- SMGPConnection

#### 示例

>  简单代码
```java
import cn.chenzw.sms.core.Connection;
import cn.chenzw.sms.core.Message;
import cn.chenzw.sms.core.Session;
import cn.chenzw.sms.core.protocol.smgp.SMGPConnection;

SMGPConnection conn = new SMGPConnection();
conn.setClientId("xxx");  // 帐号
conn.setPassword("yyy");   // 密码
conn.setVersion((byte) 0);
conn.setAutoReconnect(true);
conn.setSendInterval(200);

conn.connect("222.66.24.235", 8900); // 短信网关IP和端口

if (conn.isConnected()) {
    Session session = conn.getSession();
    try {
        for (int i = 0; i < 3; i++) {
            session.submit("第" + i + "条短信内容", "1065902100612", "18046048466");
        }
    } finally {
        try {
            Thread.sleep(3000L);
        } catch (InterruptedException ex) {}
        try {
            session.close();
        } catch (IOException ex) { }
    }
}
```

> 携带自定义ID并注册回调函数
```java
SMGPConnection conn = new SMGPConnection();
conn.setClientId("xxx");
conn.setPassword("yyy");
conn.setHost("222.66.24.235");
conn.setPort(8900);
conn.setVersion((byte) 0);
conn.setAutoReconnect(true);
conn.setSendInterval(200);

conn.connect();

// 注册全局回调函数
conn.registerCallbackHandler(new ConnectionCallback() {
    @Override
    public void onLoginSuccess(Connection connection, Message message) {
        log.info("login success:" + message);
    }

    @Override
    public void onLoingFailure(Connection connection, Message message) {
        log.info("login failure:" + message);
    }

});
if (conn.isConnected()) {
    Session session = conn.getSession();

    // 注册会话级回调函数
    session.registerCallbackHandler(new SubmitCallback() {
        @Override
        public void onSendSuccess(Connection connection, Message message) {
            log.info("send success:{}", message);
        }

        @Override
        public void onSendFailure(Connection connection, Message message) {
            log.info("send failure:{}", message);
        }
    });

    try {
        session.submit("xxx001", "测试内容1", "1065902100612", "18046048466");
        session.submit("xxx002", "测试内容2", "1065902100612", "18046048466");
    } finally {
        try {
            Thread.sleep(3000L);
            session.close();
        } catch (InterruptedException ex) {
            ex.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}

```

### 与Spring集成

#### 1）添加sms-spring依赖
````xml
<dependency>
	<groupId>cn.chenzw.sms</groupId>
    <artifactId>sms-integration</artifactId>
    <version>1.0</version>
</dependency>
````
#### 2）注册SMGPConnectionFactoryBean

> XML配置方式

```xml
<bean id="smgpConnection" class="cn.chenzw.sms.spring.protocol.smgp.SMGPConnectionFactoryBean">
   <property name="host" value="139.224.36.226"/>
   <property name="port" value="8890"/>
   <property name="clientId" value="XXXX"/>
   <property name="password" value="XXXX121"/>
</bean>
```

> Java配置方式

```java
@Configuration
public class AppConfig {

    @Bean
    public SMGPConnectionFactoryBean smgpConnectionFactoryBean() throws Exception {
        SMGPConnectionFactoryBean smgpConnectionFactoryBean = new SMGPConnectionFactoryBean();
        smgpConnectionFactoryBean.setHost("");
        smgpConnectionFactoryBean.setPassword("");
        smgpConnectionFactoryBean.setClientId("");
        smgpConnectionFactoryBean.setPort(8990);
        smgpConnectionFactoryBean.setVersion(0);
        smgpConnectionFactoryBean.setAutoReconnect(true);
        smgpConnectionFactoryBean.setSendInterval(200);
        return smgpConnectionFactoryBean;
    }
}
```

#### 3）注入SMGPConnectionFactoryBean
````java

@Autowired
SMGPConnection smgpConnection;

// ....

````

------

## CMPP协议


### 核心类

- CMPPConnection



### 示例

```java
CMPPConnection conn = new CMPPConnection();
conn.setSourceAddr("123456");
conn.setPassword("aaa001");
conn.setVersion((byte) 0);
conn.setAutoReconnect(true);
conn.setSendInterval(200);

conn.connect("127.0.0.1", 7890);

if (conn.isConnected()) {
    Session session = conn.getSession();
    String[] phones = new String[]{"13162645136"};

    long startTime = System.currentTimeMillis();
    try {
        for (int i = 0; i < phones.length * 10; i++) {
            String content = String.format("第%d条:电信cmpp测试X(%s)", i + 1, format.format(new Date()));
            session.submit(content, "1065902100612", phones[i / 10]);

        }
    } finally {
        log.info(String.format("total:%d", System.currentTimeMillis() - startTime));
    }
}
```

