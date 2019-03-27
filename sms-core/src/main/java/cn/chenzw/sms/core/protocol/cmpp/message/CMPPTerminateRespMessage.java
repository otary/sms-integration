package cn.chenzw.sms.core.protocol.cmpp.message;

/**
 * @author chenzw
 */
public class CMPPTerminateRespMessage extends CMPPBaseMessage {

    public CMPPTerminateRespMessage() {
        super(CMPPConstants.CMPP_TERMINATE_RESP, 0);
    }

    @Override
    public String toString() {
        StringBuffer buffer = new StringBuffer();
        buffer.append("CMPPTerminateRespMessage:[sequenceId=").append(sequenceString()).append("]");
        return buffer.toString();
    }
}