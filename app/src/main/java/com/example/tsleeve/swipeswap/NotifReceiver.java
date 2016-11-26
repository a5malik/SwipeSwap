package com.example.tsleeve.swipeswap;

import android.app.*;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.support.v4.app.TaskStackBuilder;
import android.support.v7.app.NotificationCompat;
import android.util.Log;

/**
 * Created by footb on 11/18/2016.
 */

public class NotifReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        /*
        UserAuth userAuth = new UserAuth();
        Notification n = new Notification(context, intent.getExtras().getString("user_ID"), userAuth.uid(), Notification.Message.REVIEW_BUYER);
        userAuth.sendNotification(n);
        */
        Log.d("notif", intent.getExtras().getString("user_ID"));
        Intent notificationIntent = new Intent(context, MainActivity.class);
        notificationIntent.putExtra("swipe_notification_type", "REVIEW_BUYER");
        notificationIntent.putExtra("user_ID", intent.getExtras().getString("user_ID"));

        TaskStackBuilder stackBuilder = TaskStackBuilder.create(context);
        stackBuilder.addParentStack(MainActivity.class);
        stackBuilder.addNextIntent(notificationIntent);

        PendingIntent pendingIntent = stackBuilder.getPendingIntent(0, PendingIntent.FLAG_UPDATE_CURRENT);

        NotificationCompat.Builder builder = new NotificationCompat.Builder(context);

        android.app.Notification notification = builder.setContentTitle("Rating Request")
                .setContentText("Please click here to rate your last transcation!")
                .setTicker("New Message from GotSwipes")
                .setSmallIcon(R.drawable.add_swipe_icon)
                .setContentIntent(pendingIntent).build();

        NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        notificationManager.notify(Notification.Message.REVIEW_BUYER.ordinal(), notification);
    }
}
