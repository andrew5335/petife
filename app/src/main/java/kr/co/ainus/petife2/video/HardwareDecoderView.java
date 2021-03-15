package kr.co.ainus.petife2.video;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.Bitmap.Config;
import android.media.MediaCodec;
import android.media.MediaCrypto;
import android.media.MediaFormat;
import android.media.MediaCodec.BufferInfo;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.Surface;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.SurfaceHolder.Callback;
import android.widget.RelativeLayout.LayoutParams;
import java.io.IOException;
import java.nio.ByteBuffer;

public class HardwareDecoderView extends SurfaceView implements Callback {
    private MediaCodec f;
    private boolean g = false;
    private int h = 0;
    private int i = 0;
    private final SurfaceHolder j = this.getHolder();
    private Paint k;
    private Canvas l;
    private HardwareDecoderView m = this;
    private Surface n;
    boolean a = false;
    private LayoutParams o;
    private Handler p = new Handler() {
        public void handleMessage(Message var1) {
            if (var1.what == 0) {
                HardwareDecoderView.this.m.setLayoutParams(HardwareDecoderView.this.o);
            }

        }
    };
    int b = -1;
    int c = -1;
    int d = -1;
    int e = -1;

    HardwareDecoderView(Context var1) {
        super(var1);
        this.a();
    }

    private void a() {
        this.j.setFormat(4);
        this.j.addCallback(this);
        this.k = new Paint(2);
        this.n = this.j.getSurface();
    }

    public void initHardwareDecoder(String var1, int var2, int var3) {
        if (!this.g) {
            try {
                this.f = MediaCodec.createDecoderByType(var1);
                MediaFormat var4 = MediaFormat.createVideoFormat(var1, var2, var3);
                this.f.configure(var4, this.n, (MediaCrypto)null, 0);
                this.f.start();
                this.g = true;
            } catch (IOException var5) {
                var5.printStackTrace();
            }
        }

    }

    public void stopHardwareDecoder() {
        if (this.f != null) {
            this.f.stop();
            this.f.release();
            this.f = null;
            this.g = false;
        }

    }

    public Bitmap takePhoto() {
        if (this.f == null) {
            return null;
        } else {
            this.a = true;
            Bitmap var1 = Bitmap.createBitmap(this.b, this.c, Config.RGB_565);
            if (this.n.isValid()) {
                Log.e("_surface=====>", "Valid");

                try {
                    this.l = this.n.lockCanvas((Rect)null);
                    if (this.l != null) {
                        synchronized(this.j) {
                            this.l.drawBitmap(var1, (float)this.b, (float)this.c, this.k);
                        }
                    }
                } catch (IllegalArgumentException var9) {
                    var9.printStackTrace();
                    Log.e("Exception=====>", var9.toString());
                } finally {
                    if (this.l != null) {
                        this.n.unlockCanvasAndPost(this.l);
                    }

                }
            }

            this.a = false;
            return var1;
        }
    }

    public boolean onFrame(int var1, int var2, byte[] var3, int var4, int var5, boolean var6) {
        if (this.f == null) {
            return false;
        } else if (!this.g) {
            return false;
        } else {
            this.a(var1, var2, var6);
            if (this.n.isValid()) {
                try {
                    ByteBuffer[] var7 = this.f.getInputBuffers();
                    int var8 = this.f.dequeueInputBuffer(0L);
                    if (var8 < 0) {
                        return false;
                    }

                    ByteBuffer var9 = var7[var8];
                    var9.clear();
                    var9.put(var3, var4, var5);
                    this.f.queueInputBuffer(var8, 0, var5, 0L, 0);
                    BufferInfo var12 = new BufferInfo();

                    for(int var10 = this.f.dequeueOutputBuffer(var12, 0L); var10 >= 0; var10 = this.f.dequeueOutputBuffer(var12, 0L)) {
                        this.f.releaseOutputBuffer(var10, true);
                    }
                } catch (IllegalStateException var11) {
                    Log.e("IllegalState==>", var11.toString());
                }
            }

            return true;
        }
    }

    public void setViewSize(int var1, int var2) {
        this.h = var1;
        this.i = var2;
    }

    public void surfaceCreated(SurfaceHolder var1) {
        if (this.h == 0) {
            this.h = this.getWidth();
            this.i = this.getHeight();
        }

        this.initHardwareDecoder("video/avc", this.h, this.i);
        Log.e("_viewWidth==>", "" + this.h);
        Log.e("_viewHeight==>", "" + this.i);
    }

    public void surfaceChanged(SurfaceHolder var1, int var2, int var3, int var4) {
    }

    public void surfaceDestroyed(SurfaceHolder var1) {
        Log.e("SurfaceHolder==>", "surfaceDestroyed");
        this.stopHardwareDecoder();
    }

    void a(int var1, int var2, boolean var3) {
        if (var1 != 0 && var2 != 0 && this.h != 0 && this.i != 0) {
            if (var1 != this.b || var2 != this.c || this.h != this.d || this.i != this.e) {
                float var4 = (float)this.i / (float)this.h;
                float var5 = (float)var2 / (float)var1;
                if (var4 != var5 && !var3) {
                    if (var4 < var5) {
                        this.o = (LayoutParams)this.getLayoutParams();
                        this.o.setMargins(this.h - this.i * var1 / var2, 0, 0, 0);
                        this.p.sendEmptyMessageDelayed(0, 0L);
                    } else {
                        this.o = (LayoutParams)this.getLayoutParams();
                        this.o.setMargins(0, this.i - this.h * var2 / var1, 0, 0);
                        this.p.sendEmptyMessageDelayed(0, 0L);
                    }
                } else {
                    this.o = (LayoutParams)this.getLayoutParams();
                    this.o.setMargins(0, 0, 0, 0);
                    this.p.sendEmptyMessageDelayed(0, 0L);
                }

                this.b = var1;
                this.c = var2;
                this.e = this.i;
                this.d = this.h;
            }

        }
    }
}

