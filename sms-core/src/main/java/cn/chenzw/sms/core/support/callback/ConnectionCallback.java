package cn.chenzw.sms.core.support.callback;

import cn.chenzw.sms.core.Callback;
import cn.chenzw.sms.core.Connection;
import cn.chenzw.sms.core.Message;


/**
 * @author chenzw
 */
public abstract class ConnectionCallback implements Callback {

    /**
     * 发起连接时回调
     * @param connection
     */
    public void onConnection(Connection connection) {

    }

    /**
     * 关闭连接时回调
     * @param connection
     */
    public void onDisconnect(Connection connection) {
    }

    /**
     * 登录成功时回调
     * @param connection
     * @param message SMGPLoginRespMessage or CMPPConnectRespMessage
     */
    public abstract void onLoginSuccess(Connection connection, Message message);

    /**
     * 登录失败时回调
     * @param connection
     * @param message SMGPLoginRespMessage or CMPPConnectRespMessage
     */
    public abstract void onLoingFailure(Connection connection, Message message);

    @Override
    public void onError(Connection connection, Exception error, String shortMsg) {
    }

}
