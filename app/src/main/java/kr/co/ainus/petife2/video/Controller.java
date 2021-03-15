package kr.co.ainus.petife2.video;

import android.os.AsyncTask;
import android.util.Base64;
import android.util.Log;
import kr.co.ainus.petife2.video.Enums.Action;
import kr.co.ainus.petife2.video.Enums.Pipe;
import kr.co.ainus.petife2.video.Enums.Resolution;
import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import org.json.JSONException;
import org.json.JSONObject;

public class Controller {
    private String a = "192.168.100.1";
    private String b = "admin";
    private String c = "admin";
    private Controller.OnResultListener d;
    private int e;

    Controller() {
    }

    void a(String var1) {
        this.a = var1;
    }

    void b(String var1) {
        this.c = var1.matches("") ? "admin" : var1;
    }

    void a(int var1) {
        this.e = var1;
    }

    void c(String var1) {
        this.b = var1.matches("") ? "admin" : var1;
    }

    public void updatePassword(String var1) {
        var1 = this.d(var1);
        this.a("http://" + this.a + ":" + this.e + "/param.cgi?action=update&group=login&username=" + this.b + "&password=" + var1, Action.UPDATE_PASSWORD);
    }

    public void joinWifi(String var1, String var2) {
        var1 = this.d(var1);
        var2 = this.d(var2);
        this.a("http://" + this.a + ":" + this.e + "/param.cgi?action=update&group=wifi&dhcp=0&network_type=1&ssid=" + var1 + "&auth_key=" + var2, Action.JOIN_WIFI);
    }

    public void checkPassword() {
        this.a("http://" + this.a + ":" + this.e + "/param.cgi?action=remove&group=userlogin&username=abcde_adsfadf_user100", Action.CHECK_PASSWORD);
    }

    public void changeVideoResolution(Pipe var1, Resolution var2) {
        this.a("http://" + this.a + ":" + this.e + "/server.command?command=set_resol&type=h264&pipe=" + var1.ordinal() + "&value=" + var2.ordinal(), Action.CHANGE_VIDEO_RESOLUTION);
    }

    public void setOnResultListener(Controller.OnResultListener var1) {
        this.d = var1;
    }

    private String d(String var1) {
        String var2 = var1;

        try {
            var2 = URLEncoder.encode(var2, "UTF-8");
        } catch (UnsupportedEncodingException var4) {
        }

        return var2;
    }

    private void a(String var1, Action var2) {
        Controller.a var3 = new Controller.a();
        var3.execute(new String[]{var1, Integer.toString(var2.ordinal())});
    }

    public interface OnResultListener {
        void onResult(Action var1, boolean var2);
    }

    class a extends AsyncTask<String, Void, Controller.b> {
        a() {
        }

        @Override
        protected Controller.b doInBackground(String... strings) {
            return null;
        }

        protected Controller.b a(String... var1) {
            String var2 = var1[0];
            Controller.b var3 = Controller.this.new b();
            var3.c = Action.values()[Integer.parseInt(var1[1])];

            try {
                HttpURLConnection var5 = (HttpURLConnection)(new URL(var2)).openConnection();
                String var6 = "GET";
                String var7 = "";
                byte[] var8 = var7.getBytes();
                String var9 = Base64.encodeToString((Controller.this.b + ":" + Controller.this.c).getBytes(), 2);
                var5.setDoInput(true);
                var5.setRequestProperty("Accept", "*/*");
                var5.setRequestProperty("connection", "close");
                var5.setRequestProperty("Authorization", "Basic " + var9);
                if (var6.equals("POST")) {
                    var5.setDoOutput(true);
                    var5.setRequestProperty("Content-Length", String.valueOf(var8.length));
                } else {
                    var5.setDoOutput(false);
                }

                var5.setRequestMethod(var6);
                var5.setConnectTimeout(5000);
                var5.setReadTimeout(10000);
                var5.connect();
                if (var6.equals("POST")) {
                    OutputStream var10 = var5.getOutputStream();
                    var10.write(var8);
                    var10.flush();
                    var10.close();
                }

                InputStream var4 = var5.getInputStream();
                BufferedReader var13 = new BufferedReader(new InputStreamReader(var4));
                String var11 = null;

                for(var3.b = var5.getResponseCode(); (var11 = var13.readLine()) != null; var3.a = var3.a + var11) {
                }

                var3.b = 200;
                Log.d("controller", "{ request url: " + var2 + ", code: " + var3.b + " }");
            } catch (Exception var12) {
                Log.e("controller", "{ request url: " + var2 + ", error: " + var12.toString() + " }");
            }

            return var3;
        }

        protected void a(Controller.b var1) {
            if (Controller.this.d != null) {
                boolean var2 = var1.b == 200;
                switch(var1.c) {
                    case JOIN_WIFI:
                        try {
                            JSONObject var3 = new JSONObject(var1.a.trim());
                            var2 = var3.getInt("value") == 0;
                        } catch (JSONException var4) {
                            var2 = false;
                        }
                    default:
                        Controller.this.d.onResult(var1.c, var2);
                }
            }

        }
    }

    class b {
        public String a = "";
        public int b = 0;
        public Action c;

        b() {
        }
    }
}
