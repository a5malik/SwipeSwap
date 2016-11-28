package com.example.tsleeve.swipeswap;

import android.graphics.drawable.GradientDrawable;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.StorageReference;

/**
 * Created by anadendla on 11/27/16.
 */

public class OtherProfileActivity extends AppCompatActivity {

    private UserAuth uAuth = new UserAuth();
    private SwipeDataAuth mDb = new SwipeDataAuth();
    private String other_profile_uid;
    private StorageReference storageReference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.other_profile);
        ImageView closeButton = (ImageView) findViewById(R.id.close_button);
        final RatingBar ratingBar = (RatingBar) findViewById(R.id.ratingBarSwipeView);
        final TextView ProfileHeader = (TextView) findViewById(R.id.profileHeader);
        final TextView ProfileSubheader = (TextView) findViewById(R.id.profileSubheader);
        final CircleImageView profile = (CircleImageView) findViewById(R.id.profileImage);
        final Button paymentButton = (Button) findViewById(R.id.buttonPay);
        final Button messageButton = (Button) findViewById(R.id.buttonMessage);
        GradientDrawable gd = new GradientDrawable();
        gd.setColor(0x00000000);
        gd.setCornerRadius(30);
        gd.setStroke(10, 0xFFF16464);
        GradientDrawable gd2 = new GradientDrawable();
        gd2.setColor(0xFFF16464);
        gd2.setCornerRadius(30);
        gd2.setStroke(10, 0xFFF16464);
        paymentButton.setBackground(gd);
        messageButton.setBackground(gd2);

        paymentButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });
        messageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });

        other_profile_uid = getIntent().getExtras().getString("uid");

        ProfileSubheader.setText("Student | Los Angeles, CA");
        ratingBar.setIsIndicator(true);
        final DatabaseReference ref = mDb.getUserReference(other_profile_uid);
        ref.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.child(SwipeDataAuth.USERNAME).getValue() != null)
                    ProfileHeader.setText(dataSnapshot.child(SwipeDataAuth.USERNAME).getValue(String.class));
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
        closeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }
}
