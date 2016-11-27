package com.example.tsleeve.swipeswap;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.firebase.database.Query;

import java.text.SimpleDateFormat;
import java.util.Calendar;

/**
 * Created by footb on 10/19/2016.
 */

public class CalendarDayFragment extends Fragment {
    protected RecyclerView recyclerViewSwipes;
    protected RecyclerView recyclerViewRequests;
    protected FirebaseRecyclerAdapter mAdapterSwipes;
    protected FirebaseRecyclerAdapter mAdapterRequests;
    Long mStartTime, mEndTime;
    //private DatabaseReference mRef;
    protected SwipeDataAuth mDb = new SwipeDataAuth();
    public final static String DATE_TO_SHOW = "DATE_TO_SHOW";
    public final static String TARGETSWIPES = "TargetSWIPES";
    public final static String TARGETREQUESTS = "TargetREQUESTS";
    public final static Long TODAY = new Long(0);
    protected String mTargetSwipes = SwipeDataAuth.ALL_SWIPES;
    protected String mTargetRequests = SwipeDataAuth.ALL_REQUESTS;

    public static CalendarDayFragment getInstance(Long time, String targetSwipes, String targetRequests) {
        Bundle bundle = new Bundle();
        bundle.putLong(CalendarDayFragment.DATE_TO_SHOW, time);
        bundle.putString(CalendarDayFragment.TARGETSWIPES, targetSwipes);
        bundle.putString(CalendarDayFragment.TARGETREQUESTS, targetRequests);
        CalendarDayFragment fragment = new CalendarDayFragment();
        fragment.setArguments(bundle);
        return fragment;
    }

    public static class SwipeViewHolder extends RecyclerView.ViewHolder {
        View mView;
        SwipeDataAuth mDb = new SwipeDataAuth();
        UserAuth mUAuth = new UserAuth();
        private final Context context;

        public SwipeViewHolder(View itemView) {
            super(itemView);
            mView = itemView;
            context = itemView.getContext();
        }

        public void setEverything(final Swipe swipe, int position, String Title, final Context context) {
            TextView tvTitle = (TextView) mView.findViewById(R.id.textViewTitle);
            tvTitle.setText(Title + " " + Integer.toString(position) + " for $" + Double.toString(swipe.getPrice()));
            //final TextView tvownerid = (TextView) mView.findViewById(R.id.textViewowner_id);
            //tvownerid.setText(swipe.getOwner_ID());

            TextView tvdininghall = (TextView) mView.findViewById(R.id.textViewdininghall);
            String diningHallString = "";
            int diningHall = swipe.getDiningHall();
            int count = 0;
            if ((diningHall & SwipeDataAuth.BPLATE_ID) == SwipeDataAuth.BPLATE_ID) {
                diningHallString += "Bruin Plate Dining Hall";
                count++;
            }
            if ((diningHall & SwipeDataAuth.COVEL_ID) == SwipeDataAuth.COVEL_ID) {
                if (count > 0) diningHallString += "\n";
                diningHallString += "Covel Dining Hall";
                count++;
            }
            if ((diningHall & SwipeDataAuth.DENEVE_ID) == SwipeDataAuth.DENEVE_ID) {
                if (count > 0) diningHallString += "\n";
                diningHallString += "De Neve Dining Hall";
                count++;
            }
            if ((diningHall & SwipeDataAuth.FEAST_ID) == SwipeDataAuth.FEAST_ID) {
                if (count > 0) diningHallString += "\n";
                diningHallString += "Feast Dining Hall";
                count++;
            }
            if (diningHallString.length() == 0)
                diningHallString = "No Dining Halls Selected.";
            tvdininghall.setText(diningHallString);

            String startDayofMonth, startDayofWeek, startTimeofDay, startMonth;
            String endTimeofDay;

            Calendar cal = Calendar.getInstance();
            cal.setTimeInMillis(swipe.getStartTime());
            startDayofMonth = Integer.toString(cal.get(Calendar.DAY_OF_MONTH));
            startDayofWeek = new SimpleDateFormat("EEE").format(cal.getTime());
            startTimeofDay = new SimpleDateFormat("h:mm a").format(cal.getTime());

            cal.setTimeInMillis(swipe.getEndTime());
            endTimeofDay = new SimpleDateFormat("h:mm a").format(cal.getTime());

            TextView tvDate = (TextView) mView.findViewById(R.id.textViewDate);
            tvDate.setText(startDayofMonth);

            TextView tvDay = (TextView) mView.findViewById(R.id.textViewDay);
            tvDay.setText(startDayofWeek);

            TextView tvtime = (TextView) mView.findViewById(R.id.textViewTime);
            tvtime.setText(startTimeofDay + "—" + endTimeofDay);
            CircleImageView profile = (CircleImageView) mView.findViewById(R.id.circleImageViewProfile);
            profile.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(context, OtherProfileActivity.class);
                    intent.putExtra("uid", swipe.getOwner_ID());
                    context.startActivity(intent);


                }
            });

            mView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    /*
                    CharSequence text = "Owner has been notified of your interest!";
                    int duration = Toast.LENGTH_SHORT;

                    Toast toast = Toast.makeText(context, text, duration);
                    toast.show();
                    Notification n;
                    Notif notif;
                    if (swipe.getType() == Swipe.Type.SALE) {
                        n = new Notification(context, mUAuth.uid(), swipe.getOwner_ID(), Notification.Message.ACCEPTED_SALE, swipe);
                        notif = new Notif(mUAuth.uid(), Notification.Message.ACCEPTED_SALE, swipe);
                    } else {
                        notif = new Notif(mUAuth.uid(), Notification.Message.ACCEPTED_REQUEST, swipe);
                        n = new Notification(context, mUAuth.uid(), swipe.getOwner_ID(), Notification.Message.ACCEPTED_REQUEST, swipe);
                    }
                    mDb.addNotif(notif, swipe.getOwner_ID());
                    mUAuth.sendNotification(n);*/

                }
            });
        }
    }

    public CalendarDayFragment() {
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Bundle bundle = this.getArguments();
        Long time = TODAY;
        if (bundle != null) {
            time = bundle.getLong(DATE_TO_SHOW, TODAY);
            mTargetSwipes = bundle.getString(TARGETSWIPES);
            mTargetRequests = bundle.getString(TARGETREQUESTS);
        }

        setStartandEndTimes(time);

    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.calendar_day_fragment, container, false);
        recyclerViewSwipes = (RecyclerView) view.findViewById(R.id.day_recycler_view_swipes);
        recyclerViewRequests = (RecyclerView) view.findViewById(R.id.day_recycler_view_requests);
        return view;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        //recyclerView.setHasFixedSize(true);
        recyclerViewSwipes.setLayoutManager(new LinearLayoutManager(this.getActivity()));
        recyclerViewRequests.setLayoutManager(new LinearLayoutManager(this.getActivity()));

        //mRef = FirebaseDatabase.getInstance().getReference().child("swipes");
        Query querySwipes = mDb.orderBy(mTargetSwipes, SwipeDataAuth.START_TIME, mStartTime, mEndTime);
        //Query query = mRef.orderByChild("startTime").startAt(startTime).endAt(endTime);
        mAdapterSwipes = new FirebaseRecyclerAdapter<Swipe, SwipeViewHolder>(Swipe.class, R.layout.swipe_view,
                SwipeViewHolder.class, querySwipes) {
            @Override
            protected void populateViewHolder(SwipeViewHolder viewHolder, Swipe model, int position) {
                viewHolder.setEverything(model, position, "Swipe", getContext());
            }
        };
        mAdapterSwipes.notifyDataSetChanged();
        recyclerViewSwipes.setAdapter(mAdapterSwipes);

        Query queryRequests = mDb.orderBy(mTargetRequests, SwipeDataAuth.START_TIME, mStartTime, mEndTime);
        //Query query = mRef.orderByChild("startTime").startAt(startTime).endAt(endTime);
        mAdapterRequests = new FirebaseRecyclerAdapter<Swipe, SwipeViewHolder>(Swipe.class, R.layout.swipe_view,
                SwipeViewHolder.class, queryRequests) {
            @Override
            protected void populateViewHolder(SwipeViewHolder viewHolder, Swipe model, int position) {
                viewHolder.setEverything(model, position, "Request", getContext());
            }
        };
        mAdapterRequests.notifyDataSetChanged();
        recyclerViewRequests.setAdapter(mAdapterRequests);

    }


    protected void setStartandEndTimes(Long currentTime) {
        Calendar calendar = Calendar.getInstance();
        if (currentTime != TODAY)
            calendar.setTimeInMillis(currentTime);

        calendar.set(Calendar.HOUR_OF_DAY, 0);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);
        mStartTime = calendar.getTimeInMillis();

        calendar.set(Calendar.HOUR_OF_DAY, 23);
        calendar.set(Calendar.MINUTE, 59);
        calendar.set(Calendar.SECOND, 59);
        mEndTime = calendar.getTimeInMillis();

    }


    @Override
    public void onDestroyView() {
        super.onDestroyView();
        mAdapterSwipes.cleanup();
        mAdapterRequests.cleanup();
    }
}
