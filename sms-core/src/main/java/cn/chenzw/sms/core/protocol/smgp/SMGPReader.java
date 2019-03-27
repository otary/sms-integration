package cn.chenzw.sms.core.protocol.smgp;

import cn.chenzw.sms.core.Message;
import cn.chenzw.sms.core.Reader;
import cn.chenzw.sms.core.protocol.smgp.util.ByteUtils;
import cn.chenzw.sms.core.protocol.smgp.message.SMGPBaseMessage;
import cn.chenzw.sms.core.protocol.smgp.message.SMGPConstants;

import java.io.*;
import java.io.IOException;
import java.io.InputStream;

/**
 * @author chenzw
 */
public class SMGPReader implements Reader {

    protected DataInputStream in;

    public SMGPReader(InputStream is) {
        this.in = new DataInputStream(is);
    }

    @Override
    public Message read() throws IOException {
        byte[] header = new byte[SMGPBaseMessage.SZ_HEADER];
        byte[] cmdBytes = null;
        try {
            readBytes(header, 0, SMGPBaseMessage.SZ_HEADER);
            int cmdLen = ByteUtils.byte2int(header, 0);

            if (cmdLen > 8096 || cmdLen < SMGPBaseMessage.SZ_HEADER) {
                throw new IOException("read stream error,cmdLen=" + cmdLen + ",close connection");
            }
            cmdBytes = new byte[cmdLen];
            System.arraycopy(header, 0, cmdBytes, 0, SMGPBaseMessage.SZ_HEADER);
            readBytes(cmdBytes, SMGPBaseMessage.SZ_HEADER, cmdLen - SMGPBaseMessage.SZ_HEADER);
        } catch (IOException e) {
            throw e;
        }

        try {
            SMGPBaseMessage baseMsg = SMGPConstants.fromBytes(cmdBytes);
            return baseMsg;
        } catch (Exception e) {
            e.printStackTrace();
            throw new IOException("build SMGPBaseMessage error:" + e.getMessage());
        }
    }

    private void readBytes(byte[] bytes, int offset, int len) throws IOException {
        in.readFully(bytes, offset, len);
    }
}
