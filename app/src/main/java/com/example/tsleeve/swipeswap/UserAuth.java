package com.example.tsleeve.swipeswap;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.AuthResult;
import android.util.Log;
import android.support.annotation.NonNull;
import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.OnCompleteListener;
import android.widget.Toast;
import android.content.Context;

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

    private static final String TAG = "UserAuth";

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
     * @return true if the user is valid; otherwise, false.
     */
    public boolean validUser() {
        return mUser != null;
    }

    /**
     * Returns the user ID of the current authenticated user, or null if the user is not valid.
     *
     * @return the user ID of the current user.
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
}
