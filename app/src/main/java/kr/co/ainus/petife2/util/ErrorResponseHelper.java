package kr.co.ainus.petife2.util;

import android.content.Context;
import android.widget.Toast;

public class ErrorResponseHelper {
    private final ErrorResponseHelper INSTANCE = new ErrorResponseHelper();

    private ErrorResponseHelper() {}

    public static void showError(Context context, int reason) {
        String message = null;
        switch (reason) {
            case 100:
                message = "다른 기기에서 로그인되었습니다. 사용을 원하시면 다시 로그인하세요";
                break;

            case 101:
                message = "이미 등록된 email 입니다. 이전에 이용한 sns를 통해 로그인하세요.";
                break;
        }

        Toast.makeText(context, message, Toast.LENGTH_SHORT).show();
    }
}
