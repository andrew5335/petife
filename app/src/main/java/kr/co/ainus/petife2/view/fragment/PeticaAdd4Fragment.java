package kr.co.ainus.petife2.view.fragment;

import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.databinding.DataBindingUtil;
import kr.co.ainus.petife2.R;
import kr.co.ainus.petife2.databinding.FragmentPeticaAdd4Binding;
import kr.co.ainus.petica_api.model.type.FeedModeType;

public class PeticaAdd4Fragment extends _BaseFragment {

    private static final String TAG = "PeticaAdd3Fragment";

    private FragmentPeticaAdd4Binding dataBinding;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setDataBinding();
        setViewModel();
    }

    @Override
    public void setDataBinding() {
        super.setDataBinding();

        dataBinding = DataBindingUtil.inflate(getLayoutInflater(), R.layout.fragment_petica_add_4, baseDataBinding.baseFragmentContainer, true);
        dataBinding.setLifecycleOwner(this);

        dataBinding.btnConfirm.setOnClickListener(v -> {

            try {

                if (dataBinding.etDeviceName.getText().toString() == null || dataBinding.etDeviceName.getText().toString().replace(" ", "").equals("")) {
                    Toast.makeText(getContext(), getString(R.string.writeName), Toast.LENGTH_SHORT).show();
                    return;
                }

            } catch (Exception e) {
                Log.e(TAG, e.getLocalizedMessage());
                e.printStackTrace();
            }


            peticaViewModel.getHasLoadingLiveData().setValue(true);

            peticaViewModel.peticaUpdateToServer(uuid
                    , peticaViewModel.getCurrentPeticaIdLiveData().getValue()
                    , dataBinding.etDeviceName.getText().toString()
                    , FeedModeType.MANUAL

            );
        });


    }

    @Override
    public void setViewModel() {
        super.setViewModel();

        peticaViewModel.getDeviceAddStepLiveData().observe(this, step -> {

            if (step == 4) {

                peticaViewModel.getHasSuccessPeticaUpdateToServer().observe(this, hasSuccessPeticaUpdateToServer -> {

                    if (hasSuccessPeticaUpdateToServer == null) {
                        Log.e(TAG, "hasSuccessPeticaUpdateToServer == null");
                        return;
                    }

                    if (hasSuccessPeticaUpdateToServer) {

                        peticaViewModel.getHasLoadingLiveData().setValue(false);

                        Log.i(TAG, "petica update to server 성공");
                        Log.i(TAG, "다음 단계로 진행합니다");
                        peticaViewModel.getDeviceAddStepLiveData().setValue(5);

                    } else {

                        peticaViewModel.getHasLoadingLiveData().setValue(false);

                        // TODO Error Code

                        Log.e(TAG, "petica update to server 실패");

                    }
                });

            } else {

                peticaViewModel.getHasSuccessPeticaUpdateToServer().removeObservers(this);

            }
        });

    }
}
