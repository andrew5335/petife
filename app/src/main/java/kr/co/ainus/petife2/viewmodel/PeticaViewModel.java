package kr.co.ainus.petife2.viewmodel;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.media.AudioFormat;
import android.media.AudioRecord;
import android.media.MediaRecorder;
import android.net.wifi.WifiManager;
import android.os.Handler;
import android.provider.Settings;
import android.util.Log;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.demo.sdk.Lx520;
import com.demo.sdk.Scanner2;
import com.nabto.api.RemoteTunnel;
import com.thanosfisherman.wifiutils.WifiUtils;
import com.thanosfisherman.wifiutils.wifiConnect.ConnectionSuccessListener;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;

import kr.co.ainus.petife2.model.LoadSsidListResponse;
import kr.co.ainus.petife2.model.PeticaStatus;
import kr.co.ainus.petife2.model.SsidInfo;
import kr.co.ainus.petife2.util.GsonHelper;
import kr.co.ainus.petife2.util.PCMA2PCM;
import kr.co.ainus.petica_api.ApiHelper;
import kr.co.ainus.petica_api.model.domain.Feed;
import kr.co.ainus.petica_api.model.domain.Petica;
import kr.co.ainus.petica_api.model.request.DeviceRequest;
import kr.co.ainus.petica_api.model.response.DeviceResponse;
import kr.co.ainus.petica_api.model.type.FeedModeType;
import kr.co.ainus.petica_api.model.type.FeedType;
import kr.co.ainus.peticaexcutor.Executor;
import kr.co.ainus.peticaexcutor.Packet;
import kr.co.ainus.peticaexcutor.Protocol10Byte;
import kr.co.ainus.peticaexcutor.Protocol22Byte;
import kr.co.ainus.peticaexcutor.StatusCheck;
import kr.co.ainus.peticaexcutor.TcpSocket;
import kr.co.ainus.peticaexcutor.callback.CompleteCallback;
import kr.co.ainus.peticaexcutor.callback.FailCallback;
import kr.co.ainus.peticaexcutor.callback.ReceiveCallback;
import kr.co.ainus.peticaexcutor.callback.SendCallback;
import kr.co.ainus.peticaexcutor.callback.SuccessCallback;
import kr.co.ainus.peticaexcutor.type.CommandType;
import kr.co.ainus.peticaexcutor.type.DataType;
import kr.co.ainus.peticaexcutor.type.ModeType;

public class PeticaViewModel extends ViewModel {
    public static final String TAG = "PeticaViewModel";

    // 작업 관련
    private final MutableLiveData<Boolean> hasLoadingLiveData = new MutableLiveData<>();
    private final MutableLiveData<Boolean> hasRecordingAudioLiveData = new MutableLiveData<>();
    private final MutableLiveData<Integer> peticaResultReasonCodeLiveData = new MutableLiveData<>();

    // 펫티카 등록 관련
    private final MutableLiveData<Integer> deviceAddStepLiveData = new MutableLiveData<>();
    private final MutableLiveData<Map<InetAddress, String>> peticaIpPeticaIdMapLiveData = new MutableLiveData<>();

    private final MutableLiveData<Boolean> hasSuccessPeticaListScan = new MutableLiveData<>();
    private final MutableLiveData<Boolean> hasCorrectSsid = new MutableLiveData<>();
    private final MutableLiveData<Boolean> hasSuccessWifiScan = new MutableLiveData<>();
    private final MutableLiveData<Boolean> hasSuccessWifiConnect = new MutableLiveData<>();
    private final MutableLiveData<Boolean> hasSuccessPeticaJoinWifi = new MutableLiveData<>();
    private final MutableLiveData<SsidInfo> selectSsidInfoLiveData = new MutableLiveData<>();
    private final MutableLiveData<Boolean> hasSuccessPeticaUpdatePassword = new MutableLiveData<>();
    private final MutableLiveData<Integer> tryCount = new MutableLiveData<>();

    // 디비 글 작성 관련
    private final MutableLiveData<Boolean> hasSuccessPeticaAddToServer = new MutableLiveData<>();
    private final MutableLiveData<Boolean> hasSuccessPeticaUpdateToServer = new MutableLiveData<>();

    // 펫티카 장비 관련
    private final MutableLiveData<String> currentPeticaIdLiveData = new MutableLiveData<>();
    private final MutableLiveData<String> currentPeticaIpLiveData = new MutableLiveData<>();
    private final MutableLiveData<String> currentPeticaPasswordLiveData = new MutableLiveData<>();
    private final MutableLiveData<Integer> currentDevicePortLiveData = new MutableLiveData<>();
    private final MutableLiveData<Petica> currentPeticaLiveData = new MutableLiveData<>();

    // 펫티카 리스트 관련
    private final MutableLiveData<List<Petica>> peticaListLiveData = new MutableLiveData<>();
    private final MutableLiveData<List<Petica>> peticaMasterListLiveData = new MutableLiveData<>();
    private final MutableLiveData<List<Petica>> peticaSlaveListLiveData = new MutableLiveData<>();
    private final MutableLiveData<List<SsidInfo>> ssidInfoListLiveData = new MutableLiveData<>();

    public MutableLiveData<SsidInfo> getSelectSsidInfoLiveData() {
        return selectSsidInfoLiveData;
    }

    public MutableLiveData<Boolean> getHasSuccessPeticaUpdateToServer() {
        return hasSuccessPeticaUpdateToServer;
    }

    public MutableLiveData<Integer> getPeticaResultReasonCodeLiveData() {
        return peticaResultReasonCodeLiveData;
    }

    public MutableLiveData<List<SsidInfo>> getSsidInfoListLiveData() {
        return ssidInfoListLiveData;
    }

    public MutableLiveData<Integer> getTryCount() {
        return tryCount;
    }

    public MutableLiveData<String> getCurrentPeticaPasswordLiveData() {
        return currentPeticaPasswordLiveData;
    }

    public MutableLiveData<Map<InetAddress, String>> getPeticaIpPeticaIdMapLiveData() {
        return peticaIpPeticaIdMapLiveData;
    }

    public MutableLiveData<Boolean> getHasSuccessPeticaListScan() {
        return hasSuccessPeticaListScan;
    }

    public MutableLiveData<Boolean> getHasSuccessPeticaUpdatePassword() {
        return hasSuccessPeticaUpdatePassword;
    }

    public MutableLiveData<Boolean> getHasLoadingLiveData() {
        return hasLoadingLiveData;
    }

    public MutableLiveData<Boolean> getHasRecordingAudioLiveData() {
        return hasRecordingAudioLiveData;
    }

    public MutableLiveData<Integer> getDeviceAddStepLiveData() {
        return deviceAddStepLiveData;
    }

    public MutableLiveData<List<Petica>> getPeticaListLiveData() {
        return peticaListLiveData;
    }

    public MutableLiveData<Boolean> getHasSuccessWifiScan() {
        return hasSuccessWifiScan;
    }

    public MutableLiveData<Boolean> getHasCorrectSsid() {
        return hasCorrectSsid;
    }

    public MutableLiveData<Boolean> getHasSuccessPeticaJoinWifi() {
        return hasSuccessPeticaJoinWifi;
    }

    public MutableLiveData<Boolean> getHasSuccessWifiConnect() {
        return hasSuccessWifiConnect;
    }

    public MutableLiveData<Boolean> getHasSuccessPeticaAddToServer() {
        return hasSuccessPeticaAddToServer;
    }

    public MutableLiveData<List<Petica>> getPeticaMasterListLiveData() {
        return peticaMasterListLiveData;
    }

    public MutableLiveData<List<Petica>> getPeticaSlaveListLiveData() {
        return peticaSlaveListLiveData;
    }

    public MutableLiveData<String> getCurrentPeticaIdLiveData() {
        return currentPeticaIdLiveData;
    }

    public MutableLiveData<String> getCurrentPeticaIpLiveData() {
        return currentPeticaIpLiveData;
    }

    public MutableLiveData<Integer> getCurrentDevicePortLiveData() {
        return currentDevicePortLiveData;
    }

    public MutableLiveData<Petica> getCurrentPeticaLiveData() {
        return currentPeticaLiveData;
    }

    public void loadDevice(String uuid, String deviceId) {
        // hasLoadingLiveData.setValue(true);

        ApiHelper.device(uuid, deviceId, new ApiHelper.SuccessHandler() {

            @Override
            public <V> void onSuccess(V response) {
                Log.i(TAG, GsonHelper.getGson().toJson(response));

                if (response instanceof DeviceResponse) {
                    List<Petica> peticaList = ((DeviceResponse) response).getItems();
                    if (peticaList.size() > 0) {
                        currentPeticaLiveData.setValue(peticaList.get(0));
                    }
                }
            }
        }, new ApiHelper.FailureHandler() {

            @Override
            public void onFailure(Throwable t) {
                Log.e(TAG, t.getLocalizedMessage());
                t.printStackTrace();
            }
        }, new ApiHelper.CompleteHandler() {

            @Override
            public void onComplete() {
                // hasLoadingLiveData.setValue(false);
            }
        });
    }

    public void loadDevice(String uuid, String deviceId, ApiHelper.SuccessHandler successHandler, ApiHelper.FailureHandler failureHandler) {
        ApiHelper.device(uuid, deviceId, successHandler, failureHandler, new ApiHelper.CompleteHandler() {

            @Override
            public void onComplete() {
                // hasLoadingLiveData.setValue(false);
            }
        });
    }

    public void loadPeticaList(String uuid) {
        DeviceRequest request = new DeviceRequest();
        request.setUuid(uuid);

        ApiHelper.deviceList(request, new ApiHelper.SuccessHandler() {
            @Override
            public <V> void onSuccess(V response) {
                Log.e(TAG, GsonHelper.getGson().toJson(response));

                if (response instanceof DeviceResponse) {
                    peticaListLiveData.setValue(((DeviceResponse) response).getItems());
                }

            }
        }, new ApiHelper.FailureHandler() {
            @Override
            public void onFailure(Throwable t) {

                Log.e(TAG, t.getLocalizedMessage());
                t.printStackTrace();

            }
        }, new ApiHelper.CompleteHandler() {
            @Override
            public void onComplete() {

            }
        });
    }

    @Deprecated
    public void loadDeviceMasterList(String uuid) {
        // hasLoadingLiveData.setValue(true);

        DeviceRequest request = new DeviceRequest();
        request.setUuid(uuid);

        ApiHelper.deviceMaster(request, new ApiHelper.SuccessHandler() {
            @Override
            public <V> void onSuccess(V response) {

                Log.i(TAG, GsonHelper.getGson().toJson(request));
                Log.i(TAG, GsonHelper.getGson().toJson(response));

                if (response instanceof DeviceResponse) {

                    peticaListLiveData.setValue(new ArrayList<>());

                    peticaMasterListLiveData.setValue(((DeviceResponse) response).getItems());

                    peticaListLiveData.getValue().addAll(((DeviceResponse) response).getItems());
                    if (peticaSlaveListLiveData.getValue() != null) {
                        peticaListLiveData.getValue().addAll(peticaSlaveListLiveData.getValue());
                    }
                }

            }
        }, new ApiHelper.FailureHandler() {
            @Override
            public void onFailure(Throwable t) {

                Log.e(TAG, t.getLocalizedMessage());
                t.printStackTrace();
            }
        }, new ApiHelper.CompleteHandler() {
            @Override
            public void onComplete() {
                // hasLoadingLiveData.setValue(false);

            }
        });
    }

    @Deprecated
    public void loadDeviceSlaveList(String uuid) {
        // hasLoadingLiveData.setValue(true);

        DeviceRequest request = new DeviceRequest();
        request.setUuid(uuid);

        ApiHelper.deviceSlave(request, new ApiHelper.SuccessHandler() {
            @Override
            public <V> void onSuccess(V response) {
                Log.i(TAG, GsonHelper.getGson().toJson(request));
                Log.i(TAG, GsonHelper.getGson().toJson(response));

                if (response instanceof DeviceResponse) {

//                        peticaListLiveData.setValue(new ArrayList<>());
//
//                        peticaMasterListLiveData.setValue(((DeviceResponse) response).getItems());
//
//                        peticaListLiveData.getValue().addAll(((DeviceResponse) response).getItems());
//                        if (peticaSlaveListLiveData.getValue() != null) {
//                            peticaListLiveData.getValue().addAll(peticaSlaveListLiveData.getValue());
//                        }
//                        peticaResult(((DeviceResponse) response).getReason());

                }
            }
        }, new ApiHelper.FailureHandler() {
            @Override
            public void onFailure(Throwable t) {
                Log.e(TAG, t.getLocalizedMessage());
                t.printStackTrace();
            }
        }, new ApiHelper.CompleteHandler() {
            @Override
            public void onComplete() {
                // hasLoadingLiveData.setValue(false);

            }
        });
    }

    public boolean isInternetAvailable() {
        try {
            InetAddress address = InetAddress.getByName("www.google.com");
            return !address.equals("");
        } catch (UnknownHostException e) {
            // Log error
        }
        return false;
    }

    public void peticaAddToServer(String uuid, String deviceId, String devicePw, FeedModeType feedModeType) {
        // hasLoadingLiveData.setValue(true);

        Log.i(TAG, "=============uuid : " + uuid);
        Log.i(TAG, "=============deviceId : " + deviceId);
        Log.i(TAG, "=============devicePw : " + devicePw);
        Log.i(TAG, "=============feedModeType : " + feedModeType);

        DeviceRequest request = new DeviceRequest();
        request.setUuid(uuid);
        request.setDeviceId(deviceId);
        request.setDevicePw(devicePw);
        request.setFeedMode(feedModeType);

        peticaAddToServer(uuid, deviceId, devicePw, feedModeType, new ApiHelper.SuccessHandler() {
            @Override
            public <V> void onSuccess(V response) {

                Log.i(TAG, GsonHelper.getGson().toJson(request));
                Log.i(TAG, GsonHelper.getGson().toJson(response));

                if (response instanceof DeviceResponse) {

                    if (((DeviceResponse) response).getSucceed()) {
                        hasSuccessPeticaAddToServer.setValue(true);

                    } else {
                        hasSuccessPeticaAddToServer.setValue(false);

                    }
                }
            }
        }, new ApiHelper.FailureHandler() {
            @Override
            public void onFailure(Throwable t) {
                hasSuccessPeticaAddToServer.setValue(false);
                Log.e(TAG, t.getLocalizedMessage());
            }
        }, new ApiHelper.CompleteHandler() {
            @Override
            public void onComplete() {
                // hasLoadingLiveData.setValue(false);
            }
        });
    }

    public void peticaAddToServer(String uuid, String deviceId, String devicePw, FeedModeType feedModeType, ApiHelper.SuccessHandler successHandler, ApiHelper.FailureHandler failureHandler, ApiHelper.CompleteHandler completeHandler) {
        // hasLoadingLiveData.setValue(true);

        DeviceRequest request = new DeviceRequest();
        request.setUuid(uuid);
        request.setDeviceId(deviceId);
        request.setDevicePw(devicePw);
        request.setFeedMode(feedModeType);

        ApiHelper.deviceAdd(request, successHandler, failureHandler, completeHandler);
    }

    public void peticaUpdateToServer(String uuid, String deviceId, String deviceName, FeedModeType feedModeType) {
        // hasLoadingLiveData.setValue(true);

        DeviceRequest request = new DeviceRequest();
        request.setUuid(uuid);
        request.setDeviceId(deviceId);
        request.setDeviceName(deviceName);
        request.setFeedMode(feedModeType);

        ApiHelper.deviceUpdate(request, new ApiHelper.SuccessHandler() {
            @Override
            public <V> void onSuccess(V response) {

                hasSuccessPeticaUpdateToServer.setValue(true);
                Log.i(TAG, GsonHelper.getGson().toJson(request));
                Log.i(TAG, GsonHelper.getGson().toJson(response));

                if (response instanceof DeviceResponse) {

                }

            }
        }, new ApiHelper.FailureHandler() {
            @Override
            public void onFailure(Throwable t) {

                hasSuccessPeticaUpdateToServer.setValue(false);
                Log.e(TAG, t.getLocalizedMessage());
                t.printStackTrace();

            }
        }, new ApiHelper.CompleteHandler() {
            @Override
            public void onComplete() {
                // hasLoadingLiveData.setValue(false);

            }
        });

    }

    public void peticaRemoveFromServer(String uuid, String deviceId) {
        // hasLoadingLiveData.setValue(true);

        ApiHelper.deviceRemove(uuid, deviceId, new ApiHelper.SuccessHandler() {
            @Override
            public <V> void onSuccess(V response) {
                Log.i(TAG, GsonHelper.getGson().toJson(response));
            }
        }, new ApiHelper.FailureHandler() {
            @Override
            public void onFailure(Throwable t) {
                Log.e(TAG, t.getLocalizedMessage());
            }
        }, new ApiHelper.CompleteHandler() {
            @Override
            public void onComplete() {
                // hasLoadingLiveData.setValue(false);

            }
        });
    }

    public void peticaRemoveFromServer(String uuid, String deviceId, ApiHelper.SuccessHandler successHandler, ApiHelper.FailureHandler failureHandler, ApiHelper.CompleteHandler completeHandler) {
        ApiHelper.deviceRemove(uuid, deviceId, successHandler, failureHandler, completeHandler);
    }

    private void onExecutePetica(String ip, int port, Packet packet, SuccessCallback successCallback, FailCallback failCallback, CompleteCallback completeCallback, SendCallback sendCallback, ReceiveCallback receiveCallback) {

        Log.i(TAG, "petica execute = " + ip + ":" + port);

        Executor.getInstance().onSendCommand(
                packet.getBytes()
                , ip
                , port
                , successCallback
                , failCallback
                , completeCallback
                , sendCallback
                , receiveCallback
        );
    }

    public void onExecutePeticaFeedFeederManual(String ip
            , int port
            , int amount
            , SuccessCallback successCallback
            , FailCallback failCallback
            , CompleteCallback completeCallback
            , SendCallback sendCallback
            , ReceiveCallback receiveCallback
    ) {

        Packet packet = new Packet();
        packet.onMake(new Protocol10Byte(
                        CommandType.FEEDER
                        , ModeType.MANUAL
                        , DataType.ON
                        , (byte) 0
                        , (byte) 0
                        , (byte) amount
                )
        );

        onExecutePetica(ip, port, packet, successCallback, failCallback, completeCallback, sendCallback, receiveCallback);
    }

    public void onExecutePeticaFeedWaterManual(String ip, int port, int amount
            , SuccessCallback successCallback
            , FailCallback failCallback
            , CompleteCallback completeCallback
            , SendCallback sendCallback
            , ReceiveCallback receiveCallback
    ) {

        Packet packet = new Packet();
        packet.onMake(new Protocol10Byte(
                        CommandType.WATER
                        , ModeType.MANUAL
                        , DataType.ON
                        , (byte) 0
                        , (byte) 0
                        , (byte) amount
                )
        );

        onExecutePetica(ip, port, packet, successCallback, failCallback, completeCallback, sendCallback, receiveCallback);
    }

    public void onExecutePeticaFeedTimeSet(String ip, int port, FeedType feedType, List<Feed> feedList
            , SuccessCallback successCallback
            , FailCallback failCallback
            , CompleteCallback completeCallback
            , SendCallback sendCallback
            , ReceiveCallback receiveCallback
    ) {
        CommandType commandType = null;

        switch (feedType) {
            case FEEDER:
                commandType = CommandType.FEEDER_TIME_SET;
                break;
            case WATER:
                commandType = CommandType.WATER_TIME_SET;
                break;
        }

        Packet packet = new Packet();

        packet.onMake(new Protocol22Byte(
                commandType
                , ModeType.AUTO

                , (byte) feedList.size()

                , feedList.size() > 0 ? (byte) feedList.get(0).getHour() : 0
                , feedList.size() > 0 ? (byte) feedList.get(0).getMin() : 0
                , feedList.size() > 0 ? (byte) feedList.get(0).getAmount() : 0

                , feedList.size() > 1 ? (byte) feedList.get(1).getHour() : 0
                , feedList.size() > 1 ? (byte) feedList.get(1).getMin() : 0
                , feedList.size() > 1 ? (byte) feedList.get(1).getAmount() : 0

                , feedList.size() > 2 ? (byte) feedList.get(2).getHour() : 0
                , feedList.size() > 2 ? (byte) feedList.get(2).getMin() : 0
                , feedList.size() > 2 ? (byte) feedList.get(2).getAmount() : 0

                , feedList.size() > 3 ? (byte) feedList.get(3).getHour() : 0
                , feedList.size() > 3 ? (byte) feedList.get(3).getMin() : 0
                , feedList.size() > 3 ? (byte) feedList.get(3).getAmount() : 0

                , feedList.size() > 4 ? (byte) feedList.get(4).getHour() : 0
                , feedList.size() > 4 ? (byte) feedList.get(4).getMin() : 0
                , feedList.size() > 4 ? (byte) feedList.get(4).getAmount() : 0
        ));

        Log.i(TAG, "packet = " + GsonHelper.getGson().toJson(packet));

        onExecutePetica(ip, port, packet, successCallback, failCallback, completeCallback, sendCallback, receiveCallback);
    }

    public void onExecutePeticaFeedWaterAuto(String ip, int port, List<Feed> feedList
            , SuccessCallback successCallback
            , FailCallback failCallback
            , CompleteCallback completeCallback
            , SendCallback sendCallback
            , ReceiveCallback receiveCallback
    ) {

        Packet packet = new Packet();

        packet.onMake(new Protocol22Byte(
                CommandType.WATER_TIME_SET
                , ModeType.AUTO

                , (byte) feedList.size()

                , feedList.size() > 0 ? (byte) feedList.get(0).getHour() : 0
                , feedList.size() > 0 ? (byte) feedList.get(0).getMin() : 0
                , feedList.size() > 0 ? (byte) feedList.get(0).getAmount() : 0

                , feedList.size() > 1 ? (byte) feedList.get(1).getHour() : 0
                , feedList.size() > 1 ? (byte) feedList.get(1).getMin() : 0
                , feedList.size() > 1 ? (byte) feedList.get(1).getAmount() : 0

                , feedList.size() > 2 ? (byte) feedList.get(2).getHour() : 0
                , feedList.size() > 2 ? (byte) feedList.get(2).getMin() : 0
                , feedList.size() > 2 ? (byte) feedList.get(2).getAmount() : 0

                , feedList.size() > 3 ? (byte) feedList.get(3).getHour() : 0
                , feedList.size() > 3 ? (byte) feedList.get(3).getMin() : 0
                , feedList.size() > 3 ? (byte) feedList.get(3).getAmount() : 0

                , feedList.size() > 4 ? (byte) feedList.get(4).getHour() : 0
                , feedList.size() > 4 ? (byte) feedList.get(4).getMin() : 0
                , feedList.size() > 4 ? (byte) feedList.get(4).getAmount() : 0
        ));

        Log.i(TAG, "packet = " + GsonHelper.getGson().toJson(packet));

        onExecutePetica(ip, port, packet, successCallback, failCallback, completeCallback, sendCallback, receiveCallback);
    }

    public void onExecutePeticaLamp(String ip, int port, boolean isLampOn
            , SuccessCallback successCallback
            , FailCallback failCallback
            , CompleteCallback completeCallback
            , SendCallback sendCallback
            , ReceiveCallback receiveCallback
    ) {

        Packet packet = new Packet();
        packet.onMake(new Protocol10Byte(
                        CommandType.LAMP
                        , ModeType.MANUAL
                        , (isLampOn) ? DataType.ON : DataType.OFF
                        , (byte) 0
                        , (byte) 0
                        , (byte) 0
                )
        );

        onExecutePetica(ip, port, packet, successCallback, failCallback, completeCallback, sendCallback, receiveCallback);
    }

    public void onExecutePeticaFeedFeederFree(String ip, int port, int latency, int amount
            , SuccessCallback successCallback
            , FailCallback failCallback
            , CompleteCallback completeCallback
            , SendCallback sendCallback
            , ReceiveCallback receiveCallback
    ) {

        Packet packet = new Packet();
        packet.onMake(new Protocol10Byte(
                        CommandType.MODE
                        , ModeType.FREE
                        , DataType.OFF
                        , (byte) 0
                        , (byte) latency
                        , (byte) amount
                )
        );

        onExecutePetica(ip, port, packet, successCallback, failCallback, completeCallback, sendCallback, receiveCallback);
    }

    public void onExecutePeticaFeedModeSelect(String ip, int port, ModeType modeType
            , SuccessCallback successCallback
            , FailCallback failCallback
            , CompleteCallback completeCallback
            , SendCallback sendCallback
            , ReceiveCallback receiveCallback
    ) {

        Packet packet = new Packet();
        packet.onMake(new Protocol10Byte(
                        CommandType.MODE
                        , modeType
                        , DataType.OFF
                        , (byte) 0
                        , (byte) 20
                        , (byte) 20
                )
        );

        onExecutePetica(ip, port, packet, successCallback, failCallback, completeCallback, sendCallback, receiveCallback);
    }

    public void onExecutePeticaStatusRequest(String ip, int port
            , SuccessCallback successCallback
            , FailCallback failCallback
            , CompleteCallback completeCallback
            , SendCallback sendCallback
            , ReceiveCallback receiveCallback
    ) {

        Packet packet = new Packet();
        packet.onMake(new Protocol10Byte(
                        CommandType.STATUS_REQUEST
                        , ModeType.MANUAL
                        , DataType.OFF
                        , (byte) 0
                        , (byte) 0
                        , (byte) 0
                )
        );

        onExecutePetica(ip, port, packet, successCallback, failCallback, completeCallback, sendCallback, receiveCallback);
    }

    public void onExecutePeticaClockSet(String ip, int port, int hour, int minute, int second
            , SuccessCallback successCallback
            , FailCallback failCallback
            , CompleteCallback completeCallback
            , SendCallback sendCallback
            , ReceiveCallback receiveCallback
    ) {

        Packet packet = new Packet();
        packet.onMake(new Protocol10Byte(
                        CommandType.CLOCK_SET
                        , ModeType.MANUAL
                        , DataType.OFF
                        , (byte) hour
                        , (byte) minute
                        , (byte) second
                )
        );

        onExecutePetica(ip, port, packet, successCallback, failCallback, completeCallback, sendCallback, receiveCallback);
    }

    public void onExecutePeticaInitialization(String ip, int port
            , SuccessCallback successCallback
            , FailCallback failCallback
            , CompleteCallback completeCallback
            , SendCallback sendCallback
            , ReceiveCallback receiveCallback
    ) {

        Packet packet = new Packet();
        packet.onMake(new Protocol10Byte(
                        CommandType.INITIALIZATION
                        , ModeType.MANUAL
                        , DataType.ON
                        , (byte) 0
                        , (byte) 0
                        , (byte) 0
                )
        );

        onExecutePetica(ip, port, packet, successCallback, failCallback, completeCallback, sendCallback, receiveCallback);
    }

    public void onExecutePeticaVersionRequest(String ip, int port
            , SuccessCallback successCallback
            , FailCallback failCallback
            , CompleteCallback completeCallback
            , SendCallback sendCallback
            , ReceiveCallback receiveCallback
    ) {

        Packet packet = new Packet();
        packet.onMake(new Protocol10Byte(
                        CommandType.VERSION_REQUEST
                        , ModeType.MANUAL
                        , DataType.ON
                        , (byte) 0
                        , (byte) 0
                        , (byte) 0
                )
        );

        onExecutePetica(ip, port, packet, successCallback, failCallback, completeCallback, sendCallback, receiveCallback);
    }

    public void onExecutePeticaKeyLock(String ip, int port, boolean hasKeyLock
            , SuccessCallback successCallback
            , FailCallback failCallback
            , CompleteCallback completeCallback
            , SendCallback sendCallback
            , ReceiveCallback receiveCallback
    ) {

        Packet packet = new Packet();
        packet.onMake(new Protocol10Byte(
                        CommandType.KEY_LOCK
                        , ModeType.MANUAL
                        , hasKeyLock ? DataType.ON : DataType.OFF
                        , (byte) 0
                        , (byte) 0
                        , (byte) 0
                )
        );

        onExecutePetica(ip, port, packet, successCallback, failCallback, completeCallback, sendCallback, receiveCallback);
    }

    public PeticaStatus getStatus(byte status) {

        boolean[] peticaStatusArray = new boolean[8];

        StatusCheck.check(status, (statusType, result) -> {
            Log.i(TAG, statusType + " : " + result);


            switch (statusType) {
                case FEEDER_RUN:
                    peticaStatusArray[0] = result;
                    break;

                case WATER_RUN:
                    peticaStatusArray[1] = result;
                    break;

                case LAMP_ON:
                    peticaStatusArray[2] = result;
                    break;

                case CLOCK_SET:
                    peticaStatusArray[3] = result;
                    break;

                case FEEDER_FULL:
                    peticaStatusArray[4] = result;
                    break;

                case WATER_FULL:
                    peticaStatusArray[5] = result;
                    break;

                case KEY_LOCK:
                    peticaStatusArray[6] = result;
                    break;

                case POWER_ON:
                    peticaStatusArray[7] = result;
                    break;
            }
        });

        PeticaStatus peticaStatus = new PeticaStatus(
                peticaStatusArray[0]
                , peticaStatusArray[1]
                , peticaStatusArray[2]
                , peticaStatusArray[3]
                , peticaStatusArray[4]
                , peticaStatusArray[5]
                , peticaStatusArray[6]
                , peticaStatusArray[7]);

        return peticaStatus;
    }

    //    // TODO 실시간 모드
    public void onRecordingVoice(String ip, int port, String path) {
        hasRecordingAudioLiveData.setValue(true);

        new Thread(() -> {

            TcpSocket tcpSocket = new TcpSocket(ip, port);

            try {
                String Audio_Post1 = "POST /audio.input HTTP/1.1\r\nHost: ";
                String Audio_Post2 = "\r\nContent-Type: audio/wav\r\nContent-Length: ";
                String Audio_Post3 = "\r\nAccept: */*\r\n\r\n";
                String audioStartHeader = Audio_Post1 + Executor.DEVICE_IP + Audio_Post2 + Integer.MAX_VALUE + Audio_Post3;

                int m_in_buf_size = AudioRecord.getMinBufferSize(8000,
                        AudioFormat.CHANNEL_IN_STEREO,
                        AudioFormat.ENCODING_PCM_16BIT);

                byte[] buffer = new byte[m_in_buf_size];
                AudioRecord audioRecord = new AudioRecord(MediaRecorder.AudioSource.VOICE_COMMUNICATION,
                        8000,















                        AudioFormat.CHANNEL_IN_STEREO,
                        AudioFormat.ENCODING_PCM_16BIT,
                        m_in_buf_size);


                audioRecord.startRecording();

                tcpSocket.send(audioStartHeader.getBytes());

                while (hasRecordingAudioLiveData.getValue()) {
                    int bufferReadResult = audioRecord.read(buffer, 0, m_in_buf_size);
                    byte[] tmpBuf = new byte[bufferReadResult];
                    System.arraycopy(buffer, 0, tmpBuf, 0, bufferReadResult);
                    byte[] PCM_Data = new byte[bufferReadResult / 2];
                    for (int i = 0; i < bufferReadResult / 2; i++) {
                        int v = tmpBuf[i * 2 + 1] * 256 + tmpBuf[i * 2];
                        PCM_Data[i] = ((byte) PCMA2PCM.linear2ulaw(v));
                    }
                    tcpSocket.send(PCM_Data);
                }

                audioRecord.stop();
                audioRecord.release();

                tcpSocket.close();

            } catch (Exception e) {
                Log.e(TAG, e.getLocalizedMessage());
                e.printStackTrace();

                hasRecordingAudioLiveData.postValue(false);
            } finally {


            }

        }).start();

    }

    // TODO 녹음모드
//    public void onRecordingVoice(String ip, int port, String path) {
//        hasRecordingAudioLiveData.setValue(true);
//
//        new Thread(() -> {
//
//            try {
//                Thread.sleep(500);
//            } catch (InterruptedException e) {
//                e.printStackTrace();
//            }
//
//            TcpSocket tcpSocket = null;
//
//            FileOutputStream audioFileOutputStream = null;
//            FileInputStream audioFileInputStream = null;
//            File audioFile = null;
//
//            try {
//                String Audio_Post1 = "POST /audio.input HTTP/1.1\r\nHost: ";
//                String Audio_Post2 = "\r\nContent-Type: audio/wav\r\nContent-Length: ";
//                String Audio_Post3 = "\r\nAccept: */*\r\n\r\n";
//                String audio = Audio_Post1 + Executor.DEVICE_IP + Audio_Post2 + Integer.MAX_VALUE + Audio_Post3;
//
//                audioFile = new File(path + "/" + "audio.pcm");
//
//                if (!audioFile.exists()) {
//                    audioFile.getParentFile().mkdirs();
//                }
//
//                audioFileOutputStream = new FileOutputStream(audioFile);
//                audioFileOutputStream.write(audio.getBytes());
//
//                int m_in_buf_size = AudioRecord.getMinBufferSize(8000,
//                        AudioFormat.CHANNEL_IN_STEREO,
//                        AudioFormat.ENCODING_PCM_16BIT);
//
//                byte[] buffer = new byte[m_in_buf_size];
//                AudioRecord audioRecord = new AudioRecord(MediaRecorder.AudioSource.MIC,
//                        8000,
//                        AudioFormat.CHANNEL_IN_STEREO,
//                        AudioFormat.ENCODING_PCM_16BIT,
//                        m_in_buf_size);
//
//
//                audioRecord.startRecording();
//
//                while (hasRecordingAudioLiveData.getValue()) {
//                    int bufferReadResult = audioRecord.read(buffer, 0, m_in_buf_size);
//                    byte[] tmpBuf = new byte[bufferReadResult];
//                    System.arraycopy(buffer, 0, tmpBuf, 0, bufferReadResult);
//                    byte[] PCM_Data = new byte[bufferReadResult / 2];
//                    for (int i = 0; i < bufferReadResult / 2; i++) {
//                        int v = tmpBuf[i * 2 + 1] * 256 + tmpBuf[i * 2];
//                        PCM_Data[i] = ((byte) PCMA2PCM.linear2ulaw(v));
//                    }
//                    audioFileOutputStream.write(PCM_Data);
//                }
//
//                audioRecord.stop();
//                audioRecord.release();
//
//                audioFileInputStream = new FileInputStream(audioFile);
//
//                byte[] buffer2 = new byte[4096];
//
//                tcpSocket = new TcpSocket(ip, port);
//
//                while ((audioFileInputStream.read(buffer2, 0, buffer2.length)) > 0) {
//                    tcpSocket.send(buffer2);
//                }
//
//                audioFileOutputStream.flush();
//                audioFileOutputStream.close();
//
//                audioFileInputStream.close();
//
//                tcpSocket.close();
//
//            } catch (Exception e) {
//                Log.e(TAG, e.getLocalizedMessage());
//                e.printStackTrace();
//
//                hasRecordingAudioLiveData.postValue(false);
//            } finally {
//
//
//            }
//
//        }).start();
//
//    }

    public void onStopRecordingVoid() {

        new Thread(() -> {

            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

            hasRecordingAudioLiveData.postValue(false);

        }).start();

    }

    public void loadSsidList(String address, String password) {

        Log.i(TAG, "loadSsidList");

        hasLoadingLiveData.setValue(true);

        Lx520 lx520 = new Lx520(address, password);
        lx520 = null;
        lx520 = new Lx520(address, password);
        lx520.setOnResultListener(new Lx520.OnResultListener() {
            @Override
            public void onResult(Lx520.Response response) {

                if (response.statusCode == 200) {
                    hasSuccessWifiScan.setValue(true);

                    try {

                        LoadSsidListResponse loadSsidListResponse = GsonHelper.getGson().fromJson(response.body, LoadSsidListResponse.class);

                        List<SsidInfo> list = new ArrayList<>();
                        for (SsidInfo ssidInfo : loadSsidListResponse.getWifimessage()) {
                            if (ssidInfo.getSsid() != null && !ssidInfo.getSsid().trim().isEmpty()) {
                                list.add(ssidInfo);
                            }
                        }

                        HashSet<SsidInfo> tmpList = new HashSet<SsidInfo>();
                        for(int i=0; i < list.size(); i++) {
                            tmpList.add(list.get(i));
                        }

                        List<SsidInfo> list2 = new ArrayList<SsidInfo>();
                        list2 = new ArrayList<SsidInfo>(tmpList);
                        ssidInfoListLiveData.setValue(list2);

                        /**
                        HashSet<SsidInfo> tmpList = new HashSet<SsidInfo>(list);
                        list = new ArrayList<SsidInfo>(tmpList);
                        ssidInfoListLiveData.setValue(list);
                         **/

                    } catch (Exception e) {

                    }

                } else {
                    hasSuccessWifiScan.setValue(false);
                }

                hasLoadingLiveData.setValue(false);
            }
        });

        lx520.Get_Ssid_List();

    }

    public void disableWifi(Context context) {

        WifiUtils.withContext(context).disableWifi();
    }

    /**
     * 2019-04-14
     * petica wifi join 시도 시 계속 192.0.0.4 로 시도해서 적용해봄
     */
    public void enabledWifi(Context context) {

        WifiUtils.withContext(context).enableWifi();
    }


    public void connectPeticaWifi(Context context, String ssid, String password) {

        // hasLoadingLiveData.setValue(true);

        Log.i(TAG, "ssid = " + ssid + ", password = " + password);

        WifiUtils.withContext(context).cancelAutoConnect();
//        disableWifi(context);
//        Log.i(TAG, "기존 wifi 연결 해제");

//        new Handler().postDelayed(() -> {
        WifiUtils.withContext(context).connectWith(ssid, password).setTimeout(5000).onConnectionResult(new ConnectionSuccessListener() {
            @Override
            public void isSuccessful(boolean isSuccess) {

                Log.e(TAG, "petica connect = " + isSuccess);
                hasCorrectSsid.setValue(isSuccess);
                // hasLoadingLiveData.setValue(false);
            }
        }).start();
//        }, 1000);

    }

    public void connectWifi(Context context, String ssid, String password) {

        // hasLoadingLiveData.setValue(true);

        Log.i(TAG, "ssid = " + ssid + ", password = " + password);

        WifiUtils.withContext(context).cancelAutoConnect();
//        disableWifi(context);
//        Log.i(TAG, "기존 wifi 연결 해제");

        WifiUtils.withContext(context).connectWith(ssid, password).setTimeout(5000).onConnectionResult(new ConnectionSuccessListener() {
            @Override
            public void isSuccessful(boolean isSuccess) {
                Log.i(TAG, "ssid = " + ssid + " connect is success = " + isSuccess);

                Log.e(TAG, "connect = " + isSuccess);
                hasSuccessWifiConnect.setValue(isSuccess);

            }
        }).start();

    }

    public void peticaJoinWifi(String deviceIp, String devicePassword, String ssid, String password) {
        Lx520 lx520 = new Lx520(deviceIp + ":" + 80, devicePassword.trim());

        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        lx520.setOnResultListener(response -> {
            Log.i(TAG, "======================PETIFE status code : " + response.statusCode);
            Log.i(TAG, "======================PETIFE status body : " + response.body);
            if (response.statusCode == 200 || response.statusCode == 0) {
                hasSuccessPeticaJoinWifi.setValue(true);

            } else {
                hasSuccessPeticaJoinWifi.setValue(false);
            }

        });

        lx520.joinWifi(ssid, password);

    }

    public void peticaUpdatePassword(Context context, String deviceIp, String deviceOldPassword, String deviceNewPassword) {
        // hasLoadingLiveData.setValue(true);

        Log.i(TAG, "petica update password " + deviceIp + ", " + deviceNewPassword);

//            Lx520 lx520 = new Lx520(context, DEVICE_IP + ":" + 80, deviceOldPassword);
        Lx520 lx520 = new Lx520(deviceIp + ":" + 80, deviceOldPassword);
        lx520.setOnResultListener(response -> {

            if (response.statusCode == 200) {
                currentPeticaPasswordLiveData.setValue(deviceNewPassword);
                hasSuccessPeticaUpdatePassword.setValue(true);

            } else {
                // 2021-01-26 일단 임시 조치, 추후 수정 필요. NetworkSpecifier로 연결 시 암호화 방식이 변경되어 문제가 되는 듯
                // 비밀번호를 업데이트하지 않아도 사용에는 지장이 없음.
                currentPeticaPasswordLiveData.setValue(deviceOldPassword);
                hasSuccessPeticaUpdatePassword.setValue(true);

            }

            // hasLoadingLiveData.setValue(false);

        });

        lx520.updatePassword(deviceNewPassword);

    }

    public void scanPeticaList(Context context, int delay) {
        Log.i(TAG, "Petica 스캔 시작");

        new Handler().postDelayed(() -> {

            Scanner2 scanner2 = new Scanner2(context);
            scanner2.setOnScanOverListener((map, inetAddress) -> {

                Log.i(TAG, "장비 스캔 결과");
                Log.i(TAG, String.format("%d 개의 장비가 발견되었습니다", map.size()));

                boolean hasSucceesScan = false;

                if (map.size() == 1) {

                    for (Map.Entry<InetAddress, String> entry : map.entrySet()) {
                        final String PETICA_IP = entry.getKey().toString().replace("/", "");
                        final String PETICA_ID = entry.getValue();

                        if (PETICA_IP.equals("192.168.100.1")
                                || (currentPeticaIdLiveData.getValue() != null && currentPeticaIdLiveData.getValue().equals(PETICA_ID))) {
                            currentPeticaIpLiveData.postValue(PETICA_IP);
                            currentPeticaIdLiveData.postValue(PETICA_ID);
                            hasSucceesScan = true;
                        }
                    }
                    hasSuccessPeticaListScan.postValue(hasSucceesScan);

                } else {

                    if (currentPeticaIdLiveData.getValue() == null || currentPeticaIdLiveData.getValue().isEmpty()
                            || currentPeticaIpLiveData.getValue() == null || currentPeticaIpLiveData.getValue().isEmpty()) {

                        hasSuccessPeticaListScan.postValue(false);
                        return;
                    }

                    for (Map.Entry<InetAddress, String> entry : map.entrySet()) {
                        final String PETICA_IP = entry.getKey().toString().replace("/", "");
                        final String PETICA_ID = entry.getValue();

                        if (currentPeticaIdLiveData.getValue().equals(PETICA_ID)) {
                            currentPeticaIpLiveData.postValue(PETICA_IP);
                            hasSucceesScan = true;
                        }
                    }

                    hasSuccessPeticaListScan.postValue(hasSucceesScan);

                }
            });

            scanner2.scanAll();

        }, delay);

    }

    private Map<String, RemoteTunnel> peticaIdRemoteTunnelVideoMap = new HashMap<>();
    private Map<String, RemoteTunnel> peticaIdRemoteTunnelAudioMap = new HashMap<>();

    private RemoteTunnel createTunnel(Context context) {
        return new RemoteTunnel(context);
    }

    private void openRemoteTunnel(Context context
            , String id
            , int localPort
            , int remotePort
            , String deviceId
            , RemoteTunnel.OnResultListener onResultListener
            , RemoteTunnel.TunnelType tunnelType) {

        RemoteTunnel remoteTunnel = createTunnel(context);
        remoteTunnel.setOnResultListener(onResultListener);
        remoteTunnel.openTunnel(id, localPort, remotePort, deviceId, tunnelType);

    }

    public void openRemoteTunnelVideo(Context context, String deviceId, int port, RemoteTunnel.OnResultListener onResultListener) {

        Log.i(TAG, "openRemoteTunnelVideo = " + port);
        openRemoteTunnel(context
                , deviceId
                , port
                , 554
                , deviceId
                , onResultListener
                , RemoteTunnel.TunnelType.VIDEO);
    }

    public void openRemoteTunnelAudio(Context context, String deviceId, int port, RemoteTunnel.OnResultListener onResultListener) {

        Log.i(TAG, "openRemoteTunnelAudio port = " + port);
        openRemoteTunnel(context
                , deviceId
                , port
                , 80
                , deviceId
                , onResultListener
                , RemoteTunnel.TunnelType.AUDIO);
    }

    public void openRemoteTunnelUart(Context context, String deviceId, int port, RemoteTunnel.OnResultListener onResultListener) {
        openRemoteTunnelAudio(context, deviceId, port, onResultListener);
    }

    public void checkCurrentSsid(Context context, boolean hasExatly, String... ssidArray) {
        hasLoadingLiveData.setValue(true);
        ProgressDialog progressDialog = null;
        progressDialog = new ProgressDialog(context);
        progressDialog.setCancelable(false);
        progressDialog.setMessage("Petica에 연결 중 입니다");

        try {

            progressDialog.show();

        } catch (Exception e) {
            Log.e(TAG, e.getLocalizedMessage());
            e.printStackTrace();
        }

        WifiManager wifiManager = (WifiManager) context.getApplicationContext().getSystemService(Context.WIFI_SERVICE);

        Log.d(TAG, GsonHelper.getGson().toJson(wifiManager.getConnectionInfo()));

        final String CURRENT_SSID = wifiManager.getConnectionInfo().getSSID().replace("\"", "");

        Log.d(TAG, "current ssid = " + CURRENT_SSID);

        boolean hasConnect = false;
        for (String ssid : ssidArray) {

            if (hasExatly) {
                if (CURRENT_SSID.equals(ssid)) {
                    hasConnect = true;
                    break;
                }
            } else {
                if (CURRENT_SSID.startsWith(ssid)) {
                    hasConnect = true;
                    break;
                }
            }

        }

        hasCorrectSsid.setValue(hasConnect);

        hasLoadingLiveData.setValue(false);

        if (progressDialog != null) progressDialog.dismiss();

        /** 2019-04-21
         * LG X4 plus wifi ssid 검색 안되서 상기 코드는 주석 하기 코드로 재작성*/
        /*
        ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo info = cm.getActiveNetworkInfo();

        Log.d(TAG, GsonHelper.getGson().toJson(info));

        if (info != null && info.isConnected()) {
            final String CURRENT_SSID = info.getExtraInfo().replace("\"", "");

            Log.d(TAG, "current ssid = " + CURRENT_SSID);

            boolean hasConnect = false;
            for (String ssid : ssidArray) {

                if (hasExatly) {
                    if (CURRENT_SSID.equals(ssid)) {
                        hasConnect = true;
                        break;
                    }
                } else {
                    if (CURRENT_SSID.startsWith(ssid)) {
                        hasConnect = true;
                        break;
                    }
                }

            }

            hasCorrectSsid.setValue(hasConnect);

            hasLoadingLiveData.setValue(false);
        }*/
    }
}