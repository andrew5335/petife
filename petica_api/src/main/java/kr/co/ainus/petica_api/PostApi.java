package kr.co.ainus.petica_api;

import com.google.gson.JsonObject;

import kr.co.ainus.petica_api.model.response.PostResponse;
import okhttp3.MultipartBody;
import retrofit2.Call;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.Part;

public interface PostApi {

    @FormUrlEncoded
    @POST("post/list")
    Call<PostResponse> postList(
            @Field("uuid") String uuid);

    @Multipart
    @POST("post/add")
    Call<PostResponse> postAdd(
            @Part MultipartBody.Part uuid
            , @Part MultipartBody.Part mediaType
            , @Part MultipartBody.Part mediaFile
            , @Part MultipartBody.Part title
            , @Part MultipartBody.Part message);

    @Multipart
    @POST("post/update")
    Call<PostResponse> postUpdate(
            @Part MultipartBody.Part uuid
            , @Part MultipartBody.Part mediaType
            , @Part MultipartBody.Part mediaFile
            , @Part MultipartBody.Part title
            , @Part MultipartBody.Part message
            , @Part MultipartBody.Part idx);

    @FormUrlEncoded
    @POST("post/remove")
    Call<PostResponse> postRemove(
            @Field("uuid") String uuid
            , @Field("idx") long idx);
}
