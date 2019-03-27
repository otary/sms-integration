package cn.chenzw.sms.core;

import java.io.IOException;

/**
 * @author chenzw
 */
public interface Reader {
    Message read() throws IOException;
}
