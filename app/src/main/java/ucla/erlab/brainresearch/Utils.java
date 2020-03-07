package ucla.erlab.brainresearch;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

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

    public static ByteReadResult extractResponse(byte[] src, int offset) {
        ByteReadResult result = new ByteReadResult();

        int targetStartIdx = 0;
        int targetLen = 0;

        boolean foundSTX = false;
        boolean foundETX = false;
        int i = 0;
        for ( ; i < offset && !foundETX; ++i) {
            if (!foundSTX) {
                if (src[i] == 0x02) {
                    targetStartIdx = i;
                    foundSTX = true;
                }
            }
            if (foundSTX) {
                targetLen++;
                if (src[i] == 0x03) {
                    foundETX = true;
                }
            }
        }

        //Log.e("BR", "extractResponse " + bytesToHex(src));
        //Log.e("BR", "extractResponse " + targetStartIdx + " " + targetLen);
        if (foundSTX && foundETX) {
            byte[] target = new byte[targetLen];
            for (int j = targetStartIdx, k = 0; j < i; ++j, ++k) {
                target[k] = src[j];
            }

            result.success = true;
            result.byteRead = i;
            result.result = target;
        }

        return result;
    }

    public static class Format13Data {
        public int pulse = 0;
        public int spo2 = 0;
    }

    public static Format13Data parseData13(byte[] src) {
        final int BYTE_OFFSET = 2;
        final int MSB_PULSE_IDX = 17;
        final int LSB_PULSE_IDX = 18;
        final int SPO2_IDX = 20;

        int pulse = 0;
        pulse |= src[MSB_PULSE_IDX - BYTE_OFFSET] & 0x01;
        pulse = pulse << 8;
        pulse |= src[LSB_PULSE_IDX - BYTE_OFFSET] & 0xFF;

        int spo2 = src[SPO2_IDX - BYTE_OFFSET];

        Log.w("BR", "parseData13 - pulse " + pulse + " spo2 " + spo2);

        Format13Data result = new Format13Data();
        result.pulse = pulse;
        result.spo2 = spo2;
        return result;
    }


    public static class SettingData {
        public int subjectId;
        public int sex; // 0 : male, 1 : female
        public boolean menstruating;
        public int protocol; // aligned with spinner array
        public String group;
        public int daycount;
        public boolean testingmode;
    }

    public static SettingData getSettingData(Context ctx) {
        SharedPreferences pref = ctx.getSharedPreferences(Config.PREF_SETTING, 0); // 0 - for private mode
        SettingData result = new SettingData();
        result.subjectId = pref.getInt(Config.PREF_SETTING_SUBJECT_ID, 0);
        result.sex = pref.getInt(Config.PREF_SETTING_SEX, 0);
        result.menstruating = pref.getBoolean(Config.PREF_SETTING_MENSTRUATING, false);
        result.protocol = pref.getInt(Config.PREF_SETTING_PROTOCOL, 0);
        result.group = pref.getString(Config.PREF_SETTING_GROUP, "");
        result.daycount = pref.getInt(Config.PREF_SETTING_DAY_COUNT, 1);
        result.testingmode = pref.getBoolean(Config.PREF_SETTING_TESTING_MODE, false);

        return result;
    }

    public static void setSettingData(Context ctx, SettingData data) {
        SharedPreferences pref = ctx.getSharedPreferences(Config.PREF_SETTING, 0); // 0 - for private mode
        SharedPreferences.Editor editor = pref.edit();

        editor.putInt(Config.PREF_SETTING_SUBJECT_ID, data.subjectId);
        editor.putInt(Config.PREF_SETTING_SEX, data.sex);
        editor.putBoolean(Config.PREF_SETTING_MENSTRUATING, data.menstruating);
        editor.putInt(Config.PREF_SETTING_PROTOCOL, data.protocol);
        editor.putString(Config.PREF_SETTING_GROUP, data.group);
        editor.putInt(Config.PREF_SETTING_DAY_COUNT, data.daycount);
        editor.putBoolean(Config.PREF_SETTING_TESTING_MODE, data.testingmode);

        editor.apply();
    }
}
