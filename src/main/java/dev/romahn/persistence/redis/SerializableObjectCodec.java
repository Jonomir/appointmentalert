package dev.romahn.persistence.redis;

import io.lettuce.core.codec.RedisCodec;
import org.apache.commons.lang3.SerializationUtils;

import java.io.Serializable;
import java.nio.ByteBuffer;

public class SerializableObjectCodec implements RedisCodec<Serializable, Serializable> {

    @Override
    public Serializable decodeKey(ByteBuffer bytes) {
        return decode(bytes);
    }

    @Override
    public Serializable decodeValue(ByteBuffer bytes) {
        return decode(bytes);
    }

    private Serializable decode(ByteBuffer bytes) {
        byte[] array = new byte[bytes.remaining()];
        bytes.get(array);
        return SerializationUtils.deserialize(array);
    }

    @Override
    public ByteBuffer encodeKey(Serializable key) {
        return encode(key);
    }

    @Override
    public ByteBuffer encodeValue(Serializable value) {
        return encode(value);
    }

    private ByteBuffer encode(Serializable key) {
        return ByteBuffer.wrap(SerializationUtils.serialize(key));
    }
}
