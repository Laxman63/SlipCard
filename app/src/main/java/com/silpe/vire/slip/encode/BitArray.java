package com.silpe.vire.slip.encode;

import java.util.Arrays;

final class BitArray {

    private int[] bits;
    private int size;

    BitArray() {
        this.size = 0;
        this.bits = new int[1];
    }

    public int getSize() {
        return size;
    }

    int getSizeInBytes() {
        return (size + 7) / 8;
    }

    private void ensureCapacity(int size) {
        if (size > bits.length * 32) {
            int[] newBits = makeArray(size);
            System.arraycopy(bits, 0, newBits, 0, bits.length);
            this.bits = newBits;
        }
    }

    /**
     * @param i bit to get
     * @return true iff bit i is set
     */
    public boolean get(int i) {
        return (bits[i / 32] & (1 << (i & 0x1F))) != 0;
    }

    /**
     * Sets bit i.
     *
     * @param i bit to set
     */
    public void set(int i) {
        bits[i / 32] |= 1 << (i & 0x1F);
    }

    void appendBit(boolean bit) {
        ensureCapacity(size + 1);
        if (bit) {
            bits[size / 32] |= 1 << (size & 0x1F);
        }
        size++;
    }

    /**
     * Appends the least-significant bits, from value, in order from most-significant to
     * least-significant. For example, appending 6 bits from 0x000001E will append the bits
     * 0, 1, 1, 1, 1, 0 in that order.
     *
     * @param value   {@code int} containing bits to append
     * @param numBits bits from value to append
     */
    void appendBits(int value, int numBits) {
        if (numBits < 0 || numBits > 32) {
            throw new IllegalArgumentException("Num bits must be between 0 and 32");
        }
        ensureCapacity(size + numBits);
        for (int numBitsLeft = numBits; numBitsLeft > 0; numBitsLeft--) {
            appendBit(((value >> (numBitsLeft - 1)) & 0x01) == 1);
        }
    }

    void appendBitArray(BitArray other) {
        int otherSize = other.size;
        ensureCapacity(size + otherSize);
        for (int i = 0; i < otherSize; i++) {
            appendBit(other.get(i));
        }
    }

    void xor(BitArray other) {
        if (size != other.size) {
            throw new IllegalArgumentException("Sizes don't match");
        }
        for (int i = 0; i < bits.length; i++) {
            // The last int could be incomplete (i.e. not have 32 bits in
            // it) but there is no problem since 0 XOR 0 == 0.
            bits[i] ^= other.bits[i];
        }
    }

    /**
     * @param bitOffset first bit to start writing
     * @param array     array to write into. Bytes are written most-significant byte first. This is the opposite
     *                  of the internal representation, which is exposed by
     * @param offset    position in array to start writing
     * @param numBytes  how many bytes to write
     */
    void toBytes(int bitOffset, byte[] array, int offset, int numBytes) {
        for (int i = 0; i < numBytes; i++) {
            int theByte = 0;
            for (int j = 0; j < 8; j++) {
                if (get(bitOffset)) {
                    theByte |= 1 << (7 - j);
                }
                bitOffset++;
            }
            array[offset + i] = (byte) theByte;
        }
    }

    private static int[] makeArray(int size) {
        return new int[(size + 31) / 32];
    }

    @Override
    public boolean equals(Object o) {
        if (!(o instanceof BitArray)) {
            return false;
        }
        BitArray other = (BitArray) o;
        return size == other.size && Arrays.equals(bits, other.bits);
    }

    @Override
    public int hashCode() {
        return 31 * size + Arrays.hashCode(bits);
    }

    @Override
    public String toString() {
        StringBuilder result = new StringBuilder(size);
        for (int i = 0; i < size; i++) {
            if ((i & 0x07) == 0) {
                result.append(' ');
            }
            result.append(get(i) ? 'X' : '.');
        }
        return result.toString();
    }

}