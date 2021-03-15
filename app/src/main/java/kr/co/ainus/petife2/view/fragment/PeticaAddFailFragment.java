package kr.co.ainus.petife2.view.fragment;

import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.databinding.DataBindingUtil;
import kr.co.ainus.petife2.R;
import kr.co.ainus.petife2.databinding.FragmentPeticaAddFailBinding;

public class PeticaAddFailFragment extends _BaseFragment {

    private FragmentPeticaAddFailBinding dataBinding;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setDataBinding();
        setViewModel();
    }

    @Override
    public void setDataBinding() {
        super.setDataBinding();

        dataBinding = DataBindingUtil.inflate(getLayoutInflater(), R.layout.fragment_petica_add_fail, baseDataBinding.baseFragmentContainer, true);
        dataBinding.setLifecycleOwner(this);

        if (getActivity() != null) {
            dataBinding.btnConfirm.setOnClickListener(v -> getActivity().finish());
        }

    }

    @Override
    public void setViewModel() {
        super.setViewModel();

        peticaViewModel.getDeviceAddStepLiveData().observe(this, step -> {

            if (step == 6) {


            } else {


            }
        });

    }
}
