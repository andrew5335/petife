package kr.co.ainus.petife2.video;

import android.content.Context;
import android.graphics.SurfaceTexture;
import android.opengl.GLUtils;
import android.opengl.GLSurfaceView.Renderer;
import android.util.AttributeSet;
import android.util.Log;
import android.view.TextureView;
import android.view.TextureView.SurfaceTextureListener;
import javax.microedition.khronos.egl.EGL10;
import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.egl.EGLContext;
import javax.microedition.khronos.egl.EGLDisplay;
import javax.microedition.khronos.egl.EGLSurface;
import javax.microedition.khronos.opengles.GL10;

public class GLTextureView extends TextureView implements SurfaceTextureListener {
    public d mRenderer;
    private SurfaceTexture a;
    private EGLDisplay b;
    private EGLSurface c;
    private EGLContext d;
    private EGL10 e;
    private EGLConfig f;
    private GL10 g;
    private int h;
    private int i;
    private int j;
    public boolean isRunning = false;
    private boolean k = true;
    private boolean l = false;
    private GLTextureView.a m;
    private int n;

    public GLTextureView(Context var1) {
        super(var1);
        this.a(var1);
    }

    public GLTextureView(Context var1, AttributeSet var2) {
        super(var1, var2);
        this.a(var1);
    }

    public GLTextureView(Context var1, AttributeSet var2, int var3) {
        super(var1, var2, var3);
        this.a(var1);
    }

    public synchronized void setRenderer(Renderer var1) {
        Log.d("RenderThread", "setRenderer and joining GLTextureView");
        this.mRenderer = new d();
        this.l = true;
    }

    private void a(Context var1) {
        this.n = 10;
        Log.d("RenderThread", "initialize and joining GLTextureView");
        this.setSurfaceTextureListener(this);
    }

    public void onSurfaceTextureAvailable(SurfaceTexture var1, int var2, int var3) {
        Log.d("RenderThread", "Available and joining GLTextureView");
        this.setRenderer(this.mRenderer);
        this.startThread(var1, var2, var3, (float)this.n);
    }

    public void startThread(SurfaceTexture var1, int var2, int var3, float var4) {
        this.m = new GLTextureView.a();
        this.a = var1;
        this.setDimensions(var2, var3);
        this.h = (int)(1.0F / var4 * 1000.0F);
        this.m.start();
    }

    public void onSurfaceTextureSizeChanged(SurfaceTexture var1, int var2, int var3) {
        this.setDimensions(var2, var3);
        if (this.mRenderer != null) {
            this.l = true;
            this.mRenderer.onSurfaceChanged(this.g, var2, var3);
        }

    }

    public synchronized void setPaused(boolean var1) {
        Log.d("RenderThread", String.format("Setting GLTextureView paused to %s", var1));
        this.k = var1;
    }

    public synchronized boolean isPaused() {
        return this.k;
    }

    public boolean onSurfaceTextureDestroyed(SurfaceTexture var1) {
        this.stopThread();
        return false;
    }

    public void stopThread() {
        if (this.m != null) {
            Log.d("RenderThread", "Stopping and joining GLTextureView");
            this.isRunning = false;

            try {
                this.m.join();
            } catch (InterruptedException var2) {
                var2.printStackTrace();
            }

            this.m = null;
        }

    }

    private boolean a() {
        return this.isPaused() || this.mRenderer == null;
    }

    private synchronized void a(d var1) {
        if (var1 != null && this.isRunning) {
            var1.onSurfaceCreated(this.g, this.f);
            var1.onSurfaceChanged(this.g, this.j, this.i);
        }

    }

    private synchronized void b() {
        this.c();
        if (this.mRenderer != null) {
            this.mRenderer.onDrawFrame(this.g);
        }

        this.e();
        if (!this.e.eglSwapBuffers(this.b, this.c)) {
            Log.e("RenderThread", "cannot swap buffers!");
        }

    }

    public void setDimensions(int var1, int var2) {
        this.j = var1;
        this.i = var2;
    }

    private void c() {
        if (!this.d.equals(this.e.eglGetCurrentContext()) || !this.c.equals(this.e.eglGetCurrentSurface(12377))) {
            this.d();
            if (!this.e.eglMakeCurrent(this.b, this.c, this.c, this.d)) {
                throw new RuntimeException("eglMakeCurrent failed " + GLUtils.getEGLErrorString(this.e.eglGetError()));
            }

            this.d();
        }

    }

    private void d() {
        int var1 = this.e.eglGetError();
        if (var1 != 12288) {
            Log.e("RenderThread", "EGL error = 0x" + Integer.toHexString(var1));
        }

    }

    private void e() {
        int var1 = this.g.glGetError();
        if (var1 != 0) {
            Log.e("RenderThread", "GL error = 0x" + Integer.toHexString(var1));
        }

    }

    private void f() {
        this.e = (EGL10)EGLContext.getEGL();
        this.b = this.e.eglGetDisplay(EGL10.EGL_DEFAULT_DISPLAY);
        if (this.b == EGL10.EGL_NO_DISPLAY) {
            throw new RuntimeException("eglGetDisplay failed " + GLUtils.getEGLErrorString(this.e.eglGetError()));
        } else {
            int[] var1 = new int[2];
            if (!this.e.eglInitialize(this.b, var1)) {
                throw new RuntimeException("eglInitialize failed " + GLUtils.getEGLErrorString(this.e.eglGetError()));
            } else {
                int[] var2 = new int[1];
                EGLConfig[] var3 = new EGLConfig[1];
                int[] var4 = new int[]{12352, 4, 12324, 8, 12323, 8, 12322, 8, 12321, 8, 12325, 0, 12326, 0, 12344};
                this.f = null;
                if (!this.e.eglChooseConfig(this.b, var4, var3, 1, var2)) {
                    throw new IllegalArgumentException("eglChooseConfig failed " + GLUtils.getEGLErrorString(this.e.eglGetError()));
                } else {
                    if (var2[0] > 0) {
                        this.f = var3[0];
                    }

                    if (this.f == null) {
                        throw new RuntimeException("eglConfig not initialized");
                    } else {
                        int[] var5 = new int[]{12440, 2, 12344};
                        this.d = this.e.eglCreateContext(this.b, this.f, EGL10.EGL_NO_CONTEXT, var5);
                        this.d();
                        this.c = this.e.eglCreateWindowSurface(this.b, this.f, this.a, (int[])null);
                        this.d();
                        if (this.c != null && this.c != EGL10.EGL_NO_SURFACE) {
                            if (!this.e.eglMakeCurrent(this.b, this.c, this.c, this.d)) {
                                throw new RuntimeException("eglMakeCurrent failed " + GLUtils.getEGLErrorString(this.e.eglGetError()));
                            } else {
                                this.d();
                                this.g = (GL10)this.d.getGL();
                                this.d();
                            }
                        } else {
                            int var6 = this.e.eglGetError();
                            if (var6 == 12299) {
                                Log.e("RenderThread", "eglCreateWindowSurface returned EGL10.EGL_BAD_NATIVE_WINDOW");
                            } else {
                                throw new RuntimeException("eglCreateWindowSurface failed " + GLUtils.getEGLErrorString(var6));
                            }
                        }
                    }
                }
            }
        }
    }

    public void onSurfaceTextureUpdated(SurfaceTexture var1) {
    }

    private class a extends Thread {
        private a() {
        }

        public void run() {
            GLTextureView.this.isRunning = true;
            GLTextureView.this.f();
            GLTextureView.this.e();
            long var1 = System.currentTimeMillis();

            while(GLTextureView.this.isRunning) {
                while(GLTextureView.this.mRenderer == null) {
                    try {
                        Thread.sleep(100L);
                    } catch (InterruptedException var4) {
                    }
                }

                if (GLTextureView.this.l) {
                    Log.e("rendererChanged==>", "" + GLTextureView.this.l);
                    GLTextureView.this.a(GLTextureView.this.mRenderer);
                    GLTextureView.this.l = false;
                }

                if (!GLTextureView.this.a()) {
                    var1 = System.currentTimeMillis();
                    GLTextureView.this.b();
                }

                try {
                    if (GLTextureView.this.a()) {
                        Thread.sleep(100L);
                    } else {
                        Thread.sleep(5L);
                    }
                } catch (InterruptedException var5) {
                }
            }

        }
    }
}
