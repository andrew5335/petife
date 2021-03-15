package kr.co.ainus.petife2.util;

import android.util.Log;

public class RandomPasswordHelper {
    private static final String TAG = "RandomPasswordHelper";

    public static String make() {
        
        String tempPassword = "";
        for (int i = 0; i < 8; i++) {
            int rndVal = (int) (Math.random() * 62);
            if (rndVal < 10) {
                tempPassword += rndVal;
            } else if (rndVal > 35) {
                tempPassword += (char) (rndVal + 61);
            } else {
                tempPassword += (char) (rndVal + 55);
            }
        }
        Log.i(TAG, "tempPassword : " + tempPassword);

        return tempPassword;

    }

}
