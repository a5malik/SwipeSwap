package com.example.tsleeve.swipeswap;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.TextView;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import org.w3c.dom.Text;

import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;

public class NotificationActivity extends AppCompatActivity {

    public static class NotifViewHolder extends RecyclerView.ViewHolder {
        View mView;
        SwipeDataAuth mDb = new SwipeDataAuth();
        UserAuth mUAuth = new UserAuth();

        public NotifViewHolder(View itemView) {
            super(itemView);
            mView = itemView;
        }

        public void setEverything(final Notif notif, final int position, final Context context, final FirebaseRecyclerAdapter<Notif, NotifViewHolder> adapter) {
            TextView tvTitle = (TextView) mView.findViewById(R.id.textViewNotifTitle);
            final TextView tvBody = (TextView) mView.findViewById(R.id.textViewNotifBody);

            tvTitle.setText(Notification.get_title(notif.getM_type()));
            final Swipe swipe = notif.getSwipe();
            DatabaseReference ref = mDb.getUserReference(notif.getFromUser());
            ref.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(final DataSnapshot dataSnapshot) {
                    String username = dataSnapshot.child(SwipeDataAuth.USERNAME).getValue(String.class);
                    tvBody.setText(Notification.get_message(username, notif.getM_type()));

                    mView.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(context);
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
                            switch (notif.getM_type()) {

                                case ACCEPTED_SALE:
                                    alertDialogBuilder.setMessage(String.format("%s (%.2f) wants to buy your swipe for %s on %s for %s",
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
                                            Notification n = new Notification(context, mUAuth.uid(),
                                                    notif.getFromUser(),
                                                    Notification.Message.ACK_SALE, swipe
                                            );
                                            mUAuth.sendNotification(n);

                                            Notif newNotif = new Notif(mUAuth.uid(), Notification.Message.ACK_SALE, swipe);
                                            mDb.addNotif(newNotif, notif.getFromUser());


                                            //1.
                                            setupRatingNotification(notif.getFromUser(),
                                                    swipe.getStartTime(), context);

                                            //3.
                                            mDb.removeSwipe(mUAuth.uid(), notif.getPostTime(), Swipe.Type.SALE);

                                            //4.
                                            SMSIntent(context, phoneNumber, username, swipeTimeofDay, swipeDate, swipePrice);


                                        }
                                    });
                                    alertDialogBuilder.setNegativeButton("Reject", new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {
                                            //send buyer the notification that the seller has rejected(ACK_NO_BUYER)
                                            Notification n = new Notification(context, mUAuth.uid(),
                                                    notif.getFromUser(),
                                                    Notification.Message.REJECTED_SALE, swipe
                                            );
                                            mUAuth.sendNotification(n);

                                            Notif newNotif = new Notif(mUAuth.uid(), Notification.Message.REJECTED_SALE, swipe);
                                            mDb.addNotif(newNotif, notif.getFromUser());

                                        }
                                    });
                                    break;
                                case ACCEPTED_REQUEST:
                                    alertDialogBuilder.setMessage(String.format("%s (%.2f) would like to sell you a swipe " +
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
                                            Notification n = new Notification(context, mUAuth.uid(),
                                                    notif.getFromUser(),
                                                    Notification.Message.ACK_REQUEST, swipe
                                            );
                                            mUAuth.sendNotification(n);

                                            Notif newNotif = new Notif(mUAuth.uid(), Notification.Message.ACK_REQUEST, swipe);
                                            mDb.addNotif(newNotif, notif.getFromUser());

                                            //1.
                                            setupRatingNotification(notif.getFromUser(),
                                                    swipe.getStartTime(), context);

                                            //3.
                                            mDb.removeSwipe(mUAuth.uid(), swipe.getPostTime(), Swipe.Type.REQUEST);


                                        }
                                    });
                                    alertDialogBuilder.setNegativeButton("Reject", new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {
                                            //send seller the notification that the buyer has rejected(ACK_NO_SELLER)
                                            Notification n = new Notification(context, mUAuth.uid(),
                                                    notif.getFromUser(),
                                                    Notification.Message.REJECTED_REQUEST, swipe
                                            );
                                            mUAuth.sendNotification(n);

                                            Notif newNotif = new Notif(mUAuth.uid(), Notification.Message.REJECTED_REQUEST, swipe);
                                            mDb.addNotif(newNotif, notif.getFromUser());


                                        }
                                    });

                                    break;
                                case ACK_SALE:
                                    alertDialogBuilder.setMessage(String.format("%s (%.2f) has agreed to sell to you a swipe " +
                                            "at %s on %s for %s", username, Rating, swipeTimeofDay, swipeDate, swipePrice));
                                    alertDialogBuilder.setPositiveButton("Got It!", new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {
                                            //Things to do:
                                            //1. start the alarm to send notifications to 30 minutes after swipe time.
                                            //3. redirect to messaging

                                            //1.
                                            setupRatingNotification(notif.getFromUser(),
                                                    swipe.getStartTime(), context);


                                        }
                                    });
                                    break;
                                case ACK_REQUEST:
                                    alertDialogBuilder.setMessage(String.format("%s (%.2f) has agreed to buy the swipe " +
                                            "at %s on %s for %s", username, Rating, swipeTimeofDay, swipeDate, swipePrice));
                                    alertDialogBuilder.setPositiveButton("Got It!", new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {
                                            //Things to do:
                                            //1. start the alarm to send notifications to 30 minutes after swipe time.
                                            //2. redirect to messaging

                                            //1.
                                            setupRatingNotification(notif.getFromUser(),
                                                    swipe.getStartTime(), context);
                                            //2.
                                            SMSIntent(context, phoneNumber, username, swipeTimeofDay, swipeDate, swipePrice);

                                        }
                                    });
                                    break;

                                case REJECTED_SALE:
                                    alertDialogBuilder.setMessage(String.format("%s (%.2f) has rejected to sell to you a swipe " +
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
                                    alertDialogBuilder.setMessage(String.format("%s (%.2f) has rejected to buy from you a swipe " +
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
                            adapter.getRef(position).removeValue();
                        }
                    });

                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });

        }
    }

    SwipeDataAuth mDb = new SwipeDataAuth();
    UserAuth mUAuth = new UserAuth();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notification);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);

        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(NotificationActivity.this, MainActivity.class));
            }
        });

        TextView tvHeader = (TextView) findViewById(R.id.textViewNotifHeader);
        tvHeader.setText("Click to view details");

        RecyclerView recyclerViewNotifications = (RecyclerView) findViewById(R.id.day_recycler_view_notifs);
        recyclerViewNotifications.setLayoutManager(new LinearLayoutManager(this));
        DatabaseReference ref = mDb.getUserReference(mUAuth.uid()).child(SwipeDataAuth.NOTIF);
        final FirebaseRecyclerAdapter adapterNotifs = new FirebaseRecyclerAdapter<Notif, NotifViewHolder>(Notif.class, R.layout.notif_view, NotifViewHolder.class, ref) {
            @Override
            protected void populateViewHolder(NotifViewHolder notifviewHolder, Notif notif, int position) {
                notifviewHolder.setEverything(notif, position, NotificationActivity.this, this);
                //getRef(position).removeValue();
            }
        };
        adapterNotifs.notifyDataSetChanged();
        recyclerViewNotifications.setAdapter(adapterNotifs);

    }

    public static void setupRatingNotification(String UID_to_Rate, Long startTime, Context context) {
        AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);

        Intent notificationIntent = new Intent("android.media.action.DISPLAY_NOTIFICATION");
        notificationIntent.putExtra("user_ID", UID_to_Rate);
        notificationIntent.addCategory("android.intent.category.DEFAULT");

        PendingIntent broadcast = PendingIntent.getBroadcast(context, 100, notificationIntent, PendingIntent.FLAG_UPDATE_CURRENT);

        Calendar cal = Calendar.getInstance();
        cal.setTimeInMillis(startTime);
        cal.add(Calendar.SECOND, 15);
        //cal.add(Calendar.MINUTE, 30);
        alarmManager.setExact(AlarmManager.RTC_WAKEUP, cal.getTimeInMillis(), broadcast);
    }

    public static void SMSIntent(final Context context, final String phoneNumber, final String username, final String swipeTimeofDay,
                                 final String swipeDate, final String swipePrice) {
        SwipeDataAuth mDb = new SwipeDataAuth();
        UserAuth mUAuth = new UserAuth();
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

}
