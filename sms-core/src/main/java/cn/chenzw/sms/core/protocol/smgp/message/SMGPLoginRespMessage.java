package cn.chenzw.sms.core.protocol.smgp.message;

import cn.chenzw.sms.core.protocol.smgp.util.ByteUtils;

public class SMGPLoginRespMessage extends SMGPBaseMessage {

    public SMGPLoginRespMessage() {
        this.commandId = SMGPConstants.SMGP_LOGIN_RESP;
    }

    // 4
    private int status;

    // 16
    private byte[] serverAuth = new byte[16];

    // 1
    private byte version;

    @Override
    protected int setBody(byte[] bodyBytes) throws Exception {
        int offset = 0;

        status = ByteUtils.byte2int(bodyBytes, offset);
        offset += 4;

        serverAuth = new byte[16];
        System.arraycopy(bodyBytes, offset, serverAuth, 0, 16);
        offset += 16;

        version = bodyBytes[offset];
        offset += 1;
        return offset;
    }

    @Override
    protected byte[] getBody() throws Exception {
        int len = 4 + 16 + 1;
        int offset = 0;
        byte[] bodyBytes = new byte[len];
        ByteUtils.int2byte(status, bodyBytes, offset);
        offset += 4;

        System.arraycopy(serverAuth, 0, bodyBytes, offset, 16);
        offset += 16;

        bodyBytes[offset] = version;
        offset += 1;

        return bodyBytes;
    }

    public int getStatus() {
        return this.status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public byte[] getServerAuth() {
        return this.serverAuth;
    }

    public void setServerAuth(byte[] serverAuth) {
        this.serverAuth = serverAuth;
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
        buffer.append("SMGPLoginRespMessage:[sequenceNumber=").append(sequenceString()).append(",");
        buffer.append("status=").append(status).append(",");
        buffer.append("serverAuth=").append(serverAuth).append(",");
        buffer.append("version=").append(version).append("]");

        return buffer.toString();
    }
}