package cn.chenzw.sms.spring.protocol.smgp;

import cn.chenzw.sms.core.Session;
import cn.chenzw.sms.core.protocol.smgp.SMGPConnection;
import cn.chenzw.sms.spring.protocol.smgp.config.AppConfig;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.io.IOException;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = {AppConfig.class})
public class SMGPJavaConfigTest {
    @Autowired
    SMGPConnection smgpConnection;

    @Test
    @Ignore
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
}
