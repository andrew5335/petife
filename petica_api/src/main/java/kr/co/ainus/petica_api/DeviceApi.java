package kr.co.ainus.petica_api;

import org.json.JSONObject;

import kr.co.ainus.petica_api.model.response.DeviceResponse;
import kr.co.ainus.petica_api.model.type.FeedModeType;
import retrofit2.Call;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.Headers;
import retrofit2.http.POST;
import retrofit2.http.Query;

public interface DeviceApi {

    @FormUrlEncoded
    @POST("device")
    Call<DeviceResponse> device(
            @Field("uuid") String uuid
            , @Field("deviceId") String deviceId);

    @FormUrlEncoded
    @POST("device/list")
    Call<DeviceResponse> deviceList(
            @Field("uuid") String uuid);

    @FormUrlEncoded
    @POST("device/master")
    Call<DeviceResponse> deviceMaster(
            @Field("uuid") String uuid);

    @FormUrlEncoded
    @POST("device/slave")
    Call<DeviceResponse> deviceSlave(
            @Field("uuid") String uuid);

    @FormUrlEncoded
    @POST("device/add")
    Call<DeviceResponse> deviceAdd(
            @Field("uuid") String uuid
            , @Field("deviceId") String deviceId
            , @Field("deviceName") String deviceName
            , @Field("devicePw") String devicePw
            , @Field("feedMode") FeedModeType feedMode
            , @Field("feedMode2") FeedModeType feedMode2);

    @FormUrlEncoded
    @POST("device/update")
    Call<DeviceResponse> deviceUpdate(
            @Field("uuid") String uuid
            , @Field("deviceId") String deviceId
            , @Field("deviceName") String deviceName
            , @Field("feedMode") FeedModeType feedMode
            , @Field("feedMode2") FeedModeType feedMode2);

    @FormUrlEncoded
    @POST("device/remove")
    Call<DeviceResponse> deviceRemove(
            @Field("uuid") String uuid
            , @Field("deviceId") String deviceId);

    @Headers({
            "Accept: application/vnd.github.v3.full+json",
            "User-Agent: Retrofit-Sample-App"
    })
    @GET("192.168.100.1/param.cgi?action=update&group=wifi")
    Call<JSONObject> joinWifi(@Query("sta_ssid") String ssid, @Query("sta_auth_key") String password);

}
