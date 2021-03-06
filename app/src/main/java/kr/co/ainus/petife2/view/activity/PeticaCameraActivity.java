package kr.co.ainus.petife2.view.activity;


import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.content.ContextCompat;
import androidx.databinding.DataBindingUtil;

//import com.demo.sdk.Controller;
//import com.demo.sdk.Enums;
//import com.demo.sdk.Module;
//import com.demo.sdk.Player;
//import kr.co.ainus.petife2.video.Module;
//import kr.co.ainus.petife2.video.Player;
import com.demo.sdk.Scanner2;
import com.arthenica.mobileffmpeg.ExecuteCallback;
import com.nabto.api.RemoteTunnel;

//import kr.co.ainus.petife2.video.Scanner2;
import kr.co.ainus.petife2.video.Enums;
import kr.co.ainus.petife2.video.Module;
import kr.co.ainus.petife2.video.Controller;
import kr.co.ainus.petife2.video.Player;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.lang.ref.WeakReference;
import java.net.InetAddress;
import java.util.ArrayList;
import java.util.Map;

import kr.co.ainus.petife2.R;
import kr.co.ainus.petife2.databinding.ActivityPeticaCameraBinding;
import kr.co.ainus.petife2.model.PeticaStatus;
import kr.co.ainus.petife2.util.GsonHelper;
import kr.co.ainus.petife2.util.RandomPortHelper;
import kr.co.ainus.petife2.view.dialog.NumberPickerSingleDialog;
import kr.co.ainus.petica_api.model.domain.Petica;
import kr.co.ainus.petica_api.model.type.FeedType;
import kr.co.ainus.peticaexcutor.Executor;
import kr.co.ainus.peticaexcutor.callback.ReceiveCallback;
//import nl.bravobit.ffmpeg.FFcommandExecuteResponseHandler;
//import nl.bravobit.ffmpeg.FFmpeg;
//import nl.bravobit.ffmpeg.FFtask;
import com.arthenica.mobileffmpeg.FFmpeg;
import com.skydoves.balloon.ArrowConstraints;
import com.skydoves.balloon.ArrowOrientation;
import com.skydoves.balloon.Balloon;
import com.skydoves.balloon.BalloonAnimation;

// libvlc ????????? ?????? import ?????? 2020-09-22 by Andrew Kim - ?????? ?????? ?????? ?????? ?????? (?????? sdk ?????? 64bit ????????? ?????? ?????? ?????? ??????)
import org.videolan.libvlc.IVLCVout;
import org.videolan.libvlc.LibVLC;
import org.videolan.libvlc.Media;
//import org.videolan.libvlc.MediaPlayer;
import org.videolan.libvlc.util.VLCUtil;

public class PeticaCameraActivity extends _BaseActivity implements IVLCVout.Callback {

    private static final String TAG = "PeticaCameraActivity";

    public static final String EXTERNAL_PATH = Environment.getExternalStorageDirectory().getAbsolutePath() + "/";
    public static final String DATA_PATH = EXTERNAL_PATH + "DCIM/PETIFE/";

    private ActivityPeticaCameraBinding dataBinding;

    private Module module;
    private Player player;
    private Controller controller;
    private Enums.Pipe _pipe;
    private Enums.Transport protocol;
    private int _fps;
    private int _connectTime;

    private int videoType = 0; // 0 h264 else mpeg
    private boolean recoding;
    private int _decoderType = 0;
    private boolean _stopTraffic;
    private Thread _trafficThread;
    private long _lastTraffic;

    private String peticaIp = Executor.DEVICE_IP;

    private int randomPortAudio = RandomPortHelper.make();
    private int randomPortVideo = RandomPortHelper.make();

    private boolean lampOn;
    private boolean voiceRecordOn;
    private boolean speakerOn;
    private boolean hasStop = false;

    private Petica petica;

    private String videoFilename;

    // libvlc ?????? ?????? ?????? 2020-09-22 by Andrew Kim
    private LibVLC libvlc;
    private VLCUtil vlcUtil;
    private IVLCVout vout;
    private org.videolan.libvlc.MediaPlayer mMediaPlayer = null;
    private int mVideoWidth;
    private int mVideoHeight;
    private String mediaFile;
    // ?????? surface ?????? ?????? 2020-10-06 by Andrew Kim
    private SurfaceView mSurface;
    private SurfaceHolder holder;
    ConstraintLayout mConstraintLayout;
    private Media m;
    private ProgressBar progressBar;

    Balloon balloon;

    //private FFmpeg ffmpeg = FFmpeg.getInstance(this);
    //private FFtask ffTask = null;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setDataBinding();
        setViewModel();

        getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_FULLSCREEN);
        if (getActionBar() != null) getActionBar().hide();

        mConstraintLayout = (ConstraintLayout) findViewById(R.id.camera_constraint_layout);
        mSurface = (SurfaceView) findViewById(R.id.video_view);
        holder = mSurface.getHolder();

        progressBar = (ProgressBar) dataBinding.progressBar1;
        progressBar.setVisibility(View.VISIBLE);

        mVideoWidth = getWindow().getDecorView().getWidth();
        mVideoHeight = getWindow().getDecorView().getHeight();
    }

    @Override
    protected void onStart() {
        super.onStart();

        String peticaJson = getIntent().getStringExtra("peticaJson");

        try {
            Log.i(TAG, "peticaJson = " + peticaJson);
            petica = GsonHelper.getGson().fromJson(peticaJson, Petica.class);
            dataBinding.setPetica(petica);

        } catch (Exception e) {
            Log.e(TAG, e.getLocalizedMessage());
            e.printStackTrace();
        }

//        RemoteTunnel.closeTunnels();

        onStartCheckRemote();
    }

    @Override
    protected void onStop() {
        super.onStop();

        onStopPlayVideo();
        RemoteTunnel.closeTunnels();

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        for (int i = 0; i < permissions.length; i++) {
            if (grantResults[i] != PackageManager.PERMISSION_GRANTED) {
                // TODO ??????????????? ?????? ?????? ?????? ?????? ??? ???????????? ??????

                finish();
            }

        }
    }

    @Override
    public void setDataBinding() {
        super.setDataBinding();

        dataBinding = DataBindingUtil.inflate(getLayoutInflater(), R.layout.activity_petica_camera, baseDataBinding.baseContainer, true);
        dataBinding.setLifecycleOwner(this);

        dataBinding.tgbSpeaker.setOnClickListener(v -> {
            Log.i(TAG, "????????? ??????");

            balloon = new Balloon.Builder(this)
                    .setArrowSize(5)
                    .setArrowOrientation(ArrowOrientation.BOTTOM)
                    .setArrowConstraints(ArrowConstraints.ALIGN_ANCHOR)
                    //.setIconDrawable(ContextCompat.getDrawable(this, R.drawable.ic_arrow_next_small))
                    .setBackgroundColor(ContextCompat.getColor(this, R.color.gray))
                    .setArrowPosition(0.5f)
                    //.setArrowVisible(true)
                    .setWidthRatio(0.1f)
                    //.setWidth(BalloonSizeSpec.WRAP)
                    .setWidth(10)
                    .setHeight(30)
                    .setTextSize(16f)
                    .setCornerRadius(4f)
                    .setAlpha(0.7f)
                    .setText(getString(R.string.tooltipSpeaker))
                    .setTextColor(R.color.black)
                    .setBalloonAnimation(BalloonAnimation.FADE)
                    .build();

            balloon.showAlignTop(dataBinding.tgbSpeaker);

            speakerOn = !speakerOn;
            setSpeaker(player, speakerOn);
        });

        dataBinding.tgbMic.setOnClickListener((v) -> {
            Log.i(TAG, "????????? ??????");

            balloon = new Balloon.Builder(this)
                    .setArrowSize(5)
                    .setArrowOrientation(ArrowOrientation.BOTTOM)
                    .setArrowConstraints(ArrowConstraints.ALIGN_ANCHOR)
                    //.setIconDrawable(ContextCompat.getDrawable(this, R.drawable.ic_arrow_next_small))
                    .setBackgroundColor(ContextCompat.getColor(this, R.color.gray))
                    .setArrowPosition(0.5f)
                    //.setArrowVisible(true)
                    .setWidthRatio(0.1f)
                    //.setWidth(BalloonSizeSpec.WRAP)
                    .setWidth(10)
                    .setHeight(30)
                    .setTextSize(16f)
                    .setCornerRadius(4f)
                    .setAlpha(0.7f)
                    .setText(getString(R.string.tooltipMic))
                    .setTextColor(R.color.black)
                    .setBalloonAnimation(BalloonAnimation.FADE)
                    .build();

            balloon.showAlignTop(dataBinding.tgbMic);

            voiceRecordOn = !voiceRecordOn;

            Log.i(TAG, "voiceRecordOn = " + voiceRecordOn);
            setMic(voiceRecordOn);
        });

        dataBinding.btnRecordingVideo.setOnClickListener(v -> {
            Log.i(TAG, "?????? ??????");

            balloon = new Balloon.Builder(this)
                    .setArrowSize(5)
                    .setArrowOrientation(ArrowOrientation.BOTTOM)
                    .setArrowConstraints(ArrowConstraints.ALIGN_ANCHOR)
                    //.setIconDrawable(ContextCompat.getDrawable(this, R.drawable.ic_arrow_next_small))
                    .setBackgroundColor(ContextCompat.getColor(this, R.color.gray))
                    .setArrowPosition(0.5f)
                    //.setArrowVisible(true)
                    .setWidthRatio(0.1f)
                    //.setWidth(BalloonSizeSpec.WRAP)
                    .setWidth(10)
                    .setHeight(30)
                    .setTextSize(16f)
                    .setCornerRadius(4f)
                    .setAlpha(0.7f)
                    .setText(getString(R.string.tooltipRec))
                    .setTextColor(R.color.black)
                    .setBalloonAnimation(BalloonAnimation.FADE)
                    .build();

            balloon.showAlignTop(dataBinding.btnRecordingVideo);

            videoRecordClick.onClick(v);
        });

        dataBinding.btnTakePhoto.setOnClickListener(v -> {
            Log.i(TAG, "???????????? ??????");

            balloon = new Balloon.Builder(this)
                    .setArrowSize(5)
                    .setArrowOrientation(ArrowOrientation.BOTTOM)
                    .setArrowConstraints(ArrowConstraints.ALIGN_ANCHOR)
                    //.setIconDrawable(ContextCompat.getDrawable(this, R.drawable.ic_arrow_next_small))
                    .setBackgroundColor(ContextCompat.getColor(this, R.color.gray))
                    .setArrowPosition(0.5f)
                    //.setArrowVisible(true)
                    .setWidthRatio(0.1f)
                    //.setWidth(BalloonSizeSpec.WRAP)
                    .setWidth(10)
                    .setHeight(30)
                    .setTextSize(16f)
                    .setCornerRadius(4f)
                    .setAlpha(0.7f)
                    .setText(getString(R.string.tooltipCamera))
                    .setTextColor(R.color.black)
                    .setBalloonAnimation(BalloonAnimation.FADE)
                    .build();

            balloon.showAlignTop(dataBinding.btnTakePhoto);

            takePhotoClick.onClick(v);
            /**
             Bitmap bitmap = takeScreenshot();
             saveBitmap(bitmap);
             MediaPlayer mediaPlayer = MediaPlayer.create(PeticaCameraActivity.this, R.raw.photo_voice);
             mediaPlayer.start();
             **/
        });

        dataBinding.btnFeed.setOnClickListener(v -> {
            Log.i(TAG, "????????? ?????? ??????");
            Log.i(TAG, "=========Device IP : " + Executor.DEVICE_IP);
            Log.i(TAG, "=========petica IP : " + peticaIp);

            balloon = new Balloon.Builder(this)
                    .setArrowSize(5)
                    .setArrowOrientation(ArrowOrientation.BOTTOM)
                    .setArrowConstraints(ArrowConstraints.ALIGN_ANCHOR)
                    //.setIconDrawable(ContextCompat.getDrawable(this, R.drawable.ic_arrow_next_small))
                    .setBackgroundColor(ContextCompat.getColor(this, R.color.gray))
                    .setArrowPosition(0.5f)
                    //.setArrowVisible(true)
                    .setWidthRatio(0.1f)
                    //.setWidth(BalloonSizeSpec.WRAP)
                    .setWidth(10)
                    .setHeight(30)
                    .setTextSize(16f)
                    .setCornerRadius(4f)
                    .setAlpha(0.7f)
                    .setText(getString(R.string.tooltipFeed))
                    .setTextColor(R.color.black)
                    .setBalloonAnimation(BalloonAnimation.FADE)
                    .build();

            balloon.showAlignTop(dataBinding.btnFeed);

            NumberPickerSingleDialog numberPickerSingleDialog = new NumberPickerSingleDialog(this, FeedType.FEEDER);
            numberPickerSingleDialog.setOnConfirmClickListner(new NumberPickerSingleDialog.OnClickListener() {
                @Override
                public void onClick(NumberPickerSingleDialog numberPickerSingleDialog, int selectValue) {
                    peticaViewModel.onExecutePeticaFeedFeederManual(peticaIp, randomPortAudio, selectValue
                    //peticaViewModel.onExecutePeticaFeedFeederManual("127.0.0.1", randomPortAudio, selectValue
                            , null
                            , null
                            , null
                            , null
                            , new ReceiveCallback() {
                                @Override
                                public void onReceive(byte[] peticaResponse) {
                                    setPeticaExecutorResult(peticaResponse);

                                    runOnUiThread(() -> {
                                        new Handler().postDelayed(() -> {
                                            peticaViewModel.onExecutePeticaStatusRequest(peticaIp, randomPortAudio
                                                    , null
                                                    , null
                                                    , null
                                                    , null
                                                    , new ReceiveCallback() {
                                                        @Override
                                                        public void onReceive(byte[] peticaResponse) {
                                                            setPeticaExecutorResult(peticaResponse);
                                                        }
                                                    });
                                        }, (selectValue + 1) * 1000);
                                    });

                                }
                            }
                    );
                }
            });

            try {

                numberPickerSingleDialog.show();

            } catch (Exception e) {
                Log.e(TAG, e.getLocalizedMessage());
                e.printStackTrace();
            }


        });

        dataBinding.btnWater.setOnClickListener(v -> {
            Log.i(TAG, "????????? ?????? ??????");

            balloon = new Balloon.Builder(this)
                    .setArrowSize(5)
                    .setArrowOrientation(ArrowOrientation.BOTTOM)
                    .setArrowConstraints(ArrowConstraints.ALIGN_ANCHOR)
                    //.setIconDrawable(ContextCompat.getDrawable(this, R.drawable.ic_arrow_next_small))
                    .setBackgroundColor(ContextCompat.getColor(this, R.color.gray))
                    .setArrowPosition(0.5f)
                    //.setArrowVisible(true)
                    .setWidthRatio(0.1f)
                    //.setWidth(BalloonSizeSpec.WRAP)
                    .setWidth(10)
                    .setHeight(30)
                    .setTextSize(16f)
                    .setCornerRadius(4f)
                    .setAlpha(0.7f)
                    .setText(getString(R.string.tooltipWater))
                    .setTextColor(R.color.black)
                    .setBalloonAnimation(BalloonAnimation.FADE)
                    .build();

            balloon.showAlignTop(dataBinding.btnWater);

            NumberPickerSingleDialog numberPickerSingleDialog = new NumberPickerSingleDialog(this, FeedType.WATER);
            numberPickerSingleDialog.setOnConfirmClickListner(new NumberPickerSingleDialog.OnClickListener() {
                @Override
                public void onClick(NumberPickerSingleDialog numberPickerSingleDialog, int selectValue) {
                    peticaViewModel.onExecutePeticaFeedWaterManual(peticaIp, randomPortAudio, selectValue
                            //peticaViewModel.onExecutePeticaFeedWaterManual("127.0.0.1", randomPortAudio, selectValue
                            , null
                            , null
                            , null
                            , null
                            , new ReceiveCallback() {
                                @Override
                                public void onReceive(byte[] peticaResponse) {
                                    setPeticaExecutorResult(peticaResponse);

                                    runOnUiThread(() -> {
                                        new Handler().postDelayed(() -> {
                                            peticaViewModel.onExecutePeticaStatusRequest(peticaIp, randomPortAudio
                                                    , null
                                                    , null
                                                    , null
                                                    , null
                                                    , new ReceiveCallback() {
                                                        @Override
                                                        public void onReceive(byte[] peticaResponse) {
                                                            setPeticaExecutorResult(peticaResponse);
                                                        }
                                                    });
                                        }, (selectValue + 1) * 1000);
                                    });
                                }
                            }
                    );
                }
            });

            try {

                numberPickerSingleDialog.show();

            } catch (Exception e) {
                Log.e(TAG, e.getLocalizedMessage());
                e.printStackTrace();
            }


        });

        dataBinding.tgbLamp.setOnClickListener((v) -> {

            balloon = new Balloon.Builder(this)
                    .setArrowSize(5)
                    .setArrowOrientation(ArrowOrientation.BOTTOM)
                    .setArrowConstraints(ArrowConstraints.ALIGN_ANCHOR)
                    //.setIconDrawable(ContextCompat.getDrawable(this, R.drawable.ic_arrow_next_small))
                    .setBackgroundColor(ContextCompat.getColor(this, R.color.gray))
                    .setArrowPosition(0.5f)
                    //.setArrowVisible(true)
                    .setWidthRatio(0.1f)
                    //.setWidth(BalloonSizeSpec.WRAP)
                    .setWidth(10)
                    .setHeight(30)
                    .setTextSize(16f)
                    .setCornerRadius(4f)
                    .setAlpha(0.7f)
                    .setText(getString(R.string.tooltipFlash))
                    .setTextColor(R.color.black)
                    .setBalloonAnimation(BalloonAnimation.FADE)
                    .build();

            balloon.showAlignTop(dataBinding.tgbLamp);

            if (!lampOn) {
                Log.i(TAG, "?????? ?????? ??????");
                lampOn = !lampOn;
            } else {
                lampOn = !lampOn;
                Log.i(TAG, "?????? ?????? ??????");
            }
            peticaViewModel.onExecutePeticaLamp(peticaIp, randomPortAudio, lampOn
                    //peticaViewModel.onExecutePeticaLamp("127.0.0.1", randomPortAudio, lampOn
                    , null
                    , null
                    , null
                    , null
                    , new ReceiveCallback() {
                        @Override
                        public void onReceive(byte[] peticaResponse) {
                            setPeticaExecutorResult(peticaResponse);
                        }
                    }
            );


        });

        dataBinding.btnLeft.setOnClickListener(v -> finish());
    }

    @Override
    public void setViewModel() {
        super.setViewModel();


    }

    void onStartCheckRemote() {

        peticaViewModel.openRemoteTunnelVideo(getApplicationContext(), dataBinding.getPetica().getDeviceId(), randomPortVideo
                , (id, result) -> {

                    Log.i(TAG, "openRemoteTunnelVideo result = " + result);

                    // TODO Nabto ???????????? ?????? ??????
                    if (result.equals("CONNECT_TIMEOUT") ||
                            result.equals("NTCS_CLOSED") ||
                            result.equals("NTCS_UNKNOWN") ||
                            result.equals("FAILED")) {

                        // RemoteTunnel.closeTunnels();

                        onStopPlayVideo();
                        finish();

                        return;
                    }

                    Log.i(TAG, "=========start check remote result : " + result);
                    if (result.equals("Local")) {
                        // RemoteTunnel.closeTunnels();

                        randomPortVideo = 554;
                        randomPortAudio = 80;

                        Scanner2 scanner2 = new Scanner2(getApplicationContext());
                        scanner2.setOnScanOverListener((map, inetAddress) -> {

                            Log.i(TAG, "scanner2 onresult");

                            boolean hasScan = false;
                            videoType = 2;

                            for (Map.Entry<InetAddress, String> entry : map.entrySet()) {

                                if (entry.getValue().equals(petica.getDeviceId())) {
                                    hasScan = true;
                                    peticaIp = entry.getKey().toString().replace("/", "");

                                    Log.i(TAG, "petica ip = " + peticaIp);
                                    break;
                                }

                            }

                            if (hasStop) {
                                Log.e(TAG, "life cycle ??? start ??? ???????????? ???????????????");
                                onStopPlayVideo();
                                return;
                            }

                            if (hasScan) {

                                onStartPlayVideo();
                                onStartPlayAudio(false);

                                try {
                                    Thread.sleep(1000);
                                } catch(Exception e) {
                                    e.printStackTrace();
                                }
                                if(progressBar.getVisibility() == View.VISIBLE) {
                                    progressBar.setVisibility(View.INVISIBLE);
                                }

                            } else {

                                // TODO ????????? ????????? ??????
                                Log.e(TAG, "????????? ?????????");

                            }

                        });
                        scanner2.scanAll();


                    } else if (result.equals("Remote P2P")) {

                        videoType = 0;
                        onStartPlayVideo();
                        onStartPlayAudio(true);    // 2021-01-15 ?????? ??????
                        try {
                            Thread.sleep(1000);
                        } catch(Exception e) {
                            e.printStackTrace();
                        }
                        if(progressBar.getVisibility() == View.VISIBLE) {
                            progressBar.setVisibility(View.INVISIBLE);
                        }
                    }

                });
    }

    private void onStartPlayAudio(boolean hasRemote) {

        if (hasRemote) {
            audioRemoteConnect(); // TODO ????????? ?????? ??????
        } else {
            randomPortAudio = 80;
        }
    }

    public void onStartPlayVideo() {

        Log.e(TAG, "randomPortVideo = " + randomPortVideo);

        // TODO Rak?????? Lx520 ?????? ???
        // TODO ????????? ????????? Lx520 ????????? ????????????
        /*
        if (!_isLx520) {
            getPipe();
        }
        */

        //System.load("/src/main/petifelibs/libnabto_client_api_jni.so");

        //String libraryPath = getApplicationContext().getApplicationInfo().nativeLibraryDir;
        //Log.i("INFO", libraryPath);
        //libraryPath = libraryPath.replace("/lib/arm64", "");
        //Log.i("INFO", libraryPath);

        //System.load("/src/main/petifelibs/libfaad.so");
        //System.load("/src/main/petifelibs/libmp4v2.so");
        //System.load("/src/main/petifelibs/libwisview.sdk.so");

        _connectTime = 0;



        try {

            if (module == null) {
                module = new Module(this);
            } else {
                module.setContext(this);
            }

        } catch (Exception e) {
            Log.e(TAG, e.getLocalizedMessage());
            e.printStackTrace();
            return;
        }

        module.setLogLevel(Enums.LogLevel.VERBOSE);
        module.setUsername("admin");
        module.setPassword(petica.getDevicePw());
        module.setPlayerPort(randomPortVideo);
        module.setModuleIp(peticaIp);
        controller = module.getController();
        player = module.getPlayer();
        player.setAudioOutput(false);
        player.setRecordFrameRate(_fps);
        player.setTimeout(10000);


        Log.i(TAG, "module.getPlayerPort = " + module.getPlayerPort());


        player.setOnTimeoutListener(() -> {
            // TODO Auto-generated method stub

        });


        recoding = player.isRecording();
        //player.setDisplayView(getApplication(), dataBinding.videoView, null, _decoderType);

        mVideoWidth = getWindow().getDecorView().getWidth();
        mVideoHeight = getWindow().getDecorView().getHeight();
        Log.i(TAG, "===============screen width : " + mVideoWidth);
        Log.i(TAG, "===============screen height : " + mVideoHeight);

        if (player.getState() == Enums.State.IDLE) {
            if (peticaIp.equals("127.0.0.1")) {
                _pipe = Enums.Pipe.H264_SECONDARY;
//                player.setImageSize(320, 240);
//                player.setImageSize(640, 480);
                player.setImageSize(1280, 720);
                //player.setImageSize(mVideoWidth, mVideoHeight);
                protocol = Enums.Transport.TCP;

            } else {
                _pipe = Enums.Pipe.H264_PRIMARY;
                player.setImageSize(1280, 720);
                //player.setImageSize(mVideoWidth, mVideoHeight);
                protocol = Enums.Transport.UDP;
            }

            try {
                //player.play(_pipe, protocol, peticaIp);

                ArrayList<String> options = new ArrayList<String>();
                //options.add("--subsdec-encoding <encoding>");
                //options.add("--aout=opensles");
                options.add("--audio-time-stretch"); // time stretching
                options.add("-vvv"); // verbosity
                //options.add("--drop-late_frames");
                options.add("--skip-frames");
                //options.add("--file-caching=500");
                //?????? ???????????? libvlc ??????
                libvlc = new LibVLC(this, options);

                // ?????? ????????? ????????? ??? ??????
                holder.setKeepScreenOn(true);

                mMediaPlayer = new org.videolan.libvlc.MediaPlayer(libvlc);
                mMediaPlayer.setEventListener(mPlayerListener);

                DisplayMetrics displayMetrics = new DisplayMetrics();
                getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);

                ViewGroup.LayoutParams videoParams = mSurface.getLayoutParams();
                videoParams.width = displayMetrics.widthPixels;
                videoParams.height = displayMetrics.heightPixels;

                // ????????? surface ?????? ?????? ??????
                vout = mMediaPlayer.getVLCVout();

                vout.setVideoView(mSurface);
                vout.setWindowSize(videoParams.width, videoParams.height);
                vout.addCallback(this);
                //vout.setWindowSize(3200, 1440);

                //?????? ?????? ??????
                vout.addCallback(this);
                //???????????? ????????? ??????
                vout.attachViews();

                //rtsp://admin:1soX3yJ7@127.0.0.1:45623/cam1/h264-1
                mediaFile = "rtsp://admin:" + petica.getDevicePw() + "@" + peticaIp + ":" + randomPortVideo + "/cam1/h264";
                Log.i(TAG, "====================remote address : " + mediaFile);

                //Media m;
                m = new Media(libvlc, Uri.parse(mediaFile));
                m.setHWDecoderEnabled(true, false);
                m.addOption(":codec=mediacodec,iomx,all");
                m.addOption(":file-caching:1500");
                m.addOption(":network-caching=1500");
                m.addOption(":live-caching=1500");
                m.addOption(":no-mediacodec-dr");
                m.addOption(":no-omxil-dr");
                m.addOption(":sout-mux-caching=10");
                m.addOption(":rtsp-mcast");
                m.addOption(":rtsp-caching=1500");
                //m.addOption(":sout=#transcode{vcodec=mp4v,vb=800,fps=30,scale=0.25,acodec=none}");
                m.addOption("rtp{sdp=rtsp://:8554/");
                m.addOption(":sout-keep");

                mMediaPlayer.setMedia(m);
                //mMediaPlayer.setAspectRatio("18:9");
                mMediaPlayer.setScale(0);
                mMediaPlayer.play();
                mMediaPlayer.setAudioTrack(-1);
                mMediaPlayer.setVolume(0);

            } catch (Exception e) {
                Log.e("====>", "psk error");
            }

        } else {
            if (player != null)
                player.stop();
        }

//        updateState(player.getState()); // TODO ???????????? ?????? ?????? ?????? ?????? ????????????
        //final int id = android.os.Process.myUid();
        //_lastTraffic = TrafficStats.getUidRxBytes(id);

        /** 2019-04-20
         * ??? ?????? ????????? player ????????? ?????????
         * ?????? ?????? ???????????? */
//        _trafficThread = new Thread(new Runnable() {
//            @Override
//            public void run() {
//                while (!_stopTraffic) {
//                    runOnUiThread(new Runnable() {
//                        @Override
//                        public void run() {
//                            if (player != null) {
//
//                                // TODO ????????? ?????? ?????? ?????? ??????
//                                /*
//                                Log.e("Reconnect...", "");
//                                if (player.getState() == Enums.State.IDLE) {
////                                    _videoConnectLayout.setVisibility(View.VISIBLE); // TODO ???????????? ???????????? ??????
////                                    _videoLayout.setVisibility(View.GONE); // TODO ???????????? ???????????? ??????
//                                    player.stop();
//                                    if (peticaIp.equals("127.0.0.1")) {
//                                        if (videoType == 0) {
//                                            _pipe = Enums.Pipe.H264_SECONDARY;
//                                        } else {
//                                            _pipe = Enums.Pipe.MJPEG_PRIMARY;
//                                        }
//
//                                        if (deviceId.equals("www.sunnyoptical.com")) {
//                                            String url = "rtsp://" + peticaIp + "/live1.sdp";
//                                            player.playUrl(url, Enums.Transport.TCP);
//                                        } else {
//                                            player.play(_pipe, Enums.Transport.TCP);
//                                        }
//                                    } else {
//                                        if (videoType == 0) {
//                                            _pipe = Enums.Pipe.H264_PRIMARY;
//                                        } else {
//                                            _pipe = Enums.Pipe.MJPEG_PRIMARY;
//                                        }
//
//                                        if (deviceId.equals("www.sunnyoptical.com")) {
//                                            String url = "rtsp://" + peticaIp + "/live1.sdp";
//                                            player.playUrl(url, Enums.Transport.UDP);
//                                        } else {
//                                            player.play(_pipe, Enums.Transport.UDP);
//                                        }
//                                    }
//                                }
//                                 */
//                            }
//
//                            // TODO ????????? ????????? ?????? ui ?????? ??????
//                            /*
//                            if (recoding) {
//                                videotime++;
//                                _videoRecordTime.setVisibility(View.VISIBLE);
//                                _videoRecordTime.setText("REC " + showTimeCount(videotime));
//                            } else {
//                                videotime = 0;
//                                _videoRecordTime.setVisibility(View.INVISIBLE);
//                            }
//
//                            if (Is_Sd_Record) {
//                                sdvideotime++;
//                                video_sd_record_time.setVisibility(View.VISIBLE);
//                                video_sd_record_time.setText("REC " + showTimeCount(sdvideotime));
//                            } else {
//                                sdvideotime = 0;
//                                video_sd_record_time.setVisibility(View.INVISIBLE);
//                            }
//                            */
//                        }
//                    });
//
//                    try {
//                        Thread.sleep(1000);
//                    } catch (InterruptedException e) {
//                        Log.e(TAG, e.getLocalizedMessage());
//                    }
//                }
//            }
//        });
//
//        _trafficThread.start();
    }

    void audioRemoteConnect() {
        peticaViewModel.openRemoteTunnelAudio(getApplicationContext(), petica.getDeviceId(), randomPortAudio, new RemoteTunnel.OnResultListener() {
            @Override
            public void onResult(String id, String result) {

                Log.i(TAG, "openRemoteTunnelAudio result = " + result);

                // TODO Auto-generated method stub
                if (result.equals("CONNECT_TIMEOUT") ||
                        result.equals("NTCS_CLOSED") ||
                        result.equals("NTCS_UNKNOWN") ||
                        result.equals("FAILED")) {

                    // RemoteTunnel.closeTunnels();
                } else {
//                    getSdStatus(); // TODO SD ?????? ?????? ?????? ?????? ?????? // ??????SD???????????????
                }
            }
        });
    }

    /**
     * onStopPlayVideo
     */
    void onStopPlayVideo() {
        hasStop = true;

        if (player != null) {
            player.stop();
            player.setAudioOutput(false);
            player = null;
        }

        // RemoteTunnel.closeTunnels();
    }

    View.OnClickListener takePhotoClick = new View.OnClickListener() {

        @Override
        public void onClick(View v) {

            MediaPlayer mediaPlayer = MediaPlayer.create(PeticaCameraActivity.this, R.raw.photo_voice);
            mediaPlayer.start();

            try {

                StringBuilder stringBuilder = new StringBuilder();
                stringBuilder.append(DATA_PATH);
                stringBuilder.append(petica.getDeviceName());
                stringBuilder.append("_");
                stringBuilder.append(System.currentTimeMillis());
                stringBuilder.append(".jpg");

                File file = new File(stringBuilder.toString());

                if (!file.getParentFile().exists()) {
                    file.getParentFile().mkdirs();
                }

                //FileOutputStream photoFileStream = new FileOutputStream(file);

                //Log.i(TAG, String.valueOf(photoFileStream == null));
                //Log.i(TAG, String.valueOf(player == null));
                //player.takePhoto().compress(Bitmap.CompressFormat.JPEG, 100, photoFileStream);

                //Media m;
                //m = new Media(libvlc, Uri.parse(mediaFile));

                /**
                 DisplayMetrics displayMetrics = new DisplayMetrics();
                 getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
                 ViewGroup.LayoutParams videoParams = mSurface.getLayoutParams();
                 videoParams.width = displayMetrics.widthPixels;
                 videoParams.height = displayMetrics.heightPixels;

                 Log.i("mediaFile===>", mediaFile);
                 Log.i("mediaFile width===>", String.valueOf(videoParams.width));
                 Log.i("mediaFile height===>", String.valueOf(videoParams.height));
                 Log.i("uri===>", (Uri.parse(mediaFile)).toString());
                 //VLCUtil.getThumbnail(m, videoParams.width, videoParams.height);
                 //byte[] b = VLCUtil.getThumbnail(libvlc, mMediaPlayer.getMedia().getUri(), videoParams.width, videoParams.height);
                 //byte[] b = VLCUtil.getThumbnail(m, videoParams.width, videoParams.height);
                 **/

                //if(b != null) {
                //photoFileStream.write(b);
                //} else {
                //  Log.i("mediaFile==>", "null");
                //}

                //photoFileStream.flush();
                //photoFileStream.close();

                //int rc = FFmpeg.execute("-rtsp_transport tcp -y -i " + mediaFile + "  -f image2 -update 1 " + file.toString());


                long executionId = FFmpeg.executeAsync("-rtsp_transport tcp -y -i " + mediaFile + "  -f image2 -update 1 " + file.toString(), new ExecuteCallback() {

                    @Override
                    public void apply(final long executionId, final int returnCode) {
                        Log.i("executionId===>", String.valueOf(executionId));
                        Intent intent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
                        intent.setData(Uri.fromFile(file));
                        sendBroadcast(intent);
                    }
                });




                /** 2019-04-20
                 * ?????? ????????? ?????? */
                //Log.i("file===>", file.toString());
                Intent intent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
                intent.setData(Uri.fromFile(file));
                sendBroadcast(intent);


                Log.i(TAG, "???????????? ??????");
            } catch (Exception e) {

                Log.e(TAG, "???????????? ??????");
                Log.e(TAG, e.getLocalizedMessage());
                e.printStackTrace();

            }
        }
    };

    View.OnClickListener videoRecordClick = new View.OnClickListener() {
        @Override
        public void onClick(View v) {

            /**
             if (FFmpeg.getInstance(getApplicationContext()).isSupported()) {
             Log.i("ffmpeg===>", "supported");
             } else {
             Log.i("ffmpeg===>", "not supported");
             }
             Intent intent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
             intent.setData(Uri.fromFile(new File(DATA_PATH + videoFilename + ".mp4")));
             sendBroadcast(intent);

             FFmpeg ffmpeg = FFmpeg.getInstance(getApplicationContext());
             **/

            if (recoding) {
                Log.i(TAG, "?????? ??????");

                /**
                 MediaPlayer mediaPlayer = MediaPlayer.create(PeticaCameraActivity.this, R.raw.end_record);
                 mediaPlayer.start();

                 player.endRecord();


                 Intent intent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
                 intent.setData(Uri.fromFile(new File(DATA_PATH + videoFilename + ".mp4")));
                 sendBroadcast(intent);
                 **/
                //ffmpeg.killRunningProcesses(ffTask);
                Intent intent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
                intent.setData(Uri.fromFile(new File(DATA_PATH + videoFilename + ".mp4")));
                sendBroadcast(intent);

                FFmpeg.cancel();

                recoding = false;

            } else {
                Log.i(TAG, "?????? ??????");


                MediaPlayer mediaPlayer = MediaPlayer.create(PeticaCameraActivity.this, R.raw.begin_record);
                mediaPlayer.start();

                Log.i(TAG, "path = " + DATA_PATH);

                videoFilename = petica.getDeviceName() + System.currentTimeMillis();
                //player.beginRecord0(DATA_PATH, videoFilename);

                recoding = true;


                try {
                    StringBuilder stringBuilder = new StringBuilder();
                    stringBuilder.append(DATA_PATH);
                    stringBuilder.append(petica.getDeviceName());
                    stringBuilder.append("_");
                    stringBuilder.append(System.currentTimeMillis());
                    stringBuilder.append(".mp4");

                    File file = new File(stringBuilder.toString());
                    if (!file.getParentFile().exists()) {
                        file.getParentFile().mkdirs();
                    }


                    FileOutputStream photoFileStream = new FileOutputStream(file);

                    photoFileStream.flush();
                    photoFileStream.close();


                    //FFmpeg ffmpeg = FFmpeg.getInstance(getApplicationContext());

                    //int rc = FFmpeg.execute("-rtsp_transport tcp -y -i " + mediaFile + " -c:v mpeg4 " + file.toString());
                    //-f avfoundation -r 60 -video_size 1280x720 -i 0:0 -vcodec h264_videotoolbox -vsync 2 -f mp4 -t 00:00:05 " + outputFilePath

                    recoding = true;

                    long executionId = FFmpeg.executeAsync("-rtsp_transport tcp -y -i " + mediaFile + " -c:v mpeg4 " + file.toString(), new ExecuteCallback() {

                        @Override
                        public void apply(final long executionId, final int returnCode) {
                            Log.i("executionId===>", String.valueOf(executionId));
                            Intent intent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
                            intent.setData(Uri.fromFile(file));
                            sendBroadcast(intent);
                            /**
                             if (rc == RETURN_CODE_SUCCESS) {
                             Log.i(Config.TAG, "Async command execution completed successfully.");
                             } else if (rc == RETURN_CODE_CANCEL) {
                             Log.i(Config.TAG, "Async command execution cancelled by user.");
                             } else {
                             Log.i(Config.TAG, String.format("Async command execution failed with rc=%d.", rc));
                             }
                             **/

                        }
                    });

                    Intent intent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
                    intent.setData(Uri.fromFile(file));
                    sendBroadcast(intent);


                    /**
                     if (rc == RETURN_CODE_SUCCESS) {
                     Log.i(Config.TAG, "Command execution completed successfully.");
                     } else if (rc == RETURN_CODE_CANCEL) {
                     Log.i(Config.TAG, "Command execution cancelled by user.");
                     } else {
                     Log.i(Config.TAG, String.format("Command execution failed with rc=%d and the output below.", rc));
                     Config.printLastCommandOutput(Log.INFO);
                     }
                     **/

                    /**
                     String[] ffmpegCommand = new String[]{"-y", "-i", "-version", mediaFile, "-acodec", "copy", "-vcodec", "copy", file.toString()};

                     ffTask = ffmpeg.execute(ffmpegCommand, new FFcommandExecuteResponseHandler() {

                    @Override
                    public void onStart() {
                    }

                    @Override
                    public void onProgress(String message) {
                    }

                    @Override
                    public void onFailure(String message) {
                    }

                    @Override
                    public void onSuccess(String message) {
                    }

                    @Override
                    public void onFinish() {
                    }

                    });

                     photoFileStream.flush();
                     photoFileStream.close();

                     recoding = true;

                     Timer timer = new java.util.Timer();
                     TimerTask timerTask = new TimerTask() {
                    @Override
                    public void run() {
                    ffTask.sendQuitSignal();
                    }
                    };

                     timer.schedule(timerTask, 30000); // Will stop recording after 30 seconds.
                     **/
                } catch(Exception e) {
                    Log.e("file create error===>", e.toString());

                }

            }
        }
    };

    private void setSpeaker(Player player, boolean state) {
        if (state) {
            Log.i(TAG, "????????? ??????");
            mMediaPlayer.setVolume(50);
        } else {
            Log.i(TAG, "????????? ??????");
            mMediaPlayer.setVolume(0);
        }

        //player.setAudioOutput(state);

    }

    private void setMic(boolean state) {
        if (state) {
            Log.i(TAG, "????????? ??????");
            MediaPlayer mediaPlayer = MediaPlayer.create(PeticaCameraActivity.this, R.raw.begin_record);
            mediaPlayer.start();
            peticaViewModel.onRecordingVoice(peticaIp, randomPortAudio, DATA_PATH);
            //peticaViewModel.onRecordingVoice("127.0.0.1", randomPortAudio, DATA_PATH);

        } else {
            Log.i(TAG, "????????? ??????");
            MediaPlayer mediaPlayer = MediaPlayer.create(PeticaCameraActivity.this, R.raw.end_record);
            mediaPlayer.start();
            peticaViewModel.onStopRecordingVoid();
        }
    }

    private void setPeticaExecutorResult(byte[] peticaResponse) {
        PeticaStatus peticaStatus = peticaViewModel.getStatus(peticaResponse[6]);

        runOnUiThread(() -> {

            if (peticaStatus.isHasFeederRun()) {
                dataBinding.btnFeed.setColorFilter(getResources().getColor(R.color.red));
            } else {
                dataBinding.btnFeed.setColorFilter(getResources().getColor(R.color.white));
            }

            if (peticaStatus.isHasFeederFull()) {

            } else {

            }

            if (peticaStatus.isHasWaterRun()) {
                dataBinding.btnWater.setColorFilter(getResources().getColor(R.color.red));
            } else {
                dataBinding.btnWater.setColorFilter(getResources().getColor(R.color.white));
            }

            if (peticaStatus.isHasWaterFull()) {

            } else {

            }

            dataBinding.tgbLamp.setChecked(peticaStatus.isHasLampOn());

            if (peticaStatus.isHasKeyLock()) {

            } else {

            }

            if (peticaStatus.isHasClcokSet()) {

            } else {

            }

            if (peticaStatus.isHasPowerOn()) {

            } else {

            }

        });

    }

    protected void onResume() {
        super.onResume();
        onStartPlayVideo();
    }


    @Override
    protected void onPause() {
        super.onPause();
        releasePlayer();
    }

    //??????
    @Override
    protected void onDestroy() {
        super.onDestroy();
        releasePlayer();
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        //?????? ????????? ???????????????...
        //????????? ?????? ????????????...
        super.onConfigurationChanged(newConfig);
        setSize(mVideoWidth, mVideoHeight);
    }

    //?????? ????????? ??????
    private void setSize(int width, int height) {
        mVideoWidth = width;
        mVideoHeight = height;
        if (mVideoWidth * mVideoHeight <= 1)
            return;

        if(holder == null || mSurface == null)
            return;

        // get screen size
        int w = getWindow().getDecorView().getWidth();
        int h = getWindow().getDecorView().getHeight();

        // getWindow().getDecorView() doesn't always take orientation into
        // account, we have to correct the values
        boolean isPortrait = getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT;
        if (w > h && isPortrait || w < h && !isPortrait) {
            int i = w;
            w = h;
            h = i;
        }

        float videoAR = (float) mVideoWidth / (float) mVideoHeight;
        float screenAR = (float) w / (float) h;

        if (screenAR < videoAR)
            h = (int) (w / videoAR);
        else
            w = (int) (h * videoAR);

        // force surface buffer size
        holder.setFixedSize(mVideoWidth, mVideoHeight);

        // set display size
        ViewGroup.LayoutParams lp = mSurface.getLayoutParams();
        lp.width = w;
        lp.height = h;
        mSurface.setLayoutParams(lp);
        mSurface.invalidate();
    }

    //@Override
    public void onNewLayout(IVLCVout vout, int width, int height, int visibleWidth, int visibleHeight, int sarNum, int sarDen) {
        //????????? ????????? ????????? ?????? ?????? ??????
        if (width * height == 0)
            return;

        // store video size
        mVideoWidth = width;
        mVideoHeight = height;
        setSize(mVideoWidth, mVideoHeight);
    }

    @Override
    public void onSurfacesCreated(IVLCVout vout) {
        //???????????? ?????? ???
    }

    @Override
    public void onSurfacesDestroyed(IVLCVout vout) {
        //???????????? ?????? ???
    }

    // player stop 2020-10-06 by Andrew Kim
    private void releasePlayer() {
        //?????????????????? ?????????
        //?????? ??????
        if (libvlc == null)
            return;
        if(mMediaPlayer != null) {
            //????????? ??????

            mMediaPlayer.stop();

            final IVLCVout vout = mMediaPlayer.getVLCVout();
            //???????????? ??????
            vout.removeCallback(this);

            //????????? ??? ??????
            vout.detachViews();
        }

        holder = null;
        libvlc.release();
        libvlc = null;

        mVideoWidth = 0;
        mVideoHeight = 0;
    }

    private org.videolan.libvlc.MediaPlayer.EventListener mPlayerListener = new MediaPlayerListener(this);
    private static class MediaPlayerListener implements org.videolan.libvlc.MediaPlayer.EventListener {
        private WeakReference<PeticaCameraActivity> mOwner;

        public MediaPlayerListener(PeticaCameraActivity owner) {
            mOwner = new WeakReference<PeticaCameraActivity>(owner);
        }

        @Override
        public void onEvent(org.videolan.libvlc.MediaPlayer.Event event) {
            PeticaCameraActivity player = mOwner.get();

            switch(event.type) {
                case org.videolan.libvlc.MediaPlayer.Event.EndReached:
                    //????????? ????????? ??????????????????..
                    player.releasePlayer();
                    break;
                case org.videolan.libvlc.MediaPlayer.Event.Playing:
                    player.mMediaPlayer.setVolume(0);
                case org.videolan.libvlc.MediaPlayer.Event.Paused:
                case org.videolan.libvlc.MediaPlayer.Event.Stopped:
                    break;

                //?????? ??? ???????????? ?????? ?????????
                case org.videolan.libvlc.MediaPlayer.Event.TimeChanged: //?????? ?????? ?????????
                    break;
                case org.videolan.libvlc.MediaPlayer.Event.PositionChanged: //????????? ?????? ?????? ?????????
                    //Log.d(TAG, "PositionChanged");
                    break;
                default:
                    break;
            }
        }
    }

    public Bitmap takeScreenshot() {
        Log.i("takeScreenShot===>", "takeScreenShot");
        View rootView = findViewById(android.R.id.content).getRootView();
        rootView.setDrawingCacheEnabled(true);
        return rootView.getDrawingCache();
    }

    public void saveBitmap(Bitmap bitmap) {
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append(DATA_PATH);
        stringBuilder.append(petica.getDeviceName());
        stringBuilder.append("_");
        stringBuilder.append(System.currentTimeMillis());
        stringBuilder.append(".jpg");

        String fileName = stringBuilder.toString();

        File imagePath = new File(Environment.getExternalStorageDirectory() + fileName);

        if (!imagePath.getParentFile().exists()) {
            imagePath.getParentFile().mkdirs();
        }

        Log.i("fileName===>", fileName);
        FileOutputStream fos;
        try {
            fos = new FileOutputStream(imagePath);
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, fos);

            Intent intent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
            intent.setData(Uri.fromFile(imagePath));
            sendBroadcast(intent);
            fos.flush();
            fos.close();
            Log.i("fileSaveComplete===>", "complete");
        } catch (FileNotFoundException e) {
            Log.e("GREC", e.getMessage(), e);
        } catch (IOException e) {
            Log.e("GREC", e.getMessage(), e);
        }
    }
}

