package kr.co.ainus.petife2.view.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.databinding.DataBindingUtil;
import androidx.lifecycle.ViewModelProviders;

import com.nabto.api.RemoteTunnel;

import kr.co.ainus.petife2.R;
import kr.co.ainus.petife2.databinding.FragmentViewPagerMainBinding;
import kr.co.ainus.petife2.util.GsonHelper;
import kr.co.ainus.petife2.util.RandomPortHelper;
import kr.co.ainus.petife2.view.activity.FeedActivity;
import kr.co.ainus.petife2.view.activity.FeedHistoryActivity;
import kr.co.ainus.petife2.view.activity.PeticaCameraActivity;
import kr.co.ainus.petife2.view.activity.PeticaSettingActivity;
import kr.co.ainus.petife2.view.activity.PostActivity;
import kr.co.ainus.petife2.viewmodel.PeticaViewModel;
import kr.co.ainus.petica_api.model.domain.Petica;
import kr.co.ainus.petica_api.model.type.FeedType;

public class MainViewPagerFragment extends _BaseFragment {

    private static final String TAG = "MainViewPagerFragment";

    private final int UART_RANDOM_PORT = RandomPortHelper.make();

    private FragmentViewPagerMainBinding dataBinding;
    private Petica petica;

    public static MainViewPagerFragment newInstance(Petica petica) {
        MainViewPagerFragment mainViewPagerFragment = new MainViewPagerFragment();
        mainViewPagerFragment.petica = petica;

        return mainViewPagerFragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Log.e(TAG, "onCreated");
        Log.e(TAG, "petife = " + GsonHelper.getGson().toJson(petica));

        setDataBinding();
        setViewModel();
    }

    @Override
    public void onStart() {
        super.onStart();

        Log.d(TAG, "onStart");
        Log.e(TAG, "petife = " + GsonHelper.getGson().toJson(petica));

        if(null != petica) {
            peticaViewModel.loadDevice(uuid, petica.getDeviceId());
        }
    }

    @Override
    public void onStop() {
        super.onStop();

        if(null != petica) {
            RemoteTunnel.closeTunnel(petica.getDeviceId());
        }
    }

    @Override
    public void setDataBinding() {
        super.setDataBinding();

        dataBinding = DataBindingUtil.inflate(getLayoutInflater(), R.layout.fragment_view_pager_main, baseDataBinding.baseFragmentContainer, true);
        dataBinding.setLifecycleOwner(this);

        Log.i(TAG, "petife binding start");
        if (petica != null) {
            dataBinding.setPetica(petica);
            Log.i(TAG, "petife binding complete");
        } else {
            Log.e(TAG, "petife == null");
        }

        try {

            if (getActivity() != null) {
                dataBinding.ibFeedFeeder.setOnClickListener(v -> {
                    if (dataBinding.getPetica().getUser().getUuid().equals(uuid)) {
                        Intent intent = new Intent(getActivity(), FeedActivity.class);
                        intent.putExtra("peticaJson", GsonHelper.getGson().toJson(dataBinding.getPetica()));
                        intent.putExtra("feedTypeJson", FeedType.FEEDER);
                        getActivity().startActivity(intent);
                    } else {

                        try {

                            Toast.makeText(getActivity(), getString(R.string.friendRestrict), Toast.LENGTH_SHORT).show();

                        } catch (Exception e) {
                            Log.e(TAG, e.getLocalizedMessage());
                            e.printStackTrace();
                        }

                    }

                });

                dataBinding.ibFeedWater.setOnClickListener(v -> {
                    if (dataBinding.getPetica().getUser().getUuid().equals(uuid)) {
                        Intent intent = new Intent(getActivity(), FeedActivity.class);
                        intent.putExtra("peticaJson", GsonHelper.getGson().toJson(dataBinding.getPetica()));
                        intent.putExtra("feedTypeJson", FeedType.WATER);
                        getActivity().startActivity(intent);
                    } else {

                        try {

                            Toast.makeText(getActivity(), getString(R.string.friendRestrict), Toast.LENGTH_SHORT).show();

                        } catch (Exception e) {
                            Log.e(TAG, e.getLocalizedMessage());
                            e.printStackTrace();
                        }

                    }
                });

                dataBinding.ibVideo.setOnClickListener(v -> {
                    Intent intent = new Intent(getActivity(), PeticaCameraActivity.class);
                    intent.putExtra("peticaJson", GsonHelper.getGson().toJson(dataBinding.getPetica()));
                    startActivity(intent);
                });

                dataBinding.ibStatics.setOnClickListener(v -> {
                    Intent intent = new Intent(getActivity(), FeedHistoryActivity.class);
                    intent.putExtra("peticaJson", GsonHelper.getGson().toJson(dataBinding.getPetica()));
                    getActivity().startActivity(intent);
                });

                dataBinding.ibDiary.setOnClickListener(v -> {
                    getActivity().startActivity(new Intent(getActivity(), PostActivity.class));
                });

                dataBinding.ibSettings.setOnClickListener(v -> {
                    if (dataBinding.getPetica().getUser().getUuid().equals(uuid)) {
                        Intent intent = new Intent(getActivity(), PeticaSettingActivity.class);
                        intent.putExtra("peticaJson", GsonHelper.getGson().toJson(dataBinding.getPetica()));

                        startActivity(intent);
                    } else {

                        try {

                            Toast.makeText(getActivity(), getString(R.string.friendRestrict), Toast.LENGTH_SHORT).show();

                        } catch (Exception e) {
                            Log.e(TAG, e.getLocalizedMessage());
                            e.printStackTrace();
                        }

                    }

                });

                if (dataBinding.getPetica().getUser().getUuid().equals(uuid)) {
                    dataBinding.tvConnectMessage.setText(getResources().getString(R.string.notice_connection_info, dataBinding.getPetica().getDeviceName()));
                } else {
                    dataBinding.tvConnectMessage.setText(getResources().getString(R.string.notice_connection_info, dataBinding.getPetica().getDeviceName() + "(친구)"));
                }
            }

        } catch (Exception e) {
            Log.e(TAG, e.getLocalizedMessage());
            e.printStackTrace();
        }


    }

    @Override
    public void setViewModel() {
        super.setViewModel();

        peticaViewModel = ViewModelProviders.of(this).get(PeticaViewModel.class);
//        peticaViewModel.openRemoteTunnelUart(getActivity(), petica.getDeviceId(), UART_RANDOM_PORT, new RemoteTunnel.OnResultListener() {
//            @Override
//            public void onResult(String id, String result) {
//
//            }
//        });
        peticaViewModel.getCurrentPeticaLiveData().observe(this, petica -> {
            dataBinding.setPetica(petica);

            String feedModeString = null;

            switch (petica.getFeedMode()) {
                case MANUAL:
                    feedModeString = getString(R.string.manual);
                    break;

                case AUTO:
                    feedModeString = getString(R.string.reserve);
                    break;

                case FREE:
                    feedModeString = getString(R.string.free);
                    break;

            }

            if (getActivity() != null) {
                dataBinding.tvConnectMessage.setText(getResources().getString(R.string.notice_connection_info, petica.getDeviceName()));
            }
        });

    }
}
