package cn.chenzw.sms.spring.protocol.cmpp;

import cn.chenzw.sms.core.protocol.cmpp.CMPPConnection;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.FactoryBean;
import org.springframework.beans.factory.InitializingBean;

/**
 * @author chenzw
 */
public class CMPPConnectionFactoryBean implements FactoryBean<CMPPConnection>, InitializingBean, DisposableBean {
    private String host;
    private int port;
    private String sourceAddr;
    private String password;
    private Integer version;
    private Integer loginMode;
    private Boolean autoReconnect;
    private Integer sendInterval;
    private Boolean keepAlive;

    private CMPPConnection connection;


    public void setHost(String host) {
        this.host = host;
    }

    public void setPort(int port) {
        this.port = port;
    }

    public void setSourceAddr(String sourceAddr) {
        this.sourceAddr = sourceAddr;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public void setVersion(Integer version) {
        this.version = version;
    }

    public void setLoginMode(Integer loginMode) {
        this.loginMode = loginMode;
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

    public void setConnection(CMPPConnection connection) {
        this.connection = connection;
    }

    public CMPPConnection getObject() throws Exception {
        return this.connection;
    }

    public Class<?> getObjectType() {
        return CMPPConnection.class;
    }

    public boolean isSingleton() {
        return true;
    }

    public void destroy() throws Exception {
        if (this.connection != null) {
            this.connection.close();
        }
    }

    public void afterPropertiesSet() throws Exception {
        this.connection = new CMPPConnection();
        this.connection.setHost(host);
        this.connection.setPort(port);
        this.connection.setSourceAddr(sourceAddr);
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
}
