package com.gottado.utilities;

/**
 * an override of the Android Log utilities class
 * @author drakuwa
 *
 */
public class Log {

    private static boolean isEnabled = true;
    public static String TAG = "GottaDO";

    public static void d(String tag, String message) {
        if (isEnabled) {
            android.util.Log.d(tag, message);

        }
    }

    public static void i(String tag, String message) {
        if (isEnabled) {
            android.util.Log.i(tag, message);
        }
    }

    public static void e(String tag, String message) {
        if (isEnabled) {
            android.util.Log.e(tag, message);
        }
    }

    public static void v(String tag, String message) {
        if (isEnabled) {
            android.util.Log.v(tag, message);
        }
    }

    public static void w(String tag, String message) {
        if (isEnabled) {
            android.util.Log.w(tag, message);
        }
    }
}
