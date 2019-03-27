package cn.chenzw.sms.core;

import java.io.IOException;

/**
 * 短信通道会话
 * @author chenzw
 */
public interface Session extends java.io.Closeable {
    String getSessionId();

    boolean isAuthenticated();

    boolean authenticate();

    void heartbeat();

    void submit(String content, String spNumber, String userNumber);

    void submit(Object extId, String content, String spNumber, String userNumber);

    void send(Message message);

    void process(Message message) throws IOException;

    /**
     * 注册回调函数
     * @param callback
     */
    void registerCallbackHandler(Callback callback);
}
