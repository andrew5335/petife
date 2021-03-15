package kr.co.ainus.petife2.video;

import android.opengl.GLES20;
import java.nio.Buffer;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;

class c {
    static float[] a = new float[]{-1.0F, -1.0F, 1.0F, -1.0F, -1.0F, 1.0F, 1.0F, 1.0F};
    static float[] b = new float[]{-1.0F, 0.0F, 0.0F, 0.0F, -1.0F, 1.0F, 0.0F, 1.0F};
    static float[] c = new float[]{0.0F, -1.0F, 1.0F, -1.0F, 0.0F, 0.0F, 1.0F, 0.0F};
    static float[] d = new float[]{-1.0F, -1.0F, 0.0F, -1.0F, -1.0F, 0.0F, 0.0F, 0.0F};
    static float[] e = new float[]{0.0F, 0.0F, 1.0F, 0.0F, 0.0F, 1.0F, 1.0F, 1.0F};
    static float[] f = new float[]{0.0F, 1.0F, 1.0F, 1.0F, 0.0F, 0.0F, 1.0F, 0.0F};
    private int g;
    private int h;
    private int i;
    private int j;
    private int k;
    private int l;
    private int m;
    private float[] n;
    private int o = -1;
    private int p = -1;
    private int q = -1;
    private int r = -1;
    private int s = -1;
    private int t = -1;
    private int u = -1;
    private int v = -1;
    private ByteBuffer w;
    private ByteBuffer x;
    private int y = -1;
    private int z = -1;

    c() {
        this.a(a);
    }

    private void a(c.a var1) {
        switch(var1) {
            case b:
                this.n = b;
                this.h = 33984;
                this.i = 33985;
                this.j = 33986;
                this.k = 0;
                this.l = 1;
                this.m = 2;
                break;
            case c:
                this.n = c;
                this.h = 33987;
                this.i = 33988;
                this.j = 33989;
                this.k = 3;
                this.l = 4;
                this.m = 5;
                break;
            case d:
                this.n = d;
                this.h = 33990;
                this.i = 33991;
                this.j = 33992;
                this.k = 6;
                this.l = 7;
                this.m = 8;
                break;
            case e:
                this.n = e;
                this.h = 33993;
                this.i = 33994;
                this.j = 33995;
                this.k = 9;
                this.l = 10;
                this.m = 11;
                break;
            case a:
            default:
                this.n = a;
                this.h = 33984;
                this.i = 33985;
                this.j = 33986;
                this.k = 0;
                this.l = 1;
                this.m = 2;
        }

    }

    private int a(int var1, String var2) {
        int var3 = GLES20.glCreateShader(var1);
        if (var3 != 0) {
            GLES20.glShaderSource(var3, var2);
            GLES20.glCompileShader(var3);
            int[] var4 = new int[1];
            GLES20.glGetShaderiv(var3, 35713, var4, 0);
            if (var4[0] == 0) {
                GLES20.glDeleteShader(var3);
                var3 = 0;
            }
        }

        return var3;
    }

    private void a(String var1) {
        while(GLES20.glGetError() != 0) {
        }

        int var2;
        if ((var2 = GLES20.glGetError()) != 0) {
            throw new RuntimeException(var1 + ": glError " + var2);
        }
    }

    void a() {
        if (this.g <= 0) {
            this.g = this.a("attribute vec4 vPosition;\nattribute vec2 a_texCoord;\nvarying vec2 tc;\nvoid main() {\n    gl_Position = vPosition;\n    tc = a_texCoord;\n}", "precision mediump float;\nuniform sampler2D tex_y;\nuniform sampler2D tex_u;\nuniform sampler2D tex_v;\nvarying vec2 tc;\nvoid main() {\n    vec4 c = vec4((texture2D(tex_y, tc).r - 16./255.) * 1.164);\n    vec4 U = vec4(texture2D(tex_u, tc).r - 128./255.);\n    vec4 V = vec4(texture2D(tex_v, tc).r - 128./255.);\n    c += V * vec4(1.596, -0.813, 0, 0);\n    c += U * vec4(0, -0.392, 2.017, 0);\n    c.a = 1.0;\n    gl_FragColor = c;\n}");
        }

        this.o = GLES20.glGetAttribLocation(this.g, "vPosition");
        this.a("glGetAttribLocation vPosition");
        if (this.o == -1) {
            throw new RuntimeException("Could not get attribute location for vPosition");
        } else {
            this.p = GLES20.glGetAttribLocation(this.g, "a_texCoord");
            this.a("glGetAttribLocation a_texCoord");
            if (this.p == -1) {
                throw new RuntimeException("Could not get attribute location for a_texCoord");
            } else {
                this.q = GLES20.glGetUniformLocation(this.g, "tex_y");
                this.a("glGetUniformLocation tex_y");
                if (this.q == -1) {
                    throw new RuntimeException("Could not get uniform location for tex_y");
                } else {
                    this.r = GLES20.glGetUniformLocation(this.g, "tex_u");
                    this.a("glGetUniformLocation tex_u");
                    if (this.r == -1) {
                        throw new RuntimeException("Could not get uniform location for tex_u");
                    } else {
                        this.s = GLES20.glGetUniformLocation(this.g, "tex_v");
                        this.a("glGetUniformLocation tex_v");
                        if (this.s == -1) {
                            throw new RuntimeException("Could not get uniform location for tex_v");
                        }
                    }
                }
            }
        }
    }

    void a(Buffer var1, Buffer var2, Buffer var3, int var4, int var5) {
        boolean var6 = var4 != this.y || var5 != this.z;
        if (var6) {
            this.y = var4;
            this.z = var5;
        }

        int[] var7;
        if (this.t < 0 || var6) {
            if (this.t >= 0) {
                GLES20.glDeleteTextures(1, new int[]{this.t}, 0);
                this.a("glDeleteTextures");
            }

            GLES20.glPixelStorei(3317, 1);
            var7 = new int[1];
            GLES20.glGenTextures(1, var7, 0);
            this.a("glGenTextures");
            this.t = var7[0];
        }

        GLES20.glBindTexture(3553, this.t);
        this.a("glBindTexture");
        GLES20.glTexImage2D(3553, 0, 6409, this.y, this.z, 0, 6409, 5121, var1);
        this.a("glTexImage2D");
        GLES20.glTexParameterf(3553, 10241, 9728.0F);
        GLES20.glTexParameterf(3553, 10240, 9729.0F);
        GLES20.glTexParameteri(3553, 10242, 33071);
        GLES20.glTexParameteri(3553, 10243, 33071);
        if (this.u < 0 || var6) {
            if (this.u >= 0) {
                GLES20.glDeleteTextures(1, new int[]{this.u}, 0);
                this.a("glDeleteTextures");
            }

            var7 = new int[1];
            GLES20.glGenTextures(1, var7, 0);
            this.a("glGenTextures");
            this.u = var7[0];
        }

        GLES20.glBindTexture(3553, this.u);
        GLES20.glTexImage2D(3553, 0, 6409, this.y / 2, this.z / 2, 0, 6409, 5121, var2);
        GLES20.glTexParameterf(3553, 10241, 9728.0F);
        GLES20.glTexParameterf(3553, 10240, 9729.0F);
        GLES20.glTexParameteri(3553, 10242, 33071);
        GLES20.glTexParameteri(3553, 10243, 33071);
        if (this.v < 0 || var6) {
            if (this.v >= 0) {
                GLES20.glDeleteTextures(1, new int[]{this.v}, 0);
                this.a("glDeleteTextures");
            }

            var7 = new int[1];
            GLES20.glGenTextures(1, var7, 0);
            this.a("glGenTextures");
            this.v = var7[0];
        }

        GLES20.glBindTexture(3553, this.v);
        GLES20.glTexImage2D(3553, 0, 6409, this.y / 2, this.z / 2, 0, 6409, 5121, var3);
        GLES20.glTexParameterf(3553, 10241, 9728.0F);
        GLES20.glTexParameterf(3553, 10240, 9729.0F);
        GLES20.glTexParameteri(3553, 10242, 33071);
        GLES20.glTexParameteri(3553, 10243, 33071);
    }

    void b() {
        if (this.g <= 0) {
            this.a();
        }

        GLES20.glUseProgram(this.g);
        this.a("glUseProgram");
        GLES20.glVertexAttribPointer(this.o, 2, 5126, false, 8, this.w);
        this.a("glVertexAttribPointer mPositionHandle");
        GLES20.glEnableVertexAttribArray(this.o);
        GLES20.glVertexAttribPointer(this.p, 2, 5126, false, 8, this.x);
        this.a("glVertexAttribPointer maTextureHandle");
        GLES20.glEnableVertexAttribArray(this.p);
        GLES20.glActiveTexture(this.h);
        GLES20.glBindTexture(3553, this.t);
        GLES20.glUniform1i(this.q, this.k);
        GLES20.glActiveTexture(this.i);
        GLES20.glBindTexture(3553, this.u);
        GLES20.glUniform1i(this.r, this.l);
        GLES20.glActiveTexture(this.j);
        GLES20.glBindTexture(3553, this.v);
        GLES20.glUniform1i(this.s, this.m);
        GLES20.glDrawArrays(5, 0, 4);
        GLES20.glFinish();
        GLES20.glDisableVertexAttribArray(this.o);
        GLES20.glDisableVertexAttribArray(this.p);
    }

    int a(String var1, String var2) {
        int var3 = this.a(35633, var1);
        int var4 = this.a(35632, var2);
        int var5 = GLES20.glCreateProgram();
        if (var5 != 0) {
            GLES20.glAttachShader(var5, var3);
            this.a("glAttachShader");
            GLES20.glAttachShader(var5, var4);
            this.a("glAttachShader");
            GLES20.glLinkProgram(var5);
            int[] var6 = new int[1];
            GLES20.glGetProgramiv(var5, 35714, var6, 0);
            if (var6[0] != 1) {
                GLES20.glDeleteProgram(var5);
                var5 = 0;
            }
        }

        return var5;
    }

    void a(float[] var1) {
        this.w = ByteBuffer.allocateDirect(var1.length * 4);
        this.w.order(ByteOrder.nativeOrder());
        this.w.asFloatBuffer().put(var1);
        this.w.position(0);
        if (this.x == null) {
            this.x = ByteBuffer.allocateDirect(f.length * 4);
            this.x.order(ByteOrder.nativeOrder());
            this.x.asFloatBuffer().put(f);
            this.x.position(0);
        }

    }

    static enum a {
        a,
        b,
        c,
        d,
        e;

        private a() {
        }
    }
}
