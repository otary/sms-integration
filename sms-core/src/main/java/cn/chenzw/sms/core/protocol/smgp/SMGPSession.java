package cn.chenzw.sms.core.protocol.smgp;

import cn.chenzw.sms.core.Callback;
import cn.chenzw.sms.core.Message;
import cn.chenzw.sms.core.Session;
import cn.chenzw.sms.core.protocol.smgp.message.*;
import cn.chenzw.sms.core.protocol.smgp.util.Md5Utils;
import cn.chenzw.sms.core.protocol.smgp.util.SequenceGenerator;
import cn.chenzw.sms.core.support.callback.ConnectionCallback;
import cn.chenzw.sms.core.support.callback.SubmitCallback;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * 登陆会话
 * @author chenzw
 */
public class SMGPSession implements Session {

    private static final Logger log = LoggerFactory.getLogger(SMGPSession.class);
    private static final SimpleDateFormat dateFormat = new SimpleDateFormat("MMddHHmmss");

    private SMGPConnection connection;
    private String sessionId;
    private boolean authenticated;
    private Object lock = new Object();
    private List<Callback> callbacks;
    private Map<Integer, Object> extIdCache;

    public SMGPSession(SMGPConnection connection, boolean authenticated) {
        super();
        this.connection = connection;
        this.sessionId = UUID.randomUUID().toString();
        this.authenticated = authenticated;
        this.callbacks = new CopyOnWriteArrayList<Callback>();
        this.extIdCache = new ConcurrentHashMap<Integer, Object>();
    }

    @Override
    public String getSessionId() {
        return sessionId;
    }

    @Override
    public boolean isAuthenticated() {
        return authenticated;
    }

    @Override
    public void submit(String content, String spNumber, String userNumber) {
        submit(null, content, spNumber, userNumber);
    }

    @Override
    public void submit(Object extId, String content, String spNumber, String userNumber) {
        SMGPSubmitMessage submit = new SMGPSubmitMessage();
        submit.setSrcTermId(spNumber);
        submit.setDestTermIdArray(new String[]{userNumber});
        submit.setMsgFmt((byte) 8);

        byte[] bContent = null;
        try {
            bContent = content.getBytes("iso-10646-ucs-2");
        } catch (UnsupportedEncodingException e) {}

        if (bContent != null && bContent.length <= 140) {
            submit.setBMsgContent(bContent);
            submit.setMsgFmt((byte) 8);
            submit.setNeedReport((byte) 1);
            submit.setServiceId("");
            submit.setAtTime("");
            submit.setNeedReport((byte) 1);
            submit.setSequenceNumber(SequenceGenerator.nextSequence());
            submit.setExtId(extId);
            addExtIdCache(submit);
            send(submit);
        }
    }

    @Override
    public void heartbeat() {
        if (isAuthenticated()) {
            SMGPActiveTestMessage activeTest = new SMGPActiveTestMessage();
            activeTest.setSequenceNumber(SequenceGenerator.nextSequence());
            send(activeTest);
        }
    }

    @Override
    public boolean authenticate() {

        SMGPLoginMessage loginMsg = new SMGPLoginMessage();
        loginMsg.setClientId(connection.getClientId());
        loginMsg.setLoginMode(connection.getLoginMode());
        loginMsg.setVersion(connection.getVersion());

        String tmp = dateFormat.format(Calendar.getInstance().getTime());
        loginMsg.setTimestamp(Integer.parseInt(tmp));
        loginMsg.setClientAuth(Md5Utils.md5(connection.getClientId(), connection.getPassword(), tmp));
        loginMsg.setSequenceNumber(SequenceGenerator.nextSequence());
        send(loginMsg);
        synchronized (lock) {
            try {
                lock.wait();
            } catch (InterruptedException ex) {
                setAuthenticated(false);
            }
        }
        return isAuthenticated();
    }

    @Override
    public void close() throws IOException {
        //保存数据
        if (isAuthenticated()) {
            SMGPExitMessage exit = new SMGPExitMessage();
            exit.setSequenceNumber(SequenceGenerator.nextSequence());
            send(exit);
            synchronized (lock) {
                try {
                    lock.wait(6000);
                } catch (InterruptedException ex) {
                    setAuthenticated(false);
                }
            }
        }
        connection.close();
    }

    @Override
    public void send(Message message) {
        connection.send(message);
    }

    @Override
    public void process(Message message) throws IOException {
        if (message instanceof SMGPBaseMessage) {
            SMGPBaseMessage baseMsg = (SMGPBaseMessage) message;
            baseMsg.setExtId(getExtIdFromCache(baseMsg));
            if (isAuthenticated()) {
                if (baseMsg instanceof SMGPActiveTestMessage) {
                    process((SMGPActiveTestMessage) baseMsg);
                } else if (baseMsg instanceof SMGPActiveTestRespMessage) {
                    // do nothing
                } else if (baseMsg instanceof SMGPExitRespMessage) {
                    process((SMGPExitRespMessage) baseMsg);
                } else if (message instanceof SMGPSubmitRespMessage) {
                    process((SMGPSubmitRespMessage) message);
                } else if (message instanceof SMGPDeliverMessage) {
                    process((SMGPDeliverMessage) message);
                }
            } else if (baseMsg instanceof SMGPLoginRespMessage) {
                process((SMGPLoginRespMessage) baseMsg);
            } else {
                throw new IOException("the first packet was not SMGPBindRespMessage:" + baseMsg);
            }
        }
    }

    @Override
    public void registerCallbackHandler(Callback callback) {
        this.callbacks.add(callback);
    }

    private void process(SMGPActiveTestMessage msg) throws IOException {
        SMGPActiveTestRespMessage resp = new SMGPActiveTestRespMessage();
        resp.setSequenceNumber(msg.getSequenceNumber());
        send(resp);
    }

    private void process(SMGPLoginRespMessage rsp) throws IOException {
        List<Callback> globalCallbacks = connection.getRegistedCallbackHandler();
        synchronized (lock) {
            if (rsp.getStatus() == 0) {
                setAuthenticated(true);

                for (Callback globalCallback : globalCallbacks) {
                    if (globalCallback instanceof ConnectionCallback) {
                        ((ConnectionCallback) globalCallback).onLoginSuccess(connection, rsp);
                    }
                }
                for (Callback callback : this.callbacks) {
                    if (callback instanceof ConnectionCallback) {
                        ((ConnectionCallback) callback).onLoginSuccess(connection, rsp);
                    }
                }
                log.debug("smgp login success host={},port={},clientId={}", connection.getHost(), connection.getPort(),
                        connection.getClientId());
            } else {
                setAuthenticated(false);

                for (Callback globalCallback : globalCallbacks) {
                    if (globalCallback instanceof ConnectionCallback) {
                        ((ConnectionCallback) globalCallback).onLoingFailure(connection, rsp);
                    }
                }

                for (Callback callback : this.callbacks) {
                    if (callback instanceof ConnectionCallback) {
                        ((ConnectionCallback) callback).onLoingFailure(connection, rsp);
                    }
                }

                if (connection != null) {
                    connection.close();
                }
                log.error("smgp login failure, host={} ,port={},clientId={},status={}", connection.getHost(),
                        connection.getPort(), connection.getClientId(), rsp.getStatus());
            }
            lock.notifyAll();
        }
    }

    private void process(SMGPExitRespMessage msg) throws IOException {
        synchronized (lock) {
            setAuthenticated(false);
            lock.notifyAll();
        }
        log.debug("smgp exist success host={},port={},clientId={}", connection.getHost(), connection.getPort(),
                connection.getClientId());
    }

    private void process(SMGPSubmitRespMessage rsp) throws IOException {
        List<Callback> globalCallbacks = connection.getRegistedCallbackHandler();
        switch (rsp.getStatus()) {

            //发送成功
            case 0: {
                for (Callback globalCallback : globalCallbacks) {
                    if (globalCallback instanceof SubmitCallback) {
                        ((SubmitCallback) globalCallback).onSendSuccess(connection, rsp);
                    }
                }

                for (Callback callback : this.callbacks) {
                    if (callback instanceof SubmitCallback) {
                        ((SubmitCallback) callback).onSendSuccess(connection, rsp);
                    }
                }
            }
            break;

            // 发送失败
            default: {
                for (Callback globalCallback : globalCallbacks) {
                    if (globalCallback instanceof SubmitCallback) {
                        ((SubmitCallback) globalCallback).onSendFailure(connection, rsp);
                    }
                }
                for (Callback callback : this.callbacks) {
                    if (callback instanceof SubmitCallback) {
                        ((SubmitCallback) callback).onSendFailure(connection, rsp);
                    }
                }
            }
            break;
        }
    }

    private void process(SMGPDeliverMessage msg) throws IOException {
        if (msg.getIsReport() == 1) {
            //下行信息(平台送达报告)
            //SMGPDeliverMessage:[
            // sequenceNumber=0,
            // msgId=42010619162037055400,
            // isReport=1,
            // msgFmt=0,
            // recvTime=20140619162037,
            // srcTermId=18917768619,
            // destTermId=1065902100612,
            // msgLength=122,
            // msgContent={
            //  msgId=42010619162030053100,
            //  sub=001,dlvrd=001,
            //  subTime=1406191620,
            //  doneTime=1406191620,
            //  stat=DELIVRD,
            //  err=000,
            //  text=076?????̡��??�
            // }]
            SMGPReportData report = msg.getReport();
        } else {
            //下行信息(用户回复短信)
            //SMGPDeliverMessage:[
            // sequenceNumber=0,
            // msgId=42010619162054061000,
            // isReport=0,
            // msgFmt=15,
            // recvTime=20140619162054,
            // srcTermId=18917768619,
            // destTermId=1065902100612,
            // msgLength=10,
            // msgContent=哈哈哈哈哈
            // ]

        }
        SMGPDeliverRespMessage rsp = new SMGPDeliverRespMessage();
        rsp.setSequenceNumber(msg.getSequenceNumber());
        rsp.setMsgId(msg.getMsgId());
        rsp.setStatus(0);
        send(rsp);
    }

    private void setAuthenticated(boolean value) {
        this.authenticated = value;
    }

    private void addExtIdCache(SMGPSubmitMessage submitMessage) {
        this.extIdCache.put(submitMessage.getSequenceNumber(), submitMessage.getExtId());
    }

    private Object getExtIdFromCache(SMGPBaseMessage baseMsg) {
        return this.extIdCache.get(baseMsg.getSequenceNumber());
    }
}

