package com.example.tsleeve.swipeswap;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.AuthResult;

import android.util.Log;
import android.support.annotation.NonNull;
import android.content.Context;

import java.io.InputStream;

import android.os.AsyncTask;
import com.amazonaws.services.sns.AmazonSNS;
import com.amazonaws.services.sns.AmazonSNSClient;
import com.amazonaws.auth.PropertiesCredentials;
import com.amazonaws.sns.samples.mobilepush.SNSMobilePush;
import com.amazonaws.AmazonServiceException;
import com.amazonaws.AmazonClientException;
import com.amazonaws.auth.CognitoCachingCredentialsProvider;
import com.amazonaws.regions.Regions;

import java.util.Map;
import java.util.HashMap;

/**
 * Created by victorlai on 10/23/16.
 */

/**
 *
 */
public class UserAuth {
    private FirebaseAuth.AuthStateListener mAuthListener;
    private final FirebaseAuth mAuth = FirebaseAuth.getInstance();
    private FirebaseUser mUser = mAuth.getCurrentUser();
    private SwipeDataAuth mDb = new SwipeDataAuth();

    private static final String TAG = "UserAuth";
    private static final String ENDPOINT = "https://sns.ap-southeast-2.amazonaws.com";
    private static final String AWS_SERVER = "Swipes_App_Server";
    private static final String IDENTITY_POOL_ID = "us-west-2:a6e9a940-92d6-4459-8ff8-96c295d1cbcc";

    public UserAuth() {
        mAuthListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                mUser = firebaseAuth.getCurrentUser();
                if (mUser != null) {
                    // User is signed in
                    Log.d(TAG, "onAuthStateChanged:signed_in:" + uid());
                } else {
                    // User is signed out
                    Log.d(TAG, "onAuthStateChanged:signed_out");
                }
            }
        };
        mAuth.addAuthStateListener(mAuthListener);
    }

    /**
     * Returns whether or not the current user is authenticated and valid.
     *
     * @return <code>true</code> if the user is valid; <code>false</code> otherwise
     */
    public boolean validUser() {
        return mUser != null;
    }

    /**
     * Returns the user ID of the current authenticated user, or null if the user is not valid.
     *
     * @return The user ID of the current user.
     */
    public String uid() {
        return validUser() ? mUser.getUid() : null;
    }

    /**
     * Safely signs out the current user.
     */
    public void signOut() {
        mAuth.signOut();
    }

    /**
     * Sends a request to the AWS server to issue a request to Firebase to send an appropriate
     * notfication to a user.
     *
     * @param n The Notification containing message details
     * @see     CognitoCachingCredentialsProvider
     */
    public void sendAWSNotification(Notification n) {
        Swipe s = n.getSwipe();
        Map<String, Object> data = new HashMap<String, Object>();
        if (s != null) {
            data.put("price", s.getPrice());
            data.put("startTime", s.getStartTime());
            data.put("endTime", s.getEndTime());
            data.put("owner_ID", s.getOwner_ID());
            data.put("diningHall", s.getDiningHall());
            data.put("postTime", s.getPostTime());
            data.put("type", s.getType());
        }
        data.put("notification_type", n.getType());

        AmazonSNS sns = null;
        try {
            // Initialize the Amazon Cognito credentials provider
            CognitoCachingCredentialsProvider credentialsProvider = new CognitoCachingCredentialsProvider(
                    n.getContext(),
                    IDENTITY_POOL_ID, // Identity Pool ID
                    Regions.US_WEST_2 // Region
            );
            sns = new AmazonSNSClient(credentialsProvider);
            sns.setEndpoint(ENDPOINT);
        } catch (Exception e) {
            e.printStackTrace();
        }

        try {
            SNSMobilePush smp = new SNSMobilePush(sns);
            String uid = n.getUserID();
            smp.initAndroidAppNotification(AWS_SERVER, mDb.getUserToken(uid), n, data);
        } catch (AmazonServiceException ase) {
            Log.d(TAG, "Caught an AmazonServiceException, which means your request made it "
                            + "to Amazon SNS, but was rejected with an error response for some reason.");
            Log.d(TAG, "Error Message:    " + ase.getMessage());
            Log.d(TAG, "HTTP Status Code: " + ase.getStatusCode());
            Log.d(TAG, "AWS Error Code:   " + ase.getErrorCode());
            Log.d(TAG, "Error Type:       " + ase.getErrorType());
            Log.d(TAG, "Request ID:       " + ase.getRequestId());
        } catch (AmazonClientException ace) {
            Log.d(TAG, "Caught an AmazonClientException, which means the client encountered "
                            + "a serious internal problem while trying to communicate with SNS, such as not "
                            + "being able to access the network.");
            Log.d(TAG, "Error Message: " + ace.getMessage());
        }
    }

    /**
     * Sends a notification.
     *
     * @param n The notification details
     */
    public void sendNotification(Notification n) {
        new SendAWSNotificationTask().execute(n);
    }

    private class SendAWSNotificationTask extends AsyncTask<Notification, Void, Void> {
        @Override
        protected Void doInBackground(Notification... params) {
            sendAWSNotification(params[0]);
            return null;
        }
    }
}
