package com.example.tsleeve.swipeswap;

import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.DatabaseReference;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.Query;

/**
 * Created by victorlai on 10/23/16.
 */

public class SwipeDataAuth {

    /* List of keys used in Firebase */
    public static final String ALL_SWIPES = "swipes";
    public static final String ALL_REQUESTS = "requests";
    public static final String ALL_USERS = "users";
    public static final String USERNAME = "username";
    public static final String START_TIME = "startTime";
    public static final String DINING_HALL = "diningHall";

    public static final Integer BPLATE_ID = 1;
    public static final Integer COVEL_ID = 2;
    public static final Integer DENEVE_ID = 4;
    public static final Integer FEAST_ID = 8;

    /**
     * Structure of Firebase data:
     * 
     * root (swipeswap-9995f)
     *   swipes
     *     swipeID (Swipe object)
     *       diningHall
     *       endTime
     *       owner_id
     *       price
     *       startTime
     *     ...
     *   users
     *     uid
     *       username
     *       swipes
     *         ...
     *       requests
     *         ...
     *       reviews
     *     ...
     *   requests
     *   diningHalls
     *     BPlate
     *       swipes
     *         ...
     *       requests
     *         ...
     *     Covel
     *       swipes
     *         ...
     *       requests
     *         ...
     *     DeNeve
     *       swipes
     *         ...
     *       requests
     *         ...
     *     Feast
     *       swipes
     *         ...
     *       requests
     *         ...
     */

    private DatabaseReference mDatabase = FirebaseDatabase.getInstance().getReference();

    public Task<Void> addSwipe(Swipe s) {
        return mDatabase.child(ALL_SWIPES).push().setValue(s);
    }

    public Task<Void> addRequest(Swipe s) {
        return mDatabase.child(ALL_REQUESTS).push().setValue(s);
    }

    public DatabaseReference getUsersReference() {
        return mDatabase.child(ALL_USERS);
    }

    public DatabaseReference getUserReference(String uid) {
        return mDatabase.child(ALL_USERS).child(uid);
    }

    public Task<Void> registerUsername(String uid, String username) {
        return mDatabase.child(ALL_USERS).child(uid).child(USERNAME).setValue(username);
    }

    public Query orderBy(String target, String key, double start, double end) {
        DatabaseReference ref = mDatabase.child(target);
        return ref.orderByChild(key).startAt(start).endAt(end);
    }
}
