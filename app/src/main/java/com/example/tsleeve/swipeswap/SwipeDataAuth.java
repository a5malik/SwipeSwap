package com.example.tsleeve.swipeswap;

import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.DatabaseReference;
import com.google.android.gms.tasks.Task;

/**
 * Created by victorlai on 10/23/16.
 */

public class SwipeDataAuth {
    private final String ALL_SWIPES = "swipes";

    private DatabaseReference mDatabase = FirebaseDatabase.getInstance().getReference();

    public Task<Void> addSwipe(Swipe s) {
        return mDatabase.child(ALL_SWIPES).push().setValue(s);
    }
}
