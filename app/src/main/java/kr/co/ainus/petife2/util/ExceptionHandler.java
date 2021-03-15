package kr.co.ainus.petife2.util;

import android.content.Context;
import android.util.Log;

public class ExceptionHandler implements Thread.UncaughtExceptionHandler {
    private final static String TAG = ExceptionHandler.class.getSimpleName();
    private final static String ERROR_FILE = Exception.class.getSimpleName() + ".error";

    private final Context context;
    private final Thread.UncaughtExceptionHandler rootHandler;

    public ExceptionHandler(Context context) {
        this.context = context;
        // we should store the current exception handler -- to invoke it for all not handled exceptions ...
        rootHandler = Thread.getDefaultUncaughtExceptionHandler();
        // we replace the exception handler now with us -- we will properly dispatch the exceptions ...
        Thread.setDefaultUncaughtExceptionHandler(this);
    }

    @Override
    public void uncaughtException(final Thread thread, final Throwable ex) {
        try {

            Log.i(TAG, "called for " + ex.getClass());

        } catch (Exception e) {
            Log.e(TAG, "Exception Logger failed!", e);
        }
    }
}