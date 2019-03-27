package cn.chenzw.sms.core.protocol.smgp.util;

/**
 * @author chenzw
 */
public class SequenceGenerator {

    /**
     *  RandomGenerator.getAbsInt();
     */
    private static int seqId = 1;

    /**
     * 取值范围为 0x00000001-0x7FFFFFFF
     */
    public static synchronized int nextSequence() {
        //循环计数
        if (seqId == Integer.MAX_VALUE) {
            seqId = 0;
        }
        return seqId++;
    }


}
