package cn.chenzw.sms.spring.protocol.smgp;

import cn.chenzw.sms.core.Connection;
import cn.chenzw.sms.core.protocol.smgp.SMGPConnection;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.FactoryBean;
import org.springframework.beans.factory.InitializingBean;

/**
 * @author chenzw
 */
public class SMGPConnectionFactoryBean implements FactoryBean<SMGPConnection>, InitializingBean, DisposableBean {
    private String host;
    private int port;
    private String clientId;
    private String password;
    private Integer version;
    private Integer loginMode;
    private Boolean autoReconnect;
    private Integer sendInterval;
    private Boolean keepAlive;

    private SMGPConnection connection;

    public void setHost(String host) {
        this.host = host;
    }

    public void setPort(int port) {
        this.port = port;
    }

    public void setClientId(String clientId) {
        this.clientId = clientId;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public void setVersion(Integer version) {
        this.version = version;
    }

    public void setAutoReconnect(Boolean autoReconnect) {
        this.autoReconnect = autoReconnect;
    }

    public void setSendInterval(Integer sendInterval) {
        this.sendInterval = sendInterval;
    }

    public void setKeepAlive(Boolean keepAlive) {
        this.keepAlive = keepAlive;
    }

    public void setLoginMode(Integer loginMode) {
        this.loginMode = loginMode;
    }

    public SMGPConnection getObject() throws Exception {
        return this.connection;
    }

    public Class<?> getObjectType() {
        return SMGPConnection.class;
    }

    public boolean isSingleton() {
        return true;
    }

    public void afterPropertiesSet() throws Exception {
        this.connection = new SMGPConnection();
        this.connection.setHost(host);
        this.connection.setPort(port);
        this.connection.setClientId(clientId);
        this.connection.setPassword(password);
        if (loginMode != null) {
            this.connection.setLoginMode((byte) loginMode.intValue());
        }
        if (version != null) {
            this.connection.setVersion((byte) version.intValue());
        }
        if (autoReconnect != null) {
            this.connection.setAutoReconnect(autoReconnect);
        }
        if (sendInterval != null) {
            this.connection.setSendInterval(sendInterval);
        }
        if (keepAlive != null) {
            this.connection.setKeepAlive(keepAlive);
        }
    }

    public void destroy() throws Exception {
        if (this.connection != null) {
            this.connection.close();
        }
    }
}
