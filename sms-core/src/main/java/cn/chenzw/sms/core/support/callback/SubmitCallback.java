package cn.chenzw.sms.core.support.callback;

import cn.chenzw.sms.core.Callback;
import cn.chenzw.sms.core.Connection;
import cn.chenzw.sms.core.Message;

public abstract class SubmitCallback implements Callback {
    /**
     * 发送时回调
     * @param connection
     * @param message SMGPSubmitMessage
     */
    public void onSend(Connection connection, Message message) {
    }

    /**
     * 发送成功时回调
     * @param connection
     * @param message SMGPSubmitRespMessage
     */
    public abstract void onSendSuccess(Connection connection, Message message);

    /**
     * 发送失败时回调
     * @param connection
     * @param message
     */
    public abstract void onSendFailure(Connection connection, Message message);

    @Override
    public void onError(Connection connection, Exception error, String shortMsg){}
}
