package ucla.erlab.brainresearch;

public class Utils {
    private static final char[] HEX_ARRAY = "0123456789ABCDEF".toCharArray();
    public static String bytesToHex(byte[] bytes) {
        char[] hexChars = new char[bytes.length * 2];
        for (int j = 0; j < bytes.length; j++) {
            int v = bytes[j] & 0xFF;
            hexChars[j * 2] = HEX_ARRAY[v >>> 4];
            hexChars[j * 2 + 1] = HEX_ARRAY[v & 0x0F];
        }
        return new String(hexChars);
    }
    public static String bytesToHex(byte[] bytes, int length) {
        char[] hexChars = new char[length * 2];
        for (int j = 0; j < length; j++) {
            int v = bytes[j] & 0xFF;
            hexChars[j * 2] = HEX_ARRAY[v >>> 4];
            hexChars[j * 2 + 1] = HEX_ARRAY[v & 0x0F];
        }
        return new String(hexChars);
    }
    public static byte[] copyByteArr(byte[] src, int startIdx, int endIdx, int fillIdx) {
        byte[] result = new byte[src.length];
        for (int i = startIdx, j = fillIdx; i < endIdx; ++i) {
            result[j] = src[i];
        }
        return result;
    }

    public static class ByteReadResult {
        public boolean success = false;
        public int byteRead = 0;
        byte[] result = null;
    }

    public static ByteReadResult extractResponse(byte[] src, int offset, int targetLength) {
        ByteReadResult result = new ByteReadResult();

        byte[] target = new byte[targetLength];
        int targetIdx = 0;

        boolean foundSTX = false;
        boolean foundETX = false;
        int i = 0;
        for ( ; i < offset && !foundETX; ++i) {
            if (!foundSTX) {
                if (src[i] == 0x02) {
                    foundSTX = true;
                }
            }

            if (foundSTX) {
                target[targetIdx++] = src[i];
                if (src[i] == 0x03) {
                    foundETX = true;
                }
            }
        }

        if (foundSTX && foundETX) {
            result.success = true;
            result.byteRead = i;
            result.result = target;
        }

        return result;
    }
}
