
// src/main/java/dev/poc/codec/MsgCodec.java
package dev.poc.codec;

import org.agrona.DirectBuffer;
import org.agrona.MutableDirectBuffer;
import org.agrona.concurrent.UnsafeBuffer;

import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;

public final class MsgCodec {
    public static final byte CMD_CAPTURE = 1;
    public static final byte CMD_ENRICH = 2;
    public static final byte CMD_VALIDATE = 3;
    public static final byte CMD_CALC = 4;
    public static final byte CMD_DISTRIBUTE = 5;

    private MsgCodec() {}

    public static byte[] capture(String tradeId) {
        return pack(CMD_CAPTURE, tradeId);
    }

    public static byte[] enrich(String tradeId, String refDataJson) {
        return pack(CMD_ENRICH, tradeId, refDataJson);
    }

    public static byte[] validate(String tradeId) {
        return pack(CMD_VALIDATE, tradeId);
    }

    public static byte[] calc(String tradeId) {
        return pack(CMD_CALC, tradeId);
    }

    public static byte[] distribute(String tradeId) {
        return pack(CMD_DISTRIBUTE, tradeId);
    }

    private static byte[] pack(byte cmd, String... fields) {
        int size = 1;
        for (String f : fields) {
            byte[] bytes = f.getBytes(StandardCharsets.UTF_8);
            size += Integer.BYTES + bytes.length;
        }
        ByteBuffer buf = ByteBuffer.allocate(size);
        buf.put(cmd);
        for (String f : fields) {
            byte[] bytes = f.getBytes(StandardCharsets.UTF_8);
            buf.putInt(bytes.length).put(bytes);
        }
        return buf.array();
    }

    public static Decoded decode(DirectBuffer buffer, int offset, int length) {
        int i = offset;
        byte cmd = buffer.getByte(i++);
        String[] fields = new String[3];
        int idx = 0;
        while (i < offset + length) {
            int len = buffer.getInt(i); i += Integer.BYTES;
            byte[] dst = new byte[len];
            buffer.getBytes(i, dst);
            i += len;
            fields[idx++] = new String(dst, StandardCharsets.UTF_8);
        }
        return new Decoded(cmd, fields);
    }

    public record Decoded(byte cmd, String[] fields) {}
}
