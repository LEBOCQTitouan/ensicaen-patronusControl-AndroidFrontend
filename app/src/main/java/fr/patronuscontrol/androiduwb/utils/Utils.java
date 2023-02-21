package fr.patronuscontrol.androiduwb.utils;

public class Utils {

    /**
     * Convert a byte array to a hex string
     *
     * @param data the byte array to convert
     * @return the hex string
     */
    public static String byteArrayToHexString(byte[] data) {
        if (data == null) {
            throw new IllegalArgumentException("Null input");
        }
        final char[] hexArray = {'0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'A', 'B', 'C', 'D', 'E', 'F'};
        char[] hexChars = new char[data.length * 2];
        int v;
        for (int j = 0; j < data.length; j++) {
            v = data[j] & 0xFF;
            hexChars[j * 2] = hexArray[v >>> 4];
            hexChars[j * 2 + 1] = hexArray[v & 0x0F];
        }
        return new String(hexChars);
    }

    /**
     * Trim leading bytes from a byte array
     * @param data the byte array to trim
     * @param amountOfBytesToTrim the amount of bytes to trim
     * @return the trimmed byte array
     */
    public static byte[] trimLeadingBytes(byte[] data, int amountOfBytesToTrim) {
        byte[] result = new byte[data.length - amountOfBytesToTrim];
        System.arraycopy(data, amountOfBytesToTrim, result, 0, data.length - amountOfBytesToTrim);
        return result;
    }

    public static byte[] concat(byte[] b1, byte[] b2) {
        if (b1 == null) {
            return b2;
        } else if (b2 == null) {
            return b1;
        } else {
            byte[] result = new byte[b1.length + b2.length];
            System.arraycopy(b1, 0, result, 0, b1.length);
            System.arraycopy(b2, 0, result, b1.length, b2.length);
            return result;
        }
    }

    /**
     * Revert a byte array
     * @param data the byte array to revert
     * @return the reverted byte array
     */
    public static byte[] revert(byte[] data) {
        byte[] result = new byte[data.length];
        for (int i = 0; i < data.length; i++) {
            result[i] = data[data.length - i - 1];
        }
        return result;
    }

    /**
     * Convert a short to a byte array
     * @param data the short to convert
     * @return the byte array
     */
    public static byte[] shortToByteArray(short data) {
        return new byte[]{(byte) (data & 0xff), (byte) ((data >> 8) & 0xff)};
    }

    /**
     * Extract data from a byte array
     * @param buffer the byte array to extract from
     * @param length the length of the data to extract
     * @param offset the offset of the data to extract
     * @return the extracted data
     */
    public static byte[] extract(byte[] buffer, int length, int offset) {
        byte[] result = new byte[length];
        System.arraycopy(buffer, offset, result, 0, length);
        return result;
    }

    /**
     * Convert a int to a byte array
     * @param data the int to convert
     * @return the byte array
     */
    public static byte[] intToByteArray(int data) {
        byte[] result = new byte[4];
        result[3] = (byte) (data & 0xFF);
        result[2] = (byte) ((data >> 8) & 0xFF);
        result[1] = (byte) ((data >> 16) & 0xFF);
        result[0] = (byte) ((data >> 24) & 0xFF);
        return result;
    }

    /**
     * Convert a byte to a byte array
     * @param value the byte to convert
     * @return the byte array
     */
    public static byte[] byteToByteArray(byte value) {
        return new byte[]{(byte) (value & 0xff)};
    }

    /**
     * Convert a byte array to a short
     * @param extract
     * @return
     */
    public static short byteArrayToShort(byte[] extract) {
        return (short) ((extract[0] & 0xff) | (extract[1] & 0xff) << 8);
    }

    /**
     * Convert a byte array to a byte
     * @param extract the byte array to convert
     * @return the byte
     */
    public static byte byteArrayToByte(byte[] extract) {
        if (extract.length != 1) {
            throw new IllegalArgumentException("Byte array must be of length 1");
        }
        return extract[0];
    }

    /**
     * Convert a byte array to a int
     * @param b the byte array to convert
     * @return the int
     */
    public static int byteArrayToInt(byte[] b) {
        switch (b.length) {
            case 1 :
                return b[0] & 0xff;
            case 2 :
                return (b[1] & 0xff) + ((b[0] & 0xff) << 8);
            case 3 :
                return (b[2] & 0xff) + ((b[1] & 0xff) << 8) + ((b[0] & 0xff) << 16);
            case 4 :
                return (b[3] & 0xff) + ((b[2] & 0xff) << 8) + ((b[1] & 0xff) << 16) + ((b[0] & 0xff) << 24);
            default:
                throw new IllegalArgumentException("Byte array must be of length 1, 2, 3 or 4");
        }
    }
}
