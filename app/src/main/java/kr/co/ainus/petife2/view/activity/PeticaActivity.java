package kr.co.ainus.petife2.view.activity;

import androidx.databinding.DataBindingUtil;
import kr.co.ainus.petife2.R;
import kr.co.ainus.petife2.databinding.ActivityPeticaBinding;

public class PeticaActivity extends _BaseActivity {

    private static final String TAG = "PeticaActivity";

    private ActivityPeticaBinding dataBinding;

    @Override
    public void setDataBinding() {
        super.setDataBinding();

        dataBinding = DataBindingUtil.setContentView(this, R.layout.activity_petica);
        dataBinding.setLifecycleOwner(this);
    }

    @Override
    public void setViewModel() {
        super.setViewModel();
    }
}
