package kr.co.ainus.petife2.util;

import android.util.Log;

public class CalendarHelper {
    public static String convertStringDayOfWeek(int dayOfWeek) {
        final String TAG = "CalendarHelper";
        Log.i(TAG, "dayOfWeek = " + dayOfWeek);

        switch (dayOfWeek) {
            case 0:
                return "일";

            case 1:
                return "월";

            case 2:
                return "화";

            case 3:
                return "수";

            case 4:
                return "목";

            case 5:
                return "금";

            case 6:
                return "토";

            default:
                return null;

        }
    }
}
