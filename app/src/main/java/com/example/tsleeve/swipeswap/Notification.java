package com.example.tsleeve.swipeswap;

import android.content.Context;

/**
 * Created by victorlai on 11/8/16.
 */

public class Notification {
    private Context mContext;
    private String mUser;
    private Message mMessage;

    public enum Message {
        ACCEPTED,
        REJECTED,
        OTHER
    }

    /**
     * Constructs a Notification with a context, target user, and message.
     *
     * @param context    The context of the application
     * @param targetUser The username of the user to receive the notification
     * @param message    The type of message to be included in the notification
     */
    public Notification(Context context, String targetUser, Message message) {
        this.mContext = context;
        this.mUser = targetUser;
        this.mMessage = message;
    }

    /**
     * Returns the message to be included in the notification.
     *
     * @return The message to include in the notification.
     */
    public String message() {
        switch (mMessage) {
            case ACCEPTED:
                return mUser + " has accepted your swipe.";
            case REJECTED:
                return mUser + " has rejected your swipe.";
            default:
                return "This is a default message.";
        }
    }

    /**
     * Returns the context of the notification.
     *
     * @return The context of the notification.
     */
    public Context getContext() {
        return mContext;
    }
}
