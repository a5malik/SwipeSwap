package com.example.tsleeve.swipeswap;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

/**
 * Created by victorlai on 10/23/16.
 */

public class UserAuth {
    private FirebaseAuth mAuth = FirebaseAuth.getInstance();
    private FirebaseUser mUser = mAuth.getCurrentUser();

    public boolean validUser() {
        return mUser != null;
    }

    public String uid() {
        return (mUser != null) ? mUser.getUid() : null;
    }
}
