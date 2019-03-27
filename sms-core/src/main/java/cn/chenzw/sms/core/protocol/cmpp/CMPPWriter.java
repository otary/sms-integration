package cn.chenzw.sms.core.protocol.cmpp;

import cn.chenzw.sms.core.Message;
import cn.chenzw.sms.core.Writer;
import cn.chenzw.sms.core.protocol.cmpp.message.CMPPBaseMessage;

import java.io.DataOutputStream;
import java.io.IOException;
import java.io.OutputStream;

/**
 * @author chenzw
 */
public class CMPPWriter implements Writer {

    protected DataOutputStream out;

    public CMPPWriter(OutputStream os) {
        this.out = new DataOutputStream(os);
    }

    @Override
    public void write(Message message) throws IOException {
        if (message instanceof CMPPBaseMessage) {
            byte[] bytes = null;
            try {
                bytes = ((CMPPBaseMessage) message).toBytes();
            } catch (Exception ex) { }
            if (bytes != null) {
                writeBytes(bytes);
            }
        }
    }

    private void writeBytes(byte[] bytes) throws IOException {
        out.write(bytes);
        out.flush();
    }
}
