package cn.chenzw.sms.core.protocol.smgp.message;

import cn.chenzw.sms.core.protocol.smgp.util.ByteUtils;

public class SMGPLoginMessage extends SMGPBaseMessage {

    private String clientId; // 8

    private byte[] clientAuth = new byte[16]; // 16

    private byte loginMode; // 1

    private int timestamp; // 4

    private byte version; // 1

    public SMGPLoginMessage() {
        this.commandId = SMGPConstants.SMGP_LOGIN;
    }

    @Override
    protected int setBody(byte[] bodyBytes) throws Exception {
        int offset = 0;
        byte[] tmp = null;

        tmp = new byte[8];
        System.arraycopy(bodyBytes, offset, tmp, 0, 8);
        clientId = new String(ByteUtils.rtrimBytes(tmp));
        offset += 8;

        System.arraycopy(bodyBytes, offset, clientAuth, 0, 16);
        offset += 16;

        loginMode = bodyBytes[offset];
        offset += 1;

        timestamp = ByteUtils.byte2int(bodyBytes, offset);
        offset += 4;

        version = bodyBytes[offset];
        offset += 1;
        return offset;
    }

    @Override
    protected byte[] getBody() throws Exception {
        int len = 8 + 16 + 1 + 4 + 1;
        int offset = 0;
        byte[] bodyBytes = new byte[len];
        ByteUtils.rfillBytes(clientId.getBytes(), 8, bodyBytes, offset);
        offset += 8;

        System.arraycopy(clientAuth, 0, bodyBytes, offset, 16);
        offset += 16;

        bodyBytes[offset] = loginMode;
        offset += 1;

        ByteUtils.int2byte(timestamp, bodyBytes, offset);
        offset += 4;

        bodyBytes[offset] = version;
        offset += 1;

        return bodyBytes;
    }

    public String getClientId() {
        return this.clientId;
    }

    public void setClientId(String clientId) {
        this.clientId = clientId;
    }

    public byte[] getClientAuth() {
        return this.clientAuth;
    }

    public void setClientAuth(byte[] clientAuth) {
        this.clientAuth = clientAuth;
    }

    public byte getLoginMode() {
        return this.loginMode;
    }

    public void setLoginMode(byte loginMode) {
        this.loginMode = loginMode;
    }

    public int getTimestamp() {
        return this.timestamp;
    }

    public void setTimestamp(int timestamp) {
        this.timestamp = timestamp;
    }

    public byte getVersion() {
        return this.version;
    }

    public void setVersion(byte version) {
        this.version = version;
    }

    @Override
    public String toString() {
        StringBuffer buffer = new StringBuffer();
        buffer.append("SMGPLoginMessage:[sequenceNumber=").append(sequenceString()).append(",");
        buffer.append("clientId=").append(clientId).append(",");
        buffer.append("clientAuth=").append(clientAuth).append(",");
        buffer.append("loginMode=").append(loginMode).append(",");
        buffer.append("timestamp=").append(timestamp).append(",");
        buffer.append("version=").append(version).append("]");
        return buffer.toString();
    }
}