package kr.co.ainus.petife2.video;

import android.opengl.GLES20;
import android.opengl.GLSurfaceView.Renderer;
import java.nio.ByteBuffer;
import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

class d implements Renderer {
    private c a = new c();
    private int b;
    private int c;
    private int d;
    private int e;
    private ByteBuffer f;
    private ByteBuffer g;
    private ByteBuffer h;
    private int i = -1;
    private int j = -1;
    private int k = -1;
    private int l = -1;

    d() {
    }

    public void onSurfaceCreated(GL10 var1, EGLConfig var2) {
        this.a.a();
    }

    public void onSurfaceChanged(GL10 var1, int var2, int var3) {
        this.b = var2;
        this.c = var3;
        GLES20.glViewport(0, 0, var2, var3);
    }

    public void onDrawFrame(GL10 var1) {
        synchronized(this) {
            if (this.f != null && this.g != null && this.h != null) {
                this.f.position(0);
                this.g.position(0);
                this.h.position(0);
                this.a.a(this.f, this.g, this.h, this.d, this.e);
                GLES20.glClearColor(0.0F, 0.0F, 0.0F, 0.0F);
                GLES20.glClear(16384);
                this.a.b();
            }

        }
    }

    void a(int var1, int var2, byte[] var3, byte[] var4, byte[] var5, boolean var6) {
        if (this.c != 0 && this.b != 0 && var1 != 0 && var2 != 0) {
            if (var1 != this.i || var2 != this.j || this.b != this.k || this.c != this.l) {
                this.d = var1;
                this.e = var2;
                float var7 = (float)this.c / (float)this.b;
                float var8 = (float)var2 / (float)var1;
                if (var7 != var8 && !var6) {
                    float var9;
                    if (var7 < var8) {
                        var9 = var7 / var8;
                        this.a.a(new float[]{-var9, -1.0F, var9, -1.0F, -var9, 1.0F, var9, 1.0F});
                    } else {
                        var9 = var8 / var7;
                        this.a.a(new float[]{-1.0F, -var9, 1.0F, -var9, -1.0F, var9, 1.0F, var9});
                    }
                } else {
                    this.a.a(kr.co.ainus.petife2.video.c.a);
                }

                this.i = var1;
                this.j = var2;
                this.l = this.c;
                this.k = this.b;
                this.f = ByteBuffer.allocate(var3.length);
                this.g = ByteBuffer.allocate(var4.length);
                this.h = ByteBuffer.allocate(var5.length);
            }

            synchronized(this) {
                this.f.put(var3);
                this.f.position(0);
                this.g.put(var4);
                this.g.position(0);
                this.h.put(var5);
                this.h.position(0);
            }
        }
    }
}
