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
    protected RecyclerView recyclerView;
    protected FirebaseRecyclerAdapter mAdapter;
    Long mStartTime, mEndTime;
    //private DatabaseReference mRef;
    protected SwipeDataAuth mDb = new SwipeDataAuth();

    public static class SwipeViewHolder extends RecyclerView.ViewHolder {
        View mView;
        SwipeDataAuth mDb = new SwipeDataAuth();

        public SwipeViewHolder(View itemView) {
            super(itemView);
            mView = itemView;
        }

        public void setEverything(final Swipe swipe, int position) {
            TextView tvTitle = (TextView) mView.findViewById(R.id.textViewTitle);
            tvTitle.setText("Swipe " + Integer.toString(position));

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

    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.calendar_day_fragment, container, false);
        recyclerView = (RecyclerView) view.findViewById(R.id.day_recycler_view);

        return view;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        //recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this.getActivity()));

        setStartandEndTimes();
        //mRef = FirebaseDatabase.getInstance().getReference().child("swipes");
        Query query = mDb.orderBy(SwipeDataAuth.ALL_SWIPES, SwipeDataAuth.START_TIME, mStartTime, mEndTime);
        //Query query = mRef.orderByChild("startTime").startAt(startTime).endAt(endTime);
        mAdapter = new FirebaseRecyclerAdapter<Swipe, SwipeViewHolder>(Swipe.class, R.layout.swipe_view,
                SwipeViewHolder.class, query) {
            @Override
            protected void populateViewHolder(SwipeViewHolder viewHolder, Swipe model, int position) {
                viewHolder.setEverything(model, position);
            }
        };
        mAdapter.notifyDataSetChanged();
        recyclerView.setAdapter(mAdapter);

    }

    protected void setStartandEndTimes() {
        Calendar calendar = Calendar.getInstance();
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
        mAdapter.cleanup();
    }
}
