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
    public static final String RATINGSUM = "rating_sum";
    public static final String NOR = "NOR";
    public static final String START_TIME = "startTime";
    public static final String DINING_HALL = "diningHall";
    public static final String BPLATE = "BPlate";
    public static final String COVEL = "Covel";
    public static final String DENEVE = "DeNeve";
    public static final String FEAST = "Feast";
    public static final String TOKEN = "regToken";
    public static final String TRANSACTIONS = "transactions";

    public static final Integer BPLATE_ID = 1;
    public static final Integer COVEL_ID = 2;
    public static final Integer DENEVE_ID = 4;
    public static final Integer FEAST_ID = 8;

    public class Rating {
        public Double RatingSum;
        public int NOR;

        public Rating(Double sum, int nor) {
            RatingSum = sum;
            NOR = nor;
        }

        public Rating() {
            RatingSum = 0.0;
            NOR = 0;
        }
    }

    public class Transaction {
        private Swipe swipe;
        private String seller_ID;
        private String buyer_ID;

        public Transaction(Swipe swipe, String buyer_ID, String seller_ID) {
            this.swipe = swipe;
            this.buyer_ID = buyer_ID;
            this.seller_ID = seller_ID;
        }
    }

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
     *       transactions
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
    private Task<Void> mTask;
    private ArrayList<Swipe> mSwipes = new ArrayList<Swipe>();
    private Double mUserRatingSum;
    private int mUserNOR;


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

    /**
     * Saves the specified transaction to the application's database.
     *
     * @param t   The Transaction object to be written to Firebase
     * @param uid The ID of the user to store the transaction for
     * @return    A task that represents the completion of the operation to add the transaction
     * @see Transaction
     */
    public Task<Void> addTransaction(Transaction t, String uid) {
        return mDatabase.child(ALL_USERS).child(uid).child(TRANSACTIONS).push().setValue(t);
    }

    /**
     * Deletes the specified swipe from the application's database.
     *
     * Implementation Note: This method is called in AsyncTask to avoid obstructing the user's
     * experience.
     *
     * @param uid      The user ID associated with the swipe to remove
     * @param postTime The datetime that the swipe was posted
     * @param type     The type of swipe post
     * @see RemoveSwipeTask
     */
    public void removeSwipe(final String uid, final Long postTime, Swipe.Type type) {
        SwipeID id = new SwipeID(uid, postTime, type);
        RemoveSwipeTask t = new RemoveSwipeTask();
        t.execute(id);
    }

    private class SwipeID {
        private String mUid;
        private Long mPostTime;
        private Swipe.Type mType;

        public SwipeID() {}

        public SwipeID(String uid, Long postTime, Swipe.Type type) {
            this.mUid = uid;
            this.mPostTime = postTime;
            this.mType = type;
        }

        public String getUid() {
            return mUid;
        }

        public Long getPostTime() {
            return mPostTime;
        }

        public Swipe.Type getType() {
            return mType;
        }
    }

    private class RemoveSwipeTask extends AsyncTask<SwipeID, Void, Task<Void>> {
        @Override
        protected Task<Void> doInBackground(SwipeID... params) {
            final SwipeID id = params[0];
            final String uid = id.getUid();
            final Long postTime = id.getPostTime();
            Swipe.Type type = id.getType();

            Query qSwipeRef = null;
            if (type == Swipe.Type.SALE)
                qSwipeRef = mDatabase.child(ALL_SWIPES).orderByChild("postTime").startAt(postTime).endAt(postTime);
            else // type == Swipe.Type.SALE
                qSwipeRef = mDatabase.child(ALL_REQUESTS).orderByChild("postTime").startAt(postTime).endAt(postTime);

            qSwipeRef.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    HashMap<String, Object> swipes = (HashMap<String, Object>)dataSnapshot.getValue();
                    if (swipes == null) {
                        // No such swipe found
                        return;
                    }

                    for (String keyObj : swipes.keySet()) {
                        HashMap<String, Object> attributes = (HashMap<String, Object>)swipes.get(keyObj);
                        String ownerID = (String)attributes.get("owner_ID");
                        Long targetPostTime = (Long)attributes.get("postTime");
                        Integer diningHall = ((Long)attributes.get("diningHall")).intValue();
                        if (ownerID.equals(uid) && targetPostTime.compareTo(postTime) == 0)
                            mTask = dataSnapshot.getRef().child(keyObj).setValue(null);
                        mTask = removeDiningHallSwipe(id, diningHall);
                    }
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });

            Query qUserRef =
                    mDatabase.child(ALL_USERS).child(uid).child(ALL_SWIPES).orderByChild("postTime").startAt(postTime).endAt(postTime);
            qUserRef.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    HashMap<String, Object> swipes = (HashMap<String, Object>)dataSnapshot.getValue();
                    if (swipes == null) {
                        // No such swipe found
                        return;
                    }

                    for (String keyObj : swipes.keySet()) {
                        HashMap<String, Object> attributes = (HashMap<String, Object>)swipes.get(keyObj);
                        String ownerID = (String)attributes.get("owner_ID");
                        Long targetPostTime = (Long)attributes.get("postTime");
                        if (ownerID.equals(uid) && targetPostTime.compareTo(postTime) == 0)
                            mTask = dataSnapshot.getRef().child(keyObj).setValue(null);
                    }
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });

            return mTask;
        }
    }

    private Task<Void> removeDiningHallSwipe(SwipeID id, int diningHall) {
        final String uid = id.getUid();
        final Long postTime = id.getPostTime();
        final Swipe.Type type = id.getType();

        ValueEventListener vel = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                HashMap<String, Object> swipes = (HashMap<String, Object>) dataSnapshot.getValue();
                if (swipes == null) {
                    // No such swipe found
                    return;
                }

                for (String keyObj : swipes.keySet()) {
                    HashMap<String, Object> attributes = (HashMap<String, Object>) swipes.get(keyObj);
                    String ownerID = (String) attributes.get("owner_ID");
                    Long targetPostTime = (Long) attributes.get("postTime");

                    if (ownerID.equals(uid) && targetPostTime.compareTo(postTime) == 0) {
                        dataSnapshot.getRef().child(keyObj).setValue(null);
                    }
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        };

        Query qRef = null;
        DatabaseReference dRef = mDatabase.child(DINING_HALL);
        if ((diningHall & BPLATE_ID) == BPLATE_ID) {
            if (type == Swipe.Type.SALE)
                qRef = dRef.child(BPLATE).child(ALL_SWIPES).orderByChild("postTime").startAt(postTime).endAt(postTime);
            else
                qRef = dRef.child(BPLATE).child(ALL_REQUESTS).orderByChild("postTime").startAt(postTime).endAt(postTime);
            qRef.addListenerForSingleValueEvent(vel);
        }
        if ((diningHall & COVEL_ID) == COVEL_ID) {
            if (type == Swipe.Type.SALE)
                qRef = dRef.child(COVEL).child(ALL_SWIPES).orderByChild("postTime").startAt(postTime).endAt(postTime);
            else
                qRef = dRef.child(COVEL).child(ALL_REQUESTS).orderByChild("postTime").startAt(postTime).endAt(postTime);
            qRef.addListenerForSingleValueEvent(vel);
        }
        if ((diningHall & DENEVE_ID) == DENEVE_ID) {
            if (type == Swipe.Type.SALE)
                qRef = dRef.child(DENEVE).child(ALL_SWIPES).orderByChild("postTime").startAt(postTime).endAt(postTime);
            else
                qRef = dRef.child(DENEVE).child(ALL_REQUESTS).orderByChild("postTime").startAt(postTime).endAt(postTime);
            qRef.addListenerForSingleValueEvent(vel);
        }
        if ((diningHall & FEAST_ID) == FEAST_ID) {
            if (type == Swipe.Type.SALE)
                qRef = dRef.child(FEAST).child(ALL_SWIPES).orderByChild("postTime").startAt(postTime).endAt(postTime);
            else
                qRef = dRef.child(FEAST).child(ALL_REQUESTS).orderByChild("postTime").startAt(postTime).endAt(postTime);
            qRef.addListenerForSingleValueEvent(vel);
        }

        return mTask;
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

    public Task<Void> setUserRatingSum(String uid, Double Rating) {
        return mDatabase.child(ALL_USERS).child(uid).child(RATINGSUM).setValue(Rating);
    }

    public Task<Void> setUserNOR(String uid, int nor) {
        return mDatabase.child(ALL_USERS).child(uid).child(NOR).setValue(nor);
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
     * Returns a listing of all swipe sales or requests associated with the specified user.
     *
     * Implementation Note: This method is considered unstable.  It may sometimes not return a
     * non-null value.
     *
     * @param uid  ID of the user to get all swipes from
     * @param type The type of swipe post to retrieve
     * @return     A list of all the swipes associated with a particular user
     */
    public ArrayList<Swipe> getAllSwipesByUser(String uid, final Swipe.Type type) {
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

                    mSwipes.add(new Swipe(price, startTime, endTime, ownerId, diningHall, type));
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

    public Rating getUserRating(String uid) {

        DatabaseReference ref = mDatabase.child(ALL_USERS).child(uid);
        ref.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                mUserRatingSum = dataSnapshot.child(RATINGSUM).getValue(Double.class);
                mUserNOR = dataSnapshot.child(NOR).getValue(Integer.class);
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
        return new Rating(mUserRatingSum, mUserNOR);
    }
}
