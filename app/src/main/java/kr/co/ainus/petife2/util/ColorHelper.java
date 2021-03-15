package kr.co.ainus.petife2.util;

import android.util.Log;

import kr.co.ainus.petife2.R;

public class ColorHelper {
    public static int convertColorByDayOfWeek(int dayOfWeek) {
        final String TAG = "ColorHelper";
        Log.i(TAG, "dayOfWeek = " + dayOfWeek);

        switch (dayOfWeek) {
            case 0:
                return R.color.sunday;

            case 6:
                return R.color.sathurday;

            default:
                return R.color.day_normal;
        }
    }
}
