package kr.co.ainus.petife2.view.activity;

import android.app.Activity;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.nabto.api.RemoteTunnel;

import java.util.Arrays;
import java.util.Calendar;
import java.util.Locale;

import androidx.appcompat.app.AlertDialog;
import androidx.databinding.DataBindingUtil;
import kr.co.ainus.petife2.R;
import kr.co.ainus.petife2.databinding.ActivityPeticaSettingBinding;
import kr.co.ainus.petife2.model.PeticaStatus;
import kr.co.ainus.petife2.util.GsonHelper;
import kr.co.ainus.petife2.util.RandomPortHelper;
import kr.co.ainus.petica_api.ApiHelper;
import kr.co.ainus.petica_api.model.domain.Petica;
import kr.co.ainus.petica_api.model.response.DeviceResponse;
import kr.co.ainus.petica_api.model.type.FeedModeType;
import kr.co.ainus.peticaexcutor.callback.FailCallback;
import kr.co.ainus.peticaexcutor.callback.ReceiveCallback;
import kr.co.ainus.peticaexcutor.callback.SuccessCallback;
import kr.co.ainus.peticaexcutor.type.ModeType;

public class PeticaSettingActivity extends _BaseNavigationActivity {

    private static final String TAG = "PeticaSettingActivity";

    private ActivityPeticaSettingBinding dataBinding;

    private String peticaJson;
    private Petica petica;

    private String ip = "127.0.0.1";
    private int randomPortAudio = RandomPortHelper.make();

    // 설정값 저장을 위해 추가 2021-05-28 by Andrew Kim
    private SharedPreferences pref;
    private SharedPreferences.Editor editor;
    private String feedSet = "";
    private String waterSet = "";

    private int petifeVersion = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // version이 110 이상인 경우 sharedpreference에 저장해놓은 급식 설정값 사용 추가 2021-05-28 by Andrew Kim
        pref = getSharedPreferences("pref", Activity.MODE_PRIVATE);
        editor = pref.edit();

        try {

            peticaJson = getIntent().getStringExtra("peticaJson");

            Log.i(TAG, "peticaJson = " + peticaJson);

            petica = GsonHelper.getGson().fromJson(peticaJson, Petica.class);

        } catch (Exception e) {
            Log.e(TAG, e.getLocalizedMessage());
            e.printStackTrace();
        }

        setDataBinding();
        setViewModel();

        if(petifeVersion < 110) {
            dataBinding.waterModeBtnArea.setVisibility(View.INVISIBLE);
        }

        peticaViewModel.loadDevice(uuid, petica.getDeviceId(), new ApiHelper.SuccessHandler() {
            @Override
            public <V> void onSuccess(V response) {
                if(response instanceof DeviceResponse) {
                    if (((DeviceResponse) response).getItems().size() > 0) {
                        Petica petica = ((DeviceResponse) response).getItems().get(0);
                        if(petifeVersion >= 110) {
                            FeedModeType feedModeType = FeedModeType.MANUAL;
                            FeedModeType feedModeType2 = FeedModeType.MANUAL;
                            feedSet = pref.getString("feedSet", "manual");
                            waterSet = pref.getString("waterSet", "manual");

                            if(null != feedSet && !"".equals(feedSet) && 0 < feedSet.length()) {
                                if("manual".equals(feedSet)) {
                                    feedModeType = FeedModeType.MANUAL;
                                } else if("auto".equals(feedSet)) {
                                    feedModeType = FeedModeType.AUTO;
                                } else {
                                    feedModeType = FeedModeType.FREE;
                                }
                            }

                            if(null != waterSet && !"".equals(waterSet) && 0 < waterSet.length()) {
                                if("manual".equals(waterSet)) {
                                    feedModeType2 = FeedModeType.MANUAL;
                                } else if("auto".equals(waterSet)) {
                                    feedModeType2 = FeedModeType.AUTO;
                                } else {
                                    feedModeType2 = FeedModeType.FREE;
                                }
                            }

                            petica.setFeedMode(feedModeType);
                            petica.setFeedMode2(feedModeType2);
                        }

                        dataBinding.setPetica(petica);
                        dataBinding.setFeedMode(petica.getFeedMode());
                        dataBinding.setFeedMode2(petica.getFeedMode2());
                    }
                }
            }
        }, new ApiHelper.FailureHandler() {
            @Override
            public void onFailure(Throwable t) {

            }
        });

        // 버전에 따른 급수모드 설정 영역 표시 처리 추가 2021-05-26 by Andrew Kim
        peticaViewModel.onExecutePeticaVersionRequest("127.0.0.1", randomPortAudio
        , null
        , null
        , null
        , null
        , new ReceiveCallback() {
            @Override
            public void onReceive(byte[] peticaResponse) {
                String result = Arrays.toString(peticaResponse);
                String version = String.valueOf(peticaResponse[7]);
                Log.e(TAG, "=========================version response : " + result);
                Log.e(TAG, "=========================version info : " + version);

                petifeVersion = Integer.parseInt(version);
                editor.putString("petifeVersion", petica.getDeviceId() + "#" + version);
                editor.apply();

                if(petifeVersion >= 110) {
                    runOnUiThread(() -> {
                        // 급수모드 설정 표시
                        dataBinding.waterModeBtnArea.setVisibility(View.VISIBLE);

                        FeedModeType feedModeType = FeedModeType.MANUAL;
                        FeedModeType feedModeType2 = FeedModeType.MANUAL;
                        feedSet = pref.getString("feedSet", "manual");
                        waterSet = pref.getString("waterSet", "manual");

                        if(null != feedSet && !"".equals(feedSet) && 0 < feedSet.length()) {
                            if("manual".equals(feedSet)) {
                                feedModeType = FeedModeType.MANUAL;
                            } else if("auto".equals(feedSet)) {
                                feedModeType = FeedModeType.AUTO;
                            } else {
                                feedModeType = FeedModeType.FREE;
                            }
                        }

                        if(null != waterSet && !"".equals(waterSet) && 0 < waterSet.length()) {
                            if("manual".equals(waterSet)) {
                                feedModeType2 = FeedModeType.MANUAL;
                            } else if("auto".equals(waterSet)) {
                                feedModeType2 = FeedModeType.AUTO;
                            } else {
                                feedModeType2 = FeedModeType.FREE;
                            }
                        }

                        Log.e(TAG, "===== version feedSet : " + feedSet);
                        Log.e(TAG, "===== version waterSet : " + waterSet);
                        dataBinding.setFeedMode(feedModeType);
                        dataBinding.setFeedMode2(feedModeType2);
                    });
                } else {
                    runOnUiThread(() -> {
                        // 급수모드 설정 제거
                        dataBinding.waterModeBtnArea.setVisibility(View.INVISIBLE);
                    });
                }
            }
        });
    }

    @Override
    public void setDataBinding() {
        super.setDataBinding();

        baseNavigationBinding.tvTitle.setText(petica.getDeviceName());
        baseNavigationBinding.btnRight.setVisibility(View.GONE);

        dataBinding = DataBindingUtil.inflate(getLayoutInflater(), R.layout.activity_petica_setting, baseNavigationBinding.baseNavContainer, true);
        dataBinding.setLifecycleOwner(this);

        dataBinding.setPetica(petica);

        dataBinding.btnFeedMode.setOnClickListener(v -> {
            Log.e(TAG, "===== petife version : " + petifeVersion);
            AlertDialog alertDialog = new AlertDialog.Builder(this)
                    .setTitle(getString(R.string.selectServeMode))
                    .setItems(new String[]{
                            getString(R.string.manual), getString(R.string.reserve), getString(R.string.free)
                    }, ((dialog, which) -> {
                        ModeType modeType = ModeType.MANUAL;
                        FeedModeType feedModeType = FeedModeType.MANUAL;
                        String feedSet = "manual";
                        switch (which) {
                            case 0:
                                modeType = ModeType.MANUAL;
                                feedModeType = FeedModeType.MANUAL;
                                feedSet = "manual";
                                break;

                            case 1:
                                modeType = ModeType.AUTO;
                                feedModeType = FeedModeType.AUTO;
                                feedSet = "auto";
                                break;

                            case 2:
                                modeType = ModeType.FREE;
                                feedModeType = FeedModeType.FREE;
                                feedSet = "free";
                                break;
                        }

                        dataBinding.setFeedMode(feedModeType);
                        editor.putString("feedSet", feedSet);
                        editor.apply();

                        peticaViewModel.peticaUpdateToServer(uuid
                                , dataBinding.getPetica().getDeviceId()
                                , dataBinding.getPetica().getDeviceName()
                                , feedModeType);

                        peticaViewModel.onExecutePeticaFeedModeSelect(ip, randomPortAudio, modeType
                                , () -> {
                                    runOnUiThread(() -> {
                                        AlertDialog alertDialog2 = new AlertDialog.Builder(PeticaSettingActivity.this)
                                                .setTitle(getString(R.string.saveServeMode))
                                                .setPositiveButton(getString(R.string.confirm), null)
                                                .create();
                                        if(!PeticaSettingActivity.this.isFinishing()) {
                                            alertDialog2.show();
                                        }
                                    });
                                }, () -> {

                                    runOnUiThread(() -> {
                                        AlertDialog alertDialog2 = new AlertDialog.Builder(PeticaSettingActivity.this)
                                                .setTitle(getString(R.string.failServeMode))
                                                .setPositiveButton(getString(R.string.confirm), null)
                                                .create();
                                        alertDialog2.show();
                                    });

                                }, null, null
                                , peticaResponse -> {

                            Log.e(TAG, "===== version feed response : " + Arrays.toString(peticaResponse));
                                    switch (peticaResponse[5]) {
                                        case 0:
                                            dataBinding.setFeedMode(FeedModeType.MANUAL);
                                            break;

                                        case 1:
                                            dataBinding.setFeedMode(FeedModeType.AUTO);
                                            break;

                                        case 2:
                                            dataBinding.setFeedMode(FeedModeType.FREE);
                                            break;

                                    }
                                    dataBinding.setPetica(dataBinding.getPetica());
                                    dataBinding.setPeticaStatus(peticaViewModel.getStatus(peticaResponse[6]));
                                });
                    }))
                    .setNegativeButton(getString(R.string.cancel), null)
                    .create();

            alertDialog.show();
        });

        dataBinding.btnWaterMode.setOnClickListener(v -> {
            Log.e(TAG, "===== petife version : " + petifeVersion);
            AlertDialog alertDialog = new AlertDialog.Builder(this)
                    .setTitle(getString(R.string.selectServeMode))
                    .setItems(new String[]{
                            getString(R.string.manual), getString(R.string.reserve), getString(R.string.free)
                    }, ((dialog, which) -> {
                        ModeType modeType2 = ModeType.MANUAL;
                        FeedModeType feedModeType2 = FeedModeType.MANUAL;
                        String waterSet = "manual";
                        switch (which) {
                            case 0:
                                modeType2 = ModeType.MANUAL;
                                feedModeType2 = FeedModeType.MANUAL;
                                waterSet = "manual";
                                break;

                            case 1:
                                modeType2 = ModeType.AUTO;
                                feedModeType2 = FeedModeType.AUTO;
                                waterSet = "auto";
                                break;

                            case 2:
                                modeType2 = ModeType.FREE;
                                feedModeType2 = FeedModeType.FREE;
                                waterSet = "free";
                                break;
                        }

                        dataBinding.setFeedMode2(feedModeType2);
                        editor.putString("waterSet", waterSet);
                        editor.apply();

                        /**
                        peticaViewModel.peticaUpdateToServer(uuid
                                , dataBinding.getPetica().getDeviceId()
                                , dataBinding.getPetica().getDeviceName()
                                , feedModeType2);
                         **/

                        peticaViewModel.onExecutePeticaWaterModeSelect(ip, randomPortAudio, modeType2
                                , () -> {
                                    runOnUiThread(() -> {
                                        AlertDialog alertDialog2 = new AlertDialog.Builder(PeticaSettingActivity.this)
                                                .setTitle(getString(R.string.saveServeMode))
                                                .setPositiveButton(getString(R.string.confirm), null)
                                                .create();
                                        if(!PeticaSettingActivity.this.isFinishing()) {
                                            alertDialog2.show();
                                        }
                                    });
                                }, () -> {

                                    runOnUiThread(() -> {
                                        AlertDialog alertDialog2 = new AlertDialog.Builder(PeticaSettingActivity.this)
                                                .setTitle(getString(R.string.failServeMode))
                                                .setPositiveButton(getString(R.string.confirm), null)
                                                .create();
                                        alertDialog2.show();
                                    });

                                }, null, null
                                , peticaResponse2 -> {

                            Log.e(TAG, "===== version water response : " + Arrays.toString(peticaResponse2));
                            Log.e(TAG, "===== version water response : " + peticaResponse2[5]);
                                    switch (peticaResponse2[5]) {
                                        case 0:
                                            dataBinding.setFeedMode2(FeedModeType.MANUAL);
                                            break;

                                        case 1:
                                            dataBinding.setFeedMode2(FeedModeType.AUTO);
                                            break;

                                        case 2:
                                            dataBinding.setFeedMode2(FeedModeType.FREE);
                                            break;

                                    }
                                    dataBinding.setPetica(dataBinding.getPetica());
                                    dataBinding.setPeticaStatus(peticaViewModel.getStatus(peticaResponse2[6]));
                                });
                    }))
                    .setNegativeButton(getString(R.string.cancel), null)
                    .create();

            alertDialog.show();
        });

        dataBinding.btnKeyLock.setOnClickListener(v -> {
            AlertDialog alertDialog = new AlertDialog.Builder(this)
                    .setTitle(getString(R.string.lockPetife))
                    .setItems(new String[]{
                            getString(R.string.setting2), getString(R.string.release)
                    }, ((dialog, which) -> {
                        boolean hasKeyLock = false;

                        switch (which) {
                            case 0:
                                hasKeyLock = true;
                                break;

                            case 1:
                                hasKeyLock = false;
                                break;
                        }

                        peticaViewModel.onExecutePeticaKeyLock(ip, randomPortAudio, hasKeyLock, new SuccessCallback() {
                                    @Override
                                    public void onSuccess() {

                                        runOnUiThread(() -> {
                                            AlertDialog alertDialog = new AlertDialog.Builder(PeticaSettingActivity.this)
                                                    .setTitle(getString(R.string.frontLockKeySave))
                                                    .setPositiveButton(getString(R.string.confirm), null)
                                                    .create();
                                            alertDialog.show();
                                        });
                                    }
                                }, new FailCallback() {
                                    @Override
                                    public void onFail() {

                                        try {
                                            runOnUiThread(() -> {
                                                AlertDialog alertDialog = new AlertDialog.Builder(PeticaSettingActivity.this)
                                                        .setTitle(getString(R.string.frontLockKeyFail))
                                                        .setPositiveButton(getString(R.string.confirm), null)
                                                        .create();
                                                alertDialog.show();
                                            });
                                        } catch (Exception e) {
                                            Log.e(TAG, e.getLocalizedMessage());
                                            e.printStackTrace();
                                        }

                                    }
                                }, null, null
                                , new ReceiveCallback() {
                                    @Override
                                    public void onReceive(byte[] peticaResponse) {
                                        PeticaStatus peticaStatus = peticaViewModel.getStatus(peticaResponse[6]);
                                        dataBinding.setPeticaStatus(peticaStatus);
                                    }
                                });
                    }))
                    .setNegativeButton(getString(R.string.cancel), null)
                    .create();

            alertDialog.show();
        });

        dataBinding.btnClockSet.setOnClickListener(v -> {

            Calendar calendar = Calendar.getInstance(Locale.KOREA);
            int hour = calendar.get(Calendar.HOUR_OF_DAY);
            int minute = calendar.get(Calendar.MINUTE);
            int second = calendar.get(Calendar.SECOND);

            peticaViewModel.onExecutePeticaClockSet(ip, randomPortAudio, hour, minute, second
                    , () -> runOnUiThread(() -> {
                        AlertDialog alertDialog = new AlertDialog.Builder(PeticaSettingActivity.this)
                                .setTitle(getString(R.string.syncTimeSuccess))
                                .setPositiveButton(getString(R.string.confirm), null)
                                .create();
                        alertDialog.show();

                    }), () -> runOnUiThread(() -> {

                        AlertDialog alertDialog = new AlertDialog.Builder(PeticaSettingActivity.this)
                                .setTitle(getString(R.string.syncTimeFail))
                                .setPositiveButton(getString(R.string.confirm), null)
                                .create();
                        alertDialog.show();

                    }), null, null, (peticaResponse -> {

                        dataBinding.setPeticaStatus(peticaViewModel.getStatus(peticaResponse[6]));

                    }));

        });

        dataBinding.btnInitialization.setOnClickListener(v -> {
            AlertDialog alertDialog = new AlertDialog.Builder(this)
                    .setTitle(getString(R.string.initPetife))
                    .setNegativeButton(getString(R.string.cancel), null)
                    .setPositiveButton(getString(R.string.confirm), (dialog, which) -> {
                        peticaViewModel.onExecutePeticaInitialization(ip, randomPortAudio,
                                () -> {
                                    Log.i(TAG, "onSuccess");
                                    runOnUiThread(() -> {
                                        AlertDialog alertDialog2 = new AlertDialog.Builder(this)
                                                .setTitle(getString(R.string.initComplete))
                                                .setPositiveButton(getString(R.string.confirm), null)
                                                .create();
                                        alertDialog2.show();
                                    });
                                }
                                , () -> Log.i(TAG, "onFail")
                                , () -> Log.i(TAG, "onComplete")
                                , () -> Log.i(TAG, "onSend")
                                , peticaResponse -> Log.i(TAG, "onReceive"));
                    })
                    .create();

            alertDialog.show();
        });

        dataBinding.btnPeticaRemove.setOnClickListener(v -> {

            AlertDialog alertDialog = new AlertDialog.Builder(this)
                    .setTitle(getString(R.string.deletePetife))
                    .setNegativeButton(getString(R.string.cancel), null)
                    .setPositiveButton(getString(R.string.confirm), (dialog, which) -> {
                        peticaViewModel.peticaRemoveFromServer(uuid, dataBinding.getPetica().getDeviceId(), new ApiHelper.SuccessHandler() {
                            @Override
                            public <V> void onSuccess(V response) {
                                Log.d(TAG, GsonHelper.getGson().toJson(response));

                                try {

                                    runOnUiThread(() -> {
                                        if (response instanceof DeviceResponse) {
                                            boolean succeed = ((DeviceResponse) response).getSucceed();

                                            if (succeed) {
                                                Toast.makeText(getApplicationContext(), getString(R.string.petifeDelete), Toast.LENGTH_SHORT).show();

                                                new Handler().postDelayed(() -> finish(), 500);

                                            } else {
                                                Toast.makeText(getApplicationContext(), getString(R.string.petifeDelFail), Toast.LENGTH_SHORT).show();
                                            }
                                        }
                                    });

                                } catch (Exception e) {
                                    Log.e(TAG, e.getLocalizedMessage());
                                    e.printStackTrace();
                                }
                            }
                        }, new ApiHelper.FailureHandler() {
                            @Override
                            public void onFailure(Throwable t) {
                                Log.e(TAG, "onFailure");
                                Log.e(TAG, t.getLocalizedMessage());
                                t.printStackTrace();
                            }
                        }, new ApiHelper.CompleteHandler() {
                            @Override
                            public void onComplete() {
                                Log.d(TAG, "peticaRemoveFromServer onComplete");
                            }
                        });
                    })
                    .create();

            alertDialog.show();
        });

        dataBinding.btnSave.setOnClickListener(v -> {
            AlertDialog alertDialog = new AlertDialog.Builder(this)
                    .setTitle(getString(R.string.saveComplete))
                    .setPositiveButton(getString(R.string.confirm), null)
                    .create();
            alertDialog.show();
        });

    }

    @Override
    public void setViewModel() {
        super.setViewModel();

        Log.e(TAG, "random port = " + randomPortAudio);
        peticaViewModel.openRemoteTunnelUart(this, dataBinding.getPetica().getDeviceId(), randomPortAudio, new RemoteTunnel.OnResultListener() {
            @Override
            public void onResult(String id, String result) {

                Log.i(TAG, "sss = " + result);

                if (result.equals("Local") || result.equals("Remote P2P")) {

                    peticaViewModel.onExecutePeticaStatusRequest("127.0.0.1", randomPortAudio
                            , null, new FailCallback() {
                                @Override
                                public void onFail() {
                                    runOnUiThread(() -> {
                                        alertFail();
                                    });
                                }
                            }, null, null
                            , new ReceiveCallback() {
                                @Override
                                public void onReceive(byte[] peticaResponse) {
                                    petica = dataBinding.getPetica();

                                    Log.i(TAG, "feed mode type = " + peticaResponse[5]);

                                    switch (peticaResponse[5]) {
                                        case 0:
                                            dataBinding.getPetica().setFeedMode(FeedModeType.MANUAL);
                                            break;

                                        case 1:
                                            dataBinding.getPetica().setFeedMode(FeedModeType.AUTO);
                                            break;

                                        case 2:
                                            dataBinding.getPetica().setFeedMode(FeedModeType.FREE);
                                            break;
                                    }
                                    dataBinding.setPeticaStatus(peticaViewModel.getStatus(peticaResponse[6]));
                                }
                            });

                } else {

                    Log.e(TAG, "openRemoteTunnel 실패");
                    alertFail();

                }

            }
        });


    }

    @Override
    protected void onStop() {
        super.onStop();

    }

    private void alertFail() {
        try {
            AlertDialog alertDialog = new AlertDialog.Builder(this)
                    .setTitle(getString(R.string.readFailPetife))
                    .setNegativeButton("닫기", null)
                    .create();
            alertDialog.show();

        } catch (Exception e) {
            Log.e(TAG, e.getLocalizedMessage());
            e.printStackTrace();
        }
    }
}
