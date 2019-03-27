package cn.chenzw.sms.core.protocol.cmpp.message;

import cn.chenzw.sms.core.protocol.cmpp.util.ByteUtils;

/**
 * 取消返回信息
 * @author chenzw
 */

public class CMPPCancelRespMessage extends CMPPBaseMessage {

    /**
     * 成功标识 0：成功 1：失败
     */
    private int successId;

    public CMPPCancelRespMessage() {
        super(CMPPConstants.CMPP_CANCEL_RESP, 1);
    }

    public int getSuccessId() {
        return successId;
    }

    public void setSuccessId(int successId) {
        this.successId = successId;
    }

    @Override
    protected void setBody(byte[] bodyBytes) throws Exception {
        int offset = 0;
        successId = ByteUtils.byteToInt(bodyBytes[offset]);
        offset += 1;
        super.setBody(bodyBytes);
    }

    @Override
    protected byte[] getBody() throws Exception {
        byte[] bodyBytes = new byte[getCommandLength()];
        int offset = 0;
        bodyBytes[offset] = ByteUtils.intToByte(successId);
        offset += 1;
        return bodyBytes;
    }

    @Override
    public String toString() {
        StringBuffer sb = new StringBuffer();
        sb.append("CMPPCancelRespMessage:[sequenceId=" + sequenceString() + ",");
        sb.append("successId=" + successId + "]");
        return sb.toString();
    }
}