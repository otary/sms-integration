package cn.chenzw.sms.core.protocol.cmpp.message;

/**
 * @author chenzw
 */

public class CMPPTerminateMessage extends CMPPBaseMessage {

    public CMPPTerminateMessage() {
        super(CMPPConstants.CMPP_TERMINATE, 0);
    }

    @Override
    public String toString() {
        StringBuffer buffer = new StringBuffer();
        buffer.append("CMPPTerminateMessage:[sequenceId=").append(sequenceString()).append("]");
        return buffer.toString();
    }
}