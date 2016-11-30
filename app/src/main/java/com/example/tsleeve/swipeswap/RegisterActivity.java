package com.example.tsleeve.swipeswap;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class RegisterActivity extends AppCompatActivity {
    private UserAuth uAuth = new UserAuth();
    private SwipeDataAuth db = new SwipeDataAuth();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        // TODO: add rules for username

        //final FirebaseAuth auth = FirebaseAuth.getInstance();
        //final DatabaseReference ref = FirebaseDatabase.getInstance().getReference().child("users");
        //final DatabaseReference ref = db.getUsersReference();
        final String uid = uAuth.uid();
        final EditText editTextusername = (EditText) findViewById(R.id.editTextaddusername);
        final EditText editTextPhone = (EditText) findViewById(R.id.editTextPhone);
        final EditText editTextVenmo = (EditText) findViewById(R.id.editTextVenmoID);
        final EditText editTextAddress = (EditText) findViewById(R.id.editTextAddress);
        editTextusername.setHint("Enter username (min 5 char)");
        editTextPhone.setHint("Enter Phone number");
        editTextVenmo.setHint("Enter Venmo ID");
        editTextAddress.setHint("Enter Address");
        Button btn = (Button) findViewById(R.id.buttonsubmitregistration);
        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String user = editTextusername.getText().toString();
                String number = editTextPhone.getText().toString();
                String venmoID = editTextVenmo.getText().toString();
                String address = editTextAddress.getText().toString();
                if (user.length() >= 5 && number.length() == 10 && !venmoID.isEmpty()) {
                    db.registerUsername(uid, user);
                    db.registerPhoneNumber(uid, number);
                    db.registerVenmoID(uid, venmoID);
                    db.registerAddress(uid, address);
                    db.setUserRatingSum(uid, 0.0);
                    db.setUserNOR(uid, 0);
                    //ref.child(uid).child("username").setValue(user);
                    startActivity(MainActivity.createIntent(RegisterActivity.this));
                    finish();
                } else {
                    // TODO: Display something to indicate that the username must be at least 5 characters.
                    Toast.makeText(RegisterActivity.this, "The fields aren't properly set! Try Again", Toast.LENGTH_SHORT).show();
                }
            }
        });


    }

}
