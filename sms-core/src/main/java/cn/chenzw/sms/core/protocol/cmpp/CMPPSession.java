package cn.chenzw.sms.core.protocol.cmpp;

import cn.chenzw.sms.core.Callback;
import cn.chenzw.sms.core.Message;
import cn.chenzw.sms.core.Session;
import cn.chenzw.sms.core.protocol.cmpp.message.*;
import cn.chenzw.sms.core.protocol.cmpp.util.SequenceGenerator;
import cn.chenzw.sms.core.support.callback.ConnectionCallback;
import cn.chenzw.sms.core.support.callback.SubmitCallback;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * @author chenzw
 */
public class CMPPSession implements Session {

    private static final Logger log = LoggerFactory.getLogger(CMPPSession.class);

    private static final SimpleDateFormat dateFormat = new SimpleDateFormat("MMddHHmmss");
    private CMPPConnection connection;
    private String sessionId;
    private boolean authenticated;
    private Object lock = new Object();
    private List<Callback> callbacks;
    private Map<Integer, Object> extIdCache;

    public CMPPSession(CMPPConnection connection, boolean authenticated) {
        super();
        this.connection = connection;
        this.sessionId = UUID.randomUUID().toString();
        this.authenticated = authenticated;
        this.callbacks = new ArrayList<Callback>();
        this.extIdCache = new HashMap<Integer, Object>();
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
        CMPPSubmitMessage submit = new CMPPSubmitMessage();
        submit.setServiceId("ACORN");
        submit.setAtTime("");
        submit.setSrcId(spNumber);
        submit.setMsgSrc(connection.getSourceAddr());
        submit.setFeeType("01");
        submit.setMsgText("你好", 15);
        submit.addDestTerminalId(userNumber);
        submit.setSequenceId(SequenceGenerator.nextSequence());
        submit.setExtId(extId);
        addExtIdCache(submit);
        send(submit);
    }

    @Override
    public void heartbeat() {
        if (isAuthenticated()) {
            CMPPActiveTestMessage activeTest = new CMPPActiveTestMessage();
            activeTest.setSequenceId(SequenceGenerator.nextSequence());
            send(activeTest);
        }
    }

    @Override
    public boolean authenticate() {

        CMPPConnectMessage loginMsg = new CMPPConnectMessage();
        loginMsg.setSourceAddr(connection.getSourceAddr());
        loginMsg.setVersion(connection.getVersion());

        String tmp = dateFormat.format(Calendar.getInstance().getTime());
        loginMsg.setTimestamp(Integer.parseInt(tmp));
        loginMsg.setSharedSecret(connection.getPassword());
        loginMsg.setSequenceId(SequenceGenerator.nextSequence());
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
            CMPPTerminateMessage exit = new CMPPTerminateMessage();
            exit.setSequenceId(SequenceGenerator.nextSequence());
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
        if (message instanceof CMPPBaseMessage) {
            CMPPBaseMessage baseMsg = (CMPPBaseMessage) message;
            baseMsg.setExtId(getExtIdFromCache(baseMsg));
            if (isAuthenticated()) {
                if (baseMsg instanceof CMPPActiveTestMessage) {
                    process((CMPPActiveTestMessage) baseMsg);
                } else if (baseMsg instanceof CMPPActiveTestRespMessage) {
                    process(baseMsg);
                } else if (baseMsg instanceof CMPPTerminateRespMessage) {
                    process((CMPPTerminateRespMessage) baseMsg);
                } else if (message instanceof CMPPSubmitRespMessage) {
                    process((CMPPSubmitRespMessage) message);
                } else if (message instanceof CMPPDeliverMessage) {
                    process((CMPPDeliverMessage) message);
                }
            } else if (baseMsg instanceof CMPPConnectRespMessage) {
                process((CMPPConnectRespMessage) baseMsg);
            } else {
                throw new IOException("the first packet was not CMPPBindRespMessage:" + baseMsg);
            }
        }
    }

    @Override
    public void registerCallbackHandler(Callback callback) {
        this.callbacks.add(callback);
    }

    private void process(CMPPActiveTestMessage msg) throws IOException {
        CMPPActiveTestRespMessage resp = new CMPPActiveTestRespMessage();
        resp.setSequenceId(msg.getSequenceId());
        send(resp);
    }

    private void process(CMPPConnectRespMessage rsp) throws IOException {
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
                log.debug("cmpp login success host={},port={},sourceAddr={}", connection.getHost(),
                        connection.getPort(), connection.getSourceAddr());
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
                log.error("cmpp login failure, host={},port={},sourceAddr={} ,status={}", connection.getHost(),
                        connection.getPort(), connection.getSourceAddr(), rsp.getStatus());
            }
            lock.notifyAll();
        }
    }

    private void process(CMPPTerminateRespMessage msg) throws IOException {
        synchronized (lock) {
            setAuthenticated(false);
            lock.notifyAll();
        }
        log.debug("cmpp exist success host={},port={},sourceAddr={}", connection.getHost(), connection.getPort(),
                connection.getSourceAddr());
    }

    private void process(CMPPSubmitRespMessage rsp) throws IOException {
        List<Callback> globalCallbacks = connection.getRegistedCallbackHandler();
        switch (rsp.getResult()) {
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

    private void process(CMPPDeliverMessage msg) throws IOException {
        CMPPDeliverRespMessage rsp = new CMPPDeliverRespMessage();
        rsp.setSequenceId(msg.getSequenceId());
        rsp.setMsgId(msg.getMsgId());
        rsp.setResult(0);
        send(rsp);
    }

    private void setAuthenticated(boolean value) {
        this.authenticated = value;
    }

    private void addExtIdCache(CMPPSubmitMessage submitMessage) {
        this.extIdCache.put(submitMessage.getSequenceId(), submitMessage.getExtId());
    }

    private Object getExtIdFromCache(CMPPBaseMessage baseMsg) {
        return this.extIdCache.get(baseMsg.getSequenceId());
    }
}

