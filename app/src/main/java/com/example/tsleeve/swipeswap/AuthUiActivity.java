package com.example.tsleeve.swipeswap;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.StringRes;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.firebase.ui.auth.AuthUI;
import com.google.firebase.auth.FirebaseAuth;

import static com.firebase.ui.auth.ui.AcquireEmailHelper.RC_SIGN_IN;

public class AuthUiActivity extends AppCompatActivity {
    private Button msigninButton;
    private static final int RC_SIGN_IN = 100;
    private View mRootView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


        FirebaseAuth auth = FirebaseAuth.getInstance();
        if (auth.getCurrentUser() != null) {
            startActivity(MainActivity.createIntent(this));
            finish();
        }

        setContentView(R.layout.auth_ui_layout);
        mRootView = findViewById(R.id.content_main);
        msigninButton = (Button) findViewById(R.id.button2);
        msigninButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivityForResult(
                        AuthUI.getInstance().createSignInIntentBuilder()
                                .build(),
                        RC_SIGN_IN);
            }
        });
    }

    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == RC_SIGN_IN) {
            handleSignInResponse(resultCode, data);
            return;
        }
    }

    private void handleSignInResponse(int resultCode, Intent data) {
        if (resultCode == RESULT_OK) {
            startActivity(MainActivity.createIntent(this));
            finish();
            return;
        }

        if (resultCode == RESULT_CANCELED) {
            Snackbar.make(mRootView, "cancelled", Snackbar.LENGTH_LONG).show();
            return;
        }

        Snackbar.make(mRootView, "unknown", Snackbar.LENGTH_LONG).show();
    }

    public static Intent createIntent(Context context) {
        Intent in = new Intent();
        in.setClass(context, AuthUiActivity.class);
        return in;
    }

}
