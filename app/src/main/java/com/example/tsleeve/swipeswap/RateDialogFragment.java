package com.example.tsleeve.swipeswap;

import android.app.DialogFragment;
import android.app.NotificationManager;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RatingBar;
import android.widget.TextView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

/**
 * Created by footb on 11/18/2016.
 */

public class RateDialogFragment extends DialogFragment {
    @Nullable
    String RateID;
    private SwipeDataAuth mDb = new SwipeDataAuth();
    private UserAuth mUAuth = new UserAuth();


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.rate_dialog, container);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        TextView ratingtextView = (TextView) view.findViewById(R.id.ratingtextView);

        Bundle b = getArguments();
        final String ID = b.getString("user_ID");
        final String username = b.getString("user_name");
        final Double RatingSum = b.getDouble("rating_sum");
        final int NOR = b.getInt("NOR");

        ratingtextView.setText("Rate your experience with " + username);
        RatingBar ratingBar = (RatingBar) view.findViewById(R.id.ratingBar);
        ratingBar.setOnRatingBarChangeListener(new RatingBar.OnRatingBarChangeListener() {
            @Override
            public void onRatingChanged(RatingBar ratingBar, float rating, boolean fromUser) {
                mDb.setUserRatingSum(ID, RatingSum + rating);
                mDb.setUserNOR(ID, NOR + 1);
                dismiss();

                NotificationManager notificationManager = (NotificationManager) getActivity().getSystemService(Context.NOTIFICATION_SERVICE);
                notificationManager.cancel(0);
            }
        });
    }
}
