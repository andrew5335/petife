package kr.co.ainus.petife2.video;

import android.content.Context;
import android.net.DhcpInfo;
import android.net.wifi.WifiManager;
import android.net.wifi.WifiManager.MulticastLock;
import android.os.AsyncTask;
import android.os.Build.VERSION;
import android.util.Log;
import java.io.IOException;
import java.math.BigInteger;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.util.HashMap;
import java.util.Map;

public class Scanner {
    private Context a;
    private Scanner.OnScanOverListener b;
    private boolean c;
    private boolean d = false;
    private WifiManager e;
    private MulticastLock f;
    private byte[] g = new byte[]{0, 0, 0, 1, 0, 0, 0, 0, 0, 0, 0, 0, 42, 0};
    private byte h = 0;
    private int i = 18;
    private Map<InetAddress, String> j = new HashMap();
    private DatagramSocket k = null;

    public Scanner(Context var1) {
        this.a = var1;
        this.e = (WifiManager)this.a.getSystemService(Context.WIFI_SERVICE);
    }

    private boolean a(boolean var1) {
        if (this.d) {
            return false;
        } else {
            this.d = true;
            this.c = var1;
            this.f = this.e.createMulticastLock("UDPwifi");
            this.f.acquire();
            Scanner.b var2 = new Scanner.b();
            Scanner.a var3 = new Scanner.a();
            if (VERSION.SDK_INT >= 11) {
                var2.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, new Void[0]);
                var3.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, new Void[0]);
            } else {
                var3.execute(new Void[0]);
                var2.execute(new Void[0]);
            }

            return true;
        }
    }

    private InetAddress a() throws UnknownHostException {
        DhcpInfo var1 = this.e.getDhcpInfo();
        int var2 = var1.ipAddress & var1.netmask | ~var1.netmask;
        byte[] var3 = new byte[4];

        for(int var4 = 0; var4 < 4; ++var4) {
            var3[var4] = (byte)(var2 >> var4 * 8 & 255);
        }

        return InetAddress.getByAddress(var3);
    }

    private InetAddress b() {
        DhcpInfo var1 = this.e.getDhcpInfo();
        byte[] var2 = BigInteger.valueOf((long)var1.serverAddress).toByteArray();
        InetAddress var3 = null;

        try {
            var3 = InetAddress.getByAddress(var2);
        } catch (UnknownHostException var5) {
        }

        return var3;
    }

    private int a(byte[] var1, byte var2, int var3) {
        if (var1 == null) {
            return -1;
        } else {
            if (var3 < 0) {
                var3 = 0;
            }

            for(int var4 = var3; var4 < var1.length; ++var4) {
                if (var2 == var1[var4]) {
                    return var4;
                }
            }

            return -1;
        }
    }

    private byte[] a(byte[] var1, int var2, int var3) {
        int var4 = var3 - var2;
        byte[] var5 = new byte[var4];
        System.arraycopy(var1, var2, var5, 0, var4);
        return var5;
    }

    public boolean scanAll() {
        return this.a(false);
    }

    public boolean scan() {
        return this.a(true);
    }

    public void setOnScanOverListener(Scanner.OnScanOverListener var1) {
        this.b = var1;
    }

    public interface OnScanOverListener {
        void onResult(Map<InetAddress, String> var1, InetAddress var2);
    }

    private class a extends AsyncTask<Void, Void, Map<InetAddress, String>> {
        private a() {
        }

        @Override
        protected Map<InetAddress, String> doInBackground(Void... voids) {
            return null;
        }

        protected Map<InetAddress, String> a(Void... var1) {
            Scanner.this.j.clear();

            while(Scanner.this.d) {
                try {
                    if (Scanner.this.k != null) {
                        try {
                            byte[] var2 = new byte[1024];
                            DatagramPacket var3 = new DatagramPacket(var2, var2.length);
                            Scanner.this.k.receive(var3);
                            InetAddress var4 = InetAddress.getByName(var3.getAddress().getHostAddress());
                            if (Scanner.this.j.containsKey(var4)) {
                                Log.e("address==>", var4.toString());
                                continue;
                            }

                            Log.e("==>", var3.getLength() + "");
                            if (var3.getLength() > Scanner.this.i) {
                                int var5 = Scanner.this.a(var2, Scanner.this.h, Scanner.this.i);
                                String var6 = new String(Scanner.this.a(var2, Scanner.this.i + 1, var5));
                                Scanner.this.j.put(var4, var6);
                                Log.e("deviceId==>", var6);
                                if (Scanner.this.c) {
                                    Scanner.this.d = false;
                                    Map var7 = Scanner.this.j;
                                    return var7;
                                }
                            }
                        } catch (SocketException var15) {
                            Log.e("e==>", var15.toString());
                            return Scanner.this.j;
                        }
                    }

                    try {
                        Thread.sleep(1L);
                    } catch (InterruptedException var14) {
                        var14.printStackTrace();
                    }
                } catch (SocketException var16) {
                    Log.e("e1==>", var16.toString());
                } catch (IOException var17) {
                    Log.e("e2==>", var17.toString());
                } finally {
                    Log.e("e==>", "disconnect");
                    if (Scanner.this.k != null) {
                        Scanner.this.k.disconnect();
                    }

                }
            }

            return Scanner.this.j;
        }

        protected void a(Map<InetAddress, String> var1) {
            super.onPostExecute(var1);
            Scanner.this.d = false;
        }
    }

    private class b extends AsyncTask<Void, Void, Map<InetAddress, String>> {
        private b() {
        }

        @Override
        protected Map<InetAddress, String> doInBackground(Void... voids) {
            return null;
        }

        protected Map<InetAddress, String> a(Void... var1) {
            try {
                if (Scanner.this.k != null) {
                    Scanner.this.k.disconnect();
                    Scanner.this.k = null;
                }

                Scanner.this.k = new DatagramSocket();
                Scanner.this.k.setSoTimeout(1000);
                Scanner.this.k.setBroadcast(true);
                DatagramPacket var2 = new DatagramPacket(Scanner.this.g, Scanner.this.g.length, Scanner.this.a(), 5570);

                for(int var3 = 0; var3 < 7 && Scanner.this.d; ++var3) {
                    Scanner.this.k.send(var2);

                    try {
                        Thread.sleep(200L);
                    } catch (InterruptedException var10) {
                        var10.printStackTrace();
                    }
                }
            } catch (SocketException var11) {
                Log.e("s1==>", var11.toString());
            } catch (IOException var12) {
                Log.e("s2==>", var12.toString());
            } finally {
                Log.e("s==>", "disconnect");
                if (Scanner.this.k != null) {
                    Scanner.this.k.disconnect();
                }

            }

            return Scanner.this.j;
        }

        protected void a(Map<InetAddress, String> var1) {
            super.onPostExecute(var1);
            Scanner.this.d = false;
            Scanner.this.f.release();
            if (Scanner.this.b != null) {
                Scanner.this.b.onResult(var1, Scanner.this.b());
            }

            System.gc();
        }
    }
}
