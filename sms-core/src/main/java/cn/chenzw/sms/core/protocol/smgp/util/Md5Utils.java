package cn.chenzw.sms.core.protocol.smgp.util;


import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/**
 * @author chenzw
 */
public class Md5Utils {

    public static byte[] md5(String clientId, String pwd, String timestamp) {
        clientId = clientId == null ? "" : clientId;
        pwd = pwd == null ? "" : pwd;
        timestamp = timestamp == null ? "" : timestamp;
        while (timestamp.length() < 10) {
            timestamp = "0" + timestamp;
        }
        MessageDigest md = null;
        try {
            md = MessageDigest.getInstance("Md5");
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException(e.getMessage());
        }
        md.update(clientId.getBytes());
        md.update(new byte[7]);
        md.update(pwd.getBytes());
        md.update(timestamp.getBytes());
        return md.digest();
    }

}