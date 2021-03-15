package kr.co.ainus.petife2.view.fragment;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.location.LocationManager;
import android.net.wifi.WifiManager;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.util.Log;
import android.view.View;

import androidx.annotation.Nullable;
import androidx.databinding.DataBindingUtil;

import kr.co.ainus.petife2.R;
import kr.co.ainus.petife2.databinding.FragmentPeticaAdd0Binding;

public class PeticaAdd0Fragment extends _BaseFragment {

    private static final String TAG = "PeticaAdd0Fragment";

    private FragmentPeticaAdd0Binding dataBinding;
    private AlertDialog alertDialog;

    private LocationManager locationManager;
    private ProgressDialog progressDialog;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (getActivity() != null) {
            locationManager = (LocationManager) getActivity().getApplicationContext().getSystemService(Context.LOCATION_SERVICE);
        }

        setDataBinding();
        setViewModel();
    }

    @Override
    public void onStart() {
        super.onStart();

        if (peticaViewModel.getDeviceAddStepLiveData().getValue() == null)
            return;

        if (peticaViewModel.getDeviceAddStepLiveData().getValue() == 0) {
            peticaViewModel.checkCurrentSsid(getActivity(), false, getResources().getStringArray(R.array.petica_ssid_array));
        }
    }

    @Override
    public void setDataBinding() {
        super.setDataBinding();


        dataBinding = DataBindingUtil.inflate(getLayoutInflater(), R.layout.fragment_petica_add_0, baseDataBinding.baseFragmentContainer, true);
        dataBinding.setLifecycleOwner(this);

        if(Build.VERSION.SDK_INT >= 30) {
            dataBinding.step5id.setVisibility(View.VISIBLE);
        } else {
            dataBinding.step5id.setVisibility(View.VISIBLE);
        }

    }

    @Override
    public void setViewModel() {
        super.setViewModel();

        peticaViewModel.getDeviceAddStepLiveData().observe(this, step -> {
            if (step == 0) {

                // wifi가 활성화되어 있지 않다면 시스템의 wifi 설정으로 이동 2021-01-29 by Andrew Kim
                WifiManager wifiManager = (WifiManager)getContext().getSystemService(Context.WIFI_SERVICE);
                if(!wifiManager.isWifiEnabled()) {
                    Intent callGPSSettingIntent = new Intent(
                            Settings.ACTION_WIFI_SETTINGS);
                    startActivity(callGPSSettingIntent);
                }

                peticaViewModel.getHasCorrectSsid().observe(this, hasCorrectSsid -> {
                    if (hasCorrectSsid == null) return;

                    /**
                    if (hasCorrectSsid == null) {
                        // petife에 연결되어 있지 않으면 시스템의 wifi 설정으로 이동 2021-01-29 by Andrew Kim
                        Intent callGPSSettingIntent = new Intent(
                                Settings.ACTION_WIFI_SETTINGS);
                        startActivity(callGPSSettingIntent);
                    }
                     **/

                    if (hasCorrectSsid) {

                        if (getActivity() != null) {
                            progressDialog = new ProgressDialog(getActivity());
                            progressDialog.setCancelable(false);
                            progressDialog.setMessage(getString(R.string.tryPetife));

                            try {

                                progressDialog.show();

                            } catch (Exception e) {
                                Log.e(TAG, e.getLocalizedMessage());
                                e.printStackTrace();
                            }

                        }

                        Log.i(TAG, "scan petica");
                        peticaViewModel.scanPeticaList(getActivity(), 5000);

                        peticaViewModel.getHasSuccessPeticaListScan().observe(this, hasSuccessPeticaListScan -> {

                            if (hasSuccessPeticaListScan == null) return;

                            /**
                            if (hasSuccessPeticaListScan == null) {
                                // petife list가 없으면 시스템의 wifi 설정으로 이동 처리 2021-01-29 by Andrew Kim
                                Intent callGPSSettingIntent = new Intent(
                                        Settings.ACTION_WIFI_SETTINGS);
                                startActivity(callGPSSettingIntent);
                            }
                             **/

                            if (hasSuccessPeticaListScan) {
                                peticaViewModel.getHasSuccessPeticaListScan().setValue(null);

                                Log.i(TAG, "다음 단계로 이동");
                                peticaViewModel.getDeviceAddStepLiveData().setValue(1);
                                peticaViewModel.getHasCorrectSsid().setValue(null);

                                if (progressDialog != null) progressDialog.dismiss();
                            } else {

                                if (progressDialog != null) progressDialog.dismiss();

                                alertDialog = new AlertDialog.Builder(getActivity())
                                        .setTitle(getString(R.string.alert))
                                        .setMessage(getString(R.string.shutdown))    // 3초 이내에 petife ssid가 검색되지 않을 경우 나타나는 메시지 2021-01-27 by Andrew Kim
                                        .setCancelable(false)
                                        .setPositiveButton(getString(R.string.confirm), null)
                                        .create();

                                try {

                                    alertDialog.show();

                                } catch (Exception e) {
                                    Log.e(TAG, e.getLocalizedMessage());
                                    e.printStackTrace();
                                }

                            }
                        });

                    } else if (locationManager != null && !locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {

                        showGPSDisabledAlertToUser();

                    } else {

                        alertDialog = new AlertDialog.Builder(getActivity())
                                .setTitle(getString(R.string.wifiConnect))
                                .setTitle(getString(R.string.connectPetife))
                                .setNegativeButton(getString(R.string.confirm), ((dialog, which) -> {
                                    Intent callGPSSettingIntent = new Intent(
                                            Settings.ACTION_WIFI_SETTINGS);
                                    startActivity(callGPSSettingIntent);
                                }))
                                .setPositiveButton(getString(R.string.step22), ((dialog, which) -> {
                                    Intent callGPSSettingIntent = new Intent(
                                            Settings.ACTION_WIFI_SETTINGS);
                                    startActivity(callGPSSettingIntent);
                                }))
                                .setCancelable(false)
                                .create();

                        try {
                            //Thread.sleep(1000);
                            //alertDialog.show();

                        } catch (Exception e) {
                            Log.e(TAG, e.getLocalizedMessage());
                            e.printStackTrace();
                        }

                    }
                });

            } else {

                peticaViewModel.getHasCorrectSsid().removeObservers(this);
            }
        });
    }

    private void showGPSDisabledAlertToUser() {

            if (getActivity() != null) {
                AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(getActivity());
                alertDialogBuilder.setMessage(getString(R.string.checkGps))
                        .setCancelable(false)
                        .setPositiveButton(getString(R.string.goGps),
                                new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int id) {
                                        Intent callGPSSettingIntent = new Intent(
                                                android.provider.Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                                        startActivity(callGPSSettingIntent);
                                    }
                                });
                alertDialogBuilder.setNegativeButton(getString(R.string.cancel),
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                dialog.cancel();
                            }
                        });
                AlertDialog alert = alertDialogBuilder.create();

                try {

                    //alert.show();    // 2021-01-11 알림창 제거

                } catch (Exception e) {
                    Log.e(TAG, e.getLocalizedMessage());
                    e.printStackTrace();
                }

            }

    }

}

