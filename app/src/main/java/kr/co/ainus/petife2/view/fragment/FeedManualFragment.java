package kr.co.ainus.petife2.view.fragment;

import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.databinding.DataBindingUtil;

import com.nabto.api.RemoteTunnel;

import kr.co.ainus.petife2.R;
import kr.co.ainus.petife2.databinding.FragmentFeedManualBinding;
import kr.co.ainus.petife2.util.RandomPortHelper;
import kr.co.ainus.petife2.view.dialog.NumberPickerSingleDialog;
import kr.co.ainus.petica_api.ApiHelper;
import kr.co.ainus.petica_api.model.domain.Petica;
import kr.co.ainus.petica_api.model.response.DeviceResponse;
import kr.co.ainus.petica_api.model.type.FeedType;
import kr.co.ainus.peticaexcutor.callback.FailCallback;
import kr.co.ainus.peticaexcutor.callback.SuccessCallback;

public class FeedManualFragment extends _BaseFragment {

    private static final String TAG = "FeedManualFragment";
    private static Petica petica;
    private static FeedType feedType;

    private FragmentFeedManualBinding dataBinding;

    private int randomPortAudio = RandomPortHelper.make();

    public boolean dialogClick = false;

    public static FeedManualFragment newInstance(Petica petica, FeedType feedType) {
        FeedManualFragment.petica = petica;
        FeedManualFragment.feedType = feedType;

        return new FeedManualFragment();
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

        dataBinding = DataBindingUtil.inflate(getLayoutInflater(), R.layout.fragment_feed_manual, baseDataBinding.baseFragmentContainer, true);
        dataBinding.setLifecycleOwner(this);

        dataBinding.setPetica(petica);

        if (getActivity() != null) {
            dataBinding.btnFeedAmount.setOnClickListener(v -> {

                NumberPickerSingleDialog numberPickerSingleDialog = new NumberPickerSingleDialog(getActivity(), feedType);
                numberPickerSingleDialog.setOnConfirmClickListner(new NumberPickerSingleDialog.OnClickListener() {
                    @Override
                    public void onClick(NumberPickerSingleDialog numberPickerSingleDialog, int selectValue) {
                        Log.i(TAG, "=====================selectvalue : " + selectValue);
                        dataBinding.tvAmount.setText(String.valueOf(selectValue));
                        dialogClick = true;
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

                int amount = 0;

                if(dialogClick) {
                    amount = Integer.parseInt(dataBinding.tvAmount.getText().toString());
                } else {
                    amount = 3;
                }
                Log.i(TAG, "=============dialogclick : " + dialogClick);
                Log.i(TAG, "=============amount : " + amount);

                switch (feedType) {
                    case FEEDER:
                        if(dialogClick) {
                            amount = Integer.parseInt(dataBinding.tvAmount.getText().toString());
                        } else {
                            amount = 3;
                        }
                        Log.i(TAG, "================amount : " + amount);
                        peticaViewModel.onExecutePeticaFeedFeederManual("127.0.0.1", randomPortAudio, amount, new SuccessCallback() {
                            @Override
                            public void onSuccess() {

                                try {

                                    getActivity().runOnUiThread(() -> {
                                        Toast.makeText(getActivity(), getString(R.string.feedSuccess), Toast.LENGTH_SHORT).show();
                                    });

                                } catch (Exception e) {
                                    Log.e(TAG, e.getLocalizedMessage());
                                    e.printStackTrace();
                                }

                            }
                        }, new FailCallback() {
                            @Override
                            public void onFail() {

                                try {

                                    getActivity().runOnUiThread(() -> {
                                        Toast.makeText(getActivity(), getString(R.string.feedFail), Toast.LENGTH_SHORT).show();
                                    });

                                } catch (Exception e) {
                                    Log.e(TAG, e.getLocalizedMessage());
                                    e.printStackTrace();
                                }

                            }
                        }, null, null, null);
                        break;

                    case WATER:
                        peticaViewModel.onExecutePeticaFeedWaterManual("127.0.0.1", randomPortAudio, amount, new SuccessCallback() {
                            @Override
                            public void onSuccess() {

                                try {

                                    getActivity().runOnUiThread(() -> {
                                        Toast.makeText(getActivity(), getString(R.string.waterSuccess), Toast.LENGTH_SHORT).show();
                                    });

                                } catch (Exception e) {
                                    Log.e(TAG, e.getLocalizedMessage());
                                    e.printStackTrace();
                                }

                            }
                        }, new FailCallback() {
                            @Override
                            public void onFail() {

                                try {

                                    getActivity().runOnUiThread(() -> {
                                        Toast.makeText(getActivity(), getString(R.string.waterFail), Toast.LENGTH_SHORT).show();
                                    });

                                } catch (Exception e) {
                                    Log.e(TAG, e.getLocalizedMessage());
                                    e.printStackTrace();
                                }

                            }
                        }, null, null, null);
                        break;
                }

            });

            if (feedType == FeedType.FEEDER) {
                dataBinding.btnFeedAmount.setText(getString(R.string.feedLevel));
                dataBinding.tvAmount.setText("3");
                dataBinding.btnAction.setText(getString(R.string.startFeed));
            } else if (feedType == FeedType.WATER) {
                dataBinding.btnFeedAmount.setText(getString(R.string.waterLevel));
                dataBinding.tvAmount.setText("3");
                dataBinding.btnAction.setText(getString(R.string.startWater));
            }
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

        peticaViewModel.loadDevice(uuid, petica.getDeviceId(), new ApiHelper.SuccessHandler() {
            @Override
            public <V> void onSuccess(V response) {

                if (response instanceof DeviceResponse && ((DeviceResponse) response).getItems().size() > 0 && ((DeviceResponse) response).getItems().get(0) != null) {
                    petica = ((DeviceResponse) response).getItems().get(0);
                }

            }
        }, new ApiHelper.FailureHandler() {
            @Override
            public void onFailure(Throwable t) {

            }
        });
    }
}
