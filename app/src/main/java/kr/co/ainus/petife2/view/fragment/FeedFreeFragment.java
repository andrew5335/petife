package kr.co.ainus.petife2.view.fragment;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import com.nabto.api.RemoteTunnel;

import androidx.annotation.Nullable;
import androidx.databinding.DataBindingUtil;

import java.util.Arrays;

import kr.co.ainus.peticaexcutor.callback.ReceiveCallback;
import kr.co.ainus.petife2.R;
import kr.co.ainus.petife2.databinding.FragmentFeedFreeBinding;
import kr.co.ainus.petife2.util.RandomPortHelper;
import kr.co.ainus.petife2.util.SharedPreferencesHelper;
import kr.co.ainus.petife2.view.dialog.NumberPickerSingleDialog2;
import kr.co.ainus.petica_api.model.domain.Petica;
import kr.co.ainus.petica_api.model.type.FeedType;
import kr.co.ainus.peticaexcutor.callback.FailCallback;
import kr.co.ainus.peticaexcutor.callback.SuccessCallback;

public class FeedFreeFragment extends _BaseFragment {

    private static final String TAG = "FeedFreeFragment";
    private static Petica petica;
    private static FeedType feedType;

    private static final String SUFFIX_WATER_FREE_LATENCY = "_water_free_laency";

    private FragmentFeedFreeBinding dataBinding;
    private String peticaId;
    private int randomPortAudio = RandomPortHelper.make();

    private int petifeVersion = 0;

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

        // petife version 확인 처리 추가 2021-05-28 by Andrew Kim
        peticaViewModel.onExecutePeticaVersionRequest("127.0.0.1", randomPortAudio
                , null
                , null
                , null
                , null
                , new ReceiveCallback() {
                    @Override
                    public void onReceive(byte[] peticaResponse) {
                        String result = Arrays.toString(peticaResponse);
                        String version = String.valueOf(peticaResponse[7]);
                        Log.e(TAG, "=========================version response : " + result);
                        Log.e(TAG, "=========================version info : " + version);

                        petifeVersion = Integer.parseInt(version);
                    }
                });
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

                Log.e(TAG, "=====petife version2 : " + petifeVersion);
                NumberPickerSingleDialog2 numberPickerSingleDialog = new NumberPickerSingleDialog2(getActivity(), FeedType.ALL);
                numberPickerSingleDialog.setOnConfirmClickListner(new NumberPickerSingleDialog2.OnClickListener() {
                    @Override
                    public void onClick(NumberPickerSingleDialog2 numberPickerSingleDialog, int selectValue) {
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

        // petife version이 110 이상이면서 급수모드라면 sharedpreference에 저장해둔 값 사
        if(petifeVersion >= 110 && feedType == FeedType.WATER) {
            int waterLatency = 0;
            waterLatency = SharedPreferencesHelper.getInt(getContext(), peticaId + SUFFIX_WATER_FREE_LATENCY);
            if (waterLatency > 0) {
                dataBinding.setMinute(waterLatency);
            } else {
                dataBinding.setMinute(10);
            }

        } else {
            feedViewModel.getFeedFreeLatencyLiveData().observe(this, feedFreeLatency -> {
                dataBinding.setMinute(feedFreeLatency);
            });
        }
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

        Log.e(TAG, "=====petife version3 : " + petifeVersion);
        // petife version이 110 이상이면서 급수 모드라면 신규 프로토콜 호출
        if(petifeVersion >= 110 && feedType == FeedType.WATER) {
            Log.e(TAG, "===== version new protocol");
            peticaViewModel.onExecutePeticaWaterFree("127.0.0.1"
                    , randomPortAudio
                    , latency
                    , 2
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

            SharedPreferencesHelper.putInt(getContext(), peticaId + SUFFIX_WATER_FREE_LATENCY, latency);
        } else {
            Log.e(TAG, "===== version old protocol");
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
    }

    private void load(Context context, String peticaId) {

        feedViewModel.loadFeedFreeLatencyPrefences(context, peticaId);
    }
}