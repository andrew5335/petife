package kr.co.ainus.petife2.video;

import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.opengl.GLSurfaceView.Renderer;
import java.nio.IntBuffer;
import javax.microedition.khronos.egl.EGL10;
import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.egl.EGLContext;
import javax.microedition.khronos.egl.EGLDisplay;
import javax.microedition.khronos.egl.EGLSurface;
import javax.microedition.khronos.opengles.GL10;

class e {
    Renderer a;
    int b;
    int c;
    Bitmap d;
    EGL10 e;
    EGLDisplay f;
    EGLConfig[] g;
    EGLConfig h;
    EGLContext i;
    EGLSurface j;
    GL10 k;
    String l;

    e(int var1, int var2) {
        this.b = var1;
        this.c = var2;
        int[] var3 = new int[2];
        int[] var4 = new int[]{12375, this.b, 12374, this.c, 12344};
        this.e = (EGL10)EGLContext.getEGL();
        this.f = this.e.eglGetDisplay(EGL10.EGL_DEFAULT_DISPLAY);
        this.e.eglInitialize(this.f, var3);
        this.h = this.c();
        short var5 = 12440;
        int[] var6 = new int[]{var5, 2, 12344};
        this.i = this.e.eglCreateContext(this.f, this.h, EGL10.EGL_NO_CONTEXT, var6);
        this.j = this.e.eglCreatePbufferSurface(this.f, this.h, var4);
        this.e.eglMakeCurrent(this.f, this.j, this.j, this.i);
        this.k = (GL10)this.i.getGL();
        this.l = Thread.currentThread().getName();
    }

    void a(Renderer var1) {
        this.a = var1;
        if (Thread.currentThread().getName().equals(this.l)) {
            this.a.onSurfaceCreated(this.k, this.h);
            this.a.onSurfaceChanged(this.k, this.b, this.c);
        }
    }

    Bitmap a() {
        if (this.a == null) {
            return null;
        } else if (!Thread.currentThread().getName().equals(this.l)) {
            return null;
        } else {
            this.a.onDrawFrame(this.k);
            this.d();
            return this.d;
        }
    }

    void b() {
        this.e.eglMakeCurrent(this.f, EGL10.EGL_NO_SURFACE, EGL10.EGL_NO_SURFACE, EGL10.EGL_NO_CONTEXT);
        this.e.eglDestroySurface(this.f, this.j);
        this.e.eglDestroyContext(this.f, this.i);
        this.e.eglTerminate(this.f);
    }

    private EGLConfig c() {
        int[] var1 = new int[]{12325, 0, 12326, 0, 12324, 8, 12323, 8, 12322, 8, 12321, 8, 12352, 4, 12344};
        int[] var2 = new int[1];
        this.e.eglChooseConfig(this.f, var1, (EGLConfig[])null, 0, var2);
        int var3 = var2[0];
        this.g = new EGLConfig[var3];
        this.e.eglChooseConfig(this.f, var1, this.g, var3, var2);
        return this.g[0];
    }

    private void d() {
        int[] var1 = new int[this.b * this.c];
        IntBuffer var2 = IntBuffer.allocate(this.b * this.c);
        this.k.glReadPixels(0, 0, this.b, this.c, 6408, 5121, var2);
        int[] var3 = var2.array();

        for(int var4 = 0; var4 < this.c; ++var4) {
            System.arraycopy(var3, var4 * this.b, var1, (this.c - var4 - 1) * this.b, this.b);
        }

        this.d = Bitmap.createBitmap(this.b, this.c, Config.ARGB_8888);
        this.d.copyPixelsFromBuffer(IntBuffer.wrap(var1));
    }
}
