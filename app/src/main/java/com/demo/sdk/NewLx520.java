package com.demo.sdk;

//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

import android.content.Context;
import android.net.DhcpInfo;
import android.net.wifi.WifiManager;
import android.os.AsyncTask;
import android.util.Base64;
import android.util.Log;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.math.BigInteger;
import java.net.HttpURLConnection;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.InterfaceAddress;
import java.net.NetworkInterface;
import java.net.Proxy;
import java.net.SocketException;
import java.net.URL;
import java.net.URLEncoder;
import java.util.Enumeration;
import com.demo.sdk.Scanner;

public class NewLx520 {
    private String a;
    private String b = "admin";
    private String c = "admin";
    private NewLx520.OnResultListener d;
    public static final int UPDATE_PASSWORD = 1;
    public static final int JOIN_WIFI = 2;
    public static final int CHECK_PASSWORD = 3;
    public static final int CHANGE_VIDEO_RESOLUTION = 4;
    public static final int START_SD_RECORD = 5;
    public static final int STOP_SD_RECORD = 6;

    private WifiManager wifiManager;

    public NewLx520(Context context, String var1, String var2) {
        this.a = var1;
        if (!var2.matches("")) {
            this.c = var2;
        }

        wifiManager = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);
    }

    private String a(String var1) {
        String var2 = var1;

        try {
            var2 = URLEncoder.encode(var2, "UTF-8");
        } catch (UnsupportedEncodingException var4) {
            ;
        }

        return var2;
    }

    private void a(String var1, int var2) {
        NewLx520.a var3 = new NewLx520.a();
        var3.execute(new String[]{var1, Integer.toString(var2)});
    }

    public void updatePassword(String var1) {
        var1 = this.a(var1);
        this.a("http://" + this.a + "/param.cgi?action=update&group=login&username=" + this.b + "&password=" + var1, 1);
    }

    public void joinWifi(String var1, String var2) {
        var1 = this.a(var1);
        var2 = this.a(var2);
        this.a("http://" + this.a + "/param.cgi?action=update&group=wifi&sta_ssid=" + var1 + "&sta_auth_key=" + var2, 2);
    }

    public void checkPassword() {
        this.a("http://" + this.a + "/param.cgi?action=remove&group=userlogin&username=abcde_adsfadf_user100", 3);
    }

    public void changeVideoResolution(int var1, int var2) {
        this.a("http://" + this.a + "/server.command?command=set_resol&type=h264&pipe=" + Integer.toString(var1) + "&value=" + Integer.toString(var2), 4);
    }

    public void startSdReaord(int var1) {
        this.a("http://" + this.a + "/server.command?command=start_record_pipe&type=h264&pipe=" + Integer.toString(var1), 5);
    }

    public void stopSdReaord(int var1) {
        this.a("http://" + this.a + "/server.command?command=stop_record&type=h264&pipe=" + Integer.toString(var1), 6);
    }

    public void isRecord(int var1) {
        this.a("http://" + this.a + "/server.command?command=is_pipe_record&type=h264&pipe=" + Integer.toString(var1), 7);
    }

    public void Get_Online() {
        this.a("http://" + this.a + "/server.command?command=get_online", 8);
    }

    public void is_Notification() {
        this.a("http://" + this.a + "/SkyEye/ctrlAlarmInConfig.ncgi?doAction=read&ActItem=SCHEDULE", 9);
    }

    public void Set_Notification(int var1) {
        this.a("http://" + this.a + "/SkyEye/ctrlAlarmInConfig.ncgi?doAction=write&btnType=Apply&ActItem=SCHEDULE&SCH0_Enable=" + var1, 10);
    }

    public void Reload() {
        this.a("http://" + this.a + "/server.command?command=reload_config", 11);
    }

    public void Notification_To(String var1, String var2) {
        this.a("http://" + this.a + "/param.cgi?action=update&group=xgpush&xg_devname=" + var1 + "&xg_account=" + var2, 12);
    }

    public void Get_Password() {
        this.a("http://" + this.a + "/param.cgi?action=list&group=login", 13);
    }

    public void Set_Password(String var1) {
        this.a("http://" + this.a + "/param.cgi?action=update&group=login&username=admin&password=" + var1, 14);
    }

    public void Set_SDRecord_Time(String var1, String var2, String var3, String var4, String var5) {
        this.a("http://" + this.a + "/SkyEye/ctrlServerConfig.ncgi?doAction=write&Date=" + var1 + "&Hour=" + var2 + "&Minute=" + var3 + "&Second=" + var4 + "&SetNow=0&TimeZone=GMT" + var5, 15);
    }

    public void Get_Ssid_List() {
        this.a("http://" + this.a + "/server.command?command=get_wifilist", 16);
    }

    public void Get_Fps() {
        this.a("http://" + this.a + "/server.command?command=get_max_fps&type=h264&pipe=0", 17);
    }

    public void get_Signal() {
        this.a("http://" + this.a + "/server.command?command=get_signal_level", 18);
    }

    public void Get_Video_Folder_List() {
        this.a("http://" + this.a + "/param.cgi?action=list&group=videodir&fmt=link&pipe=0&type=0", 19);
    }

    public void Get_Video_List(String var1) {
        this.a("http://" + this.a + "/param.cgi?action=list&group=file&fmt=link&pipe=0&type=0&folder=" + var1, 20);
    }

    public void Get_Version() {
        this.a("http://" + this.a + "/server.command?command=get_version", 21);
    }

    public void Set_Resolution(int var1) {
        this.a("http://" + this.a + "/server.command?command=set_resol&type=h264&pipe=0&value=" + var1, 22);
    }

    public void Get_Resolution() {
        this.a("http://" + this.a + "/server.command?command=get_resol&type=h264&pipe=0", 23);
    }

    public void Set_Fps(int var1) {
        this.a("http://" + this.a + "/server.command?command=set_max_fps&type=h264&pipe=0&value=" + var1, 24);
    }

    public void Set_Quality(int var1) {
        this.a("http://" + this.a + "/server.command?command=set_enc_quality&type=h264&pipe=0&value=" + var1, 26);
    }

    public void Get_Quality() {
        this.a("http://" + this.a + "/server.command?command=get_enc_quality&type=h264&pipe=0", 27);
    }

    public void Set_GOP(int var1) {
        this.a("http://" + this.a + "/server.command?command=set_enc_gop&type=h264&pipe=1&value=" + var1, 28);
    }

    public void Get_GOP() {
        this.a("http://" + this.a + "/server.command?command=get_enc_gop&type=h264&pipe=0", 29);
    }

    public void setPassword(String var1) {
        this.c = var1;
    }

    public void setOnResultListener(NewLx520.OnResultListener var1) {
        this.d = var1;
    }

    public interface OnResultListener {
        void onResult(NewLx520.Response var1);
    }

    class a extends AsyncTask<String, Void, NewLx520.Response> {
        a() {
        }

        protected void onPreExecute() {
            super.onPreExecute();
        }

        protected NewLx520.Response doInBackground(String... var1) {
            String var2 = var1[0];
            int var3 = Integer.parseInt(var1[1]);
            NewLx520.Response var4 = NewLx520.this.new Response();
            var4.type = var3;

            try {
                HttpURLConnection var6 = (HttpURLConnection) (new URL(var2)).openConnection();

                String var7 = "GET";
                String var8 = "";
                byte[] var9 = var8.getBytes();
                String var10 = Base64.encodeToString((NewLx520.this.b + ":" + NewLx520.this.c).getBytes(), 2);
                var6.setDoInput(true);
                var6.setRequestProperty("Accept", "*/*");
                var6.setRequestProperty("connection", "close");
                var6.setRequestProperty("Authorization", "Basic " + var10);
                if (var7.equals("POST")) {
                    var6.setDoOutput(true);
                    var6.setRequestProperty("Content-Length", String.valueOf(var9.length));
                } else {
                    var6.setDoOutput(false);
                }

                var6.setRequestMethod(var7);
                var6.setConnectTimeout(5000);
                var6.setReadTimeout(10000);


                var6.connect();
                if (var7.equals("POST")) {
                    OutputStream var11 = var6.getOutputStream();
                    var11.write(var9);
                    var11.flush();
                    var11.close();
                }

                InputStream var5 = var6.getInputStream();
                BufferedReader var14 = new BufferedReader(new InputStreamReader(var5));
                String var12 = null;

                for (var4.statusCode = var6.getResponseCode(); (var12 = var14.readLine()) != null; var4.body = var4.body + var12) {
                    ;
                }

                var4.statusCode = 200;
                Log.i("body", var4.body);
            } catch (Exception var13) {
                Log.e("error", var13.toString());
            }

            return var4;
        }

        protected void a(NewLx520.Response var1) {
            NewLx520.this.d.onResult(var1);
        }
    }

    public class Response {
        public String body = "";
        public int statusCode = 0;
        public int type;

        public Response() {
        }
    }

    public static InetAddress getInetAddress() {
        InetAddress inetAdress = null;
        try {

            Scanner scanner;
            Enumeration<NetworkInterface> networkInterface = NetworkInterface
                    .getNetworkInterfaces();

            while (inetAdress == null
                    && networkInterface.hasMoreElements()) {
                NetworkInterface singleInterface = networkInterface
                        .nextElement();
                String interfaceName = singleInterface.getName();
                if (interfaceName.contains("wlan0")
                        || interfaceName.contains("eth0")) {
                    for (InterfaceAddress infaceAddress : singleInterface
                            .getInterfaceAddresses()) {
                        inetAdress = infaceAddress.getAddress();
                        if (inetAdress != null) {
                            break;
                        }
                    }
                }
            }

        } catch (SocketException e) {
            e.printStackTrace();
        }

        return inetAdress;
    }
}
