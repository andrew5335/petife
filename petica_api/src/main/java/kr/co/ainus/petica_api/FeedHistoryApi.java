package kr.co.ainus.petica_api;

import com.google.gson.JsonObject;

import kr.co.ainus.petica_api.model.response.FeedHistoryResponse;
import kr.co.ainus.petica_api.model.type.FeedType;
import retrofit2.Call;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.POST;

public interface FeedHistoryApi {

    @FormUrlEncoded
    @POST("feedHistory/list")
    Call<FeedHistoryResponse> feedHistoryList(
            @Field("uuid") String uuid
            , @Field("deviceId") String deviceId
            , @Field("year") int year
            , @Field("type") FeedType feedType);
}
