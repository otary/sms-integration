package cn.chenzw.sms.core;

/**
 * @author chenzw
 */
public interface Callback {

    void onError(Connection connection, Exception error, String shortMsg);

}
