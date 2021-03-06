package kr.co.ainus.peticaexcutor;

import android.os.AsyncTask;
import android.util.Log;

import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.util.Arrays;

import kr.co.ainus.peticaexcutor.callback.CompleteCallback;
import kr.co.ainus.peticaexcutor.callback.FailCallback;
import kr.co.ainus.peticaexcutor.callback.ReceiveCallback;
import kr.co.ainus.peticaexcutor.callback.SendCallback;
import kr.co.ainus.peticaexcutor.callback.SuccessCallback;


public class Executor {
    private static final Executor INSTANCE = new Executor();
    private static final String TAG = "Executor";

    public static final String DEVICE_IP = "127.0.0.1";

    private Executor() {
    }

    public static Executor getInstance() {
        return INSTANCE;
    }

    public void onSendCommand(
            byte[] bytes
            , String deviceIp
            , int port
            , SuccessCallback successCallback
            , FailCallback failCallback
            , CompleteCallback completeCallback
            , SendCallback sendCallback
            , ReceiveCallback receiveCallback
    ) {

        Byte[] data = new Byte[bytes.length];

        for (int i = 0; i < bytes.length; i++) {
            data[i] = bytes[i];
        }

        new CommandTask(
                bytes
                , deviceIp
                , port
                , successCallback
                , failCallback
                , completeCallback
                , sendCallback
                , receiveCallback
        ).executeOnExecutor(AsyncTask.SERIAL_EXECUTOR);
    }

    public static class CommandTask extends AsyncTask<Void, Void, Void> {

        private final byte[] BYTES;
        private final String DEVICE_IP;
        private final int DEVICE_PORT;
        private final SuccessCallback SUCCESS_CALLBACK;
        private final FailCallback FAIL_CALLBACK;
        private final CompleteCallback COMPLETE_CALLBACK;
        private final SendCallback SEND_CALLBACK;
        private final ReceiveCallback RECEIVE_CALLBACK;

        CommandTask(byte[] BYTES, String DEVICE_IP, int DEVICE_PORT, SuccessCallback SUCCESS_CALLBACK, FailCallback FAIL_CALLBACK, CompleteCallback COMPLETE_CALLBACK, SendCallback SEND_CALLBACK, ReceiveCallback RECEIVE_CALLBACK) {
            this.BYTES = BYTES;
            this.DEVICE_IP = DEVICE_IP;
            this.DEVICE_PORT = DEVICE_PORT;
            this.SUCCESS_CALLBACK = SUCCESS_CALLBACK;
            this.FAIL_CALLBACK = FAIL_CALLBACK;
            this.COMPLETE_CALLBACK = COMPLETE_CALLBACK;
            this.SEND_CALLBACK = SEND_CALLBACK;
            this.RECEIVE_CALLBACK = RECEIVE_CALLBACK;
        }

        @Override
        protected Void doInBackground(Void... voids) {

            Socket socket;
            DataOutputStream dataOutputStream;

            try {
                socket = new Socket(DEVICE_IP, DEVICE_PORT);

                if(socket != null) {
                    socket.setSoTimeout(3000000);

                    if (SUCCESS_CALLBACK != null) SUCCESS_CALLBACK.onSuccess();

                    dataOutputStream = new DataOutputStream(socket.getOutputStream());
                    dataOutputStream.write(BYTES);
                    dataOutputStream.flush();

                    Log.i(TAG, "version request ??????");
                    Log.i(TAG, "version 0x01, 0x55 ?????? ??????");
                    Log.e(TAG, "==================executor version request : " + Arrays.toString(BYTES));
                    for (int i = 2; i < BYTES.length; i++) {
                        Log.i(TAG, "executor version request value = " + BYTES[i]);
                    }
                    Log.i(TAG, "version request ???");

                    if (SEND_CALLBACK != null) SEND_CALLBACK.onSend();

                    byte[] buffer = new byte[4096];

                    socket.getInputStream().read(buffer, 0, buffer.length);

                    byte[] response = new byte[12];
                    // ????????? ?????? ??????????????? ???????????? ????????? ?????? ??? ??????
                    System.arraycopy(buffer, 0, response, 0, response.length);

                    Log.i(TAG, "executor version response ??????");
                    Log.i(TAG, "executor version 0x01, 0x55 ?????? ??????");
                    Log.e(TAG, "==================executor version response : " + Arrays.toString(response));
                    for (int i = 2; i < response.length; i++) {
                        Log.i(TAG, "executor version response value = " + response[i]);
                    }
                    Log.i(TAG, "executor version response ???");

                    if (RECEIVE_CALLBACK != null) {
                        RECEIVE_CALLBACK.onReceive(response);
                    }

                    dataOutputStream.close();
                    socket.close();
                } else {
                    Log.i(TAG, "========= petife has not started yet or fail to connect to petife");
                }
            } catch (IOException e) {
                e.printStackTrace();
                if (FAIL_CALLBACK != null) FAIL_CALLBACK.onFail();
            } finally {
                if (COMPLETE_CALLBACK != null) COMPLETE_CALLBACK.onComplete();
            }
            return null;
        }
    }

}
