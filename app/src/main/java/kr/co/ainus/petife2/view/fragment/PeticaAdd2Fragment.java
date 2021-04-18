package kr.co.ainus.petife2.view.fragment;

import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.provider.Settings;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.databinding.DataBindingUtil;
import kr.co.ainus.petife2.R;
import kr.co.ainus.petife2.databinding.FragmentPeticaAdd2Binding;
import kr.co.ainus.petife2.util.RandomPasswordHelper;

public class PeticaAdd2Fragment extends _BaseFragment {

    private static final String TAG = "PeticaAdd2Fragment";

    /**
     * 2019-04-15
     * 패스워드 오입력 방지 1회만 생성
     */
    private final String NEW_PASSWORD = RandomPasswordHelper.make();

    private FragmentPeticaAdd2Binding dataBinding;
    private AlertDialog alertDialog3;

    private WifiManager wifiManager;
    private String curSsid;
    private boolean checkwifi = false;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setDataBinding();
        setViewModel();

        wifiManager = (WifiManager)getContext().getSystemService(Context.WIFI_SERVICE);
        curSsid = "";
    }

    @Override
    public void onStart() {
        super.onStart();

        if (peticaViewModel.getDeviceAddStepLiveData().getValue() == 3) {
            curSsid = wifiManager.getConnectionInfo().getSSID();

            if (curSsid.contains("LTH") || curSsid.contains(("Petife"))) {
                peticaViewModel.getHasLoadingLiveData().setValue(false);
                peticaViewModel.getHasSuccessPeticaListScan().setValue(null);
                Log.e(TAG, "공유기명이 LTH/Petife로 시작함.");

                if (getActivity() != null) {
                    /**
                    alertDialog = new AlertDialog.Builder(getActivity())
                            .setTitle("등록 실패")
                            .setMessage("Wifi 설정 화면에서 일반 공유기 선택 후 다시 진행해주세요.")
                            .setPositiveButton(getString(R.string.confirm), ((dialog, which) -> {
                                Intent callGPSSettingIntent = new Intent(
                                        Settings.ACTION_WIFI_SETTINGS);
                                startActivity(callGPSSettingIntent);
                            }))
                            .create();
                     **/
                    try {
                        //alertDialog.show();

                        Toast.makeText(getContext(), getString(R.string.goWifiSetting), Toast.LENGTH_SHORT).show();
                        Intent callGPSSettingIntent = new Intent(
                                Settings.ACTION_WIFI_SETTINGS);
                        startActivity(callGPSSettingIntent);

                    } catch (Exception e) {
                        Log.e(TAG, e.getLocalizedMessage());
                        e.printStackTrace();
                    }
                }
            } else if (curSsid.contains("+")) {
                peticaViewModel.getHasLoadingLiveData().setValue(false);
                peticaViewModel.getHasSuccessPeticaListScan().setValue(null);
                Log.e(TAG, "공유기명에 + 가 있음");
                //Toast.makeText(getContext(), "LG U+ 고객센터에 전화하여 공유기 이름의 + 를 제거 후 등록하세요.", Toast.LENGTH_LONG).show();
                if (getActivity() != null) {
                    /**
                    alertDialog = new AlertDialog.Builder(getActivity())
                            .setTitle("등록 실패")
                            .setMessage("초기화 후 LG U+ 고객센터에 전화하여 공유기 이름의 + 를 제거 후 등록하세요.")
                            .setPositiveButton(getString(R.string.confirm), ((dialog, which) -> getActivity().finish()))
                            .create();
                     **/
                    try {
                        //alertDialog.show();

                        Toast.makeText(getContext(), getString(R.string.afterReset), Toast.LENGTH_LONG).show();
                        getActivity().finish();

                    } catch (Exception e) {
                        Log.e(TAG, e.getLocalizedMessage());
                        e.printStackTrace();
                    }
                }
            } else {
                checkwifi = true;
            }
        }

        if (peticaViewModel.getDeviceAddStepLiveData().getValue() == null || peticaViewModel.getSelectSsidInfoLiveData().getValue() == null)
            return;

        if (peticaViewModel.getDeviceAddStepLiveData().getValue() == 2) {
            peticaViewModel.checkCurrentSsid(getActivity(), true, peticaViewModel.getSelectSsidInfoLiveData().getValue().getSsid());
        }
    }

    @Override
    public void setDataBinding() {
        super.setDataBinding();

        dataBinding = DataBindingUtil.inflate(getLayoutInflater(), R.layout.fragment_petica_add_2, baseDataBinding.baseFragmentContainer, true);
        dataBinding.setLifecycleOwner(this);
    }

    @Override
    public void setViewModel() {
        super.setViewModel();

        peticaViewModel.getDeviceAddStepLiveData().observe(this, step -> {
            if (step == 2) {

                peticaViewModel.getSelectSsidInfoLiveData().observe(this, ssidInfo -> {
                    dataBinding.setSsidInfo(ssidInfo);
                });

                peticaViewModel.getHasCorrectSsid().observe(this, hasCorrectSsid -> {
                    if (hasCorrectSsid == null) return;

                    if (hasCorrectSsid) {
                        peticaViewModel.getDeviceAddStepLiveData().setValue(3);
                    } else {

                        /**
                        alertDialog = new AlertDialog.Builder(getActivity())
                                .setTitle(getString(R.string.wifiConnect))
                                .setTitle(getString(R.string.notice_wifi_connect1, dataBinding.getSsidInfo().getSsid()))
                                .setNegativeButton(getString(R.string.cancel), null)
                                .setPositiveButton(getString(R.string.wifiSet), ((dialog, which) -> {
                                    Intent callGPSSettingIntent = new Intent(
                                            Settings.ACTION_WIFI_SETTINGS);
                                    startActivity(callGPSSettingIntent);
                                }))
                                .setCancelable(false)
                                .create();
                         **/

                        try {
                            if(checkwifi) {
                                alertDialog3.dismiss();
                                peticaViewModel.getDeviceAddStepLiveData().setValue(3);
                            } else {
                                //alertDialog.show();

                                Toast.makeText(getContext(), getString(R.string.goWifiSetting), Toast.LENGTH_SHORT).show();
                                Intent callGPSSettingIntent = new Intent(
                                        Settings.ACTION_WIFI_SETTINGS);
                                startActivity(callGPSSettingIntent);

                                /**
                                alertDialog3 = new AlertDialog.Builder(getActivity())
                                        .setTitle("공유기 연결")
                                        .setMessage(dataBinding.getSsidInfo().getSsid() + "선택 후 다시 진행해주세요.")
                                        .setPositiveButton(getString(R.string.confirm), ((dialog, which) -> {
                                            Intent callGPSSettingIntent = new Intent(
                                                    Settings.ACTION_WIFI_SETTINGS);
                                            startActivity(callGPSSettingIntent);
                                        }))
                                        .create();

                                alertDialog3.show();
                                 **/
                            }

                        } catch (Exception e) {
                            Log.e(TAG, e.getLocalizedMessage());
                            e.printStackTrace();
                        }

                    }
                });

            } else {
                peticaViewModel.getSelectSsidInfoLiveData().removeObservers(this);
                peticaViewModel.getHasCorrectSsid().removeObservers(this);
            }
        });

    }
}

