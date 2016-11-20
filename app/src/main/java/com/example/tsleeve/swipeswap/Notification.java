package com.example.tsleeve.swipeswap;

import android.content.Context;

/**
 * Created by victorlai on 11/8/16.
 */

public class Notification {
    private Context mContext;
    private String mInitiator;
    private String mTargetUser;
    private Message mMessage;
    private Swipe mSwipe;
    private SwipeDataAuth mDb = new SwipeDataAuth();

    public enum Message {
        ACCEPTED_SALE,    // Seller gets this when buyer is interested in a swipe sale
        ACK_SALE,         // Buyer get this when seller has confirmed
        ACCEPTED_REQUEST, // Buyer gets this when seller has responded to a swipe request
        ACK_REQUEST,      // Seller gets this when buyer has confirmed
        REJECTED_SALE,
        REJECTED_REQUEST,
        REVIEW_SELLER,
        REVIEW_BUYER,
        OTHER
    }

    /**
     * Constructs a Notification with a context, initiating user, target user, message, and Swipe.
     *
     * @param context    The context of the application
     * @param initiator  The ID of the user that initiated the notification
     * @param targetUser The ID of the user to receive the notification
     * @param message    The type of message to be included in the notification
     * @param swipe      The information about the swipe post to be included in the notification
     */
    public Notification(Context context, String initiator, String targetUser, Message message, Swipe swipe) {
        this.mContext = context;
        this.mInitiator = initiator;
        this.mTargetUser = targetUser;
        this.mMessage = message;
        this.mSwipe = swipe;
    }

    /**
     * Constructs a Notification with a context, target user, and message.
     *
     * @param context    The context of the application
     * @param initiator  The ID of the user that initiated the notification
     * @param targetUser The ID of the user to receive the notification
     * @param message    The type of message to be included in the notification
     */
    public Notification(Context context, String initiator, String targetUser, Message message) {
        this.mContext = context;
        this.mInitiator = initiator;
        this.mTargetUser = targetUser;
        this.mMessage = message;
        this.mSwipe = null;
    }

    /**
     * Returns the ID of the user to send the notification to.
     *
     * @return The user ID
     */
    public String getTargetUserID() {
        return mTargetUser;
    }

    /**
     * Returns the ID of the user that initiated the notification.
     *
     * @return The user ID
     */
    public String getInitiatorUserID() { return mInitiator; }

    /**
     * Returns the information for the swipe post.
     *
     * @return The swipe data
     */
    public Swipe getSwipe() {
        return mSwipe;
    }

    /**
     * Returns the appropriate title to be included in the notification.
     *
     * @return The title to include in the notification.
     */
    public String title() {
        switch (mMessage) {
            case ACCEPTED_SALE:
            case ACCEPTED_REQUEST:
                return "Swipe Accepted";
            case ACK_REQUEST:
            case ACK_SALE:
                return "Confirmed";
            case REJECTED_SALE:
            case REJECTED_REQUEST:
                return "Swipe Rejected";
            case REVIEW_BUYER:
            case REVIEW_SELLER:
                return "Review";
            default: // OTHER
                return "Default Message";
        }
    }

    /**
     * Returns the appropriate message to be included in the notification.
     *
     * @return The message to include in the notification
     */
    public String message() {
        String user = mDb.getUserName(mTargetUser);
        String buyer = user;
        String seller = user;

        // TODO: Add rating of user in message
        switch (mMessage) {
            case ACCEPTED_SALE:
                return buyer + " wants to buy your swipe.";
            case ACK_SALE:
                return seller + " has accepted to sell";
            case ACCEPTED_REQUEST:
                return seller + " has agreed to your swipe sale.";
            case ACK_REQUEST:
                return buyer + "has agreed to your sale";
            case REJECTED_SALE:
            case REJECTED_REQUEST:
                return user + " has rejected your swipe.";
            case REVIEW_BUYER:
                return "Rate the buyer (" + buyer + ")";
            case REVIEW_SELLER:
                return "Rate the seller (" + seller + ")";
            default: // OTHER
                return "This is a default notification message.";
        }
    }

    /**
     * Returns the context of the notification.
     *
     * @return The context of the notification
     */
    public Context getContext() {
        return mContext;
    }

    /**
     * Get the type of notification.
     *
     * @return The type of notification
     */
    public Message getType() {
        return mMessage;
    }
}
