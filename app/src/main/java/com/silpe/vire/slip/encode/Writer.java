package com.silpe.vire.slip.encode;

import java.util.Map;

interface Writer {

    BitMatrix encode(String contents, BarcodeFormat format, int width, int height) throws WriterException;

    BitMatrix encode(String contents, BarcodeFormat format, int width, int height, Map<EncodeHintType, ?> hints) throws WriterException;

}
