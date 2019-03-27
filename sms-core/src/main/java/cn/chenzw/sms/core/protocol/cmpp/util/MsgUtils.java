package cn.chenzw.sms.core.protocol.cmpp.util;

import java.io.UnsupportedEncodingException;

public class MsgUtils {

    /**
     * 设置文本内容
     * 信息格式: 0：ASCII串 3：短信写卡操作 4：二进制信息 8：UCS2编码 15：含GB汉字
     */
    public static byte[] getMsgBytes(String msgText, int msgFmt) {
        byte[] binCnt = null;
        //0：纯ASCII字符串
        //3：写卡操作
        //4：二进制编码
        //8：UCS2编码
        //15: GBK编码
        try {
            switch (msgFmt) {
                case 0:
                    binCnt = msgText.getBytes("US-ASCII");
                    break;
                case 3:
                    binCnt = msgText.getBytes("US-ASCII");
                case 4:
                    binCnt = msgText.getBytes("US-ASCII");
                case 8:
                    binCnt = msgText.getBytes("ISO-10646-UCS-2");
                    break;
                case 15:
                    binCnt = msgText.getBytes("GBK");
                    break;
                default:
                    binCnt = msgText.getBytes();
            }
        } catch (UnsupportedEncodingException e) {
            binCnt = msgText.getBytes();
        }
        return binCnt;
    }

    /**
     * 计算消息长度
     * @param msgContent
     * @param msgFmt
     * @param maxMsgLength0
     * @param maxMsgLength1
     */
    public static int calMsgLength(byte[] msgContent, int msgFmt, int maxMsgLength0, int maxMsgLength1) {
        int msgLength = 0;
        if (msgContent == null) {
            msgLength = 0;
        } else {
            if (msgFmt == 0) {
                if (msgContent.length > maxMsgLength0) {
                    msgContent = new byte[maxMsgLength0];
                    ByteUtils.bytesCopy(msgContent, msgContent, 0, maxMsgLength0 - 1, 0);
                }
            } else {
                if (msgContent.length > maxMsgLength1) {
                    msgContent = new byte[maxMsgLength1];
                    ByteUtils.bytesCopy(msgContent, msgContent, 0, maxMsgLength1 - 1, 0);
                }
            }
            msgLength = msgContent.length;
        }
        return msgLength;
    }

    /**
     * 得到消息的16进制文本。
     */
    public String getMsgHexText(byte[] msgContent, byte msgFmt) {
        String msgText = null;

        if (msgContent == null) {
            return null;
        }
        //0：纯ASCII字符串
        //3：写卡操作
        //4：二进制编码
        //8：UCS2编码
        //15: GBK编码
        switch (msgFmt) {
            case 0:
                msgText = ByteUtils.byteToHexString(msgContent, "US-ASCII");
                break;
            case 3:
                msgText = ByteUtils.byteToHexString(msgContent, "US-ASCII");
                break;
            case 4:
                msgText = ByteUtils.byteToHexString(msgContent, "BIN");
                break;
            case 8:
                msgText = ByteUtils.byteToHexString(msgContent, "ISO-10646-UCS-2");
                break;
            case 15:
                msgText = ByteUtils.byteToHexString(msgContent, "GBK");
                break;
            default:
                msgText = ByteUtils.byteToHexString(msgContent, null);
        }
        return msgText;
    }


    /**
     * 根据当前的MsgFmt得到MsgContent文本
     */
    public String getMsgText(byte[] msgContent, byte msgFmt) {
        String msgText = null;

        if (msgContent == null) {
            return null;
        }

        //0：纯ASCII字符串
        //3：写卡操作
        //4：二进制编码
        //8：UCS2编码
        //15: GBK编码
        try {
            switch (msgFmt) {
                case 0:
                    msgText = new String(msgContent, "US-ASCII");
                    break;
                case 3:
                    msgText = new String(msgContent, "US-ASCII");
                    break;
                case 4:
                    msgText = ByteUtils.toPrintableString(msgContent);
                    break;
                case 8:
                    msgText = new String(msgContent, "ISO-10646-UCS-2");
                    break;
                case 15:
                    msgText = new String(msgContent, "GBK");
                    break;
                default:
                    msgText = ByteUtils.toPrintableString(msgContent);
            }
        } catch (UnsupportedEncodingException e) {
            msgText = ByteUtils.toPrintableString(msgContent);
        }
        return msgText;
    }
}
