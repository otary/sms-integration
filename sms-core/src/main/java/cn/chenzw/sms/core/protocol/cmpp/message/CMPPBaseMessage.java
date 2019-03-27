package cn.chenzw.sms.core.protocol.cmpp.message;

import cn.chenzw.sms.core.Message;
import cn.chenzw.sms.core.protocol.cmpp.util.ByteUtils;

import java.util.Arrays;

/**
 * CMPP头信息
 * @author chenzw
 */
public class CMPPBaseMessage implements Message {

    public static final int SZ_HEADER = 12;

    /**
     * 消息总长度
     */
    private int totalLength;

    /**
     * 命令或响应类型
     */
    private int commandId;

    /**
     * 命令长度
     */
    private int commandLength;

    /**
     * 消息流水号
     */
    private int sequenceId;

    /**
     * 扩展ID
     */
    protected Object extId;

    public CMPPBaseMessage(int commandId, int commandLength) {
        this.commandId = commandId;
        this.commandLength = commandLength;
    }

    public int getTotalLength() {
        return totalLength;
    }

    public int getCommandId() {
        return commandId;
    }

    public int getCommandLength() {
        return commandLength;
    }

    public int getSequenceId() {
        return sequenceId;
    }

    public void setSequenceId(int value) {
        this.sequenceId = value;
    }

    public Object getExtId() {
        return extId;
    }

    public void setExtId(Object extId) {
        this.extId = extId;
    }

    public boolean fromBytes(byte[] bytes) throws Exception {
        if (bytes == null) {
            return false;
        }
        if (bytes.length < SZ_HEADER) {
            return false;
        }

        byte headBytes[] = new byte[4];
        int offset = 0;
        ByteUtils.bytesCopy(bytes, headBytes, offset, offset + 3, 0);
        totalLength = ByteUtils.Bytes4ToInt(headBytes);
        offset += 4;
        ByteUtils.bytesCopy(bytes, headBytes, offset, offset + 3, 0);
        commandId = ByteUtils.Bytes4ToInt(headBytes);
        offset += 4;
        ByteUtils.bytesCopy(bytes, headBytes, offset, offset + 3, 0);
        sequenceId = ByteUtils.Bytes4ToInt(headBytes);

        if (totalLength - SZ_HEADER < commandLength) {  //length too short.
            return false;
        } else {
            byte[] bodyBytes = new byte[totalLength - SZ_HEADER];
            ByteUtils.bytesCopy(bytes, bodyBytes, SZ_HEADER, bodyBytes.length - 1, 0);
            setBody(bodyBytes);
            return true;
        }
    }

    public byte[] toBytes() throws Exception {

        byte[] bodyBytes = getBody();
        totalLength = SZ_HEADER + bodyBytes.length;
        byte[] bytes = new byte[totalLength];
        Arrays.fill(bytes, (byte) 0);
        int offset = 0;
        ByteUtils.bytesCopy(ByteUtils.intToBytes4(totalLength), bytes, 0, 3, offset);
        offset += 4;
        ByteUtils.bytesCopy(ByteUtils.intToBytes4(commandId), bytes, 0, 3, offset);
        offset += 4;
        ByteUtils.bytesCopy(ByteUtils.intToBytes4(sequenceId), bytes, 0, 3, offset);
        offset += 4;
        ByteUtils.bytesCopy(bodyBytes, bytes, 0, bodyBytes.length - 1, offset);

        return bytes;
    }

    protected void setBody(byte[] bodyBytes) throws Exception {

    }

    protected byte[] getBody() throws Exception {
        return new byte[0];
    }

    protected String plus86(String mobile) {
        if (mobile == null || mobile.trim().length() == 0) {
            return "";
        }
        if (mobile.startsWith("86")) {
            return mobile;
        }
        if (mobile.startsWith("+86")) {
            return mobile.substring(1);
        }
        return "86" + mobile;
    }

    protected String minus86(String mobile) {
        return plus86(mobile);
    }

    public String sequenceString() {
        StringBuffer buffer = new StringBuffer();
        int offset = 0;
        byte[] seqBytes = new byte[8];
        System.arraycopy(ByteUtils.intToBytes4(sequenceId), offset, seqBytes, 4, 4);
        buffer.append(ByteUtils.Bytes8ToLong(seqBytes));
        return buffer.toString();
    }


    @Override
    public String toString() {
        StringBuffer buffer = new StringBuffer();
        buffer.append("CMPPBaseMessage:[sequenceId=").append(sequenceString()).append(",").append("commandId=")
                .append(commandId).append("]");
        return buffer.toString();
    }
}