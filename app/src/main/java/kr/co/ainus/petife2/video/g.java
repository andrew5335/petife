package kr.co.ainus.petife2.video;

import android.app.ActivityManager;
import android.content.Context;
import android.content.pm.ConfigurationInfo;

class g {
    static boolean a(Context var0) {
        ActivityManager var1 = (ActivityManager)var0.getSystemService("activity");
        ConfigurationInfo var2 = var1.getDeviceConfigurationInfo();
        return var2.reqGlEsVersion >= 131072;
    }
}
