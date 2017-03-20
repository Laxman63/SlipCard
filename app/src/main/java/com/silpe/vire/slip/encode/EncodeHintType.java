package com.silpe.vire.slip.encode;


public enum EncodeHintType {
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