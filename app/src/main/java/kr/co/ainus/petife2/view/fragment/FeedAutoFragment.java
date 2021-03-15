package kr.co.ainus.petife2.view.fragment;

import android.app.AlertDialog;
import android.content.Context;
import android.os.Bundle;
import android.util.Log;

import com.nabto.api.RemoteTunnel;

import java.util.ArrayList;
import java.util.List;

import androidx.annotation.Nullable;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.LinearLayoutManager;
import kr.co.ainus.petife2.R;
import kr.co.ainus.petife2.apapter.FeedAdapter;
import kr.co.ainus.petife2.databinding.FragmentFeedAutoBinding;
import kr.co.ainus.petife2.util.RandomPortHelper;
import kr.co.ainus.petife2.view.dialog.NumberPickerMultiDialog;
import kr.co.ainus.petica_api.model.domain.Feed;
import kr.co.ainus.petica_api.model.domain.Petica;
import kr.co.ainus.petica_api.model.type.FeedModeType;
import kr.co.ainus.petica_api.model.type.FeedType;
import kr.co.ainus.peticaexcutor.callback.CompleteCallback;
import kr.co.ainus.peticaexcutor.callback.FailCallback;
import kr.co.ainus.peticaexcutor.callback.ReceiveCallback;
import kr.co.ainus.peticaexcutor.callback.SuccessCallback;

public class FeedAutoFragment extends _BaseFragment {

    private static final String TAG = "FeedAutoFragment";
    private static Petica petica;
    private static FeedType feedType;

    private FragmentFeedAutoBinding dataBinding;
    private FeedAdapter feedAdapter;

    private String ip = "127.0.0.1";
    private int randomPortAudio = RandomPortHelper.make();

    public static FeedAutoFragment newInstance(Petica petica, FeedType feedType) {
        FeedAutoFragment.petica = petica;
        FeedAutoFragment.feedType = feedType;

        return new FeedAutoFragment();
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setDataBinding();
        setViewModel();
    }

    @Override
    public void onStart() {
        super.onStart();

        saveToServer(uuid, petica.getDeviceId(), feedType, feedAdapter.getItems());

    }

    @Override
    public void onStop() {
        super.onStop();

        saveToServer(uuid, petica.getDeviceId(), feedType, feedAdapter.getItems());
    }

    @Override
    public void setDataBinding() {
        super.setDataBinding();

        dataBinding = DataBindingUtil.inflate(getLayoutInflater(), R.layout.fragment_feed_auto, baseDataBinding.baseFragmentContainer, true);
        dataBinding.setLifecycleOwner(this);

        if (getActivity() != null) {
            dataBinding.rvFeed.setLayoutManager(new LinearLayoutManager(getActivity()));

            feedAdapter = new FeedAdapter(new ArrayList<>());
            feedAdapter.addOnClickLIstner(new FeedAdapter.OnClickListener() {
                @Override
                public void onClick(Feed feed, int position) {
                    AlertDialog alertDialog1 = new AlertDialog.Builder(getActivity())
                            .setTitle(getString(R.string.manageReserveSche))
                            .setItems(new String[]{getString(R.string.modify), getString(R.string.delete2)}
                                    , ((dialog1, which1) -> {
                                        switch (which1) {
                                            case 0:

                                                NumberPickerMultiDialog numberPickerMultiDialog = new NumberPickerMultiDialog(getActivity(), petica.getDeviceId(), FeedModeType.AUTO, feedType);
                                                numberPickerMultiDialog.setOnConfirmClickListner(new NumberPickerMultiDialog.OnClickListener() {
                                                    @Override
                                                    public void onClick(NumberPickerMultiDialog numberPickerlDialog, int hour, int minute, int amount) {
                                                        feedViewModel.feedRemove(uuid, petica.getDeviceId(), FeedModeType.AUTO, feed.getType(), feed.getHour(), feed.getMin(), feed.getAmount());

                                                        Feed updateFeed = new Feed();
                                                        updateFeed.setType(FeedAutoFragment.feedType);
                                                        updateFeed.setHour(hour);
                                                        updateFeed.setMin(minute);
                                                        updateFeed.setAmount(amount);

                                                        feedAdapter.removeItem(position);
                                                        feedAdapter.addItem(updateFeed);

                                                        feedViewModel.feedAdd(uuid, petica.getDeviceId(), FeedModeType.AUTO, updateFeed.getType(), updateFeed.getHour(), updateFeed.getMin(), updateFeed.getAmount());

                                                        save(getActivity(), uuid, petica.getDeviceId(), feedType, feedAdapter.getItems());

                                                        peticaViewModel.onExecutePeticaFeedTimeSet(ip, randomPortAudio, feedType, feedAdapter.getItems()
                                                                , null, null, null, null
                                                                , new ReceiveCallback() {
                                                                    @Override
                                                                    public void onReceive(byte[] peticaResponse) {

                                                                    }
                                                                });
                                                    }
                                                });

                                                try {

                                                    numberPickerMultiDialog.show();

                                                } catch (Exception e) {
                                                    Log.e(TAG, e.getLocalizedMessage());
                                                    e.printStackTrace();
                                                }


                                                break;

                                            case 1:
                                                AlertDialog alertDialog2 = new AlertDialog.Builder(getActivity())
                                                        .setTitle(getString(R.string.deleteReserveSche))
                                                        .setNegativeButton(getString(R.string.cancel2), null)
                                                        .setPositiveButton(getString(R.string.delete2), (dialog2, which2) -> {
                                                            feedAdapter.removeItem(position);

                                                            feedViewModel.feedRemove(uuid, petica.getDeviceId(), FeedModeType.AUTO, feed.getType(), feed.getHour(), feed.getMin(), feed.getAmount());

                                                            save(getActivity(), uuid, petica.getDeviceId(), feedType, feedAdapter.getItems());

                                                        })
                                                        .create();

                                                try {

                                                    alertDialog2.show();

                                                } catch (Exception e) {
                                                    Log.e(TAG, e.getLocalizedMessage());
                                                    e.printStackTrace();
                                                }

                                                break;

                                        }
                                    }))
                            .setPositiveButton(getString(R.string.cancel), null)
                            .create();

                    try {

                        alertDialog1.show();

                    } catch (Exception e) {
                        Log.e(TAG, e.getLocalizedMessage());
                        e.printStackTrace();
                    }

                }
            });

            dataBinding.rvFeed.setAdapter(feedAdapter);

            dataBinding.btnFeedAdd.setOnClickListener(v -> {
                if (feedAdapter.getItemCount() < 5) {
                    NumberPickerMultiDialog numberPickerMultiDialog = new NumberPickerMultiDialog(getActivity(), petica.getDeviceId(), FeedModeType.AUTO, feedType);
                    numberPickerMultiDialog.setOnConfirmClickListner(new NumberPickerMultiDialog.OnClickListener() {
                        @Override
                        public void onClick(NumberPickerMultiDialog numberPickerlDialog, int hour, int minute, int amount) {
                            Feed feed = new Feed();
                            feed.setType(FeedAutoFragment.feedType);
                            feed.setHour(hour);
                            feed.setMin(minute);
                            feed.setAmount(amount);

                            feedAdapter.addItem(feed);

                            feedViewModel.feedAdd(uuid, petica.getDeviceId(), FeedModeType.AUTO, feed.getType(), feed.getHour(), feed.getMin(), feed.getAmount());
                            save(getActivity(), uuid, petica.getDeviceId(), feedType, feedAdapter.getItems());

                            peticaViewModel.onExecutePeticaFeedTimeSet(ip, randomPortAudio, feedType, feedAdapter.getItems()
                                    , new SuccessCallback() {
                                        @Override
                                        public void onSuccess() {

                                        }
                                    }, new FailCallback() {
                                        @Override
                                        public void onFail() {

                                        }
                                    }, new CompleteCallback() {
                                        @Override
                                        public void onComplete() {

                                        }
                                    }, null
                                    , new ReceiveCallback() {
                                        @Override
                                        public void onReceive(byte[] peticaResponse) {

                                        }
                                    });
                        }
                    });

                    try {

                        numberPickerMultiDialog.show();

                    } catch (Exception e) {
                        Log.e(TAG, e.getLocalizedMessage());
                        e.printStackTrace();
                    }

                } else {
                    AlertDialog alertDialog = new AlertDialog.Builder(getActivity())
                            .setTitle(getString(R.string.exceedReserveSche))
                            .setNegativeButton(getString(R.string.cancel), null)
                            .create();

                    try {

                        alertDialog.show();

                    } catch (Exception e) {
                        Log.e(TAG, e.getLocalizedMessage());
                        e.printStackTrace();
                    }

                }
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

        feedViewModel.loadFeedListPreferences(getActivity(), feedType, petica.getDeviceId());

        feedViewModel.getFeedListLiveData().observe(this, feedList -> {

            for (Feed feed : feedList) {
                feedAdapter.addItem(feed);
            }

            save(getActivity(), uuid, petica.getDeviceId(), feedType, feedAdapter.getItems());
        });
    }

    private void saveToServer(String uuid, String peticaId, FeedType feedType, List<Feed> feedList) {
        feedViewModel.saveToServer(uuid, peticaId, feedType, feedList);
    }

    private void save(Context context, String uuid, String peticaId, FeedType feedType, List<Feed> feedList) {
        feedViewModel.saveFeedListPreferences(context, feedType, peticaId, feedList);
    }
}


