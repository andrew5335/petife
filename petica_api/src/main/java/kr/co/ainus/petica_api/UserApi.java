package kr.co.ainus.petica_api;

import kr.co.ainus.petica_api.model.response.UserResponse;
import retrofit2.Call;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.POST;

public interface UserApi {

    @FormUrlEncoded
    @POST("user/signIn")
    Call<UserResponse> signIn(
            @Field("email") String email
            , @Field("provider") String provider
            , @Field("uuid") String uuid
            , @Field("token") String token);
}
