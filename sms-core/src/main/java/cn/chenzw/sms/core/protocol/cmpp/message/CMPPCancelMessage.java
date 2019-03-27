package cn.chenzw.sms.core.protocol.cmpp.message;

import cn.chenzw.sms.core.protocol.cmpp.util.ByteUtils;

/**
 * CMPP取消信息
 * @author chenzw
 */
public class CMPPCancelMessage extends CMPPBaseMessage {

    /**
     * 信息标识
     */
    private long msgId;

    public CMPPCancelMessage() {
        super(CMPPConstants.CMPP_CANCEL, 8);
    }


    @Override
    protected void setBody(byte[] bodyBytes) throws Exception {
        msgId = ByteUtils.Bytes8ToLong(bodyBytes);
        super.setBody(bodyBytes);
    }

    @Override
    protected byte[] getBody() throws Exception {
        byte[] bodyBytes = new byte[getCommandLength()];
        int offset = 0;
        ByteUtils.bytesCopy(ByteUtils.longToBytes8(msgId), bodyBytes, 0, 7, offset);
        offset += 8;
        return bodyBytes;
    }

    public long getMsgId() {
        return msgId;
    }

    public void setMsgId(long msgId) {
        this.msgId = msgId;
    }

    @Override
    public String toString() {
        StringBuffer sb = new StringBuffer();
        sb.append("CMPPCancelMessage:[sequenceId=" + sequenceString() + ",");
        sb.append("msgId=" + msgId + "]");
        return sb.toString();
    }
}