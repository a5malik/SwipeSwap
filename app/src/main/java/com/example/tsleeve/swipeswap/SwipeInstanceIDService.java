package com.example.tsleeve.swipeswap;

import com.google.firebase.iid.FirebaseInstanceIdService;
import com.google.firebase.iid.FirebaseInstanceId;
import android.util.Log;

public class SwipeInstanceIDService extends FirebaseInstanceIdService {
    private static final String TAG = "SwipeInstanceIDService";
    private SwipeDataAuth mDb = new SwipeDataAuth();
    private UserAuth mUAuth = new UserAuth();

    @Override
    public void onTokenRefresh() {
        // Get updated InstanceID token.
        String refreshedToken = FirebaseInstanceId.getInstance().getToken();
        Log.d(TAG, "Refreshed token: " + refreshedToken);

        sendRegistrationToServer(refreshedToken);
    }

    /**
     * Persist token to third-party servers.
     *
     * Modify this method to associate the user's FCM InstanceID token with any server-side account
     * maintained by your application.
     *
     * @param token The new token.
     */
    private void sendRegistrationToServer(String token) {
        mDb.updateToken(token, mUAuth.uid());
    }
}
