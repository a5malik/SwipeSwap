package com.example.tsleeve.swipeswap;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import org.w3c.dom.Text;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.GregorianCalendar;

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
    public final static Long TODAY = new Long(0);

    public static CalendarDayFragment getInstance(Long time) {
        Bundle bundle = new Bundle();
        bundle.putLong(CalendarDayFragment.DATE_TO_SHOW, time);
        CalendarDayFragment fragment = new CalendarDayFragment();
        fragment.setArguments(bundle);
        return fragment;
    }

    public static class SwipeViewHolder extends RecyclerView.ViewHolder {
        View mView;
        SwipeDataAuth mDb = new SwipeDataAuth();

        public SwipeViewHolder(View itemView) {
            super(itemView);
            mView = itemView;
        }

        public void setEverything(final Swipe swipe, int position, String Title) {
            TextView tvTitle = (TextView) mView.findViewById(R.id.textViewTitle);
            tvTitle.setText(Title + " " + Integer.toString(position));

            TextView tvprice = (TextView) mView.findViewById(R.id.textViewprice);
            tvprice.setText(Double.toString(swipe.getPrice()));
            final TextView tvownerid = (TextView) mView.findViewById(R.id.textViewowner_id);
            tvownerid.setText(swipe.getOwner_ID());

            TextView tvdininghall = (TextView) mView.findViewById(R.id.textViewdininghall);
            String diningHallString = "";
            int diningHall = swipe.getDiningHall();
            if ((diningHall & SwipeDataAuth.BPLATE_ID) == SwipeDataAuth.BPLATE_ID)
                diningHallString += "BPlate.";
            if ((diningHall & SwipeDataAuth.COVEL_ID) == SwipeDataAuth.COVEL_ID)
                diningHallString += "Covel.";
            if ((diningHall & SwipeDataAuth.DENEVE_ID) == SwipeDataAuth.DENEVE_ID)
                diningHallString += "DeNeve.";
            if ((diningHall & SwipeDataAuth.FEAST_ID) == SwipeDataAuth.FEAST_ID)
                diningHallString += "Feast.";
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
            startMonth = new SimpleDateFormat("MMM").format(cal.getTime());

            cal.setTimeInMillis(swipe.getEndTime());
            endTimeofDay = new SimpleDateFormat("h:mm a").format(cal.getTime());

            TextView tvDate = (TextView) mView.findViewById(R.id.textViewDate);
            tvDate.setText(startDayofWeek + ", " + startMonth + " " + startDayofMonth);

            TextView tvtime = (TextView) mView.findViewById(R.id.textViewTime);
            tvtime.setText(startTimeofDay + "-" + endTimeofDay);


            //final DatabaseReference ref = FirebaseDatabase.getInstance().getReference().child("users").child(swipe.getOwner_ID());
            final DatabaseReference ref = mDb.getUserReference(swipe.getOwner_ID());
            ref.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    tvownerid.setText(dataSnapshot.child(SwipeDataAuth.USERNAME).getValue(String.class));
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

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
        Query querySwipes = mDb.orderBy(SwipeDataAuth.ALL_SWIPES, SwipeDataAuth.START_TIME, mStartTime, mEndTime);
        //Query query = mRef.orderByChild("startTime").startAt(startTime).endAt(endTime);
        mAdapterSwipes = new FirebaseRecyclerAdapter<Swipe, SwipeViewHolder>(Swipe.class, R.layout.swipe_view,
                SwipeViewHolder.class, querySwipes) {
            @Override
            protected void populateViewHolder(SwipeViewHolder viewHolder, Swipe model, int position) {
                viewHolder.setEverything(model, position, "Swipe");
            }
        };
        mAdapterSwipes.notifyDataSetChanged();
        recyclerViewSwipes.setAdapter(mAdapterSwipes);

        Query queryRequests = mDb.orderBy(SwipeDataAuth.ALL_REQUESTS, SwipeDataAuth.START_TIME, mStartTime, mEndTime);
        //Query query = mRef.orderByChild("startTime").startAt(startTime).endAt(endTime);
        mAdapterRequests = new FirebaseRecyclerAdapter<Swipe, SwipeViewHolder>(Swipe.class, R.layout.swipe_view,
                SwipeViewHolder.class, queryRequests) {
            @Override
            protected void populateViewHolder(SwipeViewHolder viewHolder, Swipe model, int position) {
                viewHolder.setEverything(model, position, "Request");
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
