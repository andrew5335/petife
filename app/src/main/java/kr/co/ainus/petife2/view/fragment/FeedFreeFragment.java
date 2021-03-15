package kr.co.ainus.petife2.view.fragment;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import com.nabto.api.RemoteTunnel;

import androidx.annotation.Nullable;
import androidx.databinding.DataBindingUtil;
import kr.co.ainus.petife2.R;
import kr.co.ainus.petife2.databinding.FragmentFeedFreeBinding;
import kr.co.ainus.petife2.util.RandomPortHelper;
import kr.co.ainus.petife2.util.SharedPreferencesHelper;
import kr.co.ainus.petife2.view.dialog.NumberPickerSingleDialog;
import kr.co.ainus.petica_api.model.domain.Petica;
import kr.co.ainus.petica_api.model.type.FeedType;
import kr.co.ainus.peticaexcutor.callback.FailCallback;
import kr.co.ainus.peticaexcutor.callback.SuccessCallback;

public class FeedFreeFragment extends _BaseFragment {

    private static final String TAG = "FeedFreeFragment";
    private static Petica petica;
    private static FeedType feedType;

    private FragmentFeedFreeBinding dataBinding;
    private String peticaId;
    private int randomPortAudio = RandomPortHelper.make();

    public static FeedFreeFragment newInstance(Petica petica, FeedType feedType) {
        FeedFreeFragment.petica = petica;
        FeedFreeFragment.feedType = feedType;

        return new FeedFreeFragment();
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setDataBinding();
        setViewModel();
    }

    @Override
    public void setDataBinding() {
        super.setDataBinding();

        dataBinding = DataBindingUtil.inflate(getLayoutInflater(), R.layout.fragment_feed_free, baseDataBinding.baseFragmentContainer, true);
        dataBinding.setLifecycleOwner(this);

        if (getActivity() != null) {

            int feedFreeLatency = SharedPreferencesHelper.getInt(getActivity(), "feed_free_latency");
            feedFreeLatency = feedFreeLatency >= 10 ? feedFreeLatency : 20;
            feedFreeLatency = feedFreeLatency <= 60 ? feedFreeLatency : 60;

            dataBinding.tvLatency.setText(String.valueOf(feedFreeLatency));
            dataBinding.btnFeedLatency.setOnClickListener(v -> {

                NumberPickerSingleDialog numberPickerSingleDialog = new NumberPickerSingleDialog(getActivity(), FeedType.ALL);
                numberPickerSingleDialog.setOnConfirmClickListner(new NumberPickerSingleDialog.OnClickListener() {
                    @Override
                    public void onClick(NumberPickerSingleDialog numberPickerSingleDialog, int selectValue) {
                        dataBinding.setMinute(selectValue);
                    }
                });

                try {

                    numberPickerSingleDialog.show();

                } catch (Exception e) {
                    Log.e(TAG, e.getLocalizedMessage());
                    e.printStackTrace();
                }


            });

            dataBinding.btnAction.setOnClickListener(v -> {
                save(getActivity(), petica.getDeviceId(), dataBinding.getMinute());
            });

        }

    }

    @Override
    public void setViewModel() {
        super.setViewModel();

        peticaViewModel.openRemoteTunnelUart(getActivity(), petica.getDeviceId(), randomPortAudio, new RemoteTunnel.OnResultListener() {
            @Override
            public void onResult(String id, String result) {

                if (result.equals("Local") || result.equals("Remote P2P")) {

                } else {

                    Log.e(TAG, "openRemoteTunnelUart 실패");
                }

            }
        });

        feedViewModel.getFeedFreeLatencyLiveData().observe(this, feedFreeLatency -> {
            dataBinding.setMinute(feedFreeLatency);
        });
    }

    @Override
    public void onStart() {
        super.onStart();

        load(getActivity(), petica.getDeviceId());
    }

    @Override
    public void onStop() {
        super.onStop();

        save(getActivity(), petica.getDeviceId(), dataBinding.getMinute());

        // RemoteTunnel.closeTunnels();
    }

    private void save(Context context, String peticaId, int latency) {

        peticaViewModel.onExecutePeticaFeedFeederFree("127.0.0.1"
                , randomPortAudio
                , latency
                , 10
                , new SuccessCallback() {
                    @Override
                    public void onSuccess() {
                        if (getActivity() != null && getActivity().getPackageName() != null) {
                            ((Activity) getActivity()).runOnUiThread(() -> {
                                Toast.makeText(getActivity(), "저장에 성공하였습니다", Toast.LENGTH_SHORT).show();
                            });
                        }
                    }
                }, new FailCallback() {
                    @Override
                    public void onFail() {
                        if (getActivity() != null && getActivity().getPackageName() != null) {
                            ((Activity) getActivity()).runOnUiThread(() -> {
                                Toast.makeText(getActivity(), "저장에 실패하였습니다", Toast.LENGTH_SHORT).show();
                            });
                        }
                    }
                }, null, null, null);

        feedViewModel.saveFeedFreeLatencyPrefences(context, peticaId, latency);
    }

    private void load(Context context, String peticaId) {

        feedViewModel.loadFeedFreeLatencyPrefences(context, peticaId);
    }
}
