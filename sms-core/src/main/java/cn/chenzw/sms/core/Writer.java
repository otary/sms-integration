package cn.chenzw.sms.core;

import java.io.IOException;

/**
 * @author chenzw
 */
public interface Writer {
    void write(Message message) throws IOException;
}
