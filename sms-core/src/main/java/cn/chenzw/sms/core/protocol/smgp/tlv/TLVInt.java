package cn.chenzw.sms.core.protocol.smgp.tlv;

import cn.chenzw.sms.core.protocol.smgp.util.ByteUtils;

public class TLVInt extends TLV {
    private int value = 0;

    public TLVInt() {
        super(4, 4);
    }

    public TLVInt(short p_tag) {
        super(p_tag, 4, 4);
    }

    public TLVInt(short p_tag, int p_value) {
        super(p_tag, 4, 4);
        value = p_value;
        markValueSet();
    }

    public void setValue(int p_value) {
        value = p_value;
        markValueSet();
    }

    public int getValue() {
        return value;

    }

    @Override
    public byte[] getValueData() throws Exception {
        return ByteUtils.int2byte(value);
    }

    @Override
    public void setValueData(byte[] buffer) throws Exception {
        value = ByteUtils.byte2int(buffer);
        markValueSet();
    }

}