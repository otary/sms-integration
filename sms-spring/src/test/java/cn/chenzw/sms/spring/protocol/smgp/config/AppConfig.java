package cn.chenzw.sms.spring.protocol.smgp.config;

import cn.chenzw.sms.spring.protocol.smgp.SMGPConnectionFactoryBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

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
