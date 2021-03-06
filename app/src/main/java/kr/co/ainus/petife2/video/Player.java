package kr.co.ainus.petife2.video;

import android.app.Application;
import android.content.Context;
import android.graphics.Bitmap;
import android.media.AudioRecord;
import android.media.AudioTrack;
import android.net.Uri;
import android.os.Handler;
import android.text.TextUtils;
import android.util.Log;
import android.view.TextureView;
import android.view.ViewTreeObserver;
import android.view.ViewTreeObserver.OnGlobalLayoutListener;

import org.videolan.libvlc.LibVLC;
import org.videolan.libvlc.Media;

import kr.co.ainus.petife2.video.Enums.LogLevel;
import kr.co.ainus.petife2.video.Enums.Pipe;
import kr.co.ainus.petife2.video.Enums.Resolution;
import kr.co.ainus.petife2.video.Enums.State;
import kr.co.ainus.petife2.video.Enums.Transport;
import java.io.File;
import java.util.ArrayList;

public class Player extends Application {
    private Player.OnStateChangedListener e;
    private Player.OnVideoSizeChangedListener f;
    private Player.OnRecordStateChangedListener g;
    private Player.OnVideoReceivedListener h;
    private Player.OnPipeChangedListener i;
    private Player.OnTimeoutListener j;
    private Player.OnGetYUVDataListener k;
    private Player.OnH264UpdateListener l;
    private State m;
    private int n;
    private String o;
    private String p;
    private String q;
    private String r;
    private AudioTrack s;
    private byte[] t;
    private DisplayView u;
    private DisplayView v;
    private Transport w;
    private final Object x;
    private int y;
    private int z;
    private int A;
    private int B;
    private String C;
    private String D;
    Handler a;
    private Runnable E;
    private boolean F;
    private Pipe G;
    private String H;
    private int I;
    private boolean J;
    private boolean K;
    private Resolution L;
    private boolean M;
    private int N;
    private int O;
    private long P;
    private boolean Q;
    private String R;
    private int S;
    private int T;
    AudioRecord b;
    int c;
    int d;

    private String petifeIp;

    // libvlc ?????? ?????? ?????? 2020-09-22 by Andrew Kim
    private LibVLC libvlc;
    private org.videolan.libvlc.MediaPlayer mMediaPlayer = null;
    private int mVideoWidth;
    private int mVideoHeight;
    private String mediaFile;

    private static native String jniVersion();

    private native boolean jniPlay(String var1, int var2);

    private native boolean jniStop(int var1);

    private native boolean jniBeginRecord(int var1, String var2, int var3);

    private native boolean jniEndRecord(int var1);

    private native void jniSetLogLevel(int var1);

    private native boolean jniSetRecordFrameRate(int var1, int var2);

    private native boolean jniSetAudioOutput(int var1, boolean var2);

    private native boolean jniEnableHardwareDecoder(int var1, boolean var2);

    private void jniOnClientCreated(int var1) {
        this.n = var1;
        this.jniSetAudioOutput(var1, this.M);
        this.jniEnableHardwareDecoder(var1, this.Q);
    }

    private void jniOnStateChanged(int var1) {
        this.setState(State.values()[var1]);
    }

    private void jniOnVideoInfo(String var1, String var2) {
        this.o = var1;
        this.p = var2;
    }

    private void jniOnAudioInfo(String var1, String var2, int var3, int var4) {
        this.c = var4;
        this.d = var3;
        if (var3 != -1 && var4 != -1) {
            this.q = var1;
            this.r = var2;
            int var6 = var4 == 1 ? 4 : 12;
            int var5 = AudioTrack.getMinBufferSize(var3, var6, 2) * 4;
            if (this.b != null) {
                this.s = new AudioTrack(3, var3, var6, 2, var5, 1, this.b.getAudioSessionId());
            } else {
                this.s = new AudioTrack(3, var3, var6, 2, var5, 1);
            }

            this.s.play();
            Log.e("==>", "channels=" + var4 + "sampleRate=" + var3);
        }
    }

    public void SetAudioRecord(AudioRecord var1) {
        this.b = var1;
    }

    public void SetAudioTrack(AudioRecord var1) {
        if (this.s != null && var1 != null) {
            int var2 = this.c == 1 ? 4 : 12;
            int var3 = AudioTrack.getMinBufferSize(this.d, var2, 2) * 4;
            this.s = new AudioTrack(3, this.d, var2, 2, var3, 1, var1.getAudioSessionId());
        }

    }

    private void jniOnVideo(final int var1, final int var2, final byte[] var3, final byte[] var4, final byte[] var5) {
        if (this.K && this.k != null) {
            this.a.post(new Runnable() {
                public void run() {
                    Player.this.k.onResult(var1, var2, var3, var4, var5);
                }
            });
        }

        this.P = System.currentTimeMillis();
        if (!this.J) {
            this.J = true;
            if (this.h != null) {
                this.a.post(new Runnable() {
                    public void run() {
                        Player.this.h.onReceived();
                    }
                });
            }
        }

        boolean var6 = this.z != var1 || this.y != var2;
        this.z = var1;
        this.y = var2;
        if (this.z == 0) {
            this.z = this.B;
            this.y = this.A;
        }

        if (this.f != null && var6) {
            this.a.post(new Runnable() {
                public void run() {
                    Player.this.f.onVideoSizeChanged(var1, var2);
                }
            });
        }

        if (var6) {
            switch(var1) {
                case 320:
                    this.L = Resolution.RES_QVGA;
                    break;
                case 640:
                    this.L = Resolution.RES_VGA;
                    break;
                case 720:
                    if (var2 == 576) {
                        this.L = Resolution.RES_720X576;
                    } else if (var2 == 480) {
                        this.L = Resolution.RES_720X480;
                    }
                    break;
                case 1280:
                    this.L = Resolution.RES_720P;
                case 1920:
                    this.L = Resolution.RES_1080P;
            }
        }

        if (this.u != null) {
            this.u.a(var1, var2, var3, var4, var5);
        }

        if (this.v != null) {
            this.v.a(var1, var2, var3, var4, var5);
        }

    }

    public void startGetYUVData(boolean var1) {
        this.K = var1;
    }

    private void jniOnAudio(byte[] var1) {
        this.P = System.currentTimeMillis();
        synchronized(this.x) {
            if (this.Q) {
                if (this.q.equals("AAC")) {
                    this.t = var1;
                } else {
                    int var3 = var1.length;
                    byte[] var4 = new byte[var3 * 2];

                    for(int var5 = 0; var5 < var3; ++var5) {
                        int var6 = PCMA2PCM.a(var1[var5]);
                        int var7 = var6 < -32768 ? -32768 : (var6 > 32767 ? 32767 : var6);
                        var4[var5 * 2 + 1] = (byte)((var7 & '\uff00') >> 8);
                        var4[var5 * 2] = (byte)(var7 & 255);
                    }

                    this.t = var4;
                }
            } else {
                this.t = var1;
            }

            this.x.notify();
        }
    }

    private void jniOnH264(int var1, int var2, int var3, byte[] var4) {
        if (var1 == 0) {
            var1 = this.B;
            var2 = this.A;
        }

        if (this.l != null) {
            this.l.onH264Updated(var1, var2, var3, var4);
        }

        if (this.Q && var3 > 0) {
            if (this.u != null) {
                this.u.onFrame(var1, var2, var4, 0, var3);
            }

            if (this.v != null) {
                this.v.onFrame(var1, var2, var4, 0, var3);
            }
        }

    }

    void a(Runnable var1) {
        if (this.stop()) {
            this.E = var1;
        } else {
            var1.run();
        }

    }

    public void setImageSize(int var1, int var2) {
        this.A = var2;
        this.B = var1;
    }

    public void setViewSize(int var1, int var2) {
        if (this.u != null) {
            this.u.setViewSize(var1, var2);
        }

        if (this.v != null) {
            this.v.setViewSize(var1, var2);
        }

    }

    private void setState(final State var1) {
        this.m = var1;
        switch(this.m) {
            case PLAYING:
                (new Thread(new Runnable() {
                    public void run() {
                        while(true) {
                            if (Player.this.P > 0L && System.currentTimeMillis() - Player.this.P > (long)Player.this.O) {
                                Player.this.a.post(new Runnable() {
                                    public void run() {
                                        Player.this.stop();
                                    }
                                });
                                Player.this.P = 0L;
                                if (Player.this.j != null) {
                                    Player.this.a.post(new Runnable() {
                                        public void run() {
                                            Player.this.j.onTimeout();
                                        }
                                    });
                                }
                                break;
                            }

                            if (Player.this.m != State.PLAYING) {
                                Player.this.P = 0L;
                                break;
                            }

                            try {
                                Thread.sleep(100L);
                            } catch (InterruptedException var2) {
                            }
                        }

                    }
                })).start();
                (new Thread(new Runnable() {
                    public void run() {
                        while(Player.this.m == State.PLAYING) {
                            synchronized(Player.this.x) {
                                try {
                                    Player.this.x.wait();
                                } catch (InterruptedException var5) {
                                }
                            }

                            if (Player.this.s != null && Player.this.t != null && Player.this.t.length > 0) {
                                try {
                                    Player.this.s.write(Player.this.t, 0, Player.this.t.length);
                                } catch (Exception var4) {
                                }
                            }
                        }

                    }
                })).start();
                break;
            case IDLE:
                if (this.s != null) {
                    if (this.s.getState() == 3) {
                        this.s.flush();
                        this.s.stop();
                    }

                    this.s.release();
                }

                synchronized(this.x) {
                    this.x.notify();
                }

                if (this.E != null) {
                    this.E.run();
                    this.E = null;
                }

                this.J = false;
                this.z = this.y = 0;
                this.n = 0;
                System.gc();
        }

        this.a.post(new Runnable() {
            public void run() {
                if (Player.this.e != null) {
                    Player.this.e.onStateChanged(var1);
                }

            }
        });
    }

    private void setRecordState(boolean var1) {
        this.F = var1;
        if (var1 && this.N > 0) {
            this.jniSetRecordFrameRate(this.n, this.N);
        }

        if (this.g != null) {
            this.g.onStateChanged(this.F);
        }

    }

    public Player(Context var1) {
        this.m = State.IDLE;
        this.x = new Object();
        this.A = 0;
        this.B = 0;
        this.F = false;
        this.J = false;
        this.K = false;
        this.M = true;
        this.O = 5000;
        this.Q = false;
        this.R = "video/avc";
        this.S = 1280;
        this.T = 720;
        this.a = new Handler(var1.getMainLooper());
    }

    public String version() {
        return jniVersion();
    }

    public boolean stop() {
        boolean var1 = false;
        if (this.F) {
            this.endRecord();
        }

        if (this.m == State.PLAYING || this.m == State.PREPARING) {
            //var1 = this.jniStop(this.n);
            var1 = true;
        }

        return var1;
    }

    public boolean beginRecord(String var1, String var2) {
        File var3 = new File(var1);
        if (!var3.exists() && !var3.mkdirs()) {
            return false;
        } else {
            //boolean var4 = this.m == State.PLAYING && this.jniBeginRecord(this.n, var1 + "/" + var2 + ".mp4", 1);
            boolean var4 = this.m == State.PLAYING && true;
            this.setRecordState(var4);
            return var4;
        }
    }

    public boolean beginRecord0(String var1, String var2) {
        File var3 = new File(var1);
        if (!var3.exists() && !var3.mkdirs()) {
            return false;
        } else {
            //boolean var4 = this.m == State.PLAYING && this.jniBeginRecord(this.n, var1 + "/" + var2 + ".mp4", 0);
            boolean var4 = this.m == State.PLAYING && true;
            this.setRecordState(var4);
            return var4;
        }
    }

    public boolean endRecord() {
        //boolean var1 = this.jniEndRecord(this.n);
        boolean var1 = true;
        this.setRecordState(!var1);
        System.gc();
        return var1;
    }

    public String getVideoCodecName() {
        return this.o;
    }

    public String getVideoCodecLongName() {
        return this.p;
    }

    public String getAudioCodecName() {
        return this.q;
    }

    public String getAudioCodecLongName() {
        return this.r;
    }

    public State getState() {
        return this.m;
    }

    public Transport getTransport() {
        return this.w;
    }

    public int getVideoHeight() {
        return this.y;
    }

    public int getVideoWidth() {
        return this.z;
    }

    public boolean isRecording() {
        return this.F;
    }

    public void setDisplayView(Context var1, DisplayView var2, DisplayView var3, int var4) {
        this.Q = false;
        this.setEnableHardwareDecoder(this.Q);
        if (this.u != null) {
            this.u.removeAllViews();
            this.u = null;
        }

        if (this.v != null) {
            this.v.removeAllViews();
            this.v = null;
        }

        this.u = var2;
        this.v = var3;
        ViewTreeObserver var5;
        if (this.u != null) {
            var5 = this.u.getViewTreeObserver();
            if (var5.isAlive()) {
                var5.addOnGlobalLayoutListener(new OnGlobalLayoutListener() {
                    public void onGlobalLayout() {
                        Compatibility.removeOnGlobalLayoutListener(Player.this.u.getViewTreeObserver(), this);
                        Player.this.setState(Player.this.m);
                    }
                });
            }

            this.u.a(var1, var4, 1);
        }

        if (this.v != null) {
            var5 = this.v.getViewTreeObserver();
            if (var5.isAlive()) {
                var5.addOnGlobalLayoutListener(new OnGlobalLayoutListener() {
                    public void onGlobalLayout() {
                        Compatibility.removeOnGlobalLayoutListener(Player.this.v.getViewTreeObserver(), this);
                        Player.this.setState(Player.this.m);
                    }
                });
            }

            this.v.a(var1, var4, 2);
        }

        if (var4 == 2) {
            this.Q = true;
            this.setEnableHardwareDecoder(this.Q);
        } else {
            this.Q = false;
            this.setEnableHardwareDecoder(this.Q);
        }

    }

    public boolean play(Pipe var1, Transport var2, String peticaIp) {
        boolean var3 = false;
        if(null != peticaIp && !"".equals(peticaIp)) {
            petifeIp = peticaIp;
        }
        if (this.m == State.IDLE) {
            String var4 = "";
            switch(var1) {
                case H264_PRIMARY:
                    var4 = "/cam1/h264";
                    break;
                case H264_SECONDARY:
                    var4 = "/cam1/h264-1";
                    break;
                case MJPEG_PRIMARY:
                    var4 = "/cam1/mpeg4";
                    break;
                case MJPEG_SECONDARY:
                    var4 = "/cam1/mpeg4-1";
            }

            String var5 = "rtsp://";
            if (!TextUtils.isEmpty(this.C) && !TextUtils.isEmpty(this.D)) {
                var5 = var5 + this.C + ":" + this.D + "@" + this.H + ":" + this.I + var4;
            } else {
                var5 = var5 + this.H + ":" + this.I + var4;
            }

            Log.e("rtspUrl==>", var5);
            Log.e("protocol==>", String.valueOf(var2.ordinal()));

            ArrayList<String> options = new ArrayList<String>();
            //options.add("--subsdec-encoding <encoding>");
            options.add("--aout=opensles");
            options.add("--audio-time-stretch"); // time stretching
            options.add("-vvv"); // verbosity
            //?????? ???????????? libvlc ??????
            libvlc = new LibVLC(this, options);

            mMediaPlayer = new org.videolan.libvlc.MediaPlayer(libvlc);

            //rtsp://admin:1soX3yJ7@127.0.0.1:45623/cam1/h264-1
            //mediaFile = "rtsp://admin:1soX3yJ7@" + peticaIp + ":" + randomPortVideo + "/cam1/h264-1";
            mediaFile = var5;

            Media m;
            m = new Media(libvlc, Uri.parse(mediaFile));
            mMediaPlayer.setMedia(m);
            mMediaPlayer.play();

            //var3 = this.jniPlay(var5, var2.ordinal());
            /**
            if(peticaIp.equals("127.0.0.1")) {
                var3 = this.jniPlay(var5, Enums.Transport.TCP.);
            } else {
                var3 = this.jniPlay(var5, Enums.Transport.UDP.ordinal());
            }
             **/
            //var3 = false;
            this.G = var1;
            this.w = var2;
        }

        return var3;
    }

    public boolean playUrl(String var1, Transport var2, String peticaIp) {
        boolean var3 = false;
        if (this.m == State.IDLE) {
            //var3 = this.jniPlay(var1, var2.ordinal());
            if(peticaIp.equals("127.0.0.1")) {
                var3 = this.jniPlay(var1, Enums.Transport.TCP.ordinal());
            } else {
                var3 = this.jniPlay(var1, Enums.Transport.UDP.ordinal());
            }
            //var3 = false;
            this.w = var2;
        }

        return var3;
    }

    public void changePipe(Pipe var1) {
        if (var1 != null && this.H != null && this.w != null && var1 != this.G) {
            this.G = var1;
            this.a(new Runnable() {
                public void run() {
                    Player.this.play(Player.this.G, Player.this.w, petifeIp);
                    if (Player.this.i != null) {
                        Player.this.i.onChanged(Player.this.G);
                    }

                }
            });
        }
    }

    public void changeViewNum(Context var1, DisplayView var2, DisplayView var3, int var4) {
        this.setDisplayView(var1, var2, var3, var4);
        this.a(new Runnable() {
            public void run() {
                Player.this.play(Player.this.G, Player.this.w, petifeIp);
            }
        });
    }

    public void changeViewType(Context var1, DisplayView var2, DisplayView var3, int var4) {
        this.setDisplayView(var1, var2, var3, var4);
        this.a(new Runnable() {
            public void run() {
                Player.this.play(Player.this.G, Player.this.w, petifeIp);
            }
        });
    }

    public Bitmap takePhoto() {
        return this.u == null ? null : this.u.a();
    }

    public Resolution getResolution() {
        return this.L;
    }

    public void setAudioOutput(boolean var1) {
        this.M = var1;
        if (this.n > 0) {
            this.jniSetAudioOutput(this.n, var1);
        }

    }

    public void setEnableHardwareDecoder(boolean var1) {
        this.Q = var1;
        if (this.n > 0) {
            this.jniEnableHardwareDecoder(this.n, this.Q);
        }

    }

    public boolean getAudioOutput() {
        return this.M;
    }

    public void setRecordFrameRate(int var1) {
        this.N = var1;
    }

    public void setTimeout(int var1) {
        this.O = var1;
    }

    public TextureView getTextureView() {
        return this.u.getGLTextureView();
    }

    public TextureView getTextureView2() {
        return this.v.getGLTextureView2();
    }

    void a(LogLevel var1) {
        //this.jniSetLogLevel(var1.ordinal());
    }

    void a(String var1) {
        this.H = var1;
    }

    void a(int var1) {
        this.I = var1;
    }

    void b(String var1) {
        this.C = var1;
    }

    void c(String var1) {
        this.D = var1;
    }

    public void setOnH264UpdateListener(Player.OnH264UpdateListener var1) {
        this.l = var1;
    }

    public void setOnStateChangedListener(Player.OnStateChangedListener var1) {
        this.e = var1;
    }

    public void setOnVideoSizeChangedListener(Player.OnVideoSizeChangedListener var1) {
        this.f = var1;
    }

    public void setOnRecordStateChangedListener(Player.OnRecordStateChangedListener var1) {
        this.g = var1;
    }

    public void setOnVideoReceivedListener(Player.OnVideoReceivedListener var1) {
        this.h = var1;
    }

    public void setOnPipeChangedListener(Player.OnPipeChangedListener var1) {
        this.i = var1;
    }

    public void setOnTimeoutListener(Player.OnTimeoutListener var1) {
        this.j = var1;
    }

    public void setOnGetYUVDataListener(Player.OnGetYUVDataListener var1) {
        this.k = var1;
    }

    static {
        //System.loadLibrary("faad");
        //System.loadLibrary("mp4v2");
        //System.loadLibrary("wisview.sdk");
    }

    public interface OnGetYUVDataListener {
        void onResult(int var1, int var2, byte[] var3, byte[] var4, byte[] var5);
    }

    public interface OnTimeoutListener {
        void onTimeout();
    }

    public interface OnPipeChangedListener {
        void onChanged(Pipe var1);
    }

    public interface OnVideoReceivedListener {
        void onReceived();
    }

    public interface OnRecordStateChangedListener {
        void onStateChanged(boolean var1);
    }

    public interface OnVideoSizeChangedListener {
        void onVideoSizeChanged(int var1, int var2);

        void onVideoScaledSizeChanged(int var1, int var2);
    }

    public interface OnStateChangedListener {
        void onStateChanged(State var1);
    }

    public interface OnH264UpdateListener {
        void onH264Updated(int var1, int var2, int var3, byte[] var4);
    }
}

