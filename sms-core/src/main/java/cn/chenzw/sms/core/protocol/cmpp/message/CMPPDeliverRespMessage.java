package cn.chenzw.sms.core.protocol.cmpp.message;

import cn.chenzw.sms.core.protocol.cmpp.util.ByteUtils;

import java.util.Arrays;

/**
 * 服务器下发返回信息
 * @author chenzw
 *
 */

public class CMPPDeliverRespMessage extends CMPPBaseMessage {

    private long msgId = 0;
    private int result = 0;

    public CMPPDeliverRespMessage() {
        super(CMPPConstants.CMPP_DELIVER_RESP, 9);
    }

    public long getMsgId() {
        return msgId;
    }

    public void setMsgId(long msgId) {
        this.msgId = msgId;
    }

    public int getResult() {
        return result;
    }

    public void setResult(int result) {
        this.result = result;
    }

    /**
     *
     */
    @Override
    protected void setBody(byte[] bodyBytes) {

        byte[] abyte0 = new byte[21];
        int off = 0;

        Arrays.fill(abyte0, (byte) 0);
        ByteUtils.bytesCopy(bodyBytes, abyte0, off, off + 7, 0);
        msgId = ByteUtils.Bytes8ToLong(abyte0);
        off += 8;

        Arrays.fill(abyte0, (byte) 0);
        ByteUtils.bytesCopy(bodyBytes, abyte0, off, off, 0);
        result = ByteUtils.byteToInt(abyte0[0]);
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

        // make parameter

        // make body
        int off = 0;
        ByteUtils.bytesCopy(ByteUtils.longToBytes8(msgId), bodyBytes, 0, 7, off);
        off += 8;
        bodyBytes[off] = ByteUtils.intToByte(result);
        off += 1;

        return bodyBytes;
    }

    @Override
    public String toString() {
        StringBuffer sb = new StringBuffer();
        sb.append("CMPPDeliverRespMessage:[sequenceId=" + sequenceString() + ",");
        sb.append("msgId=" + msgId + ",");
        sb.append("result=" + result + "]");
        return sb.toString();
    }
}