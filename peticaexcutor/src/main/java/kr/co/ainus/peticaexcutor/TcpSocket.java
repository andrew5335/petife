package kr.co.ainus.peticaexcutor;

import android.util.Log;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.SocketAddress;

import kr.co.ainus.peticaexcutor.callback.CompleteCallback;
import kr.co.ainus.peticaexcutor.callback.FailCallback;
import kr.co.ainus.peticaexcutor.callback.ReceiveCallback;
import kr.co.ainus.peticaexcutor.callback.SendCallback;
import kr.co.ainus.peticaexcutor.callback.SuccessCallback;


public class TcpSocket {

    private static final String TAG = "TcpSocket";
    private Socket socket;
    private DataInputStream dataInputStream;
    private DataOutputStream dataOutputStream;

    private SuccessCallback successCallback;
    private FailCallback failCallback;
    private CompleteCallback completeCallback;
    private SendCallback sendCallback;
    private ReceiveCallback receiveCallback;

    public TcpSocket(String ip, int port) {
        socket = new Socket();
        this.connect(ip, port);
    }

    public void setSuccessCallback(SuccessCallback successCallback) {
        this.successCallback = successCallback;
    }

    public void setFailCallback(FailCallback failCallback) {
        this.failCallback = failCallback;
    }

    public void setCompleteCallback(CompleteCallback completeCallback) {
        this.completeCallback = completeCallback;
    }

    public void setSendCallback(SendCallback sendCallback) {
        this.sendCallback = sendCallback;
    }

    public void setReceiveCallback(ReceiveCallback receiveCallback) {
        this.receiveCallback = receiveCallback;
    }

    public void connect(String ip, int port) {

        if (ip != null) {
            SocketAddress socketAddress = new InetSocketAddress(ip, port);
            try {
                socket.connect(socketAddress, 5000);
                dataInputStream = new DataInputStream(socket.getInputStream());
                dataOutputStream = new DataOutputStream(socket.getOutputStream());
                if (successCallback != null) successCallback.onSuccess();
            } catch (Exception e) {
                Log.e(TAG, e.getLocalizedMessage());
                e.printStackTrace();
                if (failCallback != null) failCallback.onFail();
            } finally {
                if (completeCallback != null) completeCallback.onComplete();
            }
        }

    }

    public void setSoTimeout(int ms) {

        try {
            socket.setSoTimeout(ms);
            if (successCallback != null) successCallback.onSuccess();
        } catch (Exception e) {
            Log.e(TAG, e.getLocalizedMessage());
            e.printStackTrace();
            if (failCallback != null) failCallback.onFail();
        } finally {
            if (completeCallback != null) completeCallback.onComplete();
        }

    }

    public void bind(int port) {

        try {
            socket.bind(new InetSocketAddress(port));
            if (successCallback != null) successCallback.onSuccess();
        } catch (Exception e) {
            Log.e(TAG, e.getLocalizedMessage());
            e.printStackTrace();
            if (failCallback != null) failCallback.onFail();
        } finally {
            if (completeCallback != null) completeCallback.onComplete();
        }

    }

    public void sendByte(byte[] Data, int offset, int count) {

        try {
            dataOutputStream.write(Data, offset, count);
            dataOutputStream.flush();
            if (successCallback != null) successCallback.onSuccess();
        } catch (Exception e) {
            Log.e(TAG, e.getLocalizedMessage());
            e.printStackTrace();
            if (failCallback != null) failCallback.onFail();
        } finally {
            if (completeCallback != null) completeCallback.onComplete();
        }
    }

    public void sendStr(String Data) {

        try {
            dataOutputStream.writeBytes(Data);
            dataOutputStream.flush();
            if (successCallback != null) successCallback.onSuccess();
        } catch (Exception e) {
            Log.e(TAG, e.getLocalizedMessage());
            e.printStackTrace();
            if (failCallback != null) failCallback.onFail();
        } finally {
            if (completeCallback != null) completeCallback.onComplete();
        }

    }

    public void send(byte[] Data) {

        try {
            dataOutputStream.write(Data);
            dataOutputStream.flush();
            if (successCallback != null) successCallback.onSuccess();
            if (sendCallback != null) sendCallback.onSend();
        } catch (Exception e) {
            Log.e(TAG, e.getLocalizedMessage());
            e.printStackTrace();
            if (failCallback != null) failCallback.onFail();
        } finally {
            if (completeCallback != null) completeCallback.onComplete();
        }

    }

    public byte[] read() {
        byte[] data = null;
        try {
            int num = dataInputStream.available();
            if (num > 0) {
                data = new byte[num];
                dataInputStream.read(data);
            }
            if (successCallback != null) successCallback.onSuccess();
            if (receiveCallback != null) receiveCallback.onReceive(data);
        } catch (Exception e) {
            Log.e(TAG, e.getLocalizedMessage());
            e.printStackTrace();
            if (failCallback != null) failCallback.onFail();
        } finally {
            if (completeCallback != null) completeCallback.onComplete();
        }
        return data;
    }

    public String getIp() {
        String ipString = "";
        ipString = socket.getInetAddress().getHostAddress();
        return ipString;
    }

    public int getPort() {
        int port = 0;
        port = socket.getPort();
        return port;
    }

    public void close() {
        try {
            dataInputStream.close();
            dataOutputStream.close();
            socket.close();
            socket = null;
            if (successCallback != null) successCallback.onSuccess();
        } catch (Exception e) {
            Log.e(TAG, e.getLocalizedMessage());
            e.printStackTrace();
            if (failCallback != null) failCallback.onFail();
        } finally {
            if (completeCallback != null) completeCallback.onComplete();
        }
    }

    public boolean isClose() {
        return socket.isClosed();
    }

    public boolean isConnected() {
        return socket.isConnected();
    }
}
