package cn.chenzw.sms.core.protocol.cmpp.message;

import cn.chenzw.sms.core.protocol.cmpp.util.ByteUtils;

/**
 * CMPP测试返回信息
 * @author chenzw
 */

public class CMPPActiveTestRespMessage extends CMPPBaseMessage {

    private int reserved = 0;

    public CMPPActiveTestRespMessage() {
        super(CMPPConstants.CMPP_ACTIVE_TEST_RESP, 1);
    }

    public int getReserved() {
        return reserved;
    }

    public void setReserved(int reserved) {
        this.reserved = reserved;
    }

    @Override
    protected void setBody(byte[] bodyBytes) throws Exception {
        int offset = 0;
        reserved = ByteUtils.byteToInt(bodyBytes[offset]);
        offset += 1;
        super.setBody(bodyBytes);
    }

    @Override
    protected byte[] getBody() throws Exception {
        byte[] bodyBytes = new byte[getCommandLength()];
        int offset = 0;
        bodyBytes[offset] = ByteUtils.intToByte(reserved);
        offset += 1;
        return bodyBytes;
    }

    @Override
    public String toString() {
        StringBuffer sb = new StringBuffer();
        sb.append("CMPPActiveTestRespMessage:[sequenceId=" + sequenceString() + ",");
        sb.append("reserved=" + reserved + "]");
        return sb.toString();
    }
}