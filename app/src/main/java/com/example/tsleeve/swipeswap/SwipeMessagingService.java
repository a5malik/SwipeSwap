package com.example.tsleeve.swipeswap;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.media.RingtoneManager;
import android.net.Uri;
import android.support.v4.app.NotificationCompat;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;
import android.util.Log;
import android.support.v4.content.LocalBroadcastManager;


public class SwipeMessagingService extends FirebaseMessagingService {
    private static final String TAG = "SwipeMessagingService";

    public static final String CONFIRM_FRAGMENT = "confirm";
    public static final String MESSAGING_FRAGMENT = "message";
    public static final String REVIEW_FRAGMENT = "review";

    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {

        Log.d(TAG, "From: " + remoteMessage.getFrom());
        Log.d(TAG, "Notification Message Body: " + remoteMessage.getNotification().getBody());
        //Log.d(TAG, "Click action: " + remoteMessage.getNotification().getClickAction());

        // Calling method to generate notification
        sendNotification(remoteMessage.getNotification().getBody(),
                remoteMessage.getNotification().getTitle());
    }

    /**
     * Create and show a simple notification containing the received FCM message.
     *
     * @param messageBody FCM message body received.
     * @param title       FCM title received
     */
    private void sendNotification(String messageBody, String title) {
        Intent intent = new Intent(this, MainActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0 /* Request code */, intent,
                PendingIntent.FLAG_ONE_SHOT);

        Intent broadcast = new Intent("broadcaster");
        String fragment = null;
        if (title.equals("Swipe Accepted")) {
            fragment = CONFIRM_FRAGMENT;
        } else if (title.equals("Confirmed")) {
            fragment = MESSAGING_FRAGMENT;
        } else if (title.equals("Review")) {
            fragment = REVIEW_FRAGMENT;
        } else {
            fragment = "";
        }
        broadcast.putExtra("action", fragment);
        LocalBroadcastManager.getInstance(this).sendBroadcast(broadcast);

        Uri defaultSoundUri= RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(this)
                .setSmallIcon(R.drawable.ic_person)
                .setContentTitle(getString(R.string.app_name) + " | " + title)
                .setContentText(messageBody)
                .setAutoCancel(true)
                .setSound(defaultSoundUri)
                .setContentIntent(pendingIntent);

        NotificationManager notificationManager =
                (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

        notificationManager.notify(0 /* ID of notification */, notificationBuilder.build());
    }
}
