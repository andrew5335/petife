package kr.co.ainus.petife2.view.activity;

import android.app.Activity;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import java.io.Serializable;
import java.util.Arrays;

import androidx.databinding.DataBindingUtil;
import androidx.viewpager2.widget.ViewPager2;

import kr.co.ainus.peticaexcutor.callback.ReceiveCallback;
import kr.co.ainus.petife2.R;
import kr.co.ainus.petife2.apapter.FeedFragmentAdapter;
import kr.co.ainus.petife2.databinding.ActivityFeedBinding;
import kr.co.ainus.petife2.util.GsonHelper;
import kr.co.ainus.petica_api.ApiHelper;
import kr.co.ainus.petica_api.model.domain.Petica;
import kr.co.ainus.petica_api.model.response.DeviceResponse;
import kr.co.ainus.petica_api.model.type.FeedModeType;
import kr.co.ainus.petica_api.model.type.FeedType;
import kr.co.ainus.petife2.util.RandomPortHelper;

public class FeedActivity extends _BaseNavigationActivity {

    private static final String TAG = "FeedActivity";

    private ActivityFeedBinding dataBinding;
    private String peticaJson;
    private Serializable feedTypeJson;
    private Serializable feedModeTypeJson;
    private Petica petica;
    private FeedType feedType;
    private FeedModeType feedModeType;
    private FeedModeType feedModeType2;

    private int randomPortAudio = RandomPortHelper.make();
    private int petifeVersion = 0;
    private String peticaDeviceId = "";
    private String tmpPetifeVersion = "";
    private String[] petifeVersionInfo;

    // 설정값 저장을 위해 추가 2021-05-28 by Andrew Kim
    private SharedPreferences pref;
    private SharedPreferences.Editor editor;
    private String feedSet = "";
    private String waterSet = "";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // version이 110 이상인 경우 sharedpreference에 저장해놓은 급식 설정값 사용 추가 2021-05-28 by Andrew Kim
        pref = getSharedPreferences("pref", Activity.MODE_PRIVATE);
        editor = pref.edit();

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

        tmpPetifeVersion = pref.getString("petifeVersion", null);

        if(null != tmpPetifeVersion && !"".equals(tmpPetifeVersion) && 0 < tmpPetifeVersion.length()) {
            petifeVersionInfo = tmpPetifeVersion.split("#");
            peticaDeviceId = petifeVersionInfo[0];
            petifeVersion = Integer.parseInt(petifeVersionInfo[1]);
        }

        peticaViewModel.loadDevice(uuid, petica.getDeviceId(), new ApiHelper.SuccessHandler() {
            @Override
            public <V> void onSuccess(V response) {
                if (response instanceof DeviceResponse) {
                    if (((DeviceResponse) response).getItems().size() > 0) {
                        Petica petica = ((DeviceResponse) response).getItems().get(0);
                        //dataBinding.setPetica(petica);
                        dataBinding.setFeedMode(petica.getFeedMode());

                        Log.e(TAG, "===== version feedType : " + feedType);

                        if(peticaDeviceId.equals(petica.getDeviceId()) && petifeVersion >= 110) {
                            feedModeType = FeedModeType.MANUAL;
                            feedModeType2 = FeedModeType.MANUAL;
                            feedSet = pref.getString("feedSet", "manual");
                            waterSet = pref.getString("waterSet", "manual");

                            Log.e(TAG, "===== version pref feedSet : " + feedSet);
                            Log.e(TAG, "===== version pref waterSet : " + waterSet);

                            if(null != feedSet && !"".equals(feedSet) && 0 < feedSet.length()) {
                                if("manual".equals(feedSet)) {
                                    feedModeType = FeedModeType.MANUAL;
                                } else if("auto".equals(feedSet)) {
                                    feedModeType = FeedModeType.AUTO;
                                } else {
                                    feedModeType = FeedModeType.FREE;
                                }
                            }

                            if(null != waterSet && !"".equals(waterSet) && 0 < waterSet.length()) {
                                if("manual".equals(waterSet)) {
                                    feedModeType2 = FeedModeType.MANUAL;
                                } else if("auto".equals(waterSet)) {
                                    feedModeType2 = FeedModeType.AUTO;
                                } else {
                                    feedModeType2 = FeedModeType.FREE;
                                }
                            }

                            if(feedType == FeedType.WATER) {
                                petica.setFeedMode(feedModeType2);
                            } else {
                                petica.setFeedMode(feedModeType);
                            }
                        }

                        dataBinding.setPetica(petica);
                        dataBinding.setFeedMode(petica.getFeedMode());

                        //if(petifeVersion >= 110 && feedType == FeedType.WATER) {
                        //    setViewPager2(petica.getFeedMode2());
                        //} else {
                            setViewPager2(petica.getFeedMode());
                        //}
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

        if(petifeVersion >= 110 && feedType == FeedType.WATER) {
            dataBinding.setFeedMode(feedModeType2);
        } else {
            dataBinding.setFeedMode(feedModeType);
        }

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

