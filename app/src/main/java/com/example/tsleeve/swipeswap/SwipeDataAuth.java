package com.example.tsleeve.swipeswap;

import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.DatabaseReference;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.concurrent.TimeUnit;

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
    public static final String TOKEN = "regToken";

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

    private String mUserToken;
    private ArrayList<Swipe> mSwipes = new ArrayList<Swipe>();

    private DatabaseReference mDatabase = FirebaseDatabase.getInstance().getReference();

    /**
     * This function writes a swipe obect to mDatabase
     * under both root->swipes and root->users->uid->swipes
     *
     * @param s The Swipe object to be written to Firebase
     * @param uid User ID of the swipe seller
     * @return
     */
    public Task<Void> addSwipe(Swipe s, String uid) {
        mDatabase.child(ALL_USERS).child(uid).child(ALL_SWIPES).push().setValue(s);
        return mDatabase.child(ALL_SWIPES).push().setValue(s);
    }

    /**
     * This function writes a swipe object to mDatabase
     * under root->requests
     *
     * @param s The Swipe object to be written to Firebase
     * @return
     */
    public Task<Void> addRequest(Swipe s) {
        return mDatabase.child(ALL_REQUESTS).push().setValue(s);
    }

    // TODO: Remove swipe

    /**
     * Gets a reference to the root->users in the Firebase data
     *
     * @return reference to the Google Cloud Storage object that has all users data
     */
    public DatabaseReference getUsersReference() {
        return mDatabase.child(ALL_USERS);
    }

    /**
     * Returns the reference to a specific user's reference,
     * and can be used to obtain the users metadata.
     *
     * @param uid UserID of the user whose reference we want
     * @return Reference to the Google Cloud Storage object of the user
     */
    public DatabaseReference getUserReference(String uid) {
        return mDatabase.child(ALL_USERS).child(uid);
    }

    /**
     * Set a username under root->users->uid->username
     *
     * @param uid New UserID
     * @param username New Username
     * @return
     */
    public Task<Void> registerUsername(String uid, String username) {
        return mDatabase.child(ALL_USERS).child(uid).child(USERNAME).setValue(username);
    }

    public Query orderBy(String target, String key, double start, double end) {
        DatabaseReference ref = mDatabase.child(target);
        return ref.orderByChild(key).startAt(start).endAt(end);
    }

    /**
     * Sets a value for root->users->uid->regToken in the firebase data.
     * regToken is used for user authentication, and is stored in user objects.
     *
     * @param token registration token to be updated for user
     * @param uid UserID to update user's Google Cloud Storage object
     * @return
     */
    public Task<Void> updateToken(String token, String uid) {
        if (uid == null)
            return null;
        return mDatabase.child(ALL_USERS).child(uid).child(TOKEN).setValue(token);
    }

    /**
     * Gets the registration token of a user from root->users->uid->regToken
     * in firebase, and sleeps 1 second to recieve the data.
     *
     * @param uid UserID to obtain the registration token
     * @return the user regToken in string format
     */
    public String getUserToken(String uid) {
        DatabaseReference ref = mDatabase.child(ALL_USERS).child(uid).child(TOKEN);
        ref.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                mUserToken = dataSnapshot.getValue(String.class);

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        // Wait until data has arrived
        try {
            TimeUnit.SECONDS.sleep(1);
        } catch (InterruptedException ie) {
            ie.printStackTrace();
        }
        return mUserToken;
    }

    /**
     * This function first ets a reference to root->users->uid->swipes for a
     * particular user ID, and adds all the swipes associated with this reference to an
     * Arraylist, using data snapshots, to map each attribute in firebase to
     * the corresponding field in the Swipes class
     *
     * @param uid UserID of the firebase user
     * @return ArrayList of all the swipes associated with a particular user
     */
    public ArrayList<Swipe> getAllSwipesByUser(String uid) {
        DatabaseReference ref = mDatabase.child(ALL_USERS).child(uid).child(ALL_SWIPES);
        ref.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot ds : dataSnapshot.getChildren()) {
                    HashMap<String, Object> h = (HashMap<String, Object>)ds.getValue();

                    Long dh = (Long)h.get("diningHall");
                    Integer diningHall = new Integer(dh.intValue());

                    Long endTime = (Long)h.get("endTime");

                    String ownerId = (String)h.get("owner_ID");

                    Long p = (Long)h.get("price");
                    Double price = new Double(p.doubleValue());

                    Long startTime = (Long)h.get("startTime");
                    mSwipes.add(new Swipe(price, startTime, endTime, ownerId, diningHall));
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        // Wait until data has arrived
        try {
            TimeUnit.SECONDS.sleep(1);
        } catch (InterruptedException ie) {
            ie.printStackTrace();
        }
        return mSwipes;
    }
}
