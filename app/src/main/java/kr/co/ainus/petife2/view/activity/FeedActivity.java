package kr.co.ainus.petife2.view.activity;

import android.os.Bundle;
import android.util.Log;
import android.view.View;

import java.io.Serializable;

import androidx.databinding.DataBindingUtil;
import androidx.viewpager2.widget.ViewPager2;
import kr.co.ainus.petife2.R;
import kr.co.ainus.petife2.apapter.FeedFragmentAdapter;
import kr.co.ainus.petife2.databinding.ActivityFeedBinding;
import kr.co.ainus.petife2.util.GsonHelper;
import kr.co.ainus.petica_api.ApiHelper;
import kr.co.ainus.petica_api.model.domain.Petica;
import kr.co.ainus.petica_api.model.response.DeviceResponse;
import kr.co.ainus.petica_api.model.type.FeedModeType;
import kr.co.ainus.petica_api.model.type.FeedType;

public class FeedActivity extends _BaseNavigationActivity {

    private static final String TAG = "FeedActivity";

    private ActivityFeedBinding dataBinding;
    private String peticaJson;
    private Serializable feedTypeJson;
    private Serializable feedModeTypeJson;
    private Petica petica;
    private FeedType feedType;
    private FeedModeType feedModeType;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        try {

            peticaJson = getIntent().getStringExtra("peticaJson");
            feedTypeJson = getIntent().getSerializableExtra("feedTypeJson");

            petica = GsonHelper.getGson().fromJson(peticaJson, Petica.class);
            feedType = GsonHelper.getGson().fromJson(feedTypeJson.toString(), FeedType.class);
            feedModeType = petica.getFeedMode();

        } catch (Exception e) {
            Log.e(TAG, e.getLocalizedMessage());
            e.printStackTrace();
        }

        setDataBinding();
        setViewModel();

        peticaViewModel.loadDevice(uuid, petica.getDeviceId(), new ApiHelper.SuccessHandler() {
            @Override
            public <V> void onSuccess(V response) {
                if (response instanceof DeviceResponse) {
                    if (((DeviceResponse) response).getItems().size() > 0) {
                        Petica petica = ((DeviceResponse) response).getItems().get(0);
                        dataBinding.setPetica(petica);
                        dataBinding.setFeedMode(petica.getFeedMode());

                        setViewPager2(petica.getFeedMode());
                    }
                }
            }
        }, new ApiHelper.FailureHandler() {
            @Override
            public void onFailure(Throwable t) {

            }
        });
    }

    @Override
    public void setDataBinding() {
        super.setDataBinding();

        baseNavigationBinding.btnRight.setVisibility(View.GONE);

        dataBinding = DataBindingUtil.inflate(getLayoutInflater(), R.layout.activity_feed, baseNavigationBinding.baseNavContainer, true);
        dataBinding.setLifecycleOwner(this);

        dataBinding.setPetica(petica);
        dataBinding.setFeedType(feedType);
        dataBinding.setFeedMode(feedModeType);

        switch (feedType) {
            case FEEDER:
                dataBinding.tvFeedMode.setText(getString(R.string.feedMode));
                baseNavigationBinding.tvTitle.setText(getString(R.string.startFeed));
                break;

            case WATER:
                dataBinding.tvFeedMode.setText(getString(R.string.waterMode));
                baseNavigationBinding.tvTitle.setText(getString(R.string.startWater));
                break;
        }
    }

    @Override
    public void setViewModel() {
        super.setViewModel();
    }

    private void setViewPager2(FeedModeType feedMode) {
        dataBinding.vp2Feed.setAdapter(new FeedFragmentAdapter(this, petica, feedType));
        dataBinding.vp2Feed.registerOnPageChangeCallback(new ViewPager2.OnPageChangeCallback() {
            @Override
            public void onPageSelected(int position) {
                super.onPageSelected(position);
            }
        });
        dataBinding.vp2Feed.setUserInputEnabled(false);

        switch (feedMode) {
            case MANUAL:
                dataBinding.vp2Feed.setCurrentItem(0, false);
                break;

            case AUTO:
                dataBinding.vp2Feed.setCurrentItem(1, false);
                break;

            case FREE:
                dataBinding.vp2Feed.setCurrentItem(2, false);
                break;
        }
    }
}

