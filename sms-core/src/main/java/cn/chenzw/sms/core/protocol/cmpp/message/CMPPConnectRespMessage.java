package cn.chenzw.sms.core.protocol.cmpp.message;

import cn.chenzw.sms.core.protocol.cmpp.util.ByteUtils;
import cn.chenzw.sms.core.protocol.cmpp.util.Md5Utils;

import java.util.Arrays;

/**
 * 连接回复信息
 *
 * @author chenzw
 */

public class CMPPConnectRespMessage extends CMPPBaseMessage {

    private int status = 0;
    private byte[] authenticatorISMG = null;
    private int version = CMPPConstants.CMPP_VERSION;
    private byte[] authenticatorSource = null;
    private String sharedSecret = null;

    public CMPPConnectRespMessage() {
        super(CMPPConstants.CMPP_CONNECT_RESP, 18);
    }

    /**
     * getters
     *
     * @return
     */
    public int getStatus() {
        return status;
    }

    public byte[] getAuthenticatorISMG() {
        return authenticatorISMG;
    }

    public int getVersion() {
        return version;
    }

    /**
     * setters
     *
     * @param status
     */
    public void setStatus(int status) {
        this.status = status;
    }

    public void setAuthenticatorSource(byte[] authenticatorSource) {
        this.authenticatorSource = authenticatorSource;
    }

    public void setSharedSecret(String sharedSecret) {
        this.sharedSecret = sharedSecret;
    }

    public boolean checkSharedSecret(byte[] authenticatorSource, String localSecret) {
        byte[] localAuthenticator = getAuthenticator(status, authenticatorSource, localSecret);
        return Arrays.equals(localAuthenticator, authenticatorISMG);
    }

    @Override
    protected void setBody(byte[] bodyBytes) {

        int off = 0;
        //status
        status = bodyBytes[off];
        off += 1;
        //authenticatorISMG
        authenticatorISMG = new byte[16];
        ByteUtils.bytesCopy(bodyBytes, authenticatorISMG, off, off + 15, 0);
        off += 16;
        //version
        version = bodyBytes[off];
        off += 1;

    }

    /**
     *
     */
    @Override
    protected byte[] getBody() {

        // make bodybytes
        int bodyLength = getCommandLength();
        byte[] bodyBytes = new byte[bodyLength];
        Arrays.fill(bodyBytes, (byte) 0);

        //make authenticatorSource
        if (authenticatorSource == null) {
            authenticatorSource = new byte[0];
        }
        //make sharedSecret
        if (sharedSecret == null) {
            sharedSecret = "";
        }
        //make authenticatorISMG
        authenticatorISMG = getAuthenticator(status, authenticatorSource, sharedSecret);

        // make body
        int off = 0;
        ByteUtils.bytesCopy(ByteUtils.intToBytes4(status), bodyBytes, 0, 3, off);
        off += 4;
        ByteUtils.bytesCopy(authenticatorISMG, bodyBytes, 0, 15, off);
        off += 16;
        bodyBytes[off] = ByteUtils.intToByte(version);
        off += 1;

        return bodyBytes;
    }

    /**
     * @param status
     * @param authenticatorSource
     * @param secret
     * @return
     */
    private byte[] getAuthenticator(int status, byte[] authenticatorSource, String secret) {

        if (authenticatorSource == null)
            authenticatorSource = new byte[0];
        if (secret == null)
            secret = "";

        byte[] buffer = new byte[1 + authenticatorSource.length + secret.length()];
        Arrays.fill(buffer, (byte) 0);

        int off = 0;
        buffer[off] = ByteUtils.intToByte(status);
        off += 1;
        ByteUtils.bytesCopy(authenticatorSource, buffer, 0, authenticatorSource.length - 1, off);
        off += authenticatorSource.length;
        ByteUtils.bytesCopy(secret.getBytes(), buffer, 0, secret.length() - 1, off);
        off += secret.length();

        return Md5Utils.md5(buffer);
    }

    @Override
    public String toString() {
        StringBuffer sb = new StringBuffer();
        sb.append("CMPPConnectRespMessage:[sequenceId=" + sequenceString() + ",");
        sb.append("status=" + status + ",");
        sb.append("authenticatorISMG=" + authenticatorISMG + ",");
        sb.append("version=" + version + "]");
        return sb.toString();
    }
}