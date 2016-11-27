package com.example.tsleeve.swipeswap;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
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
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;

/**
 * Created by footb on 10/18/2016.
 */

public class ProfileFragment extends Fragment {
    private UserAuth uAuth = new UserAuth();
    private SwipeDataAuth mDb = new SwipeDataAuth();
    private StorageReference storageReference;
    private static final int SELECT_PICTURE = 1;
    private static final int REQUEST_CODE_PICTURE= 1;
    private static CircleImageView profile;

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
        final TextView ProfileSubheader = (TextView) view.findViewById(R.id.profileSubheader);
        final ImageButton btnSignOut = (ImageButton) view.findViewById(R.id.buttonSignOut);
        final ImageButton btnNotif = (ImageButton) view.findViewById(R.id.btnNotif);
        storageReference = FirebaseStorage.getInstance().getReference();
        btnNotif.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getActivity(), NotificationActivity.class));
            }
        });

        profile = (CircleImageView) view.findViewById(R.id.profileImage);
        profile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent pickIntent = new Intent();
                pickIntent.setType("image/*");
                pickIntent.setAction(Intent.ACTION_GET_CONTENT);
                Intent takePhotoIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                String pickTitle = "Select a Picture from the Gallery";
                Intent chooserIntent = Intent.createChooser(pickIntent, pickTitle);
                chooserIntent.putExtra( Intent.EXTRA_INITIAL_INTENTS, new Intent[] { takePhotoIntent });
                startActivityForResult(chooserIntent, SELECT_PICTURE);
            }
        });
        ProfileSubheader.setText("Student | Los Angeles, CA");
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
                if (dataSnapshot.child(SwipeDataAuth.PROFILE_URI).getValue(String.class) != null){
                    String uri = dataSnapshot.child(SwipeDataAuth.PROFILE_URI).getValue(String.class);
                    if(uri.length() > 0){
                        try {
                            Bitmap bitmap = MediaStore.Images.Media.getBitmap(getContext().getContentResolver(), Uri.parse(uri));
                            profile.setImageBitmap(bitmap);
                        } catch (Exception e) {
                            Log.e("Error", e.getMessage());
                            e.printStackTrace();
                        }
                    }
                }
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

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_CODE_PICTURE && resultCode == Activity.RESULT_OK) {
            if (data == null) {
                return;
            }
            try {
                InputStream inputStream = getContext().getContentResolver().openInputStream(data.getData());
                Bitmap bitmap = BitmapFactory.decodeStream(inputStream);
                Uri imageUri = getImageUri(getContext(), bitmap);
                UserProfileChangeRequest userProfileChangeRequest = new UserProfileChangeRequest.Builder()
                        .setDisplayName("displayName")
                        .setPhotoUri(imageUri)
                        .build();
                uAuth.getCurrentUser().updateProfile(userProfileChangeRequest);
                mDb.setProfileURI(uAuth.uid(), imageUri.toString());
                try {
                    profile.setImageBitmap(bitmap);
                } catch (Exception e) {
                    Log.e("Error", e.getMessage());
                    e.printStackTrace();
                }
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }
        }
    }

    public Uri getImageUri(Context inContext, Bitmap inImage) {
        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        inImage.compress(Bitmap.CompressFormat.JPEG, 100, bytes);
        String path = MediaStore.Images.Media.insertImage(inContext.getContentResolver(), inImage, "Title", null);
        return Uri.parse(path);
    }
}


