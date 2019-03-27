package com.chenzw.sms.core.protocol;

import cn.chenzw.sms.core.Connection;
import cn.chenzw.sms.core.Message;
import cn.chenzw.sms.core.Session;
import cn.chenzw.sms.core.protocol.smgp.SMGPConnection;
import cn.chenzw.sms.core.support.callback.ConnectionCallback;
import cn.chenzw.sms.core.support.callback.SubmitCallback;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;

/**
 * @author chenzw
 */
@RunWith(JUnit4.class)
public class SMGPTester {
    private static Logger log = LoggerFactory.getLogger(SMGPTester.class);


    @Test
    public void testSend() {
        SMGPConnection conn = new SMGPConnection();
        conn.setClientId("xxx");
        conn.setPassword("yyy");
        conn.setVersion((byte) 0);
        conn.setAutoReconnect(true);
        conn.setSendInterval(200);

        conn.connect("222.66.24.235", 8900);
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
                } catch (IOException ex) {

                }
            }
        }
    }

    @Test
    public void testSendWidthExtId() {
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
    }


}
