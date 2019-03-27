package cn.chenzw.sms.core.protocol.smgp.util;

import cn.chenzw.sms.core.protocol.smgp.message.SMGPReportData;

import java.io.UnsupportedEncodingException;

public class MsgUtils {

    public static byte[] formatMsg(String msgContent, byte msgFmt) throws UnsupportedEncodingException {
        if (msgFmt == 8) {
            return msgContent.getBytes("iso-10646-ucs-2");
        } else if (msgFmt == 15) {
            return msgContent.getBytes("GBK");
        } else {
            return msgContent.getBytes("iso-8859-1");
        }
    }

    public static String formatMsg(byte[] bMsgContent, byte msgFmt) throws UnsupportedEncodingException {
        if (msgFmt == 8) {
            return new String(bMsgContent, "iso-10646-ucs-2");
        } else if (msgFmt == 15) {
            return new String(bMsgContent, "GBK");
        } else {
            return new String(bMsgContent, "iso-8859-1");
        }
    }

    public static SMGPReportData getReportData(byte[] bMsgContent) {
        SMGPReportData report = new SMGPReportData();
        try {
            report.fromBytes(bMsgContent);
            return report;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

}
