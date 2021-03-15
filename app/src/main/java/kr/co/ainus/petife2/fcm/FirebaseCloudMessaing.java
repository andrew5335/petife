package kr.co.ainus.petife2.fcm;

import android.app.Notification;
import android.util.Log;

import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

import java.util.Locale;

import kr.co.ainus.petife2.R;

public class FirebaseCloudMessaing extends FirebaseMessagingService {

    private static final String TAG = "FirebaseCloudMessaing";

    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        super.onMessageReceived(remoteMessage);

        String message = "";
        String language = "";
        String country = "";
        message = remoteMessage.getNotification().getBody();
        language = Locale.getDefault().getLanguage().toString();
        //country = Locale.getDefault().getISO3Country().toString();

        Log.i("=====>language : ", language);
        //Log.i("=====>country : ", country);

        if(null != language && !"".equals(language)) {
            if(message.contains("급식")) {
                if("en".equals(language)) {
                    message = "This is the scheduled meal time.";
                } else if("ja".equals(language)) {
                    message = "スケジュールされた供給時間です。";
                }
            } else {
                if("en".equals(language)) {
                    message = "The scheduled watering time.";
                } else if("ja".equals(language)) {
                    message = "スケジュールされた給水時間です。";
                }
            }
        }

        /**
        Notification notification = new NotificationCompat.Builder(this, getString(R.string.default_notification_channel_id))
                .setContentTitle(remoteMessage.getNotification().getTitle())
                .setContentText(remoteMessage.getNotification().getBody())
                .setStyle(new NotificationCompat.BigTextStyle()
                        .bigText(remoteMessage.getNotification().getBody()))
                .setSmallIcon(R.drawable.logo)
                .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                .build();
         **/
        Notification notification = new NotificationCompat.Builder(this, getString(R.string.default_notification_channel_id))
                .setContentTitle(remoteMessage.getNotification().getTitle())
                .setContentText(message)
                .setStyle(new NotificationCompat.BigTextStyle()
                        .bigText(message))
                .setSmallIcon(R.drawable.logo)
                .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                .build();
        NotificationManagerCompat manager = NotificationManagerCompat.from(getApplicationContext());
        manager.notify(123, notification);

        // TODO(developer): Handle FCM messages here.
        // Not getting messages here? See why this may be: https://goo.gl/39bRNJ
        Log.i(TAG, "From: " + remoteMessage.getFrom());

        // Check if message contains a data payload.
        if (remoteMessage.getData().size() > 0) {
            Log.i(TAG, "Message data payload: " + remoteMessage.getData());

            if (/* Check if data needs to be processed by long running job */ true) {
                // For long-running tasks (10 seconds or more) use Firebase Job Dispatcher.
//                scheduleJob();
            } else {
                // Handle message within 10 seconds
//                handleNow();
            }

        }

        // Check if message contains a notification payload.
        if (remoteMessage.getNotification() != null) {
            Log.i(TAG, "Message Notification Body: " + remoteMessage.getNotification().getBody());
        }

        // Also if you intend on generating your own notifications as a result of a received FCM
        // message, here is where that should be initiated. See sendNotification method below.
    }

    @Override
    public void onNewToken(String token) {
        Log.i(TAG, "Refreshed token: " + token);

        // If you want to send messages to this application instance or
        // manage this apps subscriptions on the server side, send the
        // Instance ID token to your app server.
        sendRegistrationToServer(token);
    }

    private void sendRegistrationToServer(String token) {
        // TODO: Implement this method to send token to your app server.


    }

}
