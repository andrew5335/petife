package kr.co.ainus.petica_api;

import kr.co.ainus.petica_api.model.response.FeedResponse;
import kr.co.ainus.petica_api.model.type.ExpireType;
import kr.co.ainus.petica_api.model.type.FeedModeType;
import kr.co.ainus.petica_api.model.type.FeedType;
import retrofit2.Call;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.POST;

public interface FeedApi {

    @FormUrlEncoded
    @POST("feed/list")
    Call<FeedResponse> feedList(
            @Field("deviceId") String deviceId);

    @FormUrlEncoded
    @POST("feed/clear")
    Call<FeedResponse> clearFeedList(
            @Field("uuid") String uuid
            , @Field("deviceId") String deviceId
            , @Field("type") FeedType feedType);

    @FormUrlEncoded
    @POST("feed/add")
    Call<FeedResponse> feedAdd(
            @Field("uuid") String uuid
            , @Field("deviceId") String deviceId
            , @Field("mode") FeedModeType mode
            , @Field("type") FeedType type
            , @Field("hour") int hour
            , @Field("min") int min
            , @Field("amount") int amount);

    @FormUrlEncoded
    @POST("feed/update")
    Call<FeedResponse> feedUpdate(
            @Field("uuid") String uuid
            , @Field("deviceId") String deviceId
            , @Field("type") FeedType type
            , @Field("hour") int hour
            , @Field("min") int min
            , @Field("amount") int amount
            , @Field("expire") ExpireType expire
            , @Field("idx") long idx);

    @FormUrlEncoded
    @POST("feed/remove")
    Call<FeedResponse> feedRemove(
            @Field("uuid") String uuid
            , @Field("deviceId") String deviceId
            , @Field("mode") FeedModeType feedMode
            , @Field("type") FeedType feedType
            , @Field("hour") int hour
            , @Field("min") int min
            , @Field("amount") int amount);
}
