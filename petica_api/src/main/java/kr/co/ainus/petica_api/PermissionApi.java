package kr.co.ainus.petica_api;

import com.google.gson.JsonObject;

import kr.co.ainus.petica_api.model.response.PermissionResponse;
import kr.co.ainus.petica_api.model.type.PermissionStateType;
import retrofit2.Call;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.POST;

public interface PermissionApi {

    @FormUrlEncoded
    @POST("permission/master")
    Call<PermissionResponse> permissionMaster(
            @Field("uuid") String uuid);

    @FormUrlEncoded
    @POST("permission/slave")
    Call<PermissionResponse> permissionSlave(
            @Field("uuid") String uuid
            , @Field("state") PermissionStateType state);

    @FormUrlEncoded
    @POST("permission/request")
    Call<PermissionResponse> permissionRequest(
            @Field("uuid") String uuid
            , @Field("masterEmail") String masterEmail);

    @FormUrlEncoded
    @POST("permission/accept")
    Call<PermissionResponse> permissionAccept(
            @Field("uuid") String uuid
            , @Field("idx") long idx);

    @FormUrlEncoded
    @POST("permission/deny")
    Call<PermissionResponse> permissionDeny(
            @Field("uuid") String uuid
            , @Field("idx") long idx);

    @FormUrlEncoded
    @POST("permission/cancel")
    Call<PermissionResponse> permissionCancel(
            @Field("uuid") String uuid
            , @Field("idx") long idx);
}
