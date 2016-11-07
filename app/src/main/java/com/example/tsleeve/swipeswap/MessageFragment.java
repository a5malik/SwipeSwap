package com.example.tsleeve.swipeswap;

import android.os.Bundle;
import android.provider.Settings;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.google.gson.JsonObject;
import com.koushikdutta.async.future.FutureCallback;
import com.koushikdutta.ion.Ion;


/**
 * Created by footb on 10/18/2016.
 */

public class MessageFragment extends Fragment {
    /*
       Change this URL to match the token URL for your quick start server

       Download the quick start server from:

       https://www.twilio.com/docs/api/ip-messaging/guides/quickstart-js
    */
    final static String SERVER_TOKEN_URL = "http://localhost:5000/token";   // Using Python server
    private static final String    DEFAULT_CLIENT_NAME = "TestUser";

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

//    @Nullable
//    @Override
//    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
//        View view = inflater.inflate(R.layout.messages_fragment, container, false);
//        TextView tv = (TextView) view.findViewById(R.id.textViewSwipeList);
//        tv.setText("Message Fragment");
//        return view;
//    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.messages_fragment, container, false);
        final TextView tv = (TextView) view.findViewById(R.id.textViewSwipeList);

        // Try to connect to server for access token
        Ion.with(this).load(SERVER_TOKEN_URL).asJsonObject().setCallback(new FutureCallback<JsonObject>() {
            @Override
            public void onCompleted(Exception e, JsonObject result) {
                if (e == null) {
                    String identity = result.get("identity").getAsString();
                    String accessToken = result.get("token").getAsString();
                    tv.setText("identity: " + identity + " access token: " + accessToken);
                }
                else {
                    tv.setText("Error receiving access token.");
                }
            }
        });

        return view;
    }
}
