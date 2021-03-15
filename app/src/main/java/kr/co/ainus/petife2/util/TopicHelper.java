package kr.co.ainus.petife2.util;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

import java.util.HashMap;
import java.util.Map;

public class TopicHelper {
    private static final String TAG = "TopicHelper";
    private static SharedPreferences sharedPreferences;

    private static SharedPreferences getSharedPreferences(Context context) {
        return context.getSharedPreferences("topic", Context.MODE_PRIVATE);
    }

    private static SharedPreferences.Editor getSharedPreferenceEditor(Context context) {
        return getSharedPreferences(context).edit();
    }

    public static boolean getBoolean(Context context, String key) {
        return getSharedPreferences(context).getBoolean(key, false);
    }

    public static void putBoolean(Context context, String key, boolean value) {
        getSharedPreferenceEditor(context).putBoolean(key, value).apply();
        Log.i(TAG, key + " ---> " + value);
    }

    public static void allFalse(Context context) {
        for (Map.Entry<String, ?> entry : getSharedPreferences(context).getAll().entrySet()) {
            if (entry.getValue() instanceof Boolean) {
                putBoolean(context, entry.getKey(), false);
            }
        }
    }

    public static HashMap<String, Boolean> getBooleans(Context context) {

        HashMap<String, Boolean> result = new HashMap<>();

        for (Map.Entry<String, ?> entry : getSharedPreferences(context).getAll().entrySet()) {
            if (entry.getValue() instanceof Boolean) {
                result.put(entry.getKey(), (Boolean) entry.getValue());
            }
        }

        return result;
    }
}
