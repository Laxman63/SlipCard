package com.silpe.vire.slip.encode;

public enum ErrorCorrectionLevel {

    L(0x01),
    M(0x00),
    Q(0x03),
    H(0x02);

    private final int bits;

    ErrorCorrectionLevel(int bits) {
        this.bits = bits;
    }

    public int getBits() {
        return bits;
    }

}