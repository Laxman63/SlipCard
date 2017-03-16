package com.silpe.vire.slip.encode;

import java.util.Map;

public interface Writer {

    BitMatrix encode(String contents, BarcodeFormat format, int width, int height) throws WriterException;

    BitMatrix encode(String contents, BarcodeFormat format, int width, int height, Map<EncodeHintType, ?> hints) throws WriterException;

}

enum BarcodeFormat {
    QR_CODE
}

enum EncodeHintType {
    ERROR_CORRECTION,
    CHARACTER_SET,
    DATA_MATRIX_SHAPE,
    @Deprecated
    MIN_SIZE,
    @Deprecated
    MAX_SIZE,
    MARGIN,
    PDF417_COMPACT,
    PDF417_COMPACTION,
    PDF417_DIMENSIONS,
    AZTEC_LAYERS,
    QR_VERSION,
}

enum ErrorCorrectionLevel {

    L(0x01),
    M(0x00),
    Q(0x03),
    H(0x02);

    private static final ErrorCorrectionLevel[] FOR_BITS = {M, L, H, Q};
    private final int bits;

    ErrorCorrectionLevel(int bits) {
        this.bits = bits;
    }

    public int getBits() {
        return bits;
    }

    public static ErrorCorrectionLevel forBits(int bits) {
        if (bits < 0 || bits >= FOR_BITS.length) {
            throw new IllegalArgumentException();
        }
        return FOR_BITS[bits];
    }

}