package cn.chenzw.sms.spring.protocol.smgp;


import cn.chenzw.sms.core.Connection;
import cn.chenzw.sms.core.Message;
import cn.chenzw.sms.core.Session;
import cn.chenzw.sms.core.protocol.smgp.SMGPConnection;
import cn.chenzw.sms.core.support.callback.ConnectionCallback;
import cn.chenzw.sms.core.support.callback.SubmitCallback;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.io.IOException;

//@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = {"classpath:applicationContext.xml"})
public class SMGPXmlConfigTest {

    private Logger logger = LoggerFactory.getLogger(SMGPXmlConfigTest.class);

    @Autowired
    SMGPConnection smgpConnection;

    //@Test
    public void testSend() {
        smgpConnection.connect();
        if (smgpConnection.isConnected()) {
            Session session = smgpConnection.getSession();
            try {
                session.submit("测试内容1", "1065902100612", "18046048466");
                session.submit("测试内容2", "1065902100612", "18046048466");
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
    }

    //@Test
    public void testSendWidthExtId() {
        smgpConnection.connect();
        // 注册全局回调函数
        smgpConnection.registerCallbackHandler(new ConnectionCallback() {
            @Override
            public void onLoginSuccess(Connection connection, Message message) {
                logger.info("login success:" + message);
            }

            @Override
            public void onLoingFailure(Connection connection, Message message) {
                logger.info("login failure:" + message);
            }

        });
        if (smgpConnection.isConnected()) {
            Session session = smgpConnection.getSession();

            // 注册会话级回调函数
            session.registerCallbackHandler(new SubmitCallback() {
                @Override
                public void onSendSuccess(Connection connection, Message message) {
                    logger.info("send success:{}", message);
                }

                @Override
                public void onSendFailure(Connection connection, Message message) {
                    logger.info("send failure:{}", message);
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
    }

}
