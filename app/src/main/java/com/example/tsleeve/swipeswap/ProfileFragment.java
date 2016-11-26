package com.example.tsleeve.swipeswap;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.RatingBar;
import android.widget.TextView;

import com.firebase.ui.auth.AuthUI;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;

/**
 * Created by footb on 10/18/2016.
 */

public class ProfileFragment extends Fragment {
    private UserAuth uAuth = new UserAuth();
    private SwipeDataAuth mDb = new SwipeDataAuth();

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.profile_fragment, container, false);
        final RatingBar ratingBar = (RatingBar) view.findViewById(R.id.ratingBarSwipeView);
        final TextView ProfileHeader = (TextView) view.findViewById(R.id.profileHeader);
        final EditText EmailAddress = (EditText) view.findViewById(R.id.editTextEmailAddress);
        final EditText PhoneNumber = (EditText) view.findViewById(R.id.editTextPhoneNumber);
        final EditText VenmoID = (EditText) view.findViewById(R.id.editTextVenmoID);
        TextView ProfileSubheader = (TextView) view.findViewById(R.id.profileSubheader);
        ProfileSubheader.setText("Student | Los Angeles, CA");
        ImageButton btnSignOut = (ImageButton) view.findViewById(R.id.buttonSignOut);
        ratingBar.setIsIndicator(true);
        if (uAuth.validUser())
            EmailAddress.setText(uAuth.getUserEmailAddress());
        final DatabaseReference ref = mDb.getUserReference(uAuth.uid());
        ref.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.child(SwipeDataAuth.USERNAME).getValue() != null)
                    ProfileHeader.setText(dataSnapshot.child(SwipeDataAuth.USERNAME).getValue(String.class));
                if (dataSnapshot.child(SwipeDataAuth.PHONENO).getValue() != null)
                    PhoneNumber.setText(dataSnapshot.child(SwipeDataAuth.PHONENO).getValue(String.class));
                if (dataSnapshot.child(SwipeDataAuth.VENMOID).getValue() != null)
                    VenmoID.setText(dataSnapshot.child(SwipeDataAuth.VENMOID).getValue(String.class));
                Double sum = 0.0;
                if (dataSnapshot.child(SwipeDataAuth.RATINGSUM).getValue() != null)
                    sum = dataSnapshot.child(SwipeDataAuth.RATINGSUM).getValue(Double.class);

                int NOR = 1;
                if (dataSnapshot.child(SwipeDataAuth.NOR).getValue() != null)
                    NOR = dataSnapshot.child(SwipeDataAuth.NOR).getValue(Integer.class);
                if (NOR == 0) NOR = 1;
                double rating = sum / NOR;
                ratingBar.setRating((float) rating);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
        btnSignOut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AuthUI.getInstance()
                        .signOut(getActivity())
                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                            public void onComplete(@NonNull Task<Void> task) {
                                // user is now signed out
                                startActivity(new Intent(getActivity(), AuthUiActivity.class));
                                getActivity().finish();
                            }
                        });
            }
        });
        return view;
    }
}
