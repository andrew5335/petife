package kr.co.ainus.petife2.viewmodel;

import android.content.Context;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;
import java.util.StringTokenizer;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;
import kr.co.ainus.petife2.util.GsonHelper;
import kr.co.ainus.petife2.util.SharedPreferencesHelper;
import kr.co.ainus.petica_api.ApiHelper;
import kr.co.ainus.petica_api.model.domain.Feed;
import kr.co.ainus.petica_api.model.request.FeedRequest;
import kr.co.ainus.petica_api.model.response.FeedResponse;
import kr.co.ainus.petica_api.model.type.FeedModeType;
import kr.co.ainus.petica_api.model.type.FeedType;

public class FeedViewModel extends ViewModel {
    private static final String TAG = "FeedViewModel";

    private static final String SUFFIX_FEED_FREE_LATENCY = "_feed_free_latency";
    private static final String SUFFIX_FEEDER_TIME_SET = "_feeder_time_set";
    private static final String SUFFIX_WATER_TIME_SET = "_water_time_set";

    private final MutableLiveData<Boolean> hasLoadingLiveData = new MutableLiveData<>();
    private final MutableLiveData<List<Feed>> feedListLiveData = new MutableLiveData<>();
    private final MutableLiveData<Integer> feedFreeLatencyLiveData = new MutableLiveData<>();

    public MutableLiveData<Boolean> getHasLoadingLiveData() {
        return hasLoadingLiveData;
    }

    public MutableLiveData<List<Feed>> getFeedListLiveData() {
        return feedListLiveData;
    }

    public MutableLiveData<Integer> getFeedFreeLatencyLiveData() {
        return feedFreeLatencyLiveData;
    }

    public void loadFeedList(String deviceId) {
        hasLoadingLiveData.setValue(true);

        ApiHelper.feedList(deviceId, new ApiHelper.SuccessHandler() {
            @Override
            public <V> void onSuccess(V response) {
                if (response instanceof FeedResponse) {
                    List<Feed> feedList = ((FeedResponse) response).getItems();

                    feedListLiveData.setValue(feedList);
                }

            }
        }, new ApiHelper.FailureHandler() {
            @Override
            public void onFailure(Throwable t) {

            }
        }, new ApiHelper.CompleteHandler() {
            @Override
            public void onComplete() {
                hasLoadingLiveData.setValue(false);

            }
        });
    }

    public void clearFeedList(String uuid, String deviceId, FeedType feedType, ApiHelper.SuccessHandler successHandler, ApiHelper.FailureHandler failureHandler, ApiHelper.CompleteHandler completeHandler) {
        ApiHelper.clearFeedList(uuid, deviceId, feedType, successHandler, failureHandler, completeHandler);
    }

    public void feedAdd(String uuid, String deviceId, FeedModeType feedMode, FeedType feedType, int hour, int minute, int amount) {
        hasLoadingLiveData.setValue(true);

        feedAdd(uuid, deviceId, feedMode, feedType, hour, minute, amount, new ApiHelper.SuccessHandler() {
            @Override
            public <V> void onSuccess(V response) {

                Log.i(TAG, GsonHelper.getGson().toJson(response));

            }
        }, new ApiHelper.FailureHandler() {
            @Override
            public void onFailure(Throwable t) {

            }
        }, new ApiHelper.CompleteHandler() {
            @Override
            public void onComplete() {
                hasLoadingLiveData.setValue(false);

            }
        });
    }

    public void feedAdd(String uuid
            , String deviceId
            , FeedModeType feedMode
            , FeedType feedType
            , int hour
            , int minute
            , int amount
            , ApiHelper.SuccessHandler successHandler
            , ApiHelper.FailureHandler failureHandler
            , ApiHelper.CompleteHandler completeHandler) {
        hasLoadingLiveData.setValue(true);

        ApiHelper.feedAdd(uuid, deviceId, feedMode, feedType, hour, minute, amount, successHandler, failureHandler, completeHandler);
    }

    public void feedUpdate(FeedRequest request) {
        hasLoadingLiveData.setValue(true);

        ApiHelper.feedUpdate(request, new ApiHelper.SuccessHandler() {
            @Override
            public <V> void onSuccess(V response) {

            }
        }, new ApiHelper.FailureHandler() {
            @Override
            public void onFailure(Throwable t) {

            }
        }, new ApiHelper.CompleteHandler() {
            @Override
            public void onComplete() {
                hasLoadingLiveData.setValue(false);

            }
        });
    }

    public void feedRemove(String uuid, String deviceId, FeedModeType feedMode, FeedType feedType, int hour, int min, int amount) {

        hasLoadingLiveData.setValue(true);

        ApiHelper.feedRemove(uuid, deviceId, feedMode, feedType, hour, min, amount, new ApiHelper.SuccessHandler() {
            @Override
            public <V> void onSuccess(V response) {

            }
        }, new ApiHelper.FailureHandler() {
            @Override
            public void onFailure(Throwable t) {

            }
        }, new ApiHelper.CompleteHandler() {
            @Override
            public void onComplete() {
                hasLoadingLiveData.setValue(false);

            }
        });

    }

    public void saveFeedFreeLatencyPrefences(Context context, String peticaId, int laytency) {
        SharedPreferencesHelper.putInt(context, peticaId + SUFFIX_FEED_FREE_LATENCY, laytency);
    }

    public void loadFeedFreeLatencyPrefences(Context context, String peticaId) {

        int feedFreeLatency = SharedPreferencesHelper.getInt(context, peticaId + SUFFIX_FEED_FREE_LATENCY);
        feedFreeLatencyLiveData.setValue(feedFreeLatency);
    }

    public void loadFeedListPreferences(Context context, FeedType feedType, String peticaId) {
        String suffix = null;

        switch (feedType) {
            case FEEDER:
                suffix = SUFFIX_FEEDER_TIME_SET;
                break;

            case WATER:
                suffix = SUFFIX_WATER_TIME_SET;
                break;
        }

        String feedListString = SharedPreferencesHelper.getString(context, peticaId + suffix);

        if (feedListString == null) {
            return;
        }

        StringTokenizer stringTokenizer = new StringTokenizer(feedListString, ",");

        List<Feed> feedList = new ArrayList<>();

        while (stringTokenizer.hasMoreTokens()) {
            try {
                Feed feed = new Feed();

                int hour = Integer.parseInt(stringTokenizer.nextToken());
                int minute = Integer.parseInt(stringTokenizer.nextToken());
                int amount = Integer.parseInt(stringTokenizer.nextToken());

                feed.setType(feedType);
                feed.setHour(hour);
                feed.setMin(minute);
                feed.setAmount(amount);

                feedList.add(feed);

            } catch (Exception e) {
                Log.e(TAG, e.getLocalizedMessage());
                e.printStackTrace();
            }

        }

        feedListLiveData.setValue(feedList);
    }

    public void saveFeedListPreferences(Context context, FeedType feedType, String peticaId, List<Feed> feedList) {

        String suffix = null;

        switch (feedType) {
            case FEEDER:
                suffix = SUFFIX_FEEDER_TIME_SET;
                break;

            case WATER:
                suffix = SUFFIX_WATER_TIME_SET;
                break;
        }

        StringBuilder stringBuilder = new StringBuilder();

        for (Feed feed : feedList) {
            stringBuilder.append(feed.getHour());
            stringBuilder.append(",");

            stringBuilder.append(feed.getMin());
            stringBuilder.append(",");

            stringBuilder.append(feed.getAmount());
            stringBuilder.append(",");
        }

        SharedPreferencesHelper.putString(context, peticaId + suffix, stringBuilder.toString());
    }


    public void saveToServer(String uuid, String peticaId, FeedType feedType, List<Feed> feedList) {
        clearFeedList(uuid, peticaId, feedType, new ApiHelper.SuccessHandler() {
            @Override
            public <V> void onSuccess(V response) {

                Log.i(TAG, GsonHelper.getGson().toJson(response));

                for (Feed feed : feedList) {
                    feedAdd(uuid, peticaId, FeedModeType.AUTO, feed.getType(), feed.getHour(), feed.getMin(), feed.getAmount());
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
