package kr.co.ainus.petife2.video;

import android.os.Build.VERSION;
import android.view.ViewTreeObserver;
import android.view.ViewTreeObserver.OnGlobalLayoutListener;

public class Compatibility {
    public Compatibility() {
    }

    public static void removeOnGlobalLayoutListener(ViewTreeObserver var0, OnGlobalLayoutListener var1) {
        if (VERSION.SDK_INT >= 16) {
            var0.removeOnGlobalLayoutListener(var1);
        } else {
            var0.removeGlobalOnLayoutListener(var1);
        }

    }
}