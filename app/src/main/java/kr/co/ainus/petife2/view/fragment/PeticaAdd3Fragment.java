package kr.co.ainus.petife2.view.fragment;

import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.provider.Settings;
import android.util.Log;
import android.widget.Toast;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.Calendar;
import java.util.Locale;

import androidx.annotation.Nullable;
import androidx.databinding.DataBindingUtil;
import kr.co.ainus.petife2.R;
import kr.co.ainus.petife2.databinding.FragmentPeticaAdd3Binding;
import kr.co.ainus.petife2.util.RandomPasswordHelper;
import kr.co.ainus.petica_api.model.type.FeedModeType;
import kr.co.ainus.peticaexcutor.callback.SuccessCallback;

public class PeticaAdd3Fragment extends _BaseFragment {

    private static final String TAG = "PeticaAdd3Fragment";

    /**
     * 2019-04-15
     * 패스워드 오입력 방지 1회만 생성
     */
    private final String NEW_PASSWORD = RandomPasswordHelper.make();

    private FragmentPeticaAdd3Binding dataBinding;
    private AlertDialog alertDialog;
    private WifiManager wifiManager;
    private String curSsid;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        wifiManager = (WifiManager)getContext().getSystemService(Context.WIFI_SERVICE);
        curSsid = "";

        setDataBinding();
        setViewModel();
    }

    @Override
    public void onStart() {
        super.onStart();

        /**
        curSsid = wifiManager.getConnectionInfo().getSSID();

        if(curSsid.contains("LTH") || curSsid.contains(("Petife"))) {
            peticaViewModel.getHasLoadingLiveData().setValue(false);
            peticaViewModel.getHasSuccessPeticaListScan().setValue(null);
            Log.e(TAG, "공유기명이 LTH/Petife로 시작함.");

            if (getActivity() != null) {
                alertDialog = new AlertDialog.Builder(getActivity())
                        .setTitle("등록 실패")
                        .setMessage("Wifi 설정 화면에서 일반 공유기 선택 후 다시 진행해주세요.")
                        .setPositiveButton(getString(R.string.confirm), ((dialog, which) -> {
                            Intent callGPSSettingIntent = new Intent(
                                    Settings.ACTION_WIFI_SETTINGS);
                            startActivity(callGPSSettingIntent);
                        }))
                        .create();
                try {
                    //alertDialog.show();

                    Toast.makeText(getContext(), "Wifi 설정 화면에서 일반 공유기 선택 후 다시 진행해주세요.", Toast.LENGTH_SHORT).show();
                    Intent callGPSSettingIntent = new Intent(
                            Settings.ACTION_WIFI_SETTINGS);
                    startActivity(callGPSSettingIntent);

                } catch (Exception e) {
                    Log.e(TAG, e.getLocalizedMessage());
                    e.printStackTrace();
                }
            }
        } else if(curSsid.contains("+")) {
            peticaViewModel.getHasLoadingLiveData().setValue(false);
            peticaViewModel.getHasSuccessPeticaListScan().setValue(null);
            Log.e(TAG, "공유기명에 + 가 있음");
            //Toast.makeText(getContext(), "LG U+ 고객센터에 전화하여 공유기 이름의 + 를 제거 후 등록하세요.", Toast.LENGTH_LONG).show();
            if (getActivity() != null) {
                alertDialog = new AlertDialog.Builder(getActivity())
                        .setTitle("등록 실패")
                        .setMessage("초기화 후 LG U+ 고객센터에 전화하여 공유기 이름의 + 를 제거 후 등록하세요.")
                        .setPositiveButton(getString(R.string.confirm), ((dialog, which) -> getActivity().finish()))
                        .create();
                try {
                    //alertDialog.show();

                    Toast.makeText(getContext(), "초기화 후 LG U+ 고객센터에 전화하여 공유기 이름의 + 를 제거 후 등록하세요.", Toast.LENGTH_LONG).show();
                    getActivity().finish();
                     
                } catch (Exception e) {
                    Log.e(TAG, e.getLocalizedMessage());
                    e.printStackTrace();
                }
            }
        } else {
            if(alertDialog != null) {
                alertDialog.dismiss();
            }
        }
        **/

        if (peticaViewModel.getDeviceAddStepLiveData().getValue() == null || peticaViewModel.getSelectSsidInfoLiveData().getValue() == null)
            return;

        if (peticaViewModel.getDeviceAddStepLiveData().getValue() == 2) {
            peticaViewModel.checkCurrentSsid(getActivity(), true, peticaViewModel.getSelectSsidInfoLiveData().getValue().getSsid());
        }
    }

    @Override
    public void setDataBinding() {
        super.setDataBinding();

        dataBinding = DataBindingUtil.inflate(getLayoutInflater(), R.layout.fragment_petica_add_3, baseDataBinding.baseFragmentContainer, true);
        dataBinding.setLifecycleOwner(this);
    }

    @Override
    public void setViewModel() {
        super.setViewModel();

        peticaViewModel.getDeviceAddStepLiveData().observe(this, step -> {

            if (step == 3) {
                peticaViewModel.getTryCount().setValue(0);
                peticaViewModel.scanPeticaList(getActivity(), 5000);

                peticaViewModel.getHasSuccessPeticaListScan().observe(this, hasSuccessPeticaListScan -> {

                    if (hasSuccessPeticaListScan == null) {
                        Log.e(TAG, "hasSuccessPeticaListScan == null");
                        return;
                    }

                    if (hasSuccessPeticaListScan) {
                            peticaViewModel.getHasSuccessPeticaListScan().setValue(null);

                            Log.i(TAG, "petica scan 성공");
                            Log.i(TAG, "petica update password 시작");
                            final String PETICA_IP = peticaViewModel.getCurrentPeticaIpLiveData().getValue();

                            peticaViewModel.peticaUpdatePassword(getActivity(), PETICA_IP, "admin", NEW_PASSWORD);
                            //peticaViewModel.peticaUpdatePassword(getActivity(), PETICA_IP, "admin", "90836242ab");
                    } else if (peticaViewModel.getTryCount().getValue() < 3) {

                        Log.i(TAG, "petica scan 재시도");
                        peticaViewModel.scanPeticaList(getContext(), 1000);
                        peticaViewModel.getTryCount().setValue(peticaViewModel.getTryCount().getValue() + 1);

                    } else {
                        peticaViewModel.getHasLoadingLiveData().setValue(false);
                        Log.e(TAG, "petica scan 실패");
                        peticaViewModel.getTryCount().setValue(0);

                        if (getActivity() != null) {
                            AlertDialog alertDialog = new AlertDialog.Builder(getActivity())
                                    .setTitle(getString(R.string.regFail))
                                    .setMessage(getString(R.string.tryAgain))
                                    .setPositiveButton(getString(R.string.confirm), ((dialog, which) -> getActivity().finish()))
                                    .create();
                            try {
                                alertDialog.show();
                            } catch (Exception e) {
                                Log.e(TAG, e.getLocalizedMessage());
                                e.printStackTrace();
                            }
                        }
                    }
                });

                peticaViewModel.getHasSuccessPeticaUpdatePassword().observe(this, hasSuccessPeticaUpdatePassword -> {

                    if (hasSuccessPeticaUpdatePassword == null) {
                        Log.e(TAG, "hasSuccessPeticaUpdatePassword == null");
                        return;
                    }

                    if (hasSuccessPeticaUpdatePassword) {
                        Log.i(TAG, "petica password update 성공");

                        String peticaId = peticaViewModel.getCurrentPeticaIdLiveData().getValue();
                        String peticaPassword = peticaViewModel.getCurrentPeticaPasswordLiveData().getValue();

                        Log.i(TAG, "petica add to server 시작");
                        peticaViewModel.peticaAddToServer(uuid, peticaId, peticaPassword, FeedModeType.MANUAL);
                        //peticaViewModel.peticaAddToServer(uuid, peticaId, "90836242ab", FeedModeType.MANUAL);


                    } else {
                        peticaViewModel.getHasLoadingLiveData().setValue(false);

                        // TODO Error Code

                        Log.e(TAG, "petica password update 실패");

                    }

                });

                peticaViewModel.getHasSuccessPeticaAddToServer().observe(this, hasSuccessPeticaAddToServer -> {

                    if (hasSuccessPeticaAddToServer == null) {
                        Log.e(TAG, "hasSuccessPeticaAddToServer == null");
                        return;
                    }

                    if (hasSuccessPeticaAddToServer) {
                        peticaViewModel.getHasLoadingLiveData().setValue(false);

                        Log.i(TAG, "petica add to server 성공");
                        Log.i(TAG, "다음 단계 진행합니다");
                        peticaViewModel.getDeviceAddStepLiveData().setValue(4);

                        peticaViewModel.onExecutePeticaInitialization(peticaViewModel.getCurrentPeticaIpLiveData().getValue(), 80, new SuccessCallback() {
                            @Override
                            public void onSuccess() {
                                Calendar calendar = Calendar.getInstance(Locale.KOREA);
                                int hour = calendar.get(Calendar.HOUR_OF_DAY);
                                int minute = calendar.get(Calendar.MINUTE);
                                int second = calendar.get(Calendar.SECOND);

                                peticaViewModel.onExecutePeticaClockSet(peticaViewModel.getCurrentPeticaIpLiveData().getValue(), 80
                                        , hour
                                        , minute
                                        , second
                                        , null, null, null, null, null);
                            }
                        }, null, null, null, null);

                    } else {
                        peticaViewModel.getHasLoadingLiveData().setValue(false);

                        // TODO Error Code

                        Log.e(TAG, "petica add to server 실패");
                        peticaViewModel.getDeviceAddStepLiveData().setValue(6);

                    }

                });

            } else {

                peticaViewModel.getHasSuccessPeticaListScan().removeObservers(this);
                peticaViewModel.getPeticaIpPeticaIdMapLiveData().removeObservers(this);
                peticaViewModel.getHasSuccessPeticaUpdatePassword().removeObservers(this);
                peticaViewModel.getHasSuccessPeticaAddToServer().removeObservers(this);

            }
        });

    }

    public boolean isInternetAvailable() {
        try {
            InetAddress address = InetAddress.getByName("www.google.com");
            return !address.equals("");
        } catch (UnknownHostException e) {
            // Log error
        }
        return false;
    }
}

