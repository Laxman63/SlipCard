package com.silpe.vire.slip.image;

import com.bumptech.glide.load.Key;

import java.io.UnsupportedEncodingException;
import java.nio.ByteBuffer;
import java.security.MessageDigest;

public class TimestampSignature implements Key {

    private long timestamp;

    public TimestampSignature(long timestamp) {
        this.timestamp = timestamp;
    }

    public TimestampSignature() {
        this(System.currentTimeMillis());
    }

    public long getSignature() {
        return timestamp;
    }

    @Override
    public void updateDiskCacheKey(MessageDigest messageDigest) throws UnsupportedEncodingException {
        messageDigest.update(ByteBuffer.allocate(Integer.SIZE).putLong(timestamp).array());
    }

    @Override
    public boolean equals(Object o) {
        if (o == null) {
            return false;
        } else if (!(o instanceof TimestampSignature)) {
            return false;
        } else {
            TimestampSignature ts = (TimestampSignature) o;
            return timestamp == ts.timestamp;
        }
    }

    @Override
    public int hashCode() {
        return Long.valueOf(timestamp).intValue();
    }

}
