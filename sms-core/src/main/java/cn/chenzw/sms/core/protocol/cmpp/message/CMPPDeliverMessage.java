package cn.chenzw.sms.core.protocol.cmpp.message;

import cn.chenzw.sms.core.protocol.cmpp.util.ByteUtils;
import cn.chenzw.sms.core.protocol.cmpp.util.MsgUtils;

import java.io.UnsupportedEncodingException;
import java.util.Arrays;

/**
 * 服务器下发信息
 * @author chenzw
 *
 */

public class CMPPDeliverMessage extends CMPPBaseMessage {

    private final int maxMsgLength0 = 160;
    private final int maxMsgLength1 = 140;

    private long msgId = 0;
    private String destId = null;
    private String serviceId = null;
    private int tpPid = 0;
    private int tpUdhi = 0;
    private int msgFmt = 0;
    private String srcTerminalId = null;
    private int registeredDelivery = 0;
    private int msgLength = 0;
    private byte[] msgContent = null;
    private String reserved = null;

    private long reportMsgId = 0;
    private String reportStat = null;
    private String reportSubmitTime = null;
    private String reportDoneTime = null;
    private String reportDestTerminalId = null;
    private int reportSMSCSequence = 0;

    public CMPPDeliverMessage() {
        super(CMPPConstants.CMPP_DELIVER, 73);
    }

    public String getReportDestTerminalId() {
        return reportDestTerminalId;
    }

    public void setReportDestTerminalId(String reportDestTerminalId) {
        this.reportDestTerminalId = reportDestTerminalId;
    }

    public String getReportDoneTime() {
        return reportDoneTime;
    }

    public void setReportDoneTime(String reportDoneTime) {
        this.reportDoneTime = reportDoneTime;
    }

    public long getReportMsgId() {
        return reportMsgId;
    }

    public void setReportMsgId(long reportMsgId) {
        this.reportMsgId = reportMsgId;
    }

    public int getReportSMSCSequence() {
        return reportSMSCSequence;
    }

    public void setReportSMSCSequence(int reportSMSCSequence) {
        this.reportSMSCSequence = reportSMSCSequence;
    }

    public String getReportStat() {
        return reportStat;
    }

    public void setReportStat(String reportStat) {
        this.reportStat = reportStat;
    }

    public String getReportSubmitTime() {
        return reportSubmitTime;
    }

    public void setReportSubmitTime(String reportSubmitTime) {
        this.reportSubmitTime = reportSubmitTime;
    }

    public String getDestId() {
        return destId;
    }

    public void setDestId(String destId) {
        this.destId = destId;
    }

    public byte[] getMsgContent() {
        return msgContent;
    }

    public int getMsgFmt() {
        return msgFmt;
    }

    public long getMsgId() {
        return msgId;
    }

    public void setMsgId(long msgId) {
        this.msgId = msgId;
    }

    public int getMsgLength() {
        return msgLength;
    }

    public int getRegisteredDelivery() {
        return registeredDelivery;
    }

    public void setRegisteredDelivery(int registeredDelivery) {
        this.registeredDelivery = registeredDelivery;
        if (registeredDelivery == 1) {
            msgLength = 60;
            msgFmt = 4;
        }
    }

    public String getReserved() {
        return reserved;
    }

    public void setReserved(String reserved) {
        this.reserved = reserved;
    }

    public String getServiceId() {
        return serviceId;
    }

    public void setServiceId(String serviceId) {
        this.serviceId = serviceId;
    }

    public String getSrcTerminalId() {
        return srcTerminalId;
    }

    public void setSrcTerminalId(String srcTerminalId) {
        this.srcTerminalId = srcTerminalId;
    }

    public int getTpPid() {
        return tpPid;
    }

    public void setTpPid(int pid) {
        tpPid = pid;
    }

    public int getTpUdhi() {
        return tpUdhi;
    }

    public void setTpUdhi(int udhi) {
        this.tpUdhi = udhi;
    }


    /**
     * 设置文本内容
     * 信息格式: 0：ASCII串 3：短信写卡操作 4：二进制信息 8：UCS2编码 15：含GB汉字
     */
    public void setMsgText(String msgText, int msgFmt) {
        byte[] msgBytes = MsgUtils.getMsgBytes(msgText, msgFmt);
        this.msgFmt = msgFmt;
        this.msgContent = msgBytes;
        this.msgLength = MsgUtils.calMsgLength(msgContent, msgFmt, maxMsgLength0, maxMsgLength1);
    }


    @Override
    protected void setBody(byte[] bodyBytes) {

        byte[] abyte0 = new byte[21];
        int off = 0;

        Arrays.fill(abyte0, (byte) 0);
        ByteUtils.bytesCopy(bodyBytes, abyte0, off, off + 7, 0);
        msgId = ByteUtils.Bytes8ToLong(abyte0);
        off += 8;

        Arrays.fill(abyte0, (byte) 0);
        ByteUtils.bytesCopy(bodyBytes, abyte0, off, off + 20, 0);
        destId = new String(abyte0, 0, 21);
        off += 21;

        Arrays.fill(abyte0, (byte) 0);
        ByteUtils.bytesCopy(bodyBytes, abyte0, off, off + 9, 0);
        serviceId = new String(abyte0, 0, 10);
        off += 10;

        Arrays.fill(abyte0, (byte) 0);
        ByteUtils.bytesCopy(bodyBytes, abyte0, off, off, 0);
        tpPid = ByteUtils.byteToInt(abyte0[0]);
        off += 1;

        Arrays.fill(abyte0, (byte) 0);
        ByteUtils.bytesCopy(bodyBytes, abyte0, off, off, 0);
        tpUdhi = ByteUtils.byteToInt(abyte0[0]);
        off += 1;

        Arrays.fill(abyte0, (byte) 0);
        ByteUtils.bytesCopy(bodyBytes, abyte0, off, off, 0);
        msgFmt = ByteUtils.byteToInt(abyte0[0]);
        off += 1;

        Arrays.fill(abyte0, (byte) 0);
        ByteUtils.bytesCopy(bodyBytes, abyte0, off, off + 20, 0);
        srcTerminalId = new String(abyte0, 0, 21);
        off += 21;

        Arrays.fill(abyte0, (byte) 0);
        ByteUtils.bytesCopy(bodyBytes, abyte0, off, off, 0);
        registeredDelivery = ByteUtils.byteToInt(abyte0[0]);
        off += 1;

        Arrays.fill(abyte0, (byte) 0);
        ByteUtils.bytesCopy(bodyBytes, abyte0, off, off, 0);
        msgLength = ByteUtils.byteToInt(abyte0[0]);
        off += 1;

        msgContent = new byte[msgLength];
        ByteUtils.bytesCopy(bodyBytes, msgContent, off, off + msgLength - 1, 0);
        off += msgLength;

        Arrays.fill(abyte0, (byte) 0);
        ByteUtils.bytesCopy(bodyBytes, abyte0, off, off + 7, 0);
        reserved = new String(abyte0, 0, 8);
        off += 8;

        if (registeredDelivery == 1) {
            off = 0;

            Arrays.fill(abyte0, (byte) 0);
            ByteUtils.bytesCopy(msgContent, abyte0, off, off + 7, 0);
            reportMsgId = ByteUtils.Bytes8ToLong(abyte0);
            off += 8;

            Arrays.fill(abyte0, (byte) 0);
            ByteUtils.bytesCopy(msgContent, abyte0, off, off + 6, 0);
            reportStat = new String(abyte0, 0, 7);
            off += 7;

            Arrays.fill(abyte0, (byte) 0);
            ByteUtils.bytesCopy(msgContent, abyte0, off, off + 9, 0);
            reportSubmitTime = new String(abyte0, 0, 10);
            off += 10;

            Arrays.fill(abyte0, (byte) 0);
            ByteUtils.bytesCopy(msgContent, abyte0, off, off + 9, 0);
            reportDoneTime = new String(abyte0, 0, 10);
            off += 10;

            Arrays.fill(abyte0, (byte) 0);
            ByteUtils.bytesCopy(msgContent, abyte0, off, off + 20, 0);
            reportDestTerminalId = new String(abyte0, 0, 21);
            off += 21;

            Arrays.fill(abyte0, (byte) 0);
            ByteUtils.bytesCopy(msgContent, abyte0, off, off + 3, 0);
            reportSMSCSequence = ByteUtils.Bytes4ToInt(abyte0);
            off += 4;
        }
    }

    @Override
    protected byte[] getBody() {

        // make bodybytes
        byte[] bodyBytes = new byte[73 + msgLength];
        Arrays.fill(bodyBytes, (byte) 0);
        // make parameter
        if (msgId == 0) {
            msgId = 123456;
        }
        if (destId == null) {
            destId = "";
        }
        if (serviceId == null) {
            serviceId = "";
        }
        if (srcTerminalId == null) {
            srcTerminalId = "";
        }
        if (reserved == null) {
            reserved = "";
        }
        if (reportStat == null) {
            reportStat = "";
        }
        if (reportSubmitTime == null) {
            reportSubmitTime = "";
        }
        if (reportDoneTime == null) {
            reportDoneTime = "";
        }
        if (reportDestTerminalId == null) {
            reportDestTerminalId = "";
        }

        // make body
        int off = 0;
        ByteUtils.bytesCopy(ByteUtils.longToBytes8(msgId), bodyBytes, 0, 7, off);
        off += 8;
        ByteUtils.bytesCopy(destId.getBytes(), bodyBytes, 0, 20, off);
        off += 21;
        ByteUtils.bytesCopy(serviceId.getBytes(), bodyBytes, 0, 9, off);
        off += 10;
        bodyBytes[off] = ByteUtils.intToByte(tpPid);
        off += 1;
        bodyBytes[off] = ByteUtils.intToByte(tpUdhi);
        off += 1;
        bodyBytes[off] = ByteUtils.intToByte(msgFmt);
        off += 1;
        ByteUtils.bytesCopy(srcTerminalId.getBytes(), bodyBytes, 0, 20, off);
        off += 21;
        bodyBytes[off] = ByteUtils.intToByte(registeredDelivery);
        off += 1;
        bodyBytes[off] = ByteUtils.intToByte(msgLength);
        off += 1;

        if (registeredDelivery == 1) {
            int off1 = 0;
            msgContent = new byte[msgLength];

            ByteUtils.bytesCopy(ByteUtils.longToBytes8(reportMsgId), msgContent, 0, 7, off1);
            off1 += 8;
            ByteUtils.bytesCopy(reportStat.getBytes(), msgContent, 0, 6, off1);
            off1 += 7;
            ByteUtils.bytesCopy(reportSubmitTime.getBytes(), msgContent, 0, 9, off1);
            off1 += 10;
            ByteUtils.bytesCopy(reportDoneTime.getBytes(), msgContent, 0, 9, off1);
            off1 += 10;
            ByteUtils.bytesCopy(reportDestTerminalId.getBytes(), msgContent, 0, 20, off1);
            off1 += 21;
            ByteUtils.bytesCopy(ByteUtils.intToBytes4(reportSMSCSequence), msgContent, 0, 3, off1);
            off1 += 4;
        }

        ByteUtils.bytesCopy(msgContent, bodyBytes, 0, msgLength - 1, off);
        off += msgLength;
        ByteUtils.bytesCopy(reserved.getBytes(), bodyBytes, 0, 7, off);
        off += 8;

        return bodyBytes;
    }

    @Override
    public String toString() {
        StringBuffer sb = new StringBuffer();
        sb.append("CMPPDeliverMessage:[sequenceId=" + sequenceString() + ",");
        sb.append("msgId=" + msgId + ",");
        sb.append("destId=" + destId + ",");
        sb.append("serviceId=" + serviceId + ",");
        sb.append("tpPid=" + tpPid + ",");
        sb.append("tpUdhi=" + tpUdhi + ",");
        sb.append("msgFmt=" + msgFmt + ",");
        sb.append("srcTerminalId=" + srcTerminalId + ",");
        sb.append("registeredDelivery=" + registeredDelivery + ",");
        sb.append("msgLength=" + msgLength + ",");
        sb.append("msgContent=" + msgContent + ",");
        sb.append("reserved=" + reserved + "]");

        return sb.toString();
    }
}