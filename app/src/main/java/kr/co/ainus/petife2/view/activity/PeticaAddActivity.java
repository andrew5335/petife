package kr.co.ainus.petife2.view.activity;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Typeface;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.provider.Settings;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import java.util.Arrays;

import androidx.annotation.Nullable;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;
import kr.co.ainus.petife2.R;
import kr.co.ainus.petife2.apapter.SectionPager;
import kr.co.ainus.petife2.databinding.ActivityDeviceAddBinding;
import kr.co.ainus.petife2.view.fragment.PeticaAdd0Fragment;
import kr.co.ainus.petife2.view.fragment.PeticaAdd1Fragment;
import kr.co.ainus.petife2.view.fragment.PeticaAdd2Fragment;
import kr.co.ainus.petife2.view.fragment.PeticaAdd3Fragment;
import kr.co.ainus.petife2.view.fragment.PeticaAdd4Fragment;
import kr.co.ainus.petife2.view.fragment.PeticaAddFailFragment;
import kr.co.ainus.petife2.view.fragment.PeticaAddSuccessFragment;
//import kr.co.ainus.petica.view.fragment.PeticaAdd0Fragment;
//import kr.co.ainus.petica.view.fragment.PeticaAdd1Fragment;
//import kr.co.ainus.petica.view.fragment.PeticaAdd2Fragment;
//import kr.co.ainus.petica.view.fragment.PeticaAdd3Fragment;
//import kr.co.ainus.petica.view.fragment.PeticaAddFailFragment;
//import kr.co.ainus.petica.view.fragment.PeticaAddSuccessFragment;

public class PeticaAddActivity extends _BaseNavigationActivity {

    private static final String TAG = "DeviceAddActivity";
    private ActivityDeviceAddBinding dataBinding;
    private ProgressDialog progressDialog;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        /**
         * 사용할 수 없는 코드
         * 개인정보 관련으로 시스템 앱이 아니면 사용못함
         * 네이버 지도... 개객끼... */
//        Intent intent=new Intent("android.location.GPS_ENABLED_CHANGE");
//        intent.putExtra("enabled", true);
//        sendBroadcast(intent);

        setDataBinding();
        setViewModel();
    }

    @Override
    public void setDataBinding() {
        super.setDataBinding();

        dataBinding = DataBindingUtil.inflate(getLayoutInflater(), R.layout.activity_device_add, baseNavigationBinding.baseNavContainer, true);
        dataBinding.setLifecycleOwner(this);

        dataBinding.vpDeviceAdd.setAdapter(new SectionPager(getSupportFragmentManager(), Arrays.asList(new Fragment[] {
                new PeticaAdd0Fragment(),
                new PeticaAdd1Fragment(),
                new PeticaAdd2Fragment(),
                new PeticaAdd3Fragment(),
                new PeticaAdd4Fragment(),
                new PeticaAddSuccessFragment(),
                new PeticaAddFailFragment(),
        })));

        dataBinding.indicator.setViewPager(dataBinding.vpDeviceAdd);
        dataBinding.vpDeviceAdd.setOnTouchListener((v, event) -> true);
        dataBinding.indicator.getChildAt(5).setVisibility(View.GONE);
    }

    @Override
    public void setViewModel() {
        super.setViewModel();

        peticaViewModel.getDeviceAddStepLiveData().observe(this, step -> {

            Log.i(TAG, "step change = " + step);

            dataBinding.vpDeviceAdd.setCurrentItem(step);

            switch (step) {
                case 0:
                    dataBinding.desc001.setTypeface(null, Typeface.BOLD);
                    dataBinding.desc002.setTypeface(null, Typeface.BOLD);
                    dataBinding.desc003.setTypeface(null, Typeface.NORMAL);
                    dataBinding.desc004.setTypeface(null, Typeface.NORMAL);
                    dataBinding.desc005.setTypeface(null, Typeface.NORMAL);

                    baseNavigationBinding.tvTitle.setText("Petife");

                    baseNavigationBinding.btnLeft.setText(getString(R.string.cancel2));
                    baseNavigationBinding.btnLeft.setOnClickListener(v -> finish());

                    baseNavigationBinding.btnRight.setText(getString(R.string.next));
                    baseNavigationBinding.btnRight.setOnClickListener(v -> {
                        WifiManager wifiManager = (WifiManager)getApplicationContext().getSystemService(Context.WIFI_SERVICE);

                        String curSsid = wifiManager.getConnectionInfo().getSSID();
                        if(!curSsid.startsWith("LTH") && !curSsid.startsWith("Petife")) {
                            Toast.makeText(getApplicationContext(), "LTH 또는 Petife로 시작하는 와이파이에 연결하세요.", Toast.LENGTH_LONG).show();
                            Intent callGPSSettingIntent = new Intent(
                                    Settings.ACTION_WIFI_SETTINGS);
                            startActivity(callGPSSettingIntent);
                        } else {
                            peticaViewModel.checkCurrentSsid(getApplicationContext(), false, getResources().getStringArray(R.array.petica_ssid_array));
                        }
                    });

                    break;

                case 1:
                    dataBinding.desc001.setTypeface(null, Typeface.NORMAL);
                    dataBinding.desc002.setTypeface(null, Typeface.NORMAL);
                    dataBinding.desc003.setTypeface(null, Typeface.BOLD);
                    dataBinding.desc004.setTypeface(null, Typeface.NORMAL);
                    dataBinding.desc005.setTypeface(null, Typeface.NORMAL);

                    baseNavigationBinding.tvTitle.setText(getString(R.string.wifiSelect));

                    baseNavigationBinding.btnLeft.setText(getString(R.string.cancel2));
                    baseNavigationBinding.btnLeft.setOnClickListener(v -> finish());

                    baseNavigationBinding.btnRight.setText(getString(R.string.refresh));
                    baseNavigationBinding.btnRight.setOnClickListener(v -> {
                        peticaViewModel.loadSsidList("192.168.100.1:80", "admin");
                    });

                    break;
                case 2:
                    dataBinding.desc001.setTypeface(null, Typeface.NORMAL);
                    dataBinding.desc002.setTypeface(null, Typeface.NORMAL);
                    dataBinding.desc003.setTypeface(null, Typeface.BOLD);
                    dataBinding.desc004.setTypeface(null, Typeface.NORMAL);
                    dataBinding.desc005.setTypeface(null, Typeface.NORMAL);

                    baseNavigationBinding.tvTitle.setText(getString(R.string.wifiConnect));

                    baseNavigationBinding.btnLeft.setText(getString(R.string.cancel2));
                    baseNavigationBinding.btnLeft.setOnClickListener(v -> finish());

                    baseNavigationBinding.btnRight.setText(getString(R.string.next));
                    baseNavigationBinding.btnRight.setOnClickListener(v -> {
                        if (peticaViewModel.getSelectSsidInfoLiveData().getValue() == null) return;

                        peticaViewModel.checkCurrentSsid(getApplicationContext(), true, peticaViewModel.getSelectSsidInfoLiveData().getValue().getSsid());
                    });

                    break;
                case 3:
                    dataBinding.desc001.setTypeface(null, Typeface.NORMAL);
                    dataBinding.desc002.setTypeface(null, Typeface.NORMAL);
                    dataBinding.desc003.setTypeface(null, Typeface.NORMAL);
                    dataBinding.desc004.setTypeface(null, Typeface.BOLD);
                    dataBinding.desc005.setTypeface(null, Typeface.NORMAL);

                    baseNavigationBinding.tvTitle.setText(getString(R.string.petifeSetting));

                    baseNavigationBinding.btnLeft.setVisibility(View.GONE);
                    baseNavigationBinding.btnRight.setVisibility(View.GONE);

                    break;


                case 4:
                    dataBinding.desc001.setTypeface(null, Typeface.NORMAL);
                    dataBinding.desc002.setTypeface(null, Typeface.NORMAL);
                    dataBinding.desc003.setTypeface(null, Typeface.NORMAL);
                    dataBinding.desc004.setTypeface(null, Typeface.NORMAL);
                    dataBinding.desc005.setTypeface(null, Typeface.BOLD);

                    baseNavigationBinding.tvTitle.setText(getString(R.string.setOwnPetife));

                    baseNavigationBinding.btnLeft.setVisibility(View.GONE);
                    baseNavigationBinding.btnRight.setVisibility(View.GONE);

                    break;

                case 5:
                    baseNavigationBinding.tvTitle.setText(getString(R.string.regComplete));

                    baseNavigationBinding.btnLeft.setVisibility(View.GONE);
                    baseNavigationBinding.btnRight.setVisibility(View.GONE);

                    break;

                case 6:
                    baseNavigationBinding.tvTitle.setText(getString(R.string.regFail));

                    baseNavigationBinding.btnLeft.setVisibility(View.GONE);
                    baseNavigationBinding.btnRight.setVisibility(View.GONE);


                    break;
            }
        });

//        peticaViewModel.getPeticaResultReasonCodeLiveData().observe(this, new ReasonCodeObserver(getApplicationContext()));

        peticaViewModel.getDeviceAddStepLiveData().setValue(0);

        /** 2019-04-15
         * petica add 방식 변경으로 인한 주석 */
//        peticaViewModel.loadSsidList(getApplicationContext());
    }

}

