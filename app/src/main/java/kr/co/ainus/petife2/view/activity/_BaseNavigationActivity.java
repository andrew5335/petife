package kr.co.ainus.petife2.view.activity;

import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.databinding.DataBindingUtil;
import kr.co.ainus.petife2.R;
import kr.co.ainus.petife2.databinding.ActivityBaseNavigationBinding;

abstract public class _BaseNavigationActivity extends _BaseActivity {

    protected ActivityBaseNavigationBinding baseNavigationBinding;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    public void setDataBinding() {
        super.setDataBinding();

        baseNavigationBinding = DataBindingUtil.inflate(getLayoutInflater(), R.layout._activity_base_navigation, baseDataBinding.baseContainer, true);
        baseNavigationBinding.setLifecycleOwner(this);
        baseNavigationBinding.btnLeft.setOnClickListener(v -> finish());
    }

    @Override
    public void setViewModel() {
        super.setViewModel();
    }
}
