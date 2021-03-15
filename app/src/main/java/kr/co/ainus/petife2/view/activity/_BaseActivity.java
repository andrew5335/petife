package kr.co.ainus.petife2.view.activity;

import android.Manifest;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.view.WindowManager;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.InstanceIdResult;
import com.nabto.api.RemoteTunnel;
import com.thanosfisherman.wifiutils.WifiUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.UUID;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.databinding.DataBindingUtil;
import androidx.lifecycle.ViewModelProviders;
import kr.co.ainus.petife2.R;
import kr.co.ainus.petife2.databinding.ActivityBaseBinding;
import kr.co.ainus.petife2.protocol.Extandable;
import kr.co.ainus.petife2.util.InternetConnectCheckHelper;
import kr.co.ainus.petife2.util.SharedPreferencesHelper;
import kr.co.ainus.petife2.viewmodel.FeedHistoryViewModel;
import kr.co.ainus.petife2.viewmodel.FeedViewModel;
import kr.co.ainus.petife2.viewmodel.PermissionViewModel;
import kr.co.ainus.petife2.viewmodel.PetViewModel;
import kr.co.ainus.petife2.viewmodel.PeticaViewModel;
import kr.co.ainus.petife2.viewmodel.PostViewModel;
import kr.co.ainus.petife2.viewmodel.UserViewModel;
import kr.co.ainus.petife2.viewmodel._BaseViewModel;

abstract public class _BaseActivity extends AppCompatActivity implements Extandable {

    private static final String TAG = "_BaseActivity";

    protected ActivityBaseBinding baseDataBinding;

    protected _BaseViewModel baseViewModel;
    protected UserViewModel userViewModel;
    protected PermissionViewModel permissionViewModel;
    protected PeticaViewModel peticaViewModel;
    protected PetViewModel petViewModel;
    protected FeedViewModel feedViewModel;
    protected FeedHistoryViewModel feedHistoryViewModel;
    protected PostViewModel postViewModel;

    protected String name;
    protected String email;
    protected String provider;
    protected String uuid;
    protected String token;
    protected boolean hasFirst;

    protected String language;
    protected String country;

    private AlertDialog internetConnectAlertDialog;
    private AlertDialog simCheckDialog;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // 핸드폰의 언어 설정값 가져오기 추가 2020-07-21 by Andrew Kim
        language = Locale.getDefault().getLanguage().toString();
        country = Locale.getDefault().getISO3Country().toString();
        //Toast.makeText(getApplicationContext(), "language : " + language, Toast.LENGTH_SHORT).show();
        //Toast.makeText(getApplicationContext(), "country : " + country, Toast.LENGTH_SHORT).show();

        // 가장 최근의 GPS 정보 가져오기 추가 2020-07-20 by Andrew Kim
        final LocationManager lm = (LocationManager) getSystemService(Context.LOCATION_SERVICE);

        if(Build.VERSION.SDK_INT >= 23 &&
                ContextCompat.checkSelfPermission(getApplicationContext(),
                        Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(_BaseActivity.this, new String[] {
                    Manifest.permission.ACCESS_FINE_LOCATION
            }, 0);
        } else {
            // 지역 정보 설정 시 GPS 우선, GPS가 없을 경우 네트워크로 설정
            Location location = lm.getLastKnownLocation(LocationManager.GPS_PROVIDER);
            if(null == location) {
                location = lm.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
            }
            //String provider = location.getProvider();

            if(null != location) {
                double longitude = location.getLongitude();
                double latitude = location.getLatitude();
                double altitude = location.getAltitude();
                long locationTime = location.getTime();
                String city = getCurrentAddress(latitude, longitude);
            }

            /**
             Toast.makeText(getApplicationContext(), "provider : "+ provider, Toast.LENGTH_SHORT).show();
             Toast.makeText(getApplicationContext(), "longitude : " + longitude, Toast.LENGTH_SHORT).show();
             Toast.makeText(getApplicationContext(), "latitude : " + latitude, Toast.LENGTH_SHORT).show();
             Toast.makeText(getApplicationContext(), "altitude : " + altitude, Toast.LENGTH_SHORT).show();
             **/
            //Toast.makeText(getApplicationContext(), "Time : " + locationTime, Toast.LENGTH_SHORT).show();
            //Toast.makeText(getApplicationContext(), "City : " + city, Toast.LENGTH_SHORT).show();

            //lm.requestLocationUpdates(LocationManager.GPS_PROVIDER, 10000, 1, gpsLocationListener);
            //lm.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 10000, 1, gpsLocationListener);
        }

        if(Build.VERSION.SDK_INT >= 23 &&
                ContextCompat.checkSelfPermission(getApplicationContext(),
                        Manifest.permission.RECORD_AUDIO) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(_BaseActivity.this, new String[] {
                    Manifest.permission.RECORD_AUDIO
            }, 1234);
        }

        if(Build.VERSION.SDK_INT >= 23 &&
                ContextCompat.checkSelfPermission(getApplicationContext(),
                        Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(_BaseActivity.this, new String[] {
                    Manifest.permission.WRITE_EXTERNAL_STORAGE
            }, 0);
        }

        if (getSupportActionBar() != null) getSupportActionBar().hide();

        name = SharedPreferencesHelper.getString(getApplicationContext(), "user_name");
        email = SharedPreferencesHelper.getString(getApplicationContext(), "user_email");
        provider = SharedPreferencesHelper.getString(getApplicationContext(), "user_provider");
        uuid = SharedPreferencesHelper.getString(getApplicationContext(), "user_uuid");
        token = SharedPreferencesHelper.getString(getApplicationContext(), "user_token");
        hasFirst = SharedPreferencesHelper.getBoolean(getApplicationContext(), "user_has_first");

        Log.i(TAG, "user_email =" + email);
        Log.i(TAG, "user_provider =" + provider);
        Log.i(TAG, "user_uuid =" + uuid);
        Log.i(TAG, "user_token =" + token);
        Log.i(TAG, "user_has_first =" + hasFirst);

        if (uuid == null) {
            uuid = UUID.randomUUID().toString();
            SharedPreferencesHelper.putString(getApplicationContext(), "user_uuid", uuid);
            SharedPreferencesHelper.putBoolean(getApplicationContext(), "user_has_first", true);
        }

        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE);

        if (getInternetCheckResult() == 0) {
            internetConnectAlertDialog = new AlertDialog.Builder(this)
                    .setTitle(getString(R.string.alert))
                    .setMessage(getString(R.string.checkConnection))
                    .setPositiveButton(getString(R.string.confirm), null)
                    .create();

            try {

                internetConnectAlertDialog.show();

            } catch (Exception e) {
                Log.e(TAG, e.getLocalizedMessage());
                e.printStackTrace();
            }

        }

        /* 스마트폰 유심 유무 확인 추가 2021-01-16 by Andrew Kim */
        TelephonyManager systemService = (TelephonyManager) getApplicationContext().getSystemService((Context.TELEPHONY_SERVICE));
        int simState = systemService.getSimState();

        if(simState == TelephonyManager.SIM_STATE_ABSENT || simState == TelephonyManager.SIM_STATE_UNKNOWN) {
            simCheckDialog = new AlertDialog.Builder(this)
                    .setTitle(getString(R.string.alert))
                    .setMessage(R.string.usimAlert)
                    .setPositiveButton(getString(R.string.confirm), null)
                    .create();

            try {
                //simCheckDialog.show();
            } catch(Exception e) {
                Log.e(TAG, e.getLocalizedMessage());
                e.printStackTrace();
            }
        }

        WifiUtils.enableLog(true);
//        WifiUtils.enableLog(false);

        RemoteTunnel.setHasLog(false);

        FirebaseInstanceId.getInstance().getInstanceId()
                .addOnCompleteListener(new OnCompleteListener<InstanceIdResult>() {
                    @Override
                    public void onComplete(@NonNull Task<InstanceIdResult> task) {
                        if (!task.isSuccessful()) {
                            Log.w(TAG, "getInstanceId failed", task.getException());
                            return;
                        }

                        // Get new Instance ID token
                        if (task.getResult() != null) {
                            token = task.getResult().getToken();
                            SharedPreferencesHelper.putString(getApplicationContext(), "user_token", token);
                            Log.i(TAG, "token = " + token);

                            createNotificationChannel();
                        }

//                        String msg = getString(R.string.msg_token_fmt, token);
                    }
                });
    }

    @Override
    protected void onStart() {
        super.onStart();

        // System.gc() // 가비지 역시 하는거 아니었어;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        for (int i = 0; i < permissions.length; i++) {
            if (grantResults[i] != PackageManager.PERMISSION_GRANTED) {

                try {

                    new AlertDialog.Builder(this)
                            .setTitle("알림")
                            .setMessage("앱 사용에 필요한 권한이 수락되지 않아 종료됩니다")
                            .setNegativeButton("종료", (dialog, which) -> finish())
                            .create()
                            .show();

                } catch (Exception e) {
                    Log.e(TAG, e.getLocalizedMessage());
                    e.printStackTrace();
                }
            }
        }
    }

    @Override
    public void setDataBinding() {
        baseDataBinding = DataBindingUtil.setContentView(this, R.layout._activity_base);
        baseDataBinding.setLifecycleOwner(this);
    }

    @Override
    public void setViewModel() {
        baseViewModel = ViewModelProviders.of(this).get(_BaseViewModel.class);

        userViewModel = ViewModelProviders.of(this).get(UserViewModel.class);
        permissionViewModel = ViewModelProviders.of(this).get(PermissionViewModel.class);
        peticaViewModel = ViewModelProviders.of(this).get(PeticaViewModel.class);
        feedViewModel = ViewModelProviders.of(this).get(FeedViewModel.class);
        feedHistoryViewModel = ViewModelProviders.of(this).get(FeedHistoryViewModel.class);
        petViewModel = ViewModelProviders.of(this).get(PetViewModel.class);
        postViewModel = ViewModelProviders.of(this).get(PostViewModel.class);

        baseViewModel.getHasLoadingLiveData().setValue(false);
    }

    protected int getInternetCheckResult() {
        return InternetConnectCheckHelper.checkInternet(getApplicationContext());
    }

    protected AlertDialog getInternetConnectAlertDialog() {
        return internetConnectAlertDialog;
    }

    private void createNotificationChannel() {
        // Create the NotificationChannel, but only on API 26+ because
        // the NotificationChannel class is new and not in the support library
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            CharSequence name = getString(R.string.default_notification_channel_name);
            String description = getString(R.string.default_notificatio_channel_description);
            int importance = NotificationManager.IMPORTANCE_DEFAULT;
            NotificationChannel channel = new NotificationChannel(getString(R.string.default_notification_channel_id), name, importance);
            channel.setDescription(description);
            // Register the channel with the system; you can't change the importance
            // or other notification behaviors after this
            NotificationManager notificationManager = getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(channel);
        }
    }

    final LocationListener gpsLocationListener = new LocationListener() {
        @Override
        public void onLocationChanged(Location location) {
            String provider = location.getProvider();
            double longitude = location.getLongitude();
            double latitude = location.getLatitude();
            double altitude = location.getAltitude();

            /**
             Toast.makeText(getApplicationContext(), "provider : " + provider, Toast.LENGTH_SHORT).show();
             Toast.makeText(getApplicationContext(), "longitude : " + longitude, Toast.LENGTH_SHORT).show();
             Toast.makeText(getApplicationContext(), "latitude : " + latitude, Toast.LENGTH_SHORT).show();
             Toast.makeText(getApplicationContext(), "altitude : " + altitude, Toast.LENGTH_SHORT).show();
             **/
        }

        @Override
        public void onStatusChanged(String provider, int status, Bundle extras) {

        }

        @Override
        public void onProviderEnabled(String provider) {

        }

        @Override
        public void onProviderDisabled(String provider) {

        }
    };

    public String getCurrentAddress(double latitude, double longitude) {
        Geocoder geocoder = new Geocoder(this, Locale.getDefault());

        List<Address> addresses = new ArrayList<Address>();

        try {
            addresses = geocoder.getFromLocation(latitude, longitude, 7);
        } catch(Exception e) {
            Toast.makeText(getApplicationContext(), "Error : " + e.toString(), Toast.LENGTH_SHORT).show();
        }

        if(null != addresses && 0 < addresses.size()) {
            Address address = addresses.get(0);
            //return address.getAddressLine(0).toString();
            return address.getCountryName().toString();
        }

        return null;
    }
}

