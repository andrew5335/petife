package kr.co.ainus.petife2.util;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

public class SharedPreferencesHelper {
    private static final String TAG = "SharedPreferencesHelper";
    private static SharedPreferences sharedPreferences;

    private static SharedPreferences getSharedPreferences(Context context) {
        return context.getSharedPreferences("petica", Context.MODE_PRIVATE);
    }

    private static SharedPreferences.Editor getSharedPreferenceEditor(Context context) {
        return getSharedPreferences(context).edit();
    }

    public static String getString(Context context, String key) {
        return getSharedPreferences(context).getString(key, null);
    }

    public static void putString(Context context, String key, String value) {
        getSharedPreferenceEditor(context).putString(key, value).apply();
        Log.i(TAG, key + " ---> " + value);
    }

    public static int getInt(Context context, String key) {
        return getSharedPreferences(context).getInt(key, 0);
    }

    public static void putInt(Context context, String key, int value) {
        getSharedPreferenceEditor(context).putInt(key, value).apply();
        Log.i(TAG, key + " ---> " + value);
    }

    public static boolean getBoolean(Context context, String key) {
        return getSharedPreferences(context).getBoolean(key, false);
    }

    public static void putBoolean(Context context, String key, boolean value) {
        getSharedPreferenceEditor(context).putBoolean(key, value).apply();
        Log.i(TAG, key + " ---> " + value);
    }
}
