package kr.co.ainus.petife2.view.activity;

import android.animation.ValueAnimator;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.view.animation.AccelerateInterpolator;

import androidx.annotation.Nullable;
import androidx.databinding.DataBindingUtil;
import kr.co.ainus.petife2.R;
import kr.co.ainus.petife2.databinding.ActivitySplashBinding;

public class SplashActivity extends _BaseActivity {

    private static final String TAG = "SplashActivity";

    private ActivitySplashBinding dataBinding;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setDataBinding();
        setViewModel();

        new Handler().postDelayed(() -> {

            startActivity(new Intent(getApplicationContext(), SignInActivity.class));
            finish();

        }, 1500);
    }

    @Override
    public void setDataBinding() {
        super.setDataBinding();

        dataBinding = DataBindingUtil.inflate(getLayoutInflater(), R.layout.activity_splash, baseDataBinding.baseContainer, true);
        dataBinding.setLifecycleOwner(this);

        // 에니메이션 관련 시작
        dataBinding.ivPhoto.measure(View.MeasureSpec.UNSPECIFIED, View.MeasureSpec.UNSPECIFIED);
        dataBinding.ivPhoto.getMeasuredWidth();
        dataBinding.ivPhoto.getMeasuredHeight();
        Log.i(TAG, "iv photo height = " + dataBinding.ivPhoto.getMeasuredHeight());

        ValueAnimator valueAnimator = ValueAnimator.ofInt(0, dataBinding.ivPhoto.getMeasuredHeight());
        valueAnimator.setInterpolator(new AccelerateInterpolator());
        valueAnimator.setDuration(500);
        valueAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                dataBinding.guideH1.setGuidelineEnd((Integer) animation.getAnimatedValue());
            }
        });
        valueAnimator.start();
        // 에니메이션 관련 끝

    }

    @Override
    public void setViewModel() {
        super.setViewModel();

        userViewModel.getHasLoadingLiveData().observe(this, hasLoading -> {
            baseViewModel.getHasLoadingLiveData().setValue(hasLoading);
        });

        userViewModel.getHasLoadingLiveData().setValue(false);
    }

}
