package com.silpe.vire.slip.encode;

import java.util.Arrays;

public final class QRCode {

    public static final int NUM_MASK_PATTERNS = 8;

    private Mode mode;
    private ErrorCorrectionLevel ecLevel;
    private Version version;
    private int maskPattern;
    private ByteMatrix matrix;

    public QRCode() {
        maskPattern = -1;
    }

    public Mode getMode() {
        return mode;
    }

    public ErrorCorrectionLevel getECLevel() {
        return ecLevel;
    }

    public Version getVersion() {
        return version;
    }

    public int getMaskPattern() {
        return maskPattern;
    }

    public ByteMatrix getMatrix() {
        return matrix;
    }

    @Override
    public String toString() {
        StringBuilder result = new StringBuilder(200);
        result.append("<<\n");
        result.append(" mode: ");
        result.append(mode);
        result.append("\n ecLevel: ");
        result.append(ecLevel);
        result.append("\n version: ");
        result.append(version);
        result.append("\n maskPattern: ");
        result.append(maskPattern);
        if (matrix == null) {
            result.append("\n matrix: null\n");
        } else {
            result.append("\n matrix:\n");
            result.append(matrix);
        }
        result.append(">>\n");
        return result.toString();
    }

    public void setMode(Mode value) {
        mode = value;
    }

    public void setECLevel(ErrorCorrectionLevel value) {
        ecLevel = value;
    }

    public void setVersion(Version version) {
        this.version = version;
    }

    public void setMaskPattern(int value) {
        maskPattern = value;
    }

    public void setMatrix(ByteMatrix value) {
        matrix = value;
    }

    public static boolean isValidMaskPattern(int maskPattern) {
        return maskPattern >= 0 && maskPattern < NUM_MASK_PATTERNS;
    }

}

class ByteMatrix {

    private final byte[][] bytes;
    private final int width;
    private final int height;

    public ByteMatrix(int width, int height) {
        bytes = new byte[height][width];
        this.width = width;
        this.height = height;
    }

    public int getHeight() {
        return height;
    }

    public int getWidth() {
        return width;
    }

    public byte get(int x, int y) {
        return bytes[y][x];
    }

    public byte[][] getArray() {
        return bytes;
    }

    public void set(int x, int y, byte value) {
        bytes[y][x] = value;
    }

    public void set(int x, int y, int value) {
        bytes[y][x] = (byte) value;
    }

    public void set(int x, int y, boolean value) {
        bytes[y][x] = (byte) (value ? 1 : 0);
    }

    public void clear(byte value) {
        for (byte[] aByte : bytes) {
            Arrays.fill(aByte, value);
        }
    }

    @Override
    public String toString() {
        StringBuilder result = new StringBuilder(2 * width * height + 2);
        for (int y = 0; y < height; ++y) {
            byte[] bytesY = bytes[y];
            for (int x = 0; x < width; ++x) {
                switch (bytesY[x]) {
                    case 0:
                        result.append(" 0");
                        break;
                    case 1:
                        result.append(" 1");
                        break;
                    default:
                        result.append("  ");
                        break;
                }
            }
            result.append('\n');
        }
        return result.toString();
    }

}

enum Mode {

    TERMINATOR(new int[]{0, 0, 0}, 0x00), // Not really a mode...
    NUMERIC(new int[]{10, 12, 14}, 0x01),
    ALPHANUMERIC(new int[]{9, 11, 13}, 0x02),
    STRUCTURED_APPEND(new int[]{0, 0, 0}, 0x03), // Not supported
    BYTE(new int[]{8, 16, 16}, 0x04),
    ECI(new int[]{0, 0, 0}, 0x07), // character counts don't apply
    KANJI(new int[]{8, 10, 12}, 0x08),
    FNC1_FIRST_POSITION(new int[]{0, 0, 0}, 0x05),
    FNC1_SECOND_POSITION(new int[]{0, 0, 0}, 0x09),
    /**
     * See GBT 18284-2000; "Hanzi" is a transliteration of this mode name.
     */
    HANZI(new int[]{8, 10, 12}, 0x0D);

    private final int[] characterCountBitsForVersions;
    private final int bits;

    Mode(int[] characterCountBitsForVersions, int bits) {
        this.characterCountBitsForVersions = characterCountBitsForVersions;
        this.bits = bits;
    }

    public static Mode forBits(int bits) {
        switch (bits) {
            case 0x0:
                return TERMINATOR;
            case 0x1:
                return NUMERIC;
            case 0x2:
                return ALPHANUMERIC;
            case 0x3:
                return STRUCTURED_APPEND;
            case 0x4:
                return BYTE;
            case 0x5:
                return FNC1_FIRST_POSITION;
            case 0x7:
                return ECI;
            case 0x8:
                return KANJI;
            case 0x9:
                return FNC1_SECOND_POSITION;
            case 0xD:
                // 0xD is defined in GBT 18284-2000, may not be supported in foreign country
                return HANZI;
            default:
                throw new IllegalArgumentException();
        }
    }

    public int getCharacterCountBits(Version version) {
        int number = version.getVersionNumber();
        int offset;
        if (number <= 9) {
            offset = 0;
        } else if (number <= 26) {
            offset = 1;
        } else {
            offset = 2;
        }
        return characterCountBitsForVersions[offset];
    }

    public int getBits() {
        return bits;
    }

}

final class Version {

    /**
     * See ISO 18004:2006 Annex D.
     * Element i represents the raw version bits that specify version i + 7
     */
    private static final int[] VERSION_DECODE_INFO = {
            0x07C94, 0x085BC, 0x09A99, 0x0A4D3, 0x0BBF6,
            0x0C762, 0x0D847, 0x0E60D, 0x0F928, 0x10B78,
            0x1145D, 0x12A17, 0x13532, 0x149A6, 0x15683,
            0x168C9, 0x177EC, 0x18EC4, 0x191E1, 0x1AFAB,
            0x1B08E, 0x1CC1A, 0x1D33F, 0x1ED75, 0x1F250,
            0x209D5, 0x216F0, 0x228BA, 0x2379F, 0x24B0B,
            0x2542E, 0x26A64, 0x27541, 0x28C69
    };

    private static final Version[] VERSIONS = buildVersions();

    private final int versionNumber;
    private final int[] alignmentPatternCenters;
    private final ECBlocks[] ecBlocks;
    private final int totalCodewords;

    private Version(int versionNumber,
                    int[] alignmentPatternCenters,
                    ECBlocks... ecBlocks) {
        this.versionNumber = versionNumber;
        this.alignmentPatternCenters = alignmentPatternCenters;
        this.ecBlocks = ecBlocks;
        int total = 0;
        int ecCodewords = ecBlocks[0].getECCodewordsPerBlock();
        ECB[] ecbArray = ecBlocks[0].getECBlocks();
        for (ECB ecBlock : ecbArray) {
            total += ecBlock.getCount() * (ecBlock.getDataCodewords() + ecCodewords);
        }
        this.totalCodewords = total;
    }

    public int getVersionNumber() {
        return versionNumber;
    }

    public int[] getAlignmentPatternCenters() {
        return alignmentPatternCenters;
    }

    public int getTotalCodewords() {
        return totalCodewords;
    }

    public int getDimensionForVersion() {
        return 17 + 4 * versionNumber;
    }

    public ECBlocks getECBlocksForLevel(ErrorCorrectionLevel ecLevel) {
        return ecBlocks[ecLevel.ordinal()];
    }

    /**
     * <p>Deduces version information purely from QR Code dimensions.</p>
     *
     * @param dimension dimension in modules
     * @return Version for a QR Code of that dimension
     * @throws FormatException if dimension is not 1 mod 4
     */
    public static Version getProvisionalVersionForDimension(int dimension) throws FormatException {
        if (dimension % 4 != 1) {
            throw FormatException.getFormatInstance();
        }
        try {
            return getVersionForNumber((dimension - 17) / 4);
        } catch (IllegalArgumentException ignored) {
            throw FormatException.getFormatInstance();
        }
    }

    public static Version getVersionForNumber(int versionNumber) {
        if (versionNumber < 1 || versionNumber > 40) {
            throw new IllegalArgumentException();
        }
        return VERSIONS[versionNumber - 1];
    }

    static Version decodeVersionInformation(int versionBits) {
        int bestDifference = Integer.MAX_VALUE;
        int bestVersion = 0;
        for (int i = 0; i < VERSION_DECODE_INFO.length; i++) {
            int targetVersion = VERSION_DECODE_INFO[i];
            if (targetVersion == versionBits) {
                return getVersionForNumber(i + 7);
            }
            int bitsDifference = FormatInformation.numBitsDiffering(versionBits, targetVersion);
            if (bitsDifference < bestDifference) {
                bestVersion = i + 7;
                bestDifference = bitsDifference;
            }
        }
        if (bestDifference <= 3) {
            return getVersionForNumber(bestVersion);
        }
        return null;
    }

    BitMatrix buildFunctionPattern() {
        int dimension = getDimensionForVersion();
        BitMatrix bitMatrix = new BitMatrix(dimension);

        bitMatrix.setRegion(0, 0, 9, 9);
        bitMatrix.setRegion(dimension - 8, 0, 8, 9);
        bitMatrix.setRegion(0, dimension - 8, 9, 8);

        int max = alignmentPatternCenters.length;
        for (int x = 0; x < max; x++) {
            int i = alignmentPatternCenters[x] - 2;
            for (int y = 0; y < max; y++) {
                if ((x == 0 && (y == 0 || y == max - 1)) || (x == max - 1 && y == 0)) {
                    continue;
                }
                bitMatrix.setRegion(alignmentPatternCenters[y] - 2, i, 5, 5);
            }
        }

        bitMatrix.setRegion(6, 9, 1, dimension - 17);
        bitMatrix.setRegion(9, 6, dimension - 17, 1);

        if (versionNumber > 6) {
            bitMatrix.setRegion(dimension - 11, 0, 3, 6);
            bitMatrix.setRegion(0, dimension - 11, 6, 3);
        }

        return bitMatrix;
    }

    public static final class ECBlocks {
        private final int ecCodewordsPerBlock;
        private final ECB[] ecBlocks;

        ECBlocks(int ecCodewordsPerBlock, ECB... ecBlocks) {
            this.ecCodewordsPerBlock = ecCodewordsPerBlock;
            this.ecBlocks = ecBlocks;
        }

        public int getECCodewordsPerBlock() {
            return ecCodewordsPerBlock;
        }

        public int getNumBlocks() {
            int total = 0;
            for (ECB ecBlock : ecBlocks) {
                total += ecBlock.getCount();
            }
            return total;
        }

        public int getTotalECCodewords() {
            return ecCodewordsPerBlock * getNumBlocks();
        }

        public ECB[] getECBlocks() {
            return ecBlocks;
        }
    }

    public static final class ECB {
        private final int count;
        private final int dataCodewords;

        ECB(int count, int dataCodewords) {
            this.count = count;
            this.dataCodewords = dataCodewords;
        }

        public int getCount() {
            return count;
        }

        public int getDataCodewords() {
            return dataCodewords;
        }
    }

    @Override
    public String toString() {
        return String.valueOf(versionNumber);
    }

    private static Version[] buildVersions() {
        return new Version[]{
                new Version(1, new int[]{},
                        new ECBlocks(7, new ECB(1, 19)),
                        new ECBlocks(10, new ECB(1, 16)),
                        new ECBlocks(13, new ECB(1, 13)),
                        new ECBlocks(17, new ECB(1, 9))),
                new Version(2, new int[]{6, 18},
                        new ECBlocks(10, new ECB(1, 34)),
                        new ECBlocks(16, new ECB(1, 28)),
                        new ECBlocks(22, new ECB(1, 22)),
                        new ECBlocks(28, new ECB(1, 16))),
                new Version(3, new int[]{6, 22},
                        new ECBlocks(15, new ECB(1, 55)),
                        new ECBlocks(26, new ECB(1, 44)),
                        new ECBlocks(18, new ECB(2, 17)),
                        new ECBlocks(22, new ECB(2, 13))),
                new Version(4, new int[]{6, 26},
                        new ECBlocks(20, new ECB(1, 80)),
                        new ECBlocks(18, new ECB(2, 32)),
                        new ECBlocks(26, new ECB(2, 24)),
                        new ECBlocks(16, new ECB(4, 9))),
                new Version(5, new int[]{6, 30},
                        new ECBlocks(26, new ECB(1, 108)),
                        new ECBlocks(24, new ECB(2, 43)),
                        new ECBlocks(18, new ECB(2, 15),
                                new ECB(2, 16)),
                        new ECBlocks(22, new ECB(2, 11),
                                new ECB(2, 12))),
                new Version(6, new int[]{6, 34},
                        new ECBlocks(18, new ECB(2, 68)),
                        new ECBlocks(16, new ECB(4, 27)),
                        new ECBlocks(24, new ECB(4, 19)),
                        new ECBlocks(28, new ECB(4, 15))),
                new Version(7, new int[]{6, 22, 38},
                        new ECBlocks(20, new ECB(2, 78)),
                        new ECBlocks(18, new ECB(4, 31)),
                        new ECBlocks(18, new ECB(2, 14),
                                new ECB(4, 15)),
                        new ECBlocks(26, new ECB(4, 13),
                                new ECB(1, 14))),
                new Version(8, new int[]{6, 24, 42},
                        new ECBlocks(24, new ECB(2, 97)),
                        new ECBlocks(22, new ECB(2, 38),
                                new ECB(2, 39)),
                        new ECBlocks(22, new ECB(4, 18),
                                new ECB(2, 19)),
                        new ECBlocks(26, new ECB(4, 14),
                                new ECB(2, 15))),
                new Version(9, new int[]{6, 26, 46},
                        new ECBlocks(30, new ECB(2, 116)),
                        new ECBlocks(22, new ECB(3, 36),
                                new ECB(2, 37)),
                        new ECBlocks(20, new ECB(4, 16),
                                new ECB(4, 17)),
                        new ECBlocks(24, new ECB(4, 12),
                                new ECB(4, 13))),
                new Version(10, new int[]{6, 28, 50},
                        new ECBlocks(18, new ECB(2, 68),
                                new ECB(2, 69)),
                        new ECBlocks(26, new ECB(4, 43),
                                new ECB(1, 44)),
                        new ECBlocks(24, new ECB(6, 19),
                                new ECB(2, 20)),
                        new ECBlocks(28, new ECB(6, 15),
                                new ECB(2, 16))),
                new Version(11, new int[]{6, 30, 54},
                        new ECBlocks(20, new ECB(4, 81)),
                        new ECBlocks(30, new ECB(1, 50),
                                new ECB(4, 51)),
                        new ECBlocks(28, new ECB(4, 22),
                                new ECB(4, 23)),
                        new ECBlocks(24, new ECB(3, 12),
                                new ECB(8, 13))),
                new Version(12, new int[]{6, 32, 58},
                        new ECBlocks(24, new ECB(2, 92),
                                new ECB(2, 93)),
                        new ECBlocks(22, new ECB(6, 36),
                                new ECB(2, 37)),
                        new ECBlocks(26, new ECB(4, 20),
                                new ECB(6, 21)),
                        new ECBlocks(28, new ECB(7, 14),
                                new ECB(4, 15))),
                new Version(13, new int[]{6, 34, 62},
                        new ECBlocks(26, new ECB(4, 107)),
                        new ECBlocks(22, new ECB(8, 37),
                                new ECB(1, 38)),
                        new ECBlocks(24, new ECB(8, 20),
                                new ECB(4, 21)),
                        new ECBlocks(22, new ECB(12, 11),
                                new ECB(4, 12))),
                new Version(14, new int[]{6, 26, 46, 66},
                        new ECBlocks(30, new ECB(3, 115),
                                new ECB(1, 116)),
                        new ECBlocks(24, new ECB(4, 40),
                                new ECB(5, 41)),
                        new ECBlocks(20, new ECB(11, 16),
                                new ECB(5, 17)),
                        new ECBlocks(24, new ECB(11, 12),
                                new ECB(5, 13))),
                new Version(15, new int[]{6, 26, 48, 70},
                        new ECBlocks(22, new ECB(5, 87),
                                new ECB(1, 88)),
                        new ECBlocks(24, new ECB(5, 41),
                                new ECB(5, 42)),
                        new ECBlocks(30, new ECB(5, 24),
                                new ECB(7, 25)),
                        new ECBlocks(24, new ECB(11, 12),
                                new ECB(7, 13))),
                new Version(16, new int[]{6, 26, 50, 74},
                        new ECBlocks(24, new ECB(5, 98),
                                new ECB(1, 99)),
                        new ECBlocks(28, new ECB(7, 45),
                                new ECB(3, 46)),
                        new ECBlocks(24, new ECB(15, 19),
                                new ECB(2, 20)),
                        new ECBlocks(30, new ECB(3, 15),
                                new ECB(13, 16))),
                new Version(17, new int[]{6, 30, 54, 78},
                        new ECBlocks(28, new ECB(1, 107),
                                new ECB(5, 108)),
                        new ECBlocks(28, new ECB(10, 46),
                                new ECB(1, 47)),
                        new ECBlocks(28, new ECB(1, 22),
                                new ECB(15, 23)),
                        new ECBlocks(28, new ECB(2, 14),
                                new ECB(17, 15))),
                new Version(18, new int[]{6, 30, 56, 82},
                        new ECBlocks(30, new ECB(5, 120),
                                new ECB(1, 121)),
                        new ECBlocks(26, new ECB(9, 43),
                                new ECB(4, 44)),
                        new ECBlocks(28, new ECB(17, 22),
                                new ECB(1, 23)),
                        new ECBlocks(28, new ECB(2, 14),
                                new ECB(19, 15))),
                new Version(19, new int[]{6, 30, 58, 86},
                        new ECBlocks(28, new ECB(3, 113),
                                new ECB(4, 114)),
                        new ECBlocks(26, new ECB(3, 44),
                                new ECB(11, 45)),
                        new ECBlocks(26, new ECB(17, 21),
                                new ECB(4, 22)),
                        new ECBlocks(26, new ECB(9, 13),
                                new ECB(16, 14))),
                new Version(20, new int[]{6, 34, 62, 90},
                        new ECBlocks(28, new ECB(3, 107),
                                new ECB(5, 108)),
                        new ECBlocks(26, new ECB(3, 41),
                                new ECB(13, 42)),
                        new ECBlocks(30, new ECB(15, 24),
                                new ECB(5, 25)),
                        new ECBlocks(28, new ECB(15, 15),
                                new ECB(10, 16))),
                new Version(21, new int[]{6, 28, 50, 72, 94},
                        new ECBlocks(28, new ECB(4, 116),
                                new ECB(4, 117)),
                        new ECBlocks(26, new ECB(17, 42)),
                        new ECBlocks(28, new ECB(17, 22),
                                new ECB(6, 23)),
                        new ECBlocks(30, new ECB(19, 16),
                                new ECB(6, 17))),
                new Version(22, new int[]{6, 26, 50, 74, 98},
                        new ECBlocks(28, new ECB(2, 111),
                                new ECB(7, 112)),
                        new ECBlocks(28, new ECB(17, 46)),
                        new ECBlocks(30, new ECB(7, 24),
                                new ECB(16, 25)),
                        new ECBlocks(24, new ECB(34, 13))),
                new Version(23, new int[]{6, 30, 54, 78, 102},
                        new ECBlocks(30, new ECB(4, 121),
                                new ECB(5, 122)),
                        new ECBlocks(28, new ECB(4, 47),
                                new ECB(14, 48)),
                        new ECBlocks(30, new ECB(11, 24),
                                new ECB(14, 25)),
                        new ECBlocks(30, new ECB(16, 15),
                                new ECB(14, 16))),
                new Version(24, new int[]{6, 28, 54, 80, 106},
                        new ECBlocks(30, new ECB(6, 117),
                                new ECB(4, 118)),
                        new ECBlocks(28, new ECB(6, 45),
                                new ECB(14, 46)),
                        new ECBlocks(30, new ECB(11, 24),
                                new ECB(16, 25)),
                        new ECBlocks(30, new ECB(30, 16),
                                new ECB(2, 17))),
                new Version(25, new int[]{6, 32, 58, 84, 110},
                        new ECBlocks(26, new ECB(8, 106),
                                new ECB(4, 107)),
                        new ECBlocks(28, new ECB(8, 47),
                                new ECB(13, 48)),
                        new ECBlocks(30, new ECB(7, 24),
                                new ECB(22, 25)),
                        new ECBlocks(30, new ECB(22, 15),
                                new ECB(13, 16))),
                new Version(26, new int[]{6, 30, 58, 86, 114},
                        new ECBlocks(28, new ECB(10, 114),
                                new ECB(2, 115)),
                        new ECBlocks(28, new ECB(19, 46),
                                new ECB(4, 47)),
                        new ECBlocks(28, new ECB(28, 22),
                                new ECB(6, 23)),
                        new ECBlocks(30, new ECB(33, 16),
                                new ECB(4, 17))),
                new Version(27, new int[]{6, 34, 62, 90, 118},
                        new ECBlocks(30, new ECB(8, 122),
                                new ECB(4, 123)),
                        new ECBlocks(28, new ECB(22, 45),
                                new ECB(3, 46)),
                        new ECBlocks(30, new ECB(8, 23),
                                new ECB(26, 24)),
                        new ECBlocks(30, new ECB(12, 15),
                                new ECB(28, 16))),
                new Version(28, new int[]{6, 26, 50, 74, 98, 122},
                        new ECBlocks(30, new ECB(3, 117),
                                new ECB(10, 118)),
                        new ECBlocks(28, new ECB(3, 45),
                                new ECB(23, 46)),
                        new ECBlocks(30, new ECB(4, 24),
                                new ECB(31, 25)),
                        new ECBlocks(30, new ECB(11, 15),
                                new ECB(31, 16))),
                new Version(29, new int[]{6, 30, 54, 78, 102, 126},
                        new ECBlocks(30, new ECB(7, 116),
                                new ECB(7, 117)),
                        new ECBlocks(28, new ECB(21, 45),
                                new ECB(7, 46)),
                        new ECBlocks(30, new ECB(1, 23),
                                new ECB(37, 24)),
                        new ECBlocks(30, new ECB(19, 15),
                                new ECB(26, 16))),
                new Version(30, new int[]{6, 26, 52, 78, 104, 130},
                        new ECBlocks(30, new ECB(5, 115),
                                new ECB(10, 116)),
                        new ECBlocks(28, new ECB(19, 47),
                                new ECB(10, 48)),
                        new ECBlocks(30, new ECB(15, 24),
                                new ECB(25, 25)),
                        new ECBlocks(30, new ECB(23, 15),
                                new ECB(25, 16))),
                new Version(31, new int[]{6, 30, 56, 82, 108, 134},
                        new ECBlocks(30, new ECB(13, 115),
                                new ECB(3, 116)),
                        new ECBlocks(28, new ECB(2, 46),
                                new ECB(29, 47)),
                        new ECBlocks(30, new ECB(42, 24),
                                new ECB(1, 25)),
                        new ECBlocks(30, new ECB(23, 15),
                                new ECB(28, 16))),
                new Version(32, new int[]{6, 34, 60, 86, 112, 138},
                        new ECBlocks(30, new ECB(17, 115)),
                        new ECBlocks(28, new ECB(10, 46),
                                new ECB(23, 47)),
                        new ECBlocks(30, new ECB(10, 24),
                                new ECB(35, 25)),
                        new ECBlocks(30, new ECB(19, 15),
                                new ECB(35, 16))),
                new Version(33, new int[]{6, 30, 58, 86, 114, 142},
                        new ECBlocks(30, new ECB(17, 115),
                                new ECB(1, 116)),
                        new ECBlocks(28, new ECB(14, 46),
                                new ECB(21, 47)),
                        new ECBlocks(30, new ECB(29, 24),
                                new ECB(19, 25)),
                        new ECBlocks(30, new ECB(11, 15),
                                new ECB(46, 16))),
                new Version(34, new int[]{6, 34, 62, 90, 118, 146},
                        new ECBlocks(30, new ECB(13, 115),
                                new ECB(6, 116)),
                        new ECBlocks(28, new ECB(14, 46),
                                new ECB(23, 47)),
                        new ECBlocks(30, new ECB(44, 24),
                                new ECB(7, 25)),
                        new ECBlocks(30, new ECB(59, 16),
                                new ECB(1, 17))),
                new Version(35, new int[]{6, 30, 54, 78, 102, 126, 150},
                        new ECBlocks(30, new ECB(12, 121),
                                new ECB(7, 122)),
                        new ECBlocks(28, new ECB(12, 47),
                                new ECB(26, 48)),
                        new ECBlocks(30, new ECB(39, 24),
                                new ECB(14, 25)),
                        new ECBlocks(30, new ECB(22, 15),
                                new ECB(41, 16))),
                new Version(36, new int[]{6, 24, 50, 76, 102, 128, 154},
                        new ECBlocks(30, new ECB(6, 121),
                                new ECB(14, 122)),
                        new ECBlocks(28, new ECB(6, 47),
                                new ECB(34, 48)),
                        new ECBlocks(30, new ECB(46, 24),
                                new ECB(10, 25)),
                        new ECBlocks(30, new ECB(2, 15),
                                new ECB(64, 16))),
                new Version(37, new int[]{6, 28, 54, 80, 106, 132, 158},
                        new ECBlocks(30, new ECB(17, 122),
                                new ECB(4, 123)),
                        new ECBlocks(28, new ECB(29, 46),
                                new ECB(14, 47)),
                        new ECBlocks(30, new ECB(49, 24),
                                new ECB(10, 25)),
                        new ECBlocks(30, new ECB(24, 15),
                                new ECB(46, 16))),
                new Version(38, new int[]{6, 32, 58, 84, 110, 136, 162},
                        new ECBlocks(30, new ECB(4, 122),
                                new ECB(18, 123)),
                        new ECBlocks(28, new ECB(13, 46),
                                new ECB(32, 47)),
                        new ECBlocks(30, new ECB(48, 24),
                                new ECB(14, 25)),
                        new ECBlocks(30, new ECB(42, 15),
                                new ECB(32, 16))),
                new Version(39, new int[]{6, 26, 54, 82, 110, 138, 166},
                        new ECBlocks(30, new ECB(20, 117),
                                new ECB(4, 118)),
                        new ECBlocks(28, new ECB(40, 47),
                                new ECB(7, 48)),
                        new ECBlocks(30, new ECB(43, 24),
                                new ECB(22, 25)),
                        new ECBlocks(30, new ECB(10, 15),
                                new ECB(67, 16))),
                new Version(40, new int[]{6, 30, 58, 86, 114, 142, 170},
                        new ECBlocks(30, new ECB(19, 118),
                                new ECB(6, 119)),
                        new ECBlocks(28, new ECB(18, 47),
                                new ECB(31, 48)),
                        new ECBlocks(30, new ECB(34, 24),
                                new ECB(34, 25)),
                        new ECBlocks(30, new ECB(20, 15),
                                new ECB(61, 16)))
        };
    }
}

final class FormatInformation {

    private static final int FORMAT_INFO_MASK_QR = 0x5412;

    /**
     * See ISO 18004:2006, Annex C, Table C.1
     */
    private static final int[][] FORMAT_INFO_DECODE_LOOKUP = {
            {0x5412, 0x00},
            {0x5125, 0x01},
            {0x5E7C, 0x02},
            {0x5B4B, 0x03},
            {0x45F9, 0x04},
            {0x40CE, 0x05},
            {0x4F97, 0x06},
            {0x4AA0, 0x07},
            {0x77C4, 0x08},
            {0x72F3, 0x09},
            {0x7DAA, 0x0A},
            {0x789D, 0x0B},
            {0x662F, 0x0C},
            {0x6318, 0x0D},
            {0x6C41, 0x0E},
            {0x6976, 0x0F},
            {0x1689, 0x10},
            {0x13BE, 0x11},
            {0x1CE7, 0x12},
            {0x19D0, 0x13},
            {0x0762, 0x14},
            {0x0255, 0x15},
            {0x0D0C, 0x16},
            {0x083B, 0x17},
            {0x355F, 0x18},
            {0x3068, 0x19},
            {0x3F31, 0x1A},
            {0x3A06, 0x1B},
            {0x24B4, 0x1C},
            {0x2183, 0x1D},
            {0x2EDA, 0x1E},
            {0x2BED, 0x1F},
    };

    private final ErrorCorrectionLevel errorCorrectionLevel;
    private final byte dataMask;

    private FormatInformation(int formatInfo) {
        // Bits 3,4
        errorCorrectionLevel = ErrorCorrectionLevel.forBits((formatInfo >> 3) & 0x03);
        // Bottom 3 bits
        dataMask = (byte) (formatInfo & 0x07);
    }

    static int numBitsDiffering(int a, int b) {
        return Integer.bitCount(a ^ b);
    }

    /**
     * @param maskedFormatInfo1 format info indicator, with mask still applied
     * @param maskedFormatInfo2 second copy of same info; both are checked at the same time
     *                          to establish best match
     * @return information about the format it specifies, or {@code null}
     * if doesn't seem to match any known pattern
     */
    static FormatInformation decodeFormatInformation(int maskedFormatInfo1, int maskedFormatInfo2) {
        FormatInformation formatInfo = doDecodeFormatInformation(maskedFormatInfo1, maskedFormatInfo2);
        if (formatInfo != null) {
            return formatInfo;
        }
        // Should return null, but, some QR codes apparently
        // do not mask this info. Try again by actually masking the pattern
        // first
        return doDecodeFormatInformation(maskedFormatInfo1 ^ FORMAT_INFO_MASK_QR,
                maskedFormatInfo2 ^ FORMAT_INFO_MASK_QR);
    }

    private static FormatInformation doDecodeFormatInformation(int maskedFormatInfo1, int maskedFormatInfo2) {
        // Find the int in FORMAT_INFO_DECODE_LOOKUP with fewest bits differing
        int bestDifference = Integer.MAX_VALUE;
        int bestFormatInfo = 0;
        for (int[] decodeInfo : FORMAT_INFO_DECODE_LOOKUP) {
            int targetInfo = decodeInfo[0];
            if (targetInfo == maskedFormatInfo1 || targetInfo == maskedFormatInfo2) {
                // Found an exact match
                return new FormatInformation(decodeInfo[1]);
            }
            int bitsDifference = numBitsDiffering(maskedFormatInfo1, targetInfo);
            if (bitsDifference < bestDifference) {
                bestFormatInfo = decodeInfo[1];
                bestDifference = bitsDifference;
            }
            if (maskedFormatInfo1 != maskedFormatInfo2) {
                // also try the other option
                bitsDifference = numBitsDiffering(maskedFormatInfo2, targetInfo);
                if (bitsDifference < bestDifference) {
                    bestFormatInfo = decodeInfo[1];
                    bestDifference = bitsDifference;
                }
            }
        }
        // Hamming distance of the 32 masked codes is 7, by construction, so <= 3 bits
        // differing means we found a match
        if (bestDifference <= 3) {
            return new FormatInformation(bestFormatInfo);
        }
        return null;
    }

    ErrorCorrectionLevel getErrorCorrectionLevel() {
        return errorCorrectionLevel;
    }

    byte getDataMask() {
        return dataMask;
    }

    @Override
    public int hashCode() {
        return (errorCorrectionLevel.ordinal() << 3) | dataMask;
    }

    @Override
    public boolean equals(Object o) {
        if (!(o instanceof FormatInformation)) {
            return false;
        }
        FormatInformation other = (FormatInformation) o;
        return this.errorCorrectionLevel == other.errorCorrectionLevel &&
                this.dataMask == other.dataMask;
    }

}