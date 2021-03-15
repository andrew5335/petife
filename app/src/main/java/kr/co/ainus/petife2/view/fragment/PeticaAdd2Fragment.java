package kr.co.ainus.petife2.view.fragment;

import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.provider.Settings;
import android.util.Log;

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
    private AlertDialog alertDialog;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setDataBinding();
        setViewModel();
    }

    @Override
    public void onStart() {
        super.onStart();

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

                        Log.i(TAG, "다음 단계로 이동");
                        peticaViewModel.getDeviceAddStepLiveData().setValue(3);

                    } else {

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

                        try {

                            //alertDialog.show();
                            peticaViewModel.getDeviceAddStepLiveData().setValue(3);

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

