package kr.co.ainus.petife2.video;

import android.graphics.Bitmap;
import android.opengl.GLSurfaceView;

class b {
    private d a;
    private GLTextureView b;
    private GLSurfaceView c;
    private int d;
    private int e;
    private byte[] f;
    private byte[] g;
    private byte[] h;
    private int i = 0;

    b(int var1) {
        this.i = var1;
        if (var1 == 0) {
            this.a = new d();
        }

    }

    void a(GLSurfaceView var1) {
        this.c = var1;
        this.c.setEGLContextClientVersion(2);
        this.c.setEGLConfigChooser(8, 8, 8, 8, 16, 0);
        this.c.getHolder().setFormat(4);
        this.c.setRenderer(this.a);
        this.c.setRenderMode(0);
        this.c.requestRender();
    }

    void a(GLTextureView var1) {
        this.b = var1;
    }

    void a(int var1, int var2, byte[] var3, byte[] var4, byte[] var5, boolean var6) {
        if (this.i == 0) {
            this.a.a(var1, var2, var3, var4, var5, var6);
            this.c.requestRender();
        } else if (this.i == 1 && this.b.mRenderer != null) {
            this.b.mRenderer.a(var1, var2, var3, var4, var5, var6);
        }

        this.d = var1;
        this.e = var2;
        this.f = var3;
        this.g = var4;
        this.h = var5;
    }

    Bitmap a() {
        if (this.d != 0 && this.e != 0 && this.f != null && this.g != null && this.h != null) {
            d var1 = new d();
            e var2 = new e(this.d, this.e);
            var2.a(var1);
            var1.a(this.d, this.e, this.f, this.g, this.h, false);
            Bitmap var3 = var2.a();
            var2.b();
            return var3;
        } else {
            return null;
        }
    }
}
