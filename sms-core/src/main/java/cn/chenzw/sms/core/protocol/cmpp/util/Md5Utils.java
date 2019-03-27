package cn.chenzw.sms.core.protocol.cmpp.util;

import java.security.NoSuchAlgorithmException;

/**
 * @author chenzw
 */
public class Md5Utils {
    public static byte[] md5(byte[] source) {

        byte[] tmp = null;
        java.security.MessageDigest md;
        try {
            md = java.security.MessageDigest.getInstance("Md5");
            md.update(source);
            // Md5Utils 的计算结果是一个 128 位的长整数， 用字节表示就是 16 个字节
            tmp = md.digest();
        } catch (NoSuchAlgorithmException e) {
            tmp = null;
        }
        return tmp;
    }

}
