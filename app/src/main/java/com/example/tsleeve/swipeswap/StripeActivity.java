package com.example.tsleeve.swipeswap;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Toast;

import com.stripe.Stripe;
import com.stripe.model.Charge;
import com.stripe.net.RequestOptions;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by Alvin on 11/14/2016.
 */

public class StripeActivity extends AppCompatActivity {

    private final String testSecretKey = "sk_test_lj1HUiYDbkURQyxL36ED4XhY";
    //    private final String testPublishableKey = "pk_test_1Sjh7kdXNzjfPCTbUaj8Ka7o";
    private final String CLIENT_ID = "ca_9UGqpT3OeYvcP3wHEpUSOaIbQmt4BI4y";
    private final String authURL = "https://connect.stripe.com/oauth/authorize?";
    private final String tokenURL = "https://connect.stripe.com/oauth/token";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.stripe_activity);

        final Intent intent = getIntent();
        final String payAmount = intent.getStringExtra(PayActivity.EXTRA_MESSAGE);

        // Prepare webview
        final WebView webView = new WebView(this);
        webView.getSettings().setJavaScriptEnabled(true);
        setContentView(webView);

        // Prepare url for retrieving authorization code
        String data = "response_type=code&client_id=" + CLIENT_ID + "&scope=read_write&always_prompt=true";

        webView.loadUrl(authURL + data);
        webView.setWebViewClient(new WebViewClient() {
            boolean authComplete = false;
            Intent resultIntent = new Intent();

            @Override
            public void onPageStarted(WebView view, String url, Bitmap favicon) {
                super.onPageStarted(view, url, favicon);
            }

            String authCode;

            @Override
            public void onPageFinished(WebView view, String url) {
                if (url.contains("?scope=") && url.contains("&code=") && !authComplete) {
                    // Grab authorization code from the return URL      https://stripe.com/docs/connect/reference
                    Uri uri = Uri.parse(url);
                    authCode = uri.getQueryParameter("code");
                    Log.i("", "CODE : " + authCode);
                    authComplete = true;
//                    startService(intent);
//                    finish();
                    resultIntent.putExtra("code", authCode);
                    StripeActivity.this.setResult(Activity.RESULT_OK, resultIntent);
                    setResult(Activity.RESULT_CANCELED, resultIntent);

                    // Construct POST parameters with authorization code    https://stripe.com/docs/connect/reference
                    final String requestParams = "code=" + authCode + "&client_secret=" + testSecretKey + "&grant_type=authorization_code";
                    new Thread() {
                        @Override
                        public void run() {
                            StripeSession session = null;
                            try {
                                // Execute POST instruction         https://stripe.com/docs/connect/reference
                                String response = doPOST(tokenURL, requestParams);

                                // Retrieve POST JSON result        https://stripe.com/docs/connect/reference
                                JSONObject obj = new JSONObject(response);

                                Log.d("", "String data[access_token]:			" + obj.getString("access_token"));
                                Log.d("", "String data[livemode]:				" + obj.getBoolean("livemode"));
                                Log.d("", "String data[refresh_token]:			" + obj.getString("refresh_token"));
                                Log.d("", "String data[token_type]:			" + obj.getString("token_type"));
                                Log.d("", "String data[stripe_publishable_key]: " + obj.getString("stripe_publishable_key"));
                                Log.d("", "String data[stripe_user_id]:		" + obj.getString("stripe_user_id"));
                                Log.d("", "String data[scope]:					" + obj.getString("scope"));

                                // Store response details
                                session = new StripeSession(getApplicationContext(), "USERNAME");
                                session.storeAccessToken(obj.getString("access_token"));
                                session.storeRefreshToken(obj.getString("refresh_token"));
                                session.storePublishableKey(obj.getString("stripe_publishable_key"));
                                session.storeUserid(obj.getString("stripe_user_id"));
                                session.storeLiveMode(obj.getBoolean("livemode"));
                                session.storeTokenType(obj.getString("token_type"));
                            } catch (Exception e) {
                                Log.i("", "POST failed: " + e.getMessage());
                            }

                            try {
                                // Use details to make payment  https://stripe.com/docs/connect/payments-fees
                                Stripe.apiKey = testSecretKey;
//                                RequestOptions requestOptions = RequestOptions.builder().setStripeAccount(session.getUserId()).build();
                                Map<String, Object> chargeParams = new HashMap<String, Object>();
                                chargeParams.put("amount", payAmount);
                                chargeParams.put("currency", "usd");
                                chargeParams.put("source", session.getAccessToken());   // Token reported as invalid???
                                /* TODO:
                                Currently using BOTH buyer's access token (source) and ID (destination).
                                NEED TO CHANGE ID to that of the Seller -> need to send this data somehow
                                 */
                                chargeParams.put("destination", session.getUserId());

//                                Charge.create(chargeParams, requestOptions);
                                Charge.create(chargeParams);

                                Toast.makeText(getApplicationContext(), "Made payment of: " + payAmount, Toast.LENGTH_SHORT).show();
                            } catch (Exception e) {
                                Log.i("", "Payment failed: " + e.getMessage());
                            }
                            startService(intent);
                            finish();
                        }
                    }.start();

                } else if (url.contains("error=access_denied")) {
                    Log.i("", "ACCESS_DENIED_HERE");
                    resultIntent.putExtra("code", authCode);
                    authComplete = true;
                    setResult(Activity.RESULT_CANCELED, resultIntent);
                    Toast.makeText(getApplicationContext(), "Error Occurred", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private String doPOST(final String url, final String parameters) throws IOException {
        URL url1 = new URL(url);
        HttpURLConnection urlConnection = (HttpURLConnection) url1.openConnection();
        urlConnection.setDoOutput(true);
        urlConnection.setDoInput(true);
        urlConnection.setInstanceFollowRedirects(false);
        urlConnection.setRequestMethod("POST");
        urlConnection.setRequestProperty("User-Agent", "GotSwipes");
        urlConnection.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
        urlConnection.setRequestProperty("charset", "utf-8");
        urlConnection.setRequestProperty("Content-Length", "" + Integer.toString(parameters.getBytes().length));
        urlConnection.setUseCaches(false);
        DataOutputStream wr = new DataOutputStream(urlConnection.getOutputStream());
        wr.writeBytes(parameters);
        wr.flush();
        wr.close();
        String response = null;
        if (urlConnection.getInputStream()!=null) {
            StringBuilder sb = new StringBuilder();
            String line;

            try {
                BufferedReader reader = new BufferedReader(new InputStreamReader(urlConnection.getInputStream()));
                while ((line=reader.readLine())!=null){
                    sb.append(line);
                }
                reader.close();
            } finally {
                urlConnection.getInputStream().close();
            }
            response = sb.toString();
        }

        Log.i("", "Success connecting to token URL");
        urlConnection.disconnect();
        Log.i("", "Disconnecting from token URL");

        return response;
    }
}