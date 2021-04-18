//
// Source code recreated from context .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package com.demo.sdk;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.DhcpInfo;
import android.net.wifi.WifiManager;
import android.net.wifi.WifiManager.MulticastLock;
import android.os.AsyncTask;
import android.os.PowerManager;
import android.util.Log;

import java.io.IOException;
import java.math.BigInteger;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.InterfaceAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;

public class Scanner2 {
    private static final String TAG = "Scanner2";
    private static boolean isRunning = false;

    private Context context;
    private Scanner2.OnScanOverListener onScanOverListener;
    private boolean isMultiple;
    private WifiManager wifiManager;
    private MulticastLock multicastLock;
    private byte[] commandBytes = new byte[]{0, 0, 0, 1, 0, 0, 0, 0, 0, 0, 0, 0, 42, 0};
    private byte h = 0;
    private int i = 18;
    private Map<InetAddress, String> inetIdMap = new HashMap<>();
    private DatagramSocket datagramSocket = null;
    private SendTask sendTask;
    private ReceiveTask receiveTask;
    
    private PowerManager powerManager;
    private PowerManager.WakeLock wakeLock;
    private WifiManager.WifiLock wifiLock;

    /**
     * 2019-04-16
     * scan()
     */

    public Scanner2(Context var1) {
        this.context = var1;
        wifiManager = (WifiManager) this.context.getSystemService(Context.WIFI_SERVICE);
    }

    private boolean scan(boolean isSingleMode) {
        if (Scanner2.isRunning) {

            return false;

        } else {

            Scanner2.isRunning = true;

//            powerManager =(PowerManager)context.getSystemService(Context.POWER_SERVICE);
//            wakeLock = powerManager.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK ,TAG + ":wakeLock"); // PARTIAL_WAKE_LOCK Only keeps CPU on
//            wifiManager = (WifiManager)context.getApplicationContext().getSystemService(Context.WIFI_SERVICE);
//            wifiLock = wifiManager.createWifiLock(3, TAG + ":wifiLock");
//            multicastLock = wifiManager.createMulticastLock(TAG + ":multicastLock");
//
//            wakeLock.acquire();
//            multicastLock.acquire();
//            wifiLock.acquire();
                    
            this.isMultiple = isSingleMode;

            this.multicastLock = wifiManager.createMulticastLock("UDPwifi");
            this.multicastLock.setReferenceCounted(true);
            this.multicastLock.acquire();

            try {
                Scanner2.this.datagramSocket = new DatagramSocket();
                Scanner2.this.datagramSocket.setSoTimeout(5000);
                Scanner2.this.datagramSocket.setBroadcast(true);
            } catch (SocketException e) {
                e.printStackTrace();
            }

            receiveTask = new ReceiveTask();
            sendTask = new SendTask();

            sendTask.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
            receiveTask.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);

            return true;
        }
    }

//    private InetAddress getBroadcastAddress() {
//        /** 2019-04-14
//         * api 29 broadcast address 구하는데서 오류 발생해서 조치함 */
//        InetAddress broadCastInetAddress = getBroadcastAddress();
//
//        return broadCastInetAddress;
//    }

    private InetAddress getGatewayAddress() {
        DhcpInfo dhcpInfo = wifiManager.getDhcpInfo();
        byte[] gatewayIpInt = BigInteger.valueOf((long) dhcpInfo.serverAddress).toByteArray();
        InetAddress inetAddress = null;

        try {
            inetAddress = InetAddress.getByAddress(gatewayIpInt);
        } catch (UnknownHostException var5) {

        }

        return inetAddress;
    }

    private int a(byte[] var1, byte var2, int var3) {
        // var 2 = 0
        // var 3 = 18
        if (var1 == null) {
            return -1;
        } else {
            if (var3 < 0) {
                var3 = 0;
            }

            for (int var4 = var3; var4 < var1.length; ++var4) {
                if (var2 == var1[var4]) {
                    return var4;
                }
            }

            return -1;
        }
    }

    private byte[] getDeviceIdBytes(byte[] var1, int var2, int var3) {
        int var4 = var3 - var2;
        byte[] var5 = new byte[var4];
        System.arraycopy(var1, var2, var5, 0, var4);
        return var5;
    }

    public boolean scanAll() {
        return this.scan(true);
    }

    public boolean scan() {
        return this.scan(false);
    }

    public void setOnScanOverListener(Scanner2.OnScanOverListener onScanOverListener) {
        this.onScanOverListener = onScanOverListener;
    }

    public interface OnScanOverListener {
        void onResult(Map<InetAddress, String> var1, InetAddress var2);
    }

    private class ReceiveTask extends AsyncTask<Void, Void, Map<InetAddress, String>> {
        private ReceiveTask() {
        }

        protected Map<InetAddress, String> doInBackground(Void... var1) {
            Scanner2.this.inetIdMap.clear();

            while (Scanner2.isRunning) {
                Log.e(TAG, "is running = " + isRunning);

                try {

                    if (Scanner2.this.datagramSocket != null) {
                        try {
                            byte[] bufferBytes = new byte[1024];
                            DatagramPacket datagramPacket = new DatagramPacket(bufferBytes, bufferBytes.length);

                            Scanner2.this.datagramSocket.receive(datagramPacket);
                            InetAddress deviceInetAddress = InetAddress.getByName(datagramPacket.getAddress().getHostAddress());
                            if (Scanner2.this.inetIdMap.containsKey(deviceInetAddress)) {
                                continue;
                            }

                            try {
                                Thread.sleep(1);
                            } catch (InterruptedException e) {
                                e.printStackTrace();
                            }

                            Log.e("==>", datagramPacket.getLength() + "");
                            if (datagramPacket.getLength() > Scanner2.this.i) {
                                int var5 = Scanner2.this.a(bufferBytes, Scanner2.this.h, Scanner2.this.i);
                                String deviceId = new String(Scanner2.this.getDeviceIdBytes(bufferBytes, Scanner2.this.i + 1, var5));
                                Scanner2.this.inetIdMap.put(deviceInetAddress, deviceId);
                                Log.e("address==>", deviceInetAddress.toString());
                                Log.e("deviceId==>", deviceId);
                                if (!Scanner2.this.isMultiple) {
                                    Scanner2.isRunning = false;
                                    return Scanner2.this.inetIdMap;
                                }
                            }
                        } catch (SocketException var15) {
                            Log.e("e==>", var15.toString());
                            return Scanner2.this.inetIdMap;
                        }
                    }

                } catch (SocketException var16) {
                    Log.e("e1==>", var16.toString());
                } catch (IOException var17) {
                    Log.e("e2==>", var17.toString());

                    /** 2019-04-21
                     *  추가 */
                    isRunning = false;
                    datagramSocket.disconnect();
                } finally {
                    Log.e("e==>", "disconnect");
                    if (Scanner2.this.datagramSocket != null) {
                        datagramSocket.disconnect();
                    }
                }
            }

            return Scanner2.this.inetIdMap;
        }

        protected void onPostExecute(Map<InetAddress, String> inetIdMap) {
            super.onPostExecute(inetIdMap);

            if (onScanOverListener != null) {
                onScanOverListener.onResult(inetIdMap, getGatewayAddress());
            }

            /** 2019-4-16
             * */
            if (Scanner2.this.datagramSocket != null) {
                Scanner2.this.datagramSocket.close();
                Scanner2.this.datagramSocket = null;
            }
        }
    }

    private class SendTask extends AsyncTask<Void, Void, Map<InetAddress, String>> {
        private SendTask() {
        }

        protected Map<InetAddress, String> doInBackground(Void... var1) {
            try {
                DatagramPacket broadcastPacket = new DatagramPacket(Scanner2.this.commandBytes, Scanner2.this.commandBytes.length, Scanner2.getBroadcastAddress(), 5570);

//                for (int j = 0; j < 100; j++) {
                for (int j = 0; j < 6; j++) {

                    Log.e(TAG, "broadcast " + j);
                    if (Scanner2.this.datagramSocket != null) {
                        try {
                            Thread.sleep(100);
//                            Thread.sleep(500);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                        Scanner2.this.datagramSocket.send(broadcastPacket);
                    }
                }

            } catch (SocketException var11) {
                Log.e("s1==>", var11.toString());
            } catch (IOException var12) {
                Log.e("s2==>", var12.toString());
            } finally {
                Log.e("s==>", "disconnect");
                if (Scanner2.this.datagramSocket != null) {
                    Scanner2.this.datagramSocket.disconnect();
                }

            }

            return Scanner2.this.inetIdMap;
        }

        protected void onPostExecute(Map<InetAddress, String> inetIdMap) {
            super.onPostExecute(inetIdMap);
            Scanner2.isRunning = false;

            if (Scanner2.this.multicastLock.isHeld()) {
                Scanner2.this.multicastLock.release();
            }

//            if (onScanOverListener != null)
//                onScanOverListener.onResult(inetIdMap, getGatewayAddress());

            /** 2019-04-16
             * gc 주석
             *  */
//            // System.gc() // 가비지 역시 하는거 아니었어;
        }
    }

    public static InetAddress getBroadcastAddress() {
        InetAddress broadcastAddress = null;
        try {
            Enumeration<NetworkInterface> networkInterface = NetworkInterface
                    .getNetworkInterfaces();

            while (broadcastAddress == null
                    && networkInterface.hasMoreElements()) {
                NetworkInterface singleInterface = networkInterface
                        .nextElement();
                String interfaceName = singleInterface.getName();
                if (interfaceName.contains("wlan0")
                        || interfaceName.contains("eth0")) {
                    for (InterfaceAddress infaceAddress : singleInterface
                            .getInterfaceAddresses()) {
                        broadcastAddress = infaceAddress.getBroadcast();
                        if (broadcastAddress != null) {
                            break;
                        }
                    }
                }
            }

        } catch (SocketException e) {
            Log.e(TAG, e.getLocalizedMessage());
//            e.printStackTrace();
        }

        //Log.d(TAG, "boardcast adress = " + broadcastAddress.getHostAddress());

        return broadcastAddress;
    }
}
