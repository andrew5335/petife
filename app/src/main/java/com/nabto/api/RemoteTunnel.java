package com.nabto.api;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;


public class RemoteTunnel {
    private static final String TAG = "RemoteTunnel";
    private static ConcurrentMap<String, Tunnel> videoTunnelMap = new ConcurrentHashMap<>();
    private static ConcurrentMap<String, Session> videoSessionMap = new ConcurrentHashMap<>();
    private static ConcurrentMap<String, NabtoApi> videoNabtoApiMap = new ConcurrentHashMap<>();
    private static ConcurrentMap<String, Tunnel> audioTunnelMap = new ConcurrentHashMap<>();
    private static ConcurrentMap<String, Session> audioSessionMap = new ConcurrentHashMap<>();
    private static ConcurrentMap<String, NabtoApi> audioNabtoApiMap = new ConcurrentHashMap<>();
    private OnResultListener _onResultListener;
    private int _tryTimes = 50;
    private Context _ctx;
    private static boolean hasLog;

    public RemoteTunnel(Context ctx) {
        _ctx = ctx;
    }

    class TunnelParams {
        int localPort;
        int remotePort;
        String remoteAddress;

        TunnelParams(int localPort, int remotePort, String remoteAddress) {
            this.localPort = localPort;
            this.remotePort = remotePort;
            this.remoteAddress = remoteAddress;
        }
    }

    public static class TunnelAsyncTask extends AsyncTask<TunnelParams, Void, String> {

        private final String PETICA_ID;
        private final int TRY_TIMES;
        private final Context CONTEXT;
        private final OnResultListener ON_RESULT_LISTENER;
        private final TunnelType TUNNEL_TYPE;

        public TunnelAsyncTask(String PETICA_ID, int TRY_TIMES, Context CONTEXT, OnResultListener ON_RESULT_LISTENER, TunnelType TUNNEL_TYPE) {
            this.PETICA_ID = PETICA_ID;
            this.TRY_TIMES = TRY_TIMES;
            this.CONTEXT = CONTEXT;
            this.ON_RESULT_LISTENER = ON_RESULT_LISTENER;
            this.TUNNEL_TYPE = TUNNEL_TYPE;
        }

        @Override
        protected String doInBackground(TunnelParams... params) {

            NabtoApi nabtoApi = new NabtoApi(new NabtoAndroidAssetManager(CONTEXT));
            nabtoApi.startup();
            if (hasLog) {
                Log.e("Nabto Version==", nabtoApi.version());
            }

            TunnelParams tunnelParams = params[0];

            Log.d(TAG, "petica id = " + tunnelParams.remoteAddress);

            String state;
            Session session = nabtoApi.openSession("guest", "");
            final Tunnel tunnel =
                    nabtoApi.tunnelOpenTcp(
                            tunnelParams.localPort,
                            tunnelParams.remoteAddress,
                            "127.0.0.1",
                            tunnelParams.remotePort,
                            session);


            if (hasLog) {
                Log.e(TAG, "remote address " + tunnelParams.remoteAddress);
            }

            if (tunnel == null ||
                    !tunnel.getStatus().toString().equals("OK"))
                return null;

            int tryTimes = 0;

            for (; ; ) {
                TunnelInfoResult tunnelInfo = nabtoApi.tunnelInfo(tunnel);
                state = tunnelInfo.getTunnelState().toString();
                if (hasLog) {
                    Log.e("state==>", state);
                    Log.e("state==>", tunnel.getHandle().toString());
                    Log.e("state==>", tunnel.getStatus().toString());
                }

                if (state.equals(NabtoTunnelState.CLOSED.toString())) {
                    state = "NTCS_CLOSED";
                }
                if (state.equals(NabtoTunnelState.UNKNOWN.toString())) {
                    state = "NTCS_UNKNOWN";
                }
                if (state.equals(NabtoTunnelState.CONNECTING.toString())) {
                    state = "CONNECT_TIMEOUT";

                    try {
                        Thread.sleep(600);
                    } catch (InterruptedException ex) {

                    }
                    tryTimes++;

                    if (tryTimes >= TRY_TIMES) {
                        break;
                    }

                    continue;
                }
                break;
            }

            switch (TUNNEL_TYPE) {
                case VIDEO:
                    videoTunnelMap.put(tunnelParams.remoteAddress, tunnel);
                    videoSessionMap.put(tunnelParams.remoteAddress, session);
                    videoNabtoApiMap.put(tunnelParams.remoteAddress, nabtoApi);
                    break;

                case AUDIO:
                    audioTunnelMap.put(tunnelParams.remoteAddress, tunnel);
                    audioSessionMap.put(tunnelParams.remoteAddress, session);
                    audioNabtoApiMap.put(tunnelParams.remoteAddress, nabtoApi);
                    break;
            }

            return state;
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            ON_RESULT_LISTENER.onResult(PETICA_ID, s);
        }
    }

    /*
    NTCS_CLOSED,
    NTCS_CONNECTING,
    NTCS_READY_FOR_RECONNECT,
    NTCS_UNKNOWN,
    NTCS_LOCAL,
    NTCS_REMOTE_P2P,
    NTCS_REMOTE_RELAY,
    NTCS_REMOTE_RELAY_MICRO,
    FAILED;
     */
    public void openTunnel(String peticaId, int localPort, int remotePort, String remoteAddress, TunnelType tunnelType) {
        new TunnelAsyncTask(peticaId, _tryTimes, _ctx, _onResultListener, tunnelType).executeOnExecutor(AsyncTask.SERIAL_EXECUTOR, // 수정2
                new TunnelParams(localPort, remotePort, remoteAddress));
    }

    public static void closeTunnel(String peticaId) {

        try {
            Tunnel videoTunnel = videoTunnelMap.get(peticaId);
            Session videoSession = videoSessionMap.get(peticaId);
            NabtoApi videoNabtoApi = videoNabtoApiMap.get(peticaId);

            if (videoNabtoApi != null) {
                videoNabtoApi.tunnelClose(videoTunnel);
                videoNabtoApi.closeSession(videoSession);

                videoTunnelMap.remove(peticaId);
                videoSessionMap.remove(peticaId);
                videoNabtoApiMap.remove(peticaId);
            }

            Tunnel audioTunnel = audioTunnelMap.get(peticaId);
            Session audioSession = audioSessionMap.get(peticaId);
            NabtoApi audioNabtoApi = audioNabtoApiMap.get(peticaId);

            if (audioNabtoApi != null) {
                audioNabtoApi.tunnelClose(audioTunnel);
                audioNabtoApi.closeSession(audioSession);

                audioTunnelMap.remove(peticaId);
                audioSessionMap.remove(peticaId);
                audioNabtoApiMap.remove(peticaId);
            }
        } catch (Exception e) {
            Log.e(TAG, e.getLocalizedMessage());
            e.printStackTrace();
        }
    }

    public static void closeTunnels() {
        Log.e(TAG, "closeTunnels");

        try {

            for (Map.Entry<String, Tunnel> entry : videoTunnelMap.entrySet()) {
                String peticaId = entry.getKey();
                closeTunnel(peticaId);
            }

            videoTunnelMap.clear();
            videoSessionMap.clear();
            videoNabtoApiMap.clear();

            audioTunnelMap.clear();
            audioSessionMap.clear();
            audioNabtoApiMap.clear();

        } catch (Exception e) {
            Log.e(TAG, e.getLocalizedMessage());
            e.printStackTrace();
        }

    }

    //region event
    public void setOnResultListener(OnResultListener listener) {
        _onResultListener = listener;
    }

    public static boolean isHasLog() {
        return hasLog;
    }

    public static void setHasLog(boolean hasLog) {
        RemoteTunnel.hasLog = hasLog;
    }

    public static interface OnResultListener {
        public void onResult(String peticaId, String result);
    }
    //endregion

    public enum TunnelType {
        VIDEO, AUDIO
    }
}
