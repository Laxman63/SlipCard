package com.silpe.vire.slip.encode;

import java.util.Arrays;

public final class BitMatrix {

    private final int width;
    private final int height;
    private final int rowSize;
    private final int[] bits;

    BitMatrix(int width, int height) {
        if (width < 1 || height < 1) {
            throw new IllegalArgumentException("Both dimensions must be greater than 0");
        }
        this.width = width;
        this.height = height;
        this.rowSize = (width + 31) / 32;
        bits = new int[rowSize * height];
    }

    public boolean get(int x, int y) {
        int offset = y * rowSize + (x / 32);
        return ((bits[offset] >>> (x & 0x1f)) & 1) != 0;
    }


    public void set(int x, int y) {
        int offset = y * rowSize + (x / 32);
        bits[offset] |= 1 << (x & 0x1f);
    }


    void setRegion(int left, int top, int width, int height) {
        if (top < 0 || left < 0) {
            throw new IllegalArgumentException("Left and top must be nonnegative");
        }
        if (height < 1 || width < 1) {
            throw new IllegalArgumentException("Height and width must be at least 1");
        }
        int right = left + width;
        int bottom = top + height;
        if (bottom > this.height || right > this.width) {
            throw new IllegalArgumentException("The region must fit inside the matrix");
        }
        for (int y = top; y < bottom; y++) {
            int offset = y * rowSize;
            for (int x = left; x < right; x++) {
                bits[offset + (x / 32)] |= 1 << (x & 0x1f);
            }
        }
    }


    public int getWidth() {
        return width;
    }

    public int getHeight() {
        return height;
    }

    @Override
    public boolean equals(Object o) {
        if (!(o instanceof BitMatrix)) {
            return false;
        }
        BitMatrix other = (BitMatrix) o;
        return width == other.width && height == other.height && rowSize == other.rowSize &&
                Arrays.equals(bits, other.bits);
    }

    @Override
    public int hashCode() {
        int hash = width;
        hash = 31 * hash + width;
        hash = 31 * hash + height;
        hash = 31 * hash + rowSize;
        hash = 31 * hash + Arrays.hashCode(bits);
        return hash;
    }


    @Override
    public String toString() {
        return toString("X ", "  ");
    }

    public String toString(String setString, String unsetString) {
        return buildToString(setString, unsetString, "\n");
    }


    @Deprecated
    public String toString(String setString, String unsetString, String lineSeparator) {
        return buildToString(setString, unsetString, lineSeparator);
    }

    private String buildToString(String setString, String unsetString, String lineSeparator) {
        StringBuilder result = new StringBuilder(height * (width + 1));
        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                result.append(get(x, y) ? setString : unsetString);
            }
            result.append(lineSeparator);
        }
        return result.toString();
    }

}