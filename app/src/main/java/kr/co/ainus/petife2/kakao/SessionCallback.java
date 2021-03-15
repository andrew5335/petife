package kr.co.ainus.petife2.kakao;

import android.app.Activity;
import android.content.Intent;

import com.kakao.auth.ISessionCallback;
import com.kakao.util.exception.KakaoException;
import com.kakao.util.helper.log.Logger;

public class SessionCallback implements ISessionCallback {

    private Activity activity;
    private Class<? extends Activity> redirectActivity;

    public SessionCallback(Activity activity, Class<? extends Activity> redirectActivity) {
        this.activity = activity;
        this.redirectActivity = redirectActivity;
    }

    @Override
    public void onSessionOpened() {
        final Intent intent = new Intent(activity, redirectActivity);
        activity.startActivity(intent);
        activity.finish();
    }

    @Override
    public void onSessionOpenFailed(KakaoException exception) {
        if(exception != null) {
            Logger.e(exception);
        }
    }
}
