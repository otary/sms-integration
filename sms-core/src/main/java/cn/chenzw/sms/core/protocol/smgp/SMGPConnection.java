package cn.chenzw.sms.core.protocol.smgp;

import cn.chenzw.sms.core.Connection;
import cn.chenzw.sms.core.Reader;
import cn.chenzw.sms.core.Session;
import cn.chenzw.sms.core.Writer;

import java.io.InputStream;
import java.io.OutputStream;

/**
 * SMGP连接器
 * @author
 */
public class SMGPConnection extends Connection {

    public static final byte MT = 0;
    public static final byte MO = 1;
    public static final byte MT_MO = 2;

    private String clientId;
    private String password;
    private byte loginMode;
    private byte version;

    public SMGPConnection() {
        super();
        setLoginMode(MT_MO);
    }

    public String getClientId() {
        return clientId;
    }

    public void setClientId(String clientId) {
        this.clientId = clientId;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public byte getLoginMode() {
        return loginMode;
    }

    public void setLoginMode(byte loginMode) {
        this.loginMode = loginMode;
    }

    public byte getVersion() {
        return version;
    }

    public void setVersion(byte version) {
        this.version = version;
    }

    @Override
    protected Session createSession() {
        return new SMGPSession(this, false);
    }

    @Override
    protected Writer createWriter(OutputStream output) {
        return new SMGPWriter(output);
    }

    @Override
    protected Reader createReader(InputStream input) {
        return new SMGPReader(input);
    }

    @Override
    public String toString() {
        StringBuffer buffer = new StringBuffer();
        buffer.append("smgp:[clientId=").append(clientId).append(",").append("host=").append(getHost()).append(",")
                .append("port=").append(getPort()).append(",").append("password=").append(password).append(",")
                .append("loginMode=").append(loginMode).append("]");
        return buffer.toString();
    }
}
