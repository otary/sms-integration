package cn.chenzw.sms.core.protocol.smgp.tlv;

import cn.chenzw.sms.core.protocol.smgp.util.ByteUtils;


public abstract class TLV {
    /** The tag of the instance of TLV as defined in SMPP v3.4 */
    private short tag = 0;

    // must be set by setValueData()
    private boolean valueIsSet = false;

    private static int DONT_CHECK_LIMIT = -1;

    private int minLength = DONT_CHECK_LIMIT;

    private int maxLength = DONT_CHECK_LIMIT;

    public TLV() {
        super();
    }

    public TLV(short tag) {
        super();
        this.tag = tag;
    }

    public TLV(int min, int max) {
        super();
        minLength = min;
        maxLength = max;
    }

    /** Sets all the necessary params of the TLV. */
    public TLV(short tag, int min, int max) {
        super();
        this.tag = tag;
        minLength = min;
        maxLength = max;
    }

    public abstract void setValueData(byte[] buffer) throws Exception;

    public abstract byte[] getValueData() throws Exception;

    /** Sets the tag of this TLV. */
    public void setTag(short tag) {
        this.tag = tag;
    }

    /** Returns the tag of this TLV. */
    public short getTag() {
        return tag;
    }

    public short getLength() {
        try {
            if (hasValue()) {
                byte[] valueBuf = getValueData();
                if (valueBuf != null) {
                    int length = valueBuf.length;
                    return (short) length;
                } else {
                    return 0;
                }
            } else {
                return 0;
            }
        } catch (Exception e) {
            return 0;
        }
    }


    public void fromBytes(byte[] buffer) throws Exception {
        int index = 0;
        fromBytes(buffer, index);
    }

    public int fromBytes(byte[] buffer, int offset) throws Exception {
        short newTag = ByteUtils.byte2short(buffer, offset);
        offset += 2;
        short length = ByteUtils.byte2short(buffer, offset);
        offset += 2;
        byte[] valueBytes = new byte[length];
        System.arraycopy(buffer, offset, valueBytes, 0, length);
        setValueData(valueBytes);
        setTag(newTag);
        return offset;
    }

    public byte[] toBytes() throws Exception {
        if (hasValue()) {
            int length = getLength();
            byte[] bytes = new byte[4 + length];
            toBytes(bytes, 0);
            return bytes;
        } else {
            return null;
        }
    }

    public int toBytes(byte[] buffer, int offset) throws Exception {
        if (hasValue()) {
            int length = getLength();
            ByteUtils.short2byte(getTag(), buffer, offset);
            offset += 2;
            ByteUtils.short2byte(getLength(), buffer, offset);
            offset += 2;
            byte[] valueBytes = getValueData();
            System.arraycopy(valueBytes, 0, buffer, offset, length);
            offset += length;
        }
        return offset;
    }

    protected void markValueSet() {
        valueIsSet = true;
    }

    public boolean hasValue() {
        return valueIsSet;
    }

    @Override
    public boolean equals(Object obj) {
        if ((obj != null) && (obj instanceof TLV)) {
            return getTag() == ((TLV) obj).getTag();
        }
        return false;
    }

}
