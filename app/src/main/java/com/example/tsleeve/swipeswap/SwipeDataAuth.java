package com.example.tsleeve.swipeswap;

import android.os.AsyncTask;

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
    public static final String BPLATE = "BPlate";
    public static final String COVEL = "Covel";
    public static final String DENEVE = "DeNeve";
    public static final String FEAST = "Feast";
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
     *       regToken
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
    private String mUsername;
    private ArrayList<Swipe> mSwipes = new ArrayList<Swipe>();

    private DatabaseReference mDatabase = FirebaseDatabase.getInstance().getReference();

    /**
     * Stores the specified swipe sale in the application's database.
     *
     * @param s   The swipe to be saved
     * @param uid The ID of the swipe seller
     * @return    A task that represents the completion of the operation to add the swipe sale
     * @see       Swipe
     */
    public Task<Void> addSwipe(Swipe s, String uid) {
        mDatabase.child(ALL_USERS).child(uid).child(ALL_SWIPES).push().setValue(s);

        int diningHall = s.getDiningHall();
        if ((diningHall & BPLATE_ID) == BPLATE_ID)
            mDatabase.child(DINING_HALL).child(BPLATE).child(ALL_SWIPES).push().setValue(s);
        if ((diningHall & COVEL_ID) == COVEL_ID)
            mDatabase.child(DINING_HALL).child(COVEL).child(ALL_SWIPES).push().setValue(s);
        if ((diningHall & DENEVE_ID) == DENEVE_ID)
            mDatabase.child(DINING_HALL).child(DENEVE).child(ALL_SWIPES).push().setValue(s);
        if ((diningHall & FEAST_ID) == FEAST_ID)
            mDatabase.child(DINING_HALL).child(FEAST).child(ALL_SWIPES).push().setValue(s);

        return mDatabase.child(ALL_SWIPES).push().setValue(s);
    }

    /**
     * Saves the specified swipe request to the application's database.
     *
     * @param  s   The Swipe object to be written to Firebase
     * @param  uid The ID of the user making the swipe request
     * @return     A task that represents the completion of the operation to add the swipe request
     * @see        Swipe
     */
    public Task<Void> addRequest(Swipe s, String uid) {
        mDatabase.child(ALL_USERS).child(uid).child(ALL_REQUESTS).push().setValue(s);

        int diningHall = s.getDiningHall();
        if ((diningHall & BPLATE_ID) == BPLATE_ID)
            mDatabase.child(DINING_HALL).child(BPLATE).child(ALL_REQUESTS).push().setValue(s);
        if ((diningHall & COVEL_ID) == COVEL_ID)
            mDatabase.child(DINING_HALL).child(COVEL).child(ALL_REQUESTS).push().setValue(s);
        if ((diningHall & DENEVE_ID) == DENEVE_ID)
            mDatabase.child(DINING_HALL).child(DENEVE).child(ALL_REQUESTS).push().setValue(s);
        if ((diningHall & FEAST_ID) == FEAST_ID)
            mDatabase.child(DINING_HALL).child(FEAST).child(ALL_REQUESTS).push().setValue(s);

        return mDatabase.child(ALL_REQUESTS).push().setValue(s);
    }

    public Task<Void> removeSwipe(String uid) {
        return null; // TODO
    }

    private class RemoveSwipeTask extends AsyncTask<Notification, Void, Void> {
        @Override
        protected Void doInBackground(Notification... params) {

            return null;
        }
    }

    /**
     * Gets a reference to the location in the application's database where all users' data is
     * located.
     *
     * @return A reference to the database location containing users' data
     * @see    DatabaseReference
     */
    public DatabaseReference getUsersReference() {
        return mDatabase.child(ALL_USERS);
    }

    /**
     * Returns the reference to a specific user under root->users,
     * and can be used to obtain the users metadata.
     *
     * @param  uid the ID of the user to get the a reference to
     * @return     Reference to the Google Cloud Storage object of the user
     * @see        DatabaseReference
     */
    public DatabaseReference getUserReference(String uid) {
        return mDatabase.child(ALL_USERS).child(uid);
    }

    /**
     * Set a username for the a user.  A username can be thought of as a nickname for a user.
     *
     * @param  uid      The ID of the user to register a username with
     * @param  username The username string
     * @return          A task that represents the completion of the operation to register a user
     *                  with a username
     */
    public Task<Void> registerUsername(String uid, String username) {
        return mDatabase.child(ALL_USERS).child(uid).child(USERNAME).setValue(username);
    }

    /**
     * Sorts a target key based on a specified key in the application's database.
     *
     * @param target The target key to sort
     * @param key    The key to sort based on
     * @param start  The lower bound constraint (inclusive)
     * @param end    The upper bound constraint (inclusive)
     * @return       A query reference to be used to add event listeners and retrieve data
     * @see          Query
     */
    public Query orderBy(String target, String key, double start, double end) {
        DatabaseReference ref = mDatabase.child(target);
        return ref.orderByChild(key).startAt(start).endAt(end);
    }

    /**
     * Updates the registration token associated.  See
     * <a href="https://firebase.google.com/docs/notifications/android/console-device#access_the_registration_token">
     * https://firebase.google.com/docs/notifications/android/console-device#access_the_registration_token
     * </a>
     * for more information about when registration tokens get updated.
     *
     * @param  token Registration token to be updated for the specified user
     * @param  uid   The ID of the user to update the token of
     * @return       A task that represents the completion of the operation to add the swipe sale,
     *               or null if the user ID is null
     * @see          SwipeInstanceIDService
     */
    public Task<Void> updateToken(String token, String uid) {
        if (uid == null)
            return null;
        return mDatabase.child(ALL_USERS).child(uid).child(TOKEN).setValue(token);
    }

    /**
     * Gets the registration token associated with the specified user.
     *
     * The registration token can be thought of as an ID associated with a unique device.
     *
     * Implementation Note: This method is considered unstable.  It may sometimes not return a
     * non-null value.
     *
     * @param uid ID of the user to obtain the registration token from
     * @return    The registration token
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
     * Returns a listing of all swipe sales associated with the specified user.
     *
     * Implementation Note: This method is considered unstable.  It may sometimes not return a
     * non-null value.
     *
     * @param uid ID of the user to get all swipes from
     * @return    A list of all the swipes associated with a particular user
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

    /**
     * Gets the username of the specified user.
     *
     * Implementation Note: This method is considered unstable.  It may sometimes not return a
     * non-null value.
     *
     * @param uid ID of the user to get the username of
     * @return    The username
     */
    public String getUserName(String uid) {
        DatabaseReference ref = mDatabase.child(ALL_USERS).child(uid).child(USERNAME);
        ref.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                mUsername = dataSnapshot.getValue(String.class);

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
        return mUsername;
    }
}
