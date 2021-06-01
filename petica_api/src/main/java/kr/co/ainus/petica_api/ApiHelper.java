package kr.co.ainus.petica_api;

import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import kr.co.ainus.petica_api.model.request.DeviceRequest;
import kr.co.ainus.petica_api.model.request.FeedRequest;
import kr.co.ainus.petica_api.model.request.PostRequest;
import kr.co.ainus.petica_api.model.response.DeviceResponse;
import kr.co.ainus.petica_api.model.response.FeedHistoryResponse;
import kr.co.ainus.petica_api.model.response.FeedResponse;
import kr.co.ainus.petica_api.model.response.PermissionResponse;
import kr.co.ainus.petica_api.model.response.PostResponse;
import kr.co.ainus.petica_api.model.response.UserResponse;
import kr.co.ainus.petica_api.model.type.FeedModeType;
import kr.co.ainus.petica_api.model.type.FeedType;
import kr.co.ainus.petica_api.model.type.PermissionStateType;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class ApiHelper {
    private static final String TAG = "ApiHelper";

    private static final ApiHelper INSTANCE = new ApiHelper();

    private static Retrofit retrofit;

//            public static final String PETICA_SERVER = "http://192.168.0.115:8080/";
//        public static final String PETICA_SERVER = "http://192.168.43.159:8080/";
//    public static final String PETICA_SERVER = "http://192.168.35.83:8080/";
//    public static final String PETICA_SERVER = "http://linrise.cafe24.com/";
    public static final String PETICA_SERVER = "http://rhodonghag2014.cafe24.com/";

    private ApiHelper() {
    }

    public static Retrofit getRetrofit() {
        Gson gson = new GsonBuilder().setDateFormat("yyyy-MM-dd'T'HH:mm:ss").create();

        if (retrofit == null) {
            retrofit = new Retrofit.Builder().baseUrl(PETICA_SERVER).addConverterFactory(GsonConverterFactory.create(gson)).build();
        }

        return retrofit;
    }

    public static void signIn(
            String email
            , String provider
            , String uuid
            , String token
            , final SuccessHandler successHandler
            , final FailureHandler failureHandler
            , final CompleteHandler completeHandler) {


        UserApi api = getRetrofit().create(UserApi.class);
        api.signIn(
                email
                , provider
                , uuid
                , token
        ).enqueue(new Callback<UserResponse>() {

            @Override
            public void onResponse(Call<UserResponse> call, Response<UserResponse> response) {
                if (successHandler != null) successHandler.onSuccess(response.body());
                if (completeHandler != null) completeHandler.onComplete();
            }

            @Override
            public void onFailure(Call<UserResponse> call, Throwable t) {
                if (failureHandler != null) failureHandler.onFailure(t);
                if (completeHandler != null) completeHandler.onComplete();
            }
        });
    }

    public static void device(String uuid, String deviceId, final SuccessHandler successHandler, final FailureHandler failureHandler, final CompleteHandler completeHandler) {
        DeviceRequest request = new DeviceRequest();
        request.setUuid(uuid);
        request.setDeviceId(deviceId);

        DeviceApi api = getRetrofit().create(DeviceApi.class);
        api.device(
                request.getUuid(),
                request.getDeviceId()
        ).enqueue(new Callback<DeviceResponse>() {

            @Override
            public void onResponse(Call<DeviceResponse> call, Response<DeviceResponse> response) {
                if (successHandler != null) successHandler.onSuccess(response.body());
                if (completeHandler != null) completeHandler.onComplete();
            }

            @Override
            public void onFailure(Call<DeviceResponse> call, Throwable t) {
                if (failureHandler != null) failureHandler.onFailure(t);
                if (completeHandler != null) completeHandler.onComplete();
            }
        });
    }

    public static void deviceList(
            DeviceRequest request
            , final SuccessHandler successHandler
            , final FailureHandler failureHandler
            , final CompleteHandler completeHandler) {


        DeviceApi api = getRetrofit().create(DeviceApi.class);
        api.deviceList(
                request.getUuid()
        ).enqueue(new Callback<DeviceResponse>() {

            @Override
            public void onResponse(Call<DeviceResponse> call, Response<DeviceResponse> response) {
                if (successHandler != null) successHandler.onSuccess(response.body());
                if (completeHandler != null) completeHandler.onComplete();
            }

            @Override
            public void onFailure(Call<DeviceResponse> call, Throwable t) {
                if (failureHandler != null) failureHandler.onFailure(t);
                if (completeHandler != null) completeHandler.onComplete();
            }
        });
    }

    public static void deviceMaster(
            DeviceRequest request
            , final SuccessHandler successHandler
            , final FailureHandler failureHandler
            , final CompleteHandler completeHandler) {


        DeviceApi api = getRetrofit().create(DeviceApi.class);
        api.deviceMaster(
                request.getUuid()
        ).enqueue(new Callback<DeviceResponse>() {

            @Override
            public void onResponse(Call<DeviceResponse> call, Response<DeviceResponse> response) {
                if (successHandler != null) successHandler.onSuccess(response.body());
                if (completeHandler != null) completeHandler.onComplete();
            }

            @Override
            public void onFailure(Call<DeviceResponse> call, Throwable t) {
                if (failureHandler != null) failureHandler.onFailure(t);
                if (completeHandler != null) completeHandler.onComplete();
            }
        });
    }

    public static void deviceSlave(
            DeviceRequest request
            , final SuccessHandler successHandler
            , final FailureHandler failureHandler
            , final CompleteHandler completeHandler
    ) {


        DeviceApi api = getRetrofit().create(DeviceApi.class);
        api.deviceSlave(
                request.getUuid()
        ).enqueue(new Callback<DeviceResponse>() {

            @Override
            public void onResponse(Call<DeviceResponse> call, Response<DeviceResponse> response) {
                if (successHandler != null) successHandler.onSuccess(response.body());
                if (completeHandler != null) completeHandler.onComplete();
            }

            @Override
            public void onFailure(Call<DeviceResponse> call, Throwable t) {
                if (failureHandler != null) failureHandler.onFailure(t);
                if (completeHandler != null) completeHandler.onComplete();
            }
        });
    }

    public static void deviceAdd(
            DeviceRequest request
            , final SuccessHandler successHandler
            , final FailureHandler failureHandler
            , final CompleteHandler completeHandler
    ) {


        DeviceApi api = getRetrofit().create(DeviceApi.class);
        api.deviceAdd(
                request.getUuid()
                , request.getDeviceId()
                , request.getDeviceName()
                , request.getDevicePw()
                , request.getFeedMode()
                , request.getFeedMode2()
        ).enqueue(new Callback<DeviceResponse>() {

            @Override
            public void onResponse(Call<DeviceResponse> call, Response<DeviceResponse> apiResponse) {
                if (successHandler != null) successHandler.onSuccess(apiResponse.body());
                if (completeHandler != null) completeHandler.onComplete();
            }

            @Override
            public void onFailure(Call<DeviceResponse> call, Throwable t) {
                if (failureHandler != null) failureHandler.onFailure(t);
                if (completeHandler != null) completeHandler.onComplete();
            }
        });
    }

    public static void deviceUpdate(
            DeviceRequest request
            , final SuccessHandler successHandler
            , final FailureHandler failureHandler
            , final CompleteHandler completeHandler
    ) {


        DeviceApi api = getRetrofit().create(DeviceApi.class);
        api.deviceUpdate(
                request.getUuid()
                , request.getDeviceId()
                , request.getDeviceName()
                , request.getFeedMode()
                , request.getFeedMode2()
        ).enqueue(new Callback<DeviceResponse>() {

            @Override
            public void onResponse(Call<DeviceResponse> call, Response<DeviceResponse> apiResponse) {
                if (successHandler != null) successHandler.onSuccess(apiResponse.body());
                if (completeHandler != null) completeHandler.onComplete();
            }

            @Override
            public void onFailure(Call<DeviceResponse> call, Throwable t) {
                if (failureHandler != null) failureHandler.onFailure(t);
                if (completeHandler != null) completeHandler.onComplete();
            }
        });
    }

    public static void deviceRemove(
            String uuid
            , String deviceId
            , final SuccessHandler successHandler
            , final FailureHandler failureHandler
            , final CompleteHandler completeHandler
    ) {

        DeviceApi api = getRetrofit().create(DeviceApi.class);
        api.deviceRemove(
                uuid
                , deviceId
        ).enqueue(new Callback<DeviceResponse>() {

            @Override
            public void onResponse(Call<DeviceResponse> call, Response<DeviceResponse> apiResponse) {
                if (successHandler != null) successHandler.onSuccess(apiResponse.body());
                if (completeHandler != null) completeHandler.onComplete();
            }

            @Override
            public void onFailure(Call<DeviceResponse> call, Throwable t) {
                if (failureHandler != null) failureHandler.onFailure(t);
                if (completeHandler != null) completeHandler.onComplete();
            }
        });
    }

    public static void feedList(
            String deviceId
            , final SuccessHandler successHandler
            , final FailureHandler failureHandler
            , final CompleteHandler completeHandler) {

        Log.i(TAG, "feedList");


        FeedApi api = getRetrofit().create(FeedApi.class);
        api.feedList(
                deviceId
        ).enqueue(new Callback<FeedResponse>() {

            @Override
            public void onResponse(Call<FeedResponse> call, Response<FeedResponse> apiResponse) {
                Log.i(TAG, "feedList.onResponse");

                if (successHandler != null) successHandler.onSuccess(apiResponse.body());
                if (completeHandler != null) completeHandler.onComplete();
            }

            @Override
            public void onFailure(Call<FeedResponse> call, Throwable t) {
                Log.i(TAG, "feedList.onFailure");

                if (failureHandler != null) failureHandler.onFailure(t);
                if (completeHandler != null) completeHandler.onComplete();
            }
        });
    }


    public static void clearFeedList(String uuid, String deviceId, FeedType feedType, final SuccessHandler successHandler, final FailureHandler failureHandler, final CompleteHandler completeHandler) {
        Log.i(TAG, "clearFeedList");


        FeedApi api = getRetrofit().create(FeedApi.class);
        api.clearFeedList(
                uuid
                , deviceId
                , feedType
        ).enqueue(new Callback<FeedResponse>() {

            @Override
            public void onResponse(Call<FeedResponse> call, Response<FeedResponse> apiResponse) {
                Log.i(TAG, "feedList.onResponse");

                if (successHandler != null) successHandler.onSuccess(apiResponse.body());
                if (completeHandler != null) completeHandler.onComplete();
            }

            @Override
            public void onFailure(Call<FeedResponse> call, Throwable t) {
                Log.i(TAG, "feedList.onFailure");

                if (failureHandler != null) failureHandler.onFailure(t);
                if (completeHandler != null) completeHandler.onComplete();
            }
        });
    }

    public static void feedAdd(
            String uuid
            , String deviceId
            , FeedModeType feedMode
            , FeedType feedType
            , int hour
            , int minute
            , int amount
            , final SuccessHandler successHandler
            , final FailureHandler failureHandler
            , final CompleteHandler completeHandler) {
        Log.i(TAG, "feedAdd");

        FeedApi api = getRetrofit().create(FeedApi.class);
        api.feedAdd(
                uuid
                , deviceId
                , feedMode
                , feedType
                , hour
                , minute
                , amount
        ).enqueue(new Callback<FeedResponse>() {

            @Override
            public void onResponse(Call<FeedResponse> call, Response<FeedResponse> apiResponse) {
                Log.i(TAG, "feedAdd.onResponse");

                if (successHandler != null) successHandler.onSuccess(apiResponse.body());
                if (completeHandler != null) completeHandler.onComplete();
            }

            @Override
            public void onFailure(Call<FeedResponse> call, Throwable t) {
                Log.i(TAG, "feedAdd.onFailure");

                if (failureHandler != null) failureHandler.onFailure(t);
                if (completeHandler != null) completeHandler.onComplete();
            }
        });
    }

    public static void feedUpdate(
            FeedRequest request
            , final SuccessHandler successHandler
            , final FailureHandler failureHandler
            , final CompleteHandler completeHandler) {
        Log.i(TAG, "feedAdd");


        FeedApi api = getRetrofit().create(FeedApi.class);

        api.feedUpdate(
                request.getUuid()
                , request.getDeviceId()
                , request.getType()
                , request.getHour()
                , request.getMin()
                , request.getAmount()
                , request.getExpire()
                , request.getIdx()

        ).enqueue(new Callback<FeedResponse>() {
            @Override
            public void onResponse(Call<FeedResponse> call, Response<FeedResponse> apiResponse) {
                Log.i(TAG, "feedAdd.onResponse");

                if (successHandler != null) successHandler.onSuccess(apiResponse.body());
                if (completeHandler != null) completeHandler.onComplete();
            }

            @Override
            public void onFailure(Call<FeedResponse> call, Throwable t) {
                Log.i(TAG, "feedAdd.onFailure");

                if (failureHandler != null) failureHandler.onFailure(t);
                if (completeHandler != null) completeHandler.onComplete();
            }
        });
    }

    public static void feedRemove(
            String uuid
            , String deviceId
            , FeedModeType feedMode
            , FeedType feedType
            , int hour
            , int min
            , int amount
            , final SuccessHandler successHandler
            , final FailureHandler failureHandler
            , final CompleteHandler completeHandler) {
        Log.i(TAG, "feedAdd");


        FeedApi api = getRetrofit().create(FeedApi.class);

        api.feedRemove(
                uuid
                , deviceId
                , feedMode
                , feedType
                , hour
                , min
                , amount
        ).enqueue(new Callback<FeedResponse>() {

            @Override
            public void onResponse(Call<FeedResponse> call, Response<FeedResponse> apiResponse) {
                Log.i(TAG, "feedAdd.onResponse");

                if (successHandler != null) successHandler.onSuccess(apiResponse.body());
                if (completeHandler != null) completeHandler.onComplete();
            }

            @Override
            public void onFailure(Call<FeedResponse> call, Throwable t) {
                Log.i(TAG, "feedAdd.onFailure");

                if (failureHandler != null) failureHandler.onFailure(t);
                if (completeHandler != null) completeHandler.onComplete();
            }
        });
    }

    public static void feedHistoryList(
            String uuid
            , String deviceId
            , int year
            , FeedType type
            , final SuccessHandler successHandler
            , final FailureHandler failureHandler
            , final CompleteHandler completeHandler) {

        Log.i(TAG, "feedHistoryList");

        FeedHistoryApi api = getRetrofit().create(FeedHistoryApi.class);
        api.feedHistoryList(
                uuid
                , deviceId
                , year
                , type
        ).enqueue(new Callback<FeedHistoryResponse>() {

            @Override
            public void onResponse(Call<FeedHistoryResponse> call, Response<FeedHistoryResponse> apiResponse) {
                Log.i(TAG, "feedHistoryList.onResponse");

                if (successHandler != null) successHandler.onSuccess(apiResponse.body());
                if (completeHandler != null) completeHandler.onComplete();
            }

            @Override
            public void onFailure(Call<FeedHistoryResponse> call, Throwable t) {
                Log.i(TAG, "feedHistoryList.onFailure");

                if (failureHandler != null) failureHandler.onFailure(t);
                if (completeHandler != null) completeHandler.onComplete();
            }
        });
    }


    public static void permissionMaster(
            String uuid
            , final SuccessHandler successHandler
            , final FailureHandler failureHandler
            , final CompleteHandler completeHandler) {
        Log.i(TAG, "permissionMaster");


        PermissionApi api = getRetrofit().create(PermissionApi.class);

        api.permissionMaster(
                uuid
        ).enqueue(new Callback<PermissionResponse>() {

            @Override
            public void onResponse(Call<PermissionResponse> call, Response<PermissionResponse> apiResponse) {
                Log.i(TAG, "permissionMaster.onResponse");

                if (successHandler != null) successHandler.onSuccess(apiResponse.body());
                if (completeHandler != null) completeHandler.onComplete();
            }

            @Override
            public void onFailure(Call<PermissionResponse> call, Throwable t) {
                Log.i(TAG, "permissionMaster.onFailure");

                if (failureHandler != null) failureHandler.onFailure(t);
                if (completeHandler != null) completeHandler.onComplete();
            }
        });
    }

    public static void permissionSlave(
            String uuid
            , PermissionStateType stateType
            , final SuccessHandler successHandler
            , final FailureHandler failureHandler
            , final CompleteHandler completeHandler) {
        Log.i(TAG, "permissionSlave");

        PermissionApi api = getRetrofit().create(PermissionApi.class);

        api.permissionSlave(
                uuid
                , stateType
        ).enqueue(new Callback<PermissionResponse>() {

            @Override
            public void onResponse(Call<PermissionResponse> call, Response<PermissionResponse> apiResponse) {
                Log.i(TAG, "permissionSlave.onResponse");

                if (successHandler != null) successHandler.onSuccess(apiResponse.body());
                if (completeHandler != null) completeHandler.onComplete();
            }

            @Override
            public void onFailure(Call<PermissionResponse> call, Throwable t) {
                Log.i(TAG, "permissionSlave.onFailure");

                if (failureHandler != null) failureHandler.onFailure(t);
                if (completeHandler != null) completeHandler.onComplete();
            }
        });
    }


    public static void permissionRequest(
            String uuid
            , String masterEmail
            , final SuccessHandler successHandler
            , final FailureHandler failureHandler
            , final CompleteHandler completeHandler) {
        Log.i(TAG, "permissionRequest");

        PermissionApi api = getRetrofit().create(PermissionApi.class);

        api.permissionRequest(
                uuid
                , masterEmail
        ).enqueue(new Callback<PermissionResponse>() {

            @Override
            public void onResponse(Call<PermissionResponse> call, Response<PermissionResponse> apiResponse) {
                Log.i(TAG, "permissionRequest.onResponse");

                if (successHandler != null) successHandler.onSuccess(apiResponse.body());
                if (completeHandler != null) completeHandler.onComplete();
            }

            @Override
            public void onFailure(Call<PermissionResponse> call, Throwable t) {
                Log.i(TAG, "permissionRequest.onFailure");

                if (failureHandler != null) failureHandler.onFailure(t);
                if (completeHandler != null) completeHandler.onComplete();
            }
        });
    }


    public static void permissionAccept(
            String uuid
            , long permissionIdx
            , final SuccessHandler successHandler
            , final FailureHandler failureHandler
            , final CompleteHandler completeHandler) {
        Log.i(TAG, "permissionAccept");


        PermissionApi api = getRetrofit().create(PermissionApi.class);

        api.permissionAccept(
                uuid
                , permissionIdx
        ).enqueue(new Callback<PermissionResponse>() {

            @Override
            public void onResponse(Call<PermissionResponse> call, Response<PermissionResponse> apiResponse) {
                Log.i(TAG, "permissionAccept.onResponse");

                if (successHandler != null) successHandler.onSuccess(apiResponse.body());
                if (completeHandler != null) completeHandler.onComplete();
            }

            @Override
            public void onFailure(Call<PermissionResponse> call, Throwable t) {
                Log.i(TAG, "permissionAccept.onFailure");

                if (failureHandler != null) failureHandler.onFailure(t);
                if (completeHandler != null) completeHandler.onComplete();
            }
        });
    }

    public static void permissionDeny(
            String uuid
            , long permissionIdx
            , final SuccessHandler successHandler
            , final FailureHandler failureHandler
            , final CompleteHandler completeHandler) {
        Log.i(TAG, "permissionDeny");


        PermissionApi api = getRetrofit().create(PermissionApi.class);

        api.permissionDeny(
                uuid
                , permissionIdx
        ).enqueue(new Callback<PermissionResponse>() {

            @Override
            public void onResponse(Call<PermissionResponse> call, Response<PermissionResponse> apiResponse) {
                Log.i(TAG, "permissionDeny.onResponse");

                if (successHandler != null) successHandler.onSuccess(apiResponse.body());
                if (completeHandler != null) completeHandler.onComplete();
            }

            @Override
            public void onFailure(Call<PermissionResponse> call, Throwable t) {
                Log.i(TAG, "permissionDeny.onFailure");

                if (failureHandler != null) failureHandler.onFailure(t);
                if (completeHandler != null) completeHandler.onComplete();
            }
        });
    }

    public static void permissionCancel(
            String uuid
            , long permissionIdx
            , final SuccessHandler successHandler
            , final FailureHandler failureHandler
            , final CompleteHandler completeHandler) {
        Log.i(TAG, "permissionCancel");

        PermissionApi api = getRetrofit().create(PermissionApi.class);

        api.permissionCancel(
                uuid
                , permissionIdx
        ).enqueue(new Callback<PermissionResponse>() {

            @Override
            public void onResponse(Call<PermissionResponse> call, Response<PermissionResponse> apiResponse) {
                Log.i(TAG, "permissionCancel.onResponse");

                if (successHandler != null) successHandler.onSuccess(apiResponse.body());
                if (completeHandler != null) completeHandler.onComplete();
            }

            @Override
            public void onFailure(Call<PermissionResponse> call, Throwable t) {
                Log.i(TAG, "permissionCancel.onFailure");

                if (failureHandler != null) failureHandler.onFailure(t);
                if (completeHandler != null) completeHandler.onComplete();
            }
        });
    }

    public static void postList(
            PostRequest request
            , final SuccessHandler successHandler
            , final FailureHandler failureHandler
            , final CompleteHandler completeHandler) {

        Log.i(TAG, "postList");


        PostApi api = getRetrofit().create(PostApi.class);
        api.postList(
                request.getUuid()
        ).enqueue(new Callback<PostResponse>() {

            @Override
            public void onResponse(Call<PostResponse> call, Response<PostResponse> apiResponse) {
                Log.i(TAG, "postList.onResponse");

                if (successHandler != null) successHandler.onSuccess(apiResponse.body());
                if (completeHandler != null) completeHandler.onComplete();
            }

            @Override
            public void onFailure(Call<PostResponse> call, Throwable t) {
                Log.i(TAG, "postList.onFailure");

                if (failureHandler != null) failureHandler.onFailure(t);
                if (completeHandler != null) completeHandler.onComplete();
            }
        });
    }


    public static void postAdd(
            PostRequest request
            , final SuccessHandler successHandler
            , final FailureHandler failureHandler
            , final CompleteHandler completeHandler) {
        Log.i(TAG, "postAdd");

        MultipartBody.Part mediaFile = null;
        if (request.getMediaFile() != null) {
            RequestBody fileReqBody =
                    request.getMediaType().equals(kr.co.ainus.petica_api.model.type.MediaType.IMAGE) ? RequestBody.create(MediaType.parse("image/*"), request.getMediaFile()) : RequestBody.create(MediaType.parse("video/*"), request.getMediaFile());
            // Create MultipartBody.Part using file request-body,file name and part name
            mediaFile = MultipartBody.Part.createFormData("mediaFile", request.getMediaFile().getName(), fileReqBody);
            //Create request body with text description and text media type
        }


        PostApi api = getRetrofit().create(PostApi.class);

        final MultipartBody.Part UUID = MultipartBody.Part.createFormData("uuid", request.getUuid());
        final MultipartBody.Part MEDIA_TYPE = MultipartBody.Part.createFormData("mediaType", request.getMediaType().toString());
        final MultipartBody.Part TITLE = MultipartBody.Part.createFormData("title", request.getTitle());
        final MultipartBody.Part MESSAGE = MultipartBody.Part.createFormData("message", request.getMessage());

        api.postAdd(UUID, MEDIA_TYPE, mediaFile, TITLE, MESSAGE).enqueue(new Callback<PostResponse>() {
            @Override
            public void onResponse(Call<PostResponse> call, Response<PostResponse> apiResponse) {
                Log.i(TAG, "postAdd.onResponse");

                if (successHandler != null) successHandler.onSuccess(apiResponse.body());
                if (completeHandler != null) completeHandler.onComplete();
            }

            @Override
            public void onFailure(Call<PostResponse> call, Throwable t) {
                Log.i(TAG, "postAdd.onFailure");

                if (failureHandler != null) failureHandler.onFailure(t);
                if (completeHandler != null) completeHandler.onComplete();
            }
        });
    }

    public static void postUpdate(
            PostRequest request
            , final SuccessHandler successHandler
            , final FailureHandler failureHandler
            , final CompleteHandler completeHandler) {
        Log.i(TAG, "postAdd");

        MultipartBody.Part mediaFile = null;
        if (request.getMediaFile() != null) {
            RequestBody fileReqBody =
                    request.getMediaType().equals(kr.co.ainus.petica_api.model.type.MediaType.IMAGE) ? RequestBody.create(MediaType.parse("image/*"), request.getMediaFile()) : RequestBody.create(MediaType.parse("video/*"), request.getMediaFile());
            mediaFile = MultipartBody.Part.createFormData("mediaFile", request.getMediaFile().getName(), fileReqBody);
        }


        PostApi api = getRetrofit().create(PostApi.class);

        final MultipartBody.Part UUID = MultipartBody.Part.createFormData("uuid", request.getUuid());
        final MultipartBody.Part MEDIA_TYPE = MultipartBody.Part.createFormData("mediaType", request.getMediaType().toString());
        final MultipartBody.Part TITLE = MultipartBody.Part.createFormData("title", request.getTitle());
        final MultipartBody.Part MESSAGE = MultipartBody.Part.createFormData("message", request.getMessage());
        final MultipartBody.Part IDX = MultipartBody.Part.createFormData("idx", String.valueOf(request.getIdx()));

        api.postUpdate(UUID, MEDIA_TYPE, mediaFile, TITLE, MESSAGE, IDX).enqueue(new Callback<PostResponse>() {
            @Override
            public void onResponse(Call<PostResponse> call, Response<PostResponse> apiResponse) {
                Log.i(TAG, "postAdd.onResponse");

                if (successHandler != null) successHandler.onSuccess(apiResponse.body());
                if (completeHandler != null) completeHandler.onComplete();
            }

            @Override
            public void onFailure(Call<PostResponse> call, Throwable t) {
                Log.i(TAG, "postAdd.onFailure");

                if (failureHandler != null) failureHandler.onFailure(t);
                if (completeHandler != null) completeHandler.onComplete();
            }
        });
    }

    public static void postRemove(
            PostRequest request
            , final SuccessHandler successHandler
            , final FailureHandler failureHandler
            , final CompleteHandler completeHandler) {
        Log.i(TAG, "postAdd");


        PostApi api = getRetrofit().create(PostApi.class);

        api.postRemove(
                request.getUuid()
                , request.getIdx()
        ).enqueue(new Callback<PostResponse>() {

            @Override
            public void onResponse(Call<PostResponse> call, Response<PostResponse> apiResponse) {
                Log.i(TAG, "postAdd.onResponse");

                if (successHandler != null) successHandler.onSuccess(apiResponse.body());
                if (completeHandler != null) completeHandler.onComplete();
            }

            @Override
            public void onFailure(Call<PostResponse> call, Throwable t) {
                Log.i(TAG, "postAdd.onFailure");

                if (failureHandler != null) failureHandler.onFailure(t);
                if (completeHandler != null) completeHandler.onComplete();
            }
        });
    }


    public interface SuccessHandler {
        <V> void onSuccess(V response);
    }

    public interface FailureHandler {
        void onFailure(Throwable t);
    }

    public interface CompleteHandler {
        void onComplete();
    }
}
