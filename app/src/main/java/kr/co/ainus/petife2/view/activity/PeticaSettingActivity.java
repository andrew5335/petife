package kr.co.ainus.petife2.view.activity;

import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.nabto.api.RemoteTunnel;

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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

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

        peticaViewModel.loadDevice(uuid, petica.getDeviceId(), new ApiHelper.SuccessHandler() {
            @Override
            public <V> void onSuccess(V response) {
                if(response instanceof DeviceResponse) {
                    if (((DeviceResponse) response).getItems().size() > 0) {
                        Petica petica = ((DeviceResponse) response).getItems().get(0);
                        dataBinding.setPetica(petica);
                        dataBinding.setFeedMode(petica.getFeedMode());
                    }
                }
            }
        }, new ApiHelper.FailureHandler() {
            @Override
            public void onFailure(Throwable t) {

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
            AlertDialog alertDialog = new AlertDialog.Builder(this)
                    .setTitle(getString(R.string.selectServeMode))
                    .setItems(new String[]{
                            getString(R.string.manual), getString(R.string.reserve), getString(R.string.free)
                    }, ((dialog, which) -> {
                        ModeType modeType = ModeType.MANUAL;
                        FeedModeType feedModeType = FeedModeType.MANUAL;
                        switch (which) {
                            case 0:
                                modeType = ModeType.MANUAL;
                                feedModeType = FeedModeType.MANUAL;
                                break;

                            case 1:
                                modeType = ModeType.AUTO;
                                feedModeType = FeedModeType.AUTO;
                                break;

                            case 2:
                                modeType = ModeType.FREE;
                                feedModeType = FeedModeType.FREE;
                                break;
                        }
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
