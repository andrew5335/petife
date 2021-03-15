package kr.co.ainus.petife2.view.activity;

import androidx.databinding.DataBindingUtil;
import kr.co.ainus.petife2.R;
import kr.co.ainus.petife2.databinding.ActivityFriendBinding;

public class FriendActivity extends _BaseNavigationActivity {

    private static final String TAG = "FriendActivity";

    private ActivityFriendBinding dataBinding;

    @Override
    public void setDataBinding() {
        super.setDataBinding();

        dataBinding = DataBindingUtil.inflate(getLayoutInflater(), R.layout.activity_friend, baseNavigationBinding.baseNavContainer, true);
        dataBinding.setLifecycleOwner(this);
    }

    @Override
    public void setViewModel() {
        super.setViewModel();
    }
}
