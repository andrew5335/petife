package kr.co.ainus.petife2.viewmodel;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.facebook.AccessToken;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.login.LoginManager;
import com.facebook.login.LoginResult;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.iid.FirebaseInstanceId;
import com.kakao.auth.ISessionCallback;
import com.kakao.auth.Session;
import com.kakao.network.ErrorResult;
import com.kakao.usermgmt.UserManagement;
import com.kakao.usermgmt.callback.LogoutResponseCallback;
import com.kakao.usermgmt.callback.MeV2ResponseCallback;
import com.kakao.usermgmt.response.MeV2Response;
import com.kakao.util.exception.KakaoException;

import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import kr.co.ainus.petife2.R;
import kr.co.ainus.petife2.kakao.KakaoLoginControl;
import kr.co.ainus.petife2.util.GsonHelper;
import kr.co.ainus.petife2.util.SharedPreferencesHelper;
import kr.co.ainus.petife2.view.activity.SignInActivity;
import kr.co.ainus.petica_api.ApiHelper;
import kr.co.ainus.petica_api.model.domain.User;
import kr.co.ainus.petica_api.model.request.UserRequest;
import kr.co.ainus.petica_api.model.response.UserResponse;

public class UserViewModel extends ViewModel {

    private static final String TAG = "UserViewModel";

    private final MutableLiveData<Boolean> hasLoadingLiveData = new MutableLiveData<>();
    private final MutableLiveData<Boolean> hasSignInLiveData = new MutableLiveData<>();
    private final MutableLiveData<Integer> signInResultReasonCodeLiveData = new MutableLiveData<>();
    private final MutableLiveData<User> currentUserLiveData = new MutableLiveData<>();

    public MutableLiveData<Boolean> getHasLoadingLiveData() {
        return hasLoadingLiveData;
    }

    public MutableLiveData<Boolean> getHasSignInLiveData() {
        return hasSignInLiveData;
    }

    public MutableLiveData<Integer> getSignInResultReasonCodeLiveData() {
        return signInResultReasonCodeLiveData;
    }

    public MutableLiveData<User> getCurrentUserLiveData() {
        return currentUserLiveData;
    }

    public void signInGoogle(Activity activity, String uuid, String token, GoogleSignInAccount googleSignInAccount) {

        hasLoadingLiveData.setValue(true);

        if (googleSignInAccount == null) {
            GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                    .requestIdToken(activity.getString(R.string.default_web_client_id))
                    .requestEmail()
                    .build();

            GoogleSignInClient mGoogleSignInClient = GoogleSignIn.getClient(activity, gso);

            Intent signInIntent = mGoogleSignInClient.getSignInIntent();
            activity.startActivityForResult(signInIntent, SignInActivity.REQ_GOOGLE_SIGN_IN);

        } else {

            String name = googleSignInAccount.getDisplayName();
            String email = googleSignInAccount.getEmail();

            SharedPreferencesHelper.putString(activity, "user_name", name);
            SharedPreferencesHelper.putString(activity, "user_email", email);
            SharedPreferencesHelper.putString(activity, "user_provider", "google");

            signIn(activity, email, "google", uuid, token);
        }
    }

    public void signInFacebook(Activity activity, String uuid, String token, CallbackManager callbackManager) {
        hasLoadingLiveData.setValue(true);

        if (AccessToken.getCurrentAccessToken() == null) {

            LoginManager.getInstance().registerCallback(callbackManager,
                    new FacebookCallback<LoginResult>() {
                        @Override
                        public void onSuccess(LoginResult loginResult) {
                            // App code
                            GraphRequest graphRequest = GraphRequest.newMeRequest(
                                    loginResult.getAccessToken(),
                                    new GraphRequest.GraphJSONObjectCallback() {
                                        @Override
                                        public void onCompleted(JSONObject me, GraphResponse response) {
                                            String name = me.optString("name");
                                            String email = me.optString("email");

                                            SharedPreferencesHelper.putString(activity, "user_name", name);
                                            SharedPreferencesHelper.putString(activity, "user_email", email);
                                            SharedPreferencesHelper.putString(activity, "user_provider", "facebook");

                                            signIn(activity, email, "facebook", uuid, token);

                                        }
                                    });

                            Bundle parameters = new Bundle();
                            parameters.putString("fields", "name,email");
                            graphRequest.setParameters(parameters);
                            graphRequest.executeAsync();

                        }

                        @Override
                        public void onCancel() {
                            // App code
                            hasLoadingLiveData.setValue(false);
                        }

                        @Override
                        public void onError(FacebookException exception) {
                            // App code
                            Log.e(TAG, exception.getLocalizedMessage());
                            exception.printStackTrace();

                            hasLoadingLiveData.setValue(false);
                        }
                    });

            LoginManager.getInstance().logInWithReadPermissions(activity, Arrays.asList("public_profile", "email"));

        } else {

            String email = SharedPreferencesHelper.getString(activity, "user_email");
            signIn(activity, email, "facebook", uuid, token);
        }

    }

    public void signInKakao(Activity activity, String uuid, String token) {
        hasLoadingLiveData.setValue(true);

        ISessionCallback callback = new ISessionCallback() {

            @Override
            public void onSessionOpened() {
                List<String> keys = new ArrayList<>();
                keys.add("kakao_account.email");

                UserManagement.getInstance().me(keys, new MeV2ResponseCallback() {
                    @Override
                    public void onFailure(ErrorResult errorResult) {
                        // TODO error process
                        Session.getCurrentSession().clearCallbacks();

                        hasLoadingLiveData.setValue(false);
                        signInResult(-1);

                        Log.e(TAG, "onFailure");
                    }

                    @Override
                    public void onSessionClosed(ErrorResult errorResult) {
                        // TODO error process
                        Session.getCurrentSession().clearCallbacks();

                        hasLoadingLiveData.setValue(false);

                        Log.e(TAG, "onSessionClosed");

                    }

                    @Override
                    public void onSuccess(MeV2Response response) {
                        Session.getCurrentSession().clearCallbacks();


                        String email = response.getKakaoAccount().getEmail();

                        if (email == null || email.replace(" ", "").equals("")) {

                            signInResult(105);
                            hasLoadingLiveData.setValue(false);

                            return;
                        }

                        signIn(activity, email, "kakao", uuid, token);

                    }
                });
            }

            @Override
            public void onSessionOpenFailed(KakaoException exception) {
                Log.e(TAG, exception.getLocalizedMessage());
                exception.printStackTrace();

                hasLoadingLiveData.setValue(false);
            }
        };

        Session.getCurrentSession().addCallback(callback);

        KakaoLoginControl kakaoLoginControl = new KakaoLoginControl(activity);
        kakaoLoginControl.call();
    }

    private void signIn(Activity activity, String email, String provider, String uuid, String token) {
        hasLoadingLiveData.setValue(true);

        SharedPreferencesHelper.putString(activity, "user_email", email);
        SharedPreferencesHelper.putString(activity, "user_provider", provider);
        SharedPreferencesHelper.putString(activity, "user_uuid", uuid);

        UserRequest request = new UserRequest();
        request.setEmail(email);
        request.setProvider(provider);
        request.setUuid(uuid);

        ApiHelper.signIn(
                email
                , provider
                , uuid
                , token
                , new ApiHelper.SuccessHandler() {
                    @Override
                    public <V> void onSuccess(V response) {
                        Log.i(TAG, GsonHelper.getGson().toJson(response));

                        if (response instanceof UserResponse) {

                            boolean succeed = ((UserResponse) response).getSucceed();
                            int reasonCode = ((UserResponse) response).getReason();
                            signInResult(reasonCode);

                            if (succeed) {
                                currentUserLiveData.setValue(((UserResponse) response).getItems().get(0));
                                hasSignInLiveData.setValue(true);

                            } else {
                                signOut(activity);

                                /** 2019-04-09
                                 * 로그아웃 시 firebase instance id 같이 delete */
                                try {
                                    FirebaseInstanceId.getInstance().deleteInstanceId();
                                } catch (IOException e) {
                                    e.printStackTrace();
                                }

                                hasSignInLiveData.setValue(false);
                            }

                        }
                    }
                }, t -> {
                    Log.e(TAG, t.getLocalizedMessage());
                    t.printStackTrace();

                    signInResult(-1);
                    hasSignInLiveData.setValue(false);

                }, () -> hasLoadingLiveData.setValue(false));
    }

    private void signInResult(int reason) {
        signInResultReasonCodeLiveData.setValue(reason);
        signInResultReasonCodeLiveData.setValue(null);
    }

    public void signOut(Activity activity) {

        // 구글 로그아웃
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(activity.getString(R.string.default_web_client_id))
                .requestEmail()
                .build();

        GoogleSignInClient mGoogleSignInClient = GoogleSignIn.getClient(activity, gso);
        mGoogleSignInClient.signOut()
                .addOnCompleteListener(activity, new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {

                    }
                });

        // 페이스북 로그아웃
        LoginManager.getInstance().logOut();

        // 카카오 로그아웃
        UserManagement.getInstance().requestLogout(new LogoutResponseCallback() {
            @Override
            public void onCompleteLogout() {

            }
        });

        SharedPreferences.Editor sharedPreference = activity.getSharedPreferences("petica", Context.MODE_PRIVATE).edit();
        sharedPreference.clear().apply();
    }
}

