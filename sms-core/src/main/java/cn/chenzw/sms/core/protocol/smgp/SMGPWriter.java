package cn.chenzw.sms.core.protocol.smgp;

import cn.chenzw.sms.core.Message;
import cn.chenzw.sms.core.Writer;
import cn.chenzw.sms.core.protocol.smgp.message.SMGPBaseMessage;

import java.io.DataOutputStream;
import java.io.IOException;
import java.io.OutputStream;

/**
 * @author chenzw
 */
public class SMGPWriter implements Writer {

    protected DataOutputStream out;

    public SMGPWriter(OutputStream os) {
        this.out = new DataOutputStream(os);
    }

    @Override
    public void write(Message message) throws IOException {
        if (message instanceof SMGPBaseMessage) {
            byte[] bytes = null;
            try {
                bytes = ((SMGPBaseMessage) message).toBytes();
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
