package com.example.tsleeve.swipeswap;

import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class RegisterActivity extends AppCompatActivity {


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        //TODO add rules for username

        final FirebaseAuth auth = FirebaseAuth.getInstance();
        final DatabaseReference ref = FirebaseDatabase.getInstance().getReference().child("users");
        final String uid = auth.getCurrentUser().getUid();
        final EditText editTextusername = (EditText) findViewById(R.id.editTextaddusername);
        Button btn = (Button) findViewById(R.id.buttonsubmitregistration);
        btn.setText("Submit");
        editTextusername.setHint("Enter username(min 5 characters)");
        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (editTextusername.getText().toString().length() >= 5) {
                    ref.child(uid).child("username").setValue(editTextusername.getText().toString());
                    startActivity(MainActivity.createIntent(RegisterActivity.this));
                    finish();
                }
            }
        });


    }

}
