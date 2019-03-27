package cn.chenzw.sms.core.protocol.cmpp.message;

/**
 * CMPP连接测试信息
 * @author chenzw
 */

public class CMPPActiveTestMessage extends CMPPBaseMessage {

    public CMPPActiveTestMessage() {
        super(CMPPConstants.CMPP_ACTIVE_TEST, 0);
    }

    @Override
    public String toString() {
        StringBuffer buffer = new StringBuffer();
        buffer.append("CMPPActiveTestMessage:[sequenceId=" + sequenceString() + "]");
        return buffer.toString();
    }
}