package kr.co.ainus.petife2.util;

import android.util.Log;

public class PingTestHelper {
    private static final String TAG = "PingTestHelper";

    public static PingTestHelper getNewInstance() {
        return new PingTestHelper();
    }

    private ResultLister resultLister;

    public void test() {
        String host = "8.8.8.8";
        String cmd = "ping -c 1 -W 10 " + host;
        try {
            Process proc = Runtime.getRuntime().exec(cmd);
            proc.waitFor();
            int result = proc.exitValue();
            Log.i(TAG, "result = " + result);

            switch (result) {
                case 0:
                    if (resultLister != null) resultLister.onResult(PingTestResultType.SUCCESS);
                    break;

                case 1:
                    if (resultLister != null) resultLister.onResult(PingTestResultType.FAIL);
                    break;

                case 2:
                    if (resultLister != null) resultLister.onResult(PingTestResultType.ERROR);
                    break;
            }

        } catch (Exception e) {
            Log.i(TAG, e.getMessage());
        }
    }

    public void setResultLister(ResultLister resultLister) {
        this.resultLister = resultLister;
    }

    public interface ResultLister {
        public void onResult(PingTestResultType pintTestResult);
    }

    public enum PingTestResultType {
        SUCCESS, FAIL, ERROR
    }
}
