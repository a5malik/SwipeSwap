package com.example.tsleeve.swipeswap;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.app.FragmentManager;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

import com.firebase.ui.auth.AuthUI;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.messaging.RemoteMessage;

import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;

import static android.R.id.message;

public class MainActivity extends AppCompatActivity {

    private static final int RC_SIGN_IN = 100;
    public static final String TYPE_OF_INTENT = "TOI";

    public static enum TYPE {
        RATE_SELLER, //buyer will get this to rate the seller
        RATE_BUYER,  // seller will get this to rate the buyer
        ACCEPT_BUYER, //seller will get this when a buyer is interested in a swipe
        ACCEPT_SELLER, //buyer will get this when a seller wants to respond to a request
        ACK_BUYER, //buyer will get this when a seller has confirmed he wants to sell to him
        ACK_SELLER, //seller will get this when a buyer has confirmed he will buy from him
        ACK_NO_BUYER, //buyer will get this when a seller has rejected to sell to him
        ACK_NO_SELLER, //seller will get this when a buyer has rejected to buy from him
        NONE
    }
    //private Button dateButton;
    private TabLayout tabLayout;
    private UserAuth mUAuth = new UserAuth();
    /*private BroadcastReceiver mReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            Bundle extras = intent.getExtras();
            if (extras.getString("action").equals(SwipeMessagingService.CONFIRM_FRAGMENT)) {
                // TODO: Show fragment to confirm acceptance of the swipe sale/request

            } else if (extras.getString("action").equals(SwipeMessagingService.MESSAGING_FRAGMENT)) {
                // TODO: Show fragment to connect user to chat room

            } else if (extras.getString("action").equals(SwipeMessagingService.REVIEW_FRAGMENT)) {
                // TODO: Show fragment to review the user

            }
        }
    };*/
    private SwipeDataAuth mDb = new SwipeDataAuth();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (!mUAuth.validUser()) {
            Intent intent = AuthUiActivity.createIntent(this);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(intent);
            finish();
        }
        ViewPager viewPager = (ViewPager) findViewById(R.id.main_view_pager);
        viewPager.setAdapter(new MainFragmentPagerAdapter(getSupportFragmentManager(), MainActivity.this));
        viewPager.setOffscreenPageLimit(3);
        tabLayout = (TabLayout) findViewById(R.id.main_tab_layout);
        tabLayout.setupWithViewPager(viewPager);
        setTabIcons(0);
        tabLayout.setOnTabSelectedListener(
                new TabLayout.ViewPagerOnTabSelectedListener(viewPager) {

                    @Override
                    public void onTabSelected(TabLayout.Tab tab) {
                        super.onTabSelected(tab);
                        setTabIcons(tab.getPosition());
                    }
                }
        );

        /*LocalBroadcastManager.getInstance(this).registerReceiver(mReceiver,
                new IntentFilter("broadcaster"));*/
        mDb.updateToken(FirebaseInstanceId.getInstance().getToken(), mUAuth.uid());


        //deal with different types of intents here.
        if (getIntent().getExtras() != null) {
            //TYPE intentType = TYPE.values()[getIntent().getExtras().getInt(TYPE_OF_INTENT, 6)];
            final Bundle b = getIntent().getExtras();
            final Notification.Message notifType = Notification.Message.valueOf(b.getString("swipe_notification_type"));
            Log.d("notiftype", Integer.toString(notifType.ordinal()));
            if (notifType == Notification.Message.REVIEW_BUYER || notifType == Notification.Message.REVIEW_SELLER) {
                Log.d("REVIEW", b.getString("user_ID"));
                final String UID = b.getString("user_ID");
                DatabaseReference ref = mDb.getUserReference(UID);
                ref.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        RateDialogFragment rateDialogFragment = new RateDialogFragment();

                        Bundle args = new Bundle();

                        args.putString("user_ID", UID);

                        Double sum = 0.0;

                        if (dataSnapshot.child(SwipeDataAuth.RATINGSUM).getValue() != null)
                            sum = dataSnapshot.child(SwipeDataAuth.RATINGSUM).getValue(Double.class);

                        int NOR = 0;

                        if (dataSnapshot.child(SwipeDataAuth.NOR).getValue() != null)
                            NOR = dataSnapshot.child(SwipeDataAuth.NOR).getValue(Integer.class);

                        args.putString("user_name", dataSnapshot.child(SwipeDataAuth.USERNAME).getValue(String.class));
                        args.putDouble("rating_sum", sum);
                        args.putInt("NOR", NOR);

                        rateDialogFragment.setArguments(args);
                        rateDialogFragment.setCancelable(false);
                        rateDialogFragment.show(getFragmentManager(), "RateDialog");
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });

            } else {
                String UID = b.getString("swipe_initiating_user_ID");
                Log.d("UID", UID);
                final Swipe swipe = new Swipe(Double.parseDouble(b.getString("swipe_price")),
                        Long.parseLong(b.getString("swipe_startTime")),
                        Long.parseLong(b.getString("swipe_endTime")),
                        b.getString("swipe_owner_ID"),
                        Integer.parseInt(b.getString("swipe_diningHall")),
                        Swipe.Type.SALE);
                DatabaseReference ref = mDb.getUserReference(UID);
                ref.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(MainActivity.this);
                        AlertDialog alertDialog;

                        final String username = dataSnapshot.child(SwipeDataAuth.USERNAME).getValue(String.class);
                        final String swipeDate, swipeTimeofDay;
                        NumberFormat formatter = NumberFormat.getCurrencyInstance();
                        final String swipePrice = formatter.format(swipe.getPrice());
                        Calendar cal = Calendar.getInstance();
                        cal.setTimeInMillis(swipe.getStartTime());
                        swipeDate = new SimpleDateFormat("EEE, MMM d").format(cal.getTime());
                        swipeTimeofDay = new SimpleDateFormat("h:mm a").format(cal.getTime());
                        final String phoneNumber = dataSnapshot.child(SwipeDataAuth.PHONENO).getValue(String.class);

                        Double sum = 0.0;
                        if (dataSnapshot.child(SwipeDataAuth.RATINGSUM).getValue() != null)
                            sum = dataSnapshot.child(SwipeDataAuth.RATINGSUM).getValue(Double.class);

                        int NOR = 1;
                        if (dataSnapshot.child(SwipeDataAuth.NOR).getValue() != null)
                            NOR = dataSnapshot.child(SwipeDataAuth.NOR).getValue(Integer.class);
                        if (NOR == 0) NOR = 1;
                        double Rating = sum / NOR;
                        switch (notifType) {

                            case ACCEPTED_SALE:
                                alertDialogBuilder.setMessage(String.format("%s (%f)wants to buy your swipe for %s on %s for %s",
                                        username, Rating, swipeTimeofDay, swipeDate, swipePrice));
                                alertDialogBuilder.setPositiveButton("Accept", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        //Things to do:
                                        //1. start the alarm to send notifications to 30 minutes after swipe time.
                                        //2. send the notification to the buyer(ACK_BUYER)
                                        //3. remove swipe, add transaction to both users
                                        //4. redirect to messaging

                                        //2.
                                        Notification n = new Notification(MainActivity.this, mUAuth.uid(),
                                                b.getString("swipe_initiating_user_ID"),
                                                Notification.Message.ACK_SALE, swipe
                                        );
                                        mUAuth.sendNotification(n);

                                        //1.
                                        setupRatingNotification(b.getString("swipe_initiating_user_ID"),
                                                swipe.getStartTime());

                                        //3.
                                        mDb.removeSwipe(mUAuth.uid(), Long.parseLong(b.getString("swipe_postTime")), Swipe.Type.SALE);

                                        //4.
                                        SMSIntent(MainActivity.this, phoneNumber, username, swipeTimeofDay, swipeDate, swipePrice);


                                    }
                                });
                                alertDialogBuilder.setNegativeButton("Reject", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        //send buyer the notification that the seller has rejected(ACK_NO_BUYER)
                                        Notification n = new Notification(MainActivity.this, mUAuth.uid(),
                                                b.getString("swipe_initiating_user_ID"),
                                                Notification.Message.REJECTED_SALE, swipe
                                        );
                                        mUAuth.sendNotification(n);

                                    }
                                });
                                break;
                            case ACCEPTED_REQUEST:
                                alertDialogBuilder.setMessage(String.format("%s (%f) would like to sell you a swipe " +
                                        "at %s on %s for %s", username, Rating, swipeTimeofDay, swipeDate, swipePrice));
                                alertDialogBuilder.setPositiveButton("Accept", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        //Things to do:
                                        //1. start the alarm to send notifications to 30 minutes after swipe time.
                                        //2. send the notification to the seller(ACK_SELLER)
                                        //3. remove request, add transaction to both users
                                        //4. redirect to messaging

                                        //2.
                                        Notification n = new Notification(MainActivity.this, mUAuth.uid(),
                                                b.getString("swipe_initiating_user_ID"),
                                                Notification.Message.ACK_REQUEST, swipe
                                        );
                                        mUAuth.sendNotification(n);

                                        //1.
                                        setupRatingNotification(b.getString("swipe_initiating_user_ID"),
                                                swipe.getStartTime());

                                        //3.
                                        mDb.removeSwipe(mUAuth.uid(), Long.parseLong(b.getString("swipe_postTime")), Swipe.Type.REQUEST);


                                    }
                                });
                                alertDialogBuilder.setNegativeButton("Reject", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        //send seller the notification that the buyer has rejected(ACK_NO_SELLER)
                                        Notification n = new Notification(MainActivity.this, mUAuth.uid(),
                                                b.getString("swipe_initiating_user_ID"),
                                                Notification.Message.REJECTED_REQUEST, swipe
                                        );
                                        mUAuth.sendNotification(n);


                                    }
                                });

                                break;
                            case ACK_SALE:
                                alertDialogBuilder.setMessage(String.format("%s (%f) has agreed to sell to you a swipe " +
                                        "at %s on %s for %s", username, Rating, swipeTimeofDay, swipeDate, swipePrice));
                                alertDialogBuilder.setPositiveButton("Got It!", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        //Things to do:
                                        //1. start the alarm to send notifications to 30 minutes after swipe time.
                                        //3. redirect to messaging

                                        //1.
                                        setupRatingNotification(b.getString("swipe_initiating_user_ID"),
                                                swipe.getStartTime());


                                    }
                                });
                                break;
                            case ACK_REQUEST:
                                alertDialogBuilder.setMessage(String.format("%s (%f) has agreed to buy the swipe " +
                                        "at %s on %s for %s", username, Rating, swipeTimeofDay, swipeDate, swipePrice));
                                alertDialogBuilder.setPositiveButton("Got It!", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        //Things to do:
                                        //1. start the alarm to send notifications to 30 minutes after swipe time.
                                        //2. redirect to messaging

                                        //1.
                                        setupRatingNotification(b.getString("swipe_initiating_user_ID"),
                                                swipe.getStartTime());
                                        //2.
                                        SMSIntent(MainActivity.this, phoneNumber, username, swipeTimeofDay, swipeDate, swipePrice);

                                    }
                                });
                                break;

                            case REJECTED_SALE:
                                alertDialogBuilder.setMessage(String.format("%s (%f) has rejected to sell to you a swipe " +
                                        "at %s on %s for %s", username, Rating, swipeTimeofDay, swipeDate, swipePrice));
                                alertDialogBuilder.setPositiveButton("Their Loss!", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        //Things to do:
                                        //Nothing
                                    }
                                });
                                break;
                            case REJECTED_REQUEST:
                                alertDialogBuilder.setMessage(String.format("%s (%f) has rejected to buy from you a swipe " +
                                        "at %s on %s for %s", username, Rating, swipeTimeofDay, swipeDate, swipePrice));
                                alertDialogBuilder.setPositiveButton("Their Loss!", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        //Things to do:
                                        //Nothing
                                    }
                                });
                                break;
                            default:
                                // TODO
                                break;

                            }
                        alertDialog = alertDialogBuilder.create();
                        alertDialog.setCanceledOnTouchOutside(false);
                        alertDialog.show();
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });

                }

            }
        }

    private void setupRatingNotification(String UID_to_Rate, Long startTime) {
        AlarmManager alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);

        Intent notificationIntent = new Intent("android.media.action.DISPLAY_NOTIFICATION");
        notificationIntent.putExtra("user_ID", UID_to_Rate);
        notificationIntent.addCategory("android.intent.category.DEFAULT");

        PendingIntent broadcast = PendingIntent.getBroadcast(MainActivity.this, 100, notificationIntent, PendingIntent.FLAG_UPDATE_CURRENT);

        Calendar cal = Calendar.getInstance();
        cal.setTimeInMillis(startTime);
        cal.add(Calendar.SECOND, 15);
        //cal.add(Calendar.MINUTE, 30);
        alarmManager.setExact(AlarmManager.RTC_WAKEUP, cal.getTimeInMillis(), broadcast);
    }

    public void SMSIntent(final Context context, final String phoneNumber, final String username, final String swipeTimeofDay,
                          final String swipeDate, final String swipePrice) {

        DatabaseReference ref = mDb.getUserReference(mUAuth.uid());
        ref.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Intent smsIntent = new Intent(Intent.ACTION_VIEW);

                // smsIntent.setData(Uri.parse("smsto:"));
                smsIntent.setType("vnd.android-dir/mms-sms");
                smsIntent.putExtra("address", phoneNumber);
                String venmoID = dataSnapshot.child(SwipeDataAuth.VENMOID).getValue(String.class);

                Uri uri = Uri.parse("smsto:" + phoneNumber);
                // Create intent with the action and data
                Intent smsIntent1 = new Intent(Intent.ACTION_SENDTO, uri);

                String msg = String.format("Hi, I am your seller for the swipe " +
                        "at %s on %s for %s. How would you like to pay me? " +
                        "My venmo ID is %s.", swipeTimeofDay, swipeDate, swipePrice, venmoID);
                smsIntent1.putExtra("sms_body", msg);

                context.startActivity(smsIntent1);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

    }


    private void makeAlertDialog(String message) {
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);
        alertDialogBuilder.setMessage(message);
        alertDialogBuilder.setPositiveButton("Accept", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                //Things to do:
                //1. start the alarm to send notifications to 30 minutes after swipe time.
                //2. send the notification to the buyer(ACK_BUYER)
                //3. redirect to messaging
            }
        });
        alertDialogBuilder.setNegativeButton("Reject", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                //send buyer the notification that the seller has rejected(ACK_NO_BUYER)

            }
        });
        AlertDialog alertDialog = alertDialogBuilder.create();
        alertDialog.show();
    }

    private void setTabIcons(int tabposition) {
        tabLayout.getTabAt(0).setIcon(R.drawable.calendar_icon);
        tabLayout.getTabAt(1).setIcon(R.drawable.message_icon);
        tabLayout.getTabAt(2).setIcon(R.drawable.profile_icon);
        if(tabposition == 0){
            tabLayout.getTabAt(0).setIcon(R.drawable.calendar_icon_highlighted);
        }
        else if(tabposition == 1){
            tabLayout.getTabAt(1).setIcon(R.drawable.message_icon_highlighted);
        }
        else if(tabposition == 2){
            tabLayout.getTabAt(2).setIcon(R.drawable.profile_icon_highlighted);
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private void handleSignInResponse(int resultCode, Intent data) {
        if (resultCode == RESULT_OK) {
            Intent intent = getIntent();
            finish();
            startActivity(intent);
            return;
        }

        if (resultCode == RESULT_CANCELED) {
            startActivityForResult(
                    AuthUI.getInstance().createSignInIntentBuilder()
                            .build(),
                    RC_SIGN_IN);
            return;
        }

    }

    public static Intent createIntent(Context context) {
        Intent in = new Intent();
        in.setClass(context, MainActivity.class);
        return in;
    }

    public void showAddSwipe(){
        AddSwipeDialogFragment addSwipeDialogFragment = new AddSwipeDialogFragment();
        addSwipeDialogFragment.show(getFragmentManager(), "ADD_SWIPE_FRAGMENT");
    }

}
