package kr.co.ainus.petife2.view.activity;

import android.Manifest;
import android.content.Intent;
import android.os.Bundle;
import android.os.StrictMode;
import android.util.Log;
import android.view.View;

import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.databinding.DataBindingUtil;

import com.facebook.CallbackManager;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.Task;

import kr.co.ainus.petife2.R;
import kr.co.ainus.petife2.databinding.ActivitySignInBinding;
import kr.co.ainus.petife2.util.HashKeyHelper;

public class SignInActivity extends _BaseNavigationActivity {

    private static final String TAG = "SignInActivity";
    public static final int REQ_GOOGLE_SIGN_IN = 0x123;
    public static final int REQ_FACEBOOK_SIGN_IN = 0xface;

    private ActivitySignInBinding dataBinding;

    private CallbackManager callbackManager = CallbackManager.Factory.create();

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);

        Log.i(TAG, "onCreate");

        setDataBinding();
        setViewModel();

        ActivityCompat.requestPermissions(this, new String[]{
                Manifest.permission.WRITE_EXTERNAL_STORAGE
                , Manifest.permission.READ_EXTERNAL_STORAGE
                , Manifest.permission.ACCESS_COARSE_LOCATION
                , Manifest.permission.ACCESS_FINE_LOCATION
                , Manifest.permission.RECORD_AUDIO
                , Manifest.permission.CHANGE_WIFI_STATE
                , Manifest.permission.ACCESS_WIFI_STATE
                , Manifest.permission.CHANGE_WIFI_MULTICAST_STATE

        }, 1);

        // 로그인 관련 시작
        Log.i(TAG, "provider = " + provider);

        if (provider == null) return;

        switch (provider) {
            case "google":
                userViewModel.signInGoogle(this, uuid, token, GoogleSignIn.getLastSignedInAccount(getApplicationContext()));
                break;

            case "facebook":
                userViewModel.signInFacebook(this, uuid, token, callbackManager);
                break;
        }
        // 로그인 관련 끝

        Log.e(TAG, "hash key = " + HashKeyHelper.getKeyHash(getApplicationContext()));

    }

    @Override
    public void setDataBinding() {
        super.setDataBinding();

        baseNavigationBinding.tvTitle.setText(R.string.titleText);
        baseNavigationBinding.btnLeft.setVisibility(View.GONE);
        baseNavigationBinding.btnRight.setVisibility(View.GONE);

        dataBinding = DataBindingUtil.inflate(getLayoutInflater(), R.layout.activity_sign_in, baseNavigationBinding.baseNavContainer, true);
        dataBinding.setLifecycleOwner(this);
        dataBinding.btnSigninGoogle.setText(R.string.googleLogin);
        dataBinding.btnSigninFacebook.setText(R.string.facebookLogin);


        dataBinding.btnSigninGoogle.setOnClickListener(v -> {
            userViewModel.signInGoogle(this, uuid, token, GoogleSignIn.getLastSignedInAccount(this));
        });

        dataBinding.btnSigninFacebook.setOnClickListener(v -> {
            userViewModel.signInFacebook(this, uuid, token, callbackManager);
        });

        dataBinding.btnSigninKakaotalk.setOnClickListener(v -> {
            userViewModel.signInKakao(this, uuid, token);
        });
    }

    @Override
    public void setViewModel() {
        super.setViewModel();

        userViewModel.getHasLoadingLiveData().observe(this, hasLoading -> {
            baseViewModel.getHasLoadingLiveData().setValue(hasLoading);
        });

        userViewModel.getHasSignInLiveData().observe(this, hasSignIn -> {
            startActivity(new Intent(getApplicationContext(), MainActivity.class));
            finish();
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        Log.i(TAG, "resultCode = " + resultCode);

        userViewModel.getHasLoadingLiveData().setValue(false);

        if (getInternetCheckResult() == 0) {

            try {
                getInternetConnectAlertDialog().show();
            } catch (Exception e) {
                Log.e(TAG, e.getLocalizedMessage());
                e.printStackTrace();
            }
        }

        switch (requestCode) {
            // 구글은 로그인 성공시에 account != null, 나머지 케이스 null
            case REQ_GOOGLE_SIGN_IN:
                try {
                    Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
                    GoogleSignInAccount account = task.getResult(ApiException.class);

                    Log.i(TAG, "account = " + account);

                    if (account != null) {
                        userViewModel.signInGoogle(this, uuid, token, account);
                    }
                } catch (ApiException e) {
                    Log.e(TAG, "Google sign in failed", e);
                }

//                GoogleSignInAccount account = GoogleSignIn.getLastSignedInAccount(this);
//                Log.i(TAG, "account = " + account);
//
//                if (account != null) {
//                    userViewModel.signInGoogle(this, uuid, token, account);
//                }
                break;

            case REQ_FACEBOOK_SIGN_IN:

                // 인터넷 연결이 안됐을 때 resultCode -1 로 반환
                /**  2019-04-03
                 * 위 사항 잘못된 것임
                 * 페이스북은 늘 result code -1 반환
                 * 그 사유로 하단 코드 주석처리함 */
//                if (resultCode == -1) {
//                    return;
//                }

                callbackManager.onActivityResult(requestCode, resultCode, data);
                break;

        }

        super.onActivityResult(requestCode, resultCode, data);
    }
}

