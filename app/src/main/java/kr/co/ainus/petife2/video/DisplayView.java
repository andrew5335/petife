package kr.co.ainus.petife2.video;

import android.content.Context;
import android.graphics.Bitmap;
import android.opengl.GLSurfaceView;
import android.util.AttributeSet;
import android.widget.RelativeLayout;
import android.widget.RelativeLayout.LayoutParams;

public class DisplayView extends RelativeLayout {
    private boolean a = false;
    private a b = null;
    private GLSurfaceView c = null;
    private GLSurfaceView d = null;
    private HardwareDecoderView e = null;
    private HardwareDecoderView f = null;
    public GLTextureView _displayGLTextureView = null;
    public GLTextureView _displayGLTextureView2 = null;
    private b g;
    private boolean h = false;

    public DisplayView(Context var1) {
        super(var1);
        this.a(var1);
    }

    public DisplayView(Context var1, AttributeSet var2) {
        super(var1, var2);
        this.a(var1);
    }

    public DisplayView(Context var1, AttributeSet var2, int var3) {
        super(var1, var2, var3);
        this.a(var1);
    }

    public boolean isGpuRender() {
        return this.a;
    }

    public void setFullScreen(boolean var1) {
        this.h = var1;
    }

    Bitmap a() {
        if (this.e != null) {
            return this.e.takePhoto();
        } else {
            return this.f != null ? this.f.takePhoto() : this.g.a();
        }
    }

    void a(int var1, int var2, byte[] var3, byte[] var4, byte[] var5) {
        if (this.g != null) {
            this.g.a(var1, var2, var3, var4, var5, this.h);
        }
    }

    public void onFrame(int var1, int var2, byte[] var3, int var4, int var5) {
        if (this.e != null) {
            this.e.onFrame(var1, var2, var3, var4, var5, this.h);
        }

        if (this.f != null) {
            this.f.onFrame(var1, var2, var3, var4, var5, this.h);
        }

    }

    public void stophardwareDecoder() {
        if (this.e != null) {
            this.e.stopHardwareDecoder();
        }

        if (this.f != null) {
            this.f.stopHardwareDecoder();
        }

    }

    public void setViewSize(int var1, int var2) {
        if (this.e != null) {
            this.e.setViewSize(var1, var2);
        }

        if (this.f != null) {
            this.f.setViewSize(var1, var2);
        }

    }

    public GLTextureView getGLTextureView() {
        return this._displayGLTextureView;
    }

    public GLTextureView getGLTextureView2() {
        return this._displayGLTextureView2;
    }

    private void a(Context var1) {
        this.a = kr.co.ainus.petife2.video.g.a(var1);
    }

    void a(Context var1, int var2, int var3) {
        if (this.a) {
            LayoutParams var4;
            if (var2 == 0) {
                if (var3 == 1) {
                    this.c = new GLSurfaceView(var1);
                    this.g = new b(var2);
                    this.g.a(this.c);
                    this.addView(this.c);
                    var4 = (LayoutParams)this.c.getLayoutParams();
                    var4.addRule(13, -1);
                } else if (var3 == 2) {
                    this.d = new GLSurfaceView(var1);
                    this.g = new b(var2);
                    this.g.a(this.d);
                    this.addView(this.d);
                    var4 = (LayoutParams)this.d.getLayoutParams();
                    var4.addRule(13, -1);
                }
            } else if (var2 == 1) {
                if (var3 == 1) {
                    this._displayGLTextureView = new GLTextureView(var1);
                    this.addView(this._displayGLTextureView);
                    this._displayGLTextureView.setPaused(false);
                    var4 = (LayoutParams)this._displayGLTextureView.getLayoutParams();
                    var4.addRule(13, -1);
                    this.g = new b(var2);
                    this.g.a(this._displayGLTextureView);
                } else if (var3 == 2) {
                    this._displayGLTextureView2 = new GLTextureView(var1);
                    this.addView(this._displayGLTextureView2);
                    this._displayGLTextureView2.setPaused(false);
                    var4 = (LayoutParams)this._displayGLTextureView2.getLayoutParams();
                    var4.addRule(13, -1);
                    this.g = new b(var2);
                    this.g.a(this._displayGLTextureView2);
                }
            } else if (var2 == 2) {
                if (var3 == 1) {
                    this.e = new HardwareDecoderView(var1);
                    this.addView(this.e);
                    var4 = (LayoutParams)this.e.getLayoutParams();
                    var4.addRule(13, -1);
                } else if (var3 == 2) {
                    this.f = new HardwareDecoderView(var1);
                    this.addView(this.f);
                    var4 = (LayoutParams)this.f.getLayoutParams();
                    var4.addRule(13, -1);
                }
            }
        } else {
            this.b = new a(var1);
            this.addView(this.b);
        }

    }
}
