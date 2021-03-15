package kr.co.ainus.petife2.video;

import android.content.Context;
import kr.co.ainus.petife2.video.Enums.LogLevel;

public class Module {
    private Player a;
    private Controller b;
    private Context c;
    private String d = "admin";
    private String e = "admin";
    private String f = "192.168.100.1";
    private int g = 554;
    private int h = 80;

    public Module(Context var1) {
        this.c = var1;
        this.a = new Player(var1);
        this.a.a(this.g);
        this.a.b(this.d);
        this.a.c(this.e);
        this.a.a(this.f);
        this.a.a(this.g);
        this.b = new Controller();
        this.b.a(this.h);
        this.b.c(this.d);
        this.b.b(this.e);
        this.b.a(this.f);
        this.b.a(this.h);
    }

    public void setUsername(String var1) {
        this.d = var1;
        this.b.c(var1);
        this.a.b(var1);
    }

    public String getUsername() {
        return this.d;
    }

    public void setPassword(String var1) {
        this.e = var1;
        this.b.b(var1);
        this.a.c(var1);
    }

    public String getPassword() {
        return this.e;
    }

    public void setModuleIp(String var1) {
        this.f = var1;
        this.b.a(var1);
        this.a.a(var1);
    }

    public String getModuleIp() {
        return this.f;
    }

    public void setPlayerPort(int var1) {
        this.g = var1;
        this.a.a(var1);
    }

    public int getPlayerPort() {
        return this.g;
    }

    public void setControllerPort(int var1) {
        this.h = var1;
        this.b.a(var1);
    }

    public int getControllerPort() {
        return this.h;
    }

    public void setContext(Context var1) {
        this.c = var1;
    }

    public Player getPlayer() {
        return this.a;
    }

    public Controller getController() {
        return this.b;
    }

    public void setLogLevel(LogLevel var1) {
        this.a.a(var1);
    }
}

