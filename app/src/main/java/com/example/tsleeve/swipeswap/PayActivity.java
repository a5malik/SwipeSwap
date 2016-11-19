package com.example.tsleeve.swipeswap;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;

/**
 * Created by Alvin on 11/14/2016.
 */

public class PayActivity extends AppCompatActivity {
    public final static String EXTRA_MESSAGE = "com.example.alvin.myapplication.MESSAGE";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

    // Called when user clicks Pay button
    public void sendPayment(View view) {
        // initiate payment
        Intent intent = new Intent(this, StripeActivity.class);
        EditText editText = (EditText) findViewById(R.id.payAmount);
        String payAmount = editText.getText().toString();
        intent.putExtra(EXTRA_MESSAGE, payAmount);
        startActivity(intent);
    }
}