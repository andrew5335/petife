package kr.co.ainus.petife2.viewmodel;

import android.util.Log;

import java.util.List;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;
import kr.co.ainus.petife2.util.GsonHelper;
import kr.co.ainus.petica_api.ApiHelper;
import kr.co.ainus.petica_api.model.domain.FeedHistory;
import kr.co.ainus.petica_api.model.response.FeedHistoryResponse;
import kr.co.ainus.petica_api.model.type.FeedType;

public class FeedHistoryViewModel extends ViewModel {
    private static final String TAG = "FeedHistoryViewModel";

    private final MutableLiveData<Boolean> hasLoadingLiveData = new MutableLiveData<>();
    private final MutableLiveData<List<FeedHistory>> feederHistoryLiveData = new MutableLiveData<>();
    private final MutableLiveData<List<FeedHistory>> waterHistoryLiveData = new MutableLiveData<>();

    public MutableLiveData<Boolean> getHasLoadingLiveData() {
        return hasLoadingLiveData;
    }

    public MutableLiveData<List<FeedHistory>> getFeederHistoryLiveData() {
        return feederHistoryLiveData;
    }

    public MutableLiveData<List<FeedHistory>> getWaterHistoryLiveData() {
        return waterHistoryLiveData;
    }

    public void loadFeedHistoryList(String uuid, String deviceId, int year) {
        loadYear(uuid, deviceId, year, FeedType.FEEDER);
        loadYear(uuid, deviceId, year, FeedType.WATER);
    }

    private void loadYear(String uuid, String deviceId, int year, FeedType type) {
        hasLoadingLiveData.setValue(true);

        ApiHelper.feedHistoryList(
                uuid
                , deviceId
                , year
                , type
                , new ApiHelper.SuccessHandler() {
                    @Override
                    public <V> void onSuccess(V response) {

                        Log.i(TAG, GsonHelper.getGson().toJson(response));

                        if (response instanceof FeedHistoryResponse) {
                            List<FeedHistory> feedHistoryList = ((FeedHistoryResponse) response).getItems();

                            switch (type) {
                                case FEEDER:
                                    feederHistoryLiveData.setValue(feedHistoryList);

                                    break;

                                case WATER:
                                    waterHistoryLiveData.setValue(feedHistoryList);

                                    break;
                            }
                        }

                    }
                }, new ApiHelper.FailureHandler() {
                    @Override
                    public void onFailure(Throwable t) {

                    }
                }, new ApiHelper.CompleteHandler() {
                    @Override
                    public void onComplete() {

                    }
                });
    }
}
