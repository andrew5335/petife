package kr.co.ainus.petife2.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

public class NotificationsReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        //handle your data
        abortBroadcast(); //stop sending this broadcast to other receivers.
    }
}