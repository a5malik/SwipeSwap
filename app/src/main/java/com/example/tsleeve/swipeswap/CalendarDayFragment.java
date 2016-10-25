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

import java.util.Calendar;

/**
 * Created by footb on 10/19/2016.
 */

public class CalendarDayFragment extends Fragment {
    private RecyclerView recyclerView;
    private FirebaseRecyclerAdapter mAdapter;
    private DatabaseReference mRef;

    public static class SwipeViewHolder extends RecyclerView.ViewHolder {
        View mView;

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
            if ((diningHall & 1) == 1)
                diningHallString += "BPlate.";
            if ((diningHall & 2) == 2)
                diningHallString += "Covel.";
            if ((diningHall & 4) == 4)
                diningHallString += "DeNeve.";
            if ((diningHall & 8) == 8)
                diningHallString += "Feast.";
            if (diningHallString.length() == 0)
                diningHallString = "No Dining Halls Selected.";
            tvdininghall.setText(diningHallString);

            TextView tvstarttime = (TextView) mView.findViewById(R.id.textViewstarttime);
            tvstarttime.setText(Long.toString(swipe.getStartTime()));

            TextView tvendtime = (TextView) mView.findViewById(R.id.textViewendtime);
            tvendtime.setText(Long.toString(swipe.getEndTime()));

            final DatabaseReference ref = FirebaseDatabase.getInstance().getReference().child("users").child(swipe.getOwner_ID());
            ref.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    tvownerid.setText(dataSnapshot.child("username").getValue(String.class));
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

        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.HOUR_OF_DAY, 0);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);
        Long startTime = calendar.getTimeInMillis();

        calendar.set(Calendar.HOUR_OF_DAY, 23);
        calendar.set(Calendar.MINUTE, 59);
        calendar.set(Calendar.SECOND, 59);
        Long endTime = calendar.getTimeInMillis();

        mRef = FirebaseDatabase.getInstance().getReference().child("swipes");
        Query query = mRef.orderByChild("startTime").startAt(startTime).endAt(endTime);
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


    @Override
    public void onDestroyView() {
        super.onDestroyView();
        mAdapter.cleanup();
    }
}
