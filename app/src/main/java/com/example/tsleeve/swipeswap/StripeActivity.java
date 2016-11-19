package com.example.tsleeve.swipeswap;

import android.app.Activity;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.ViewGroup;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.FrameLayout;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;

import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;

/**
 * Created by Alvin on 11/14/2016.
 */

public class StripeActivity extends AppCompatActivity {

    private String testSecretKey = "sk_test_lj1HUiYDbkURQyxL36ED4XhY";
    private String testPublishableKey = "pk_test_1Sjh7kdXNzjfPCTbUaj8Ka7o";
    private final String CLIENT_ID = "ca_9UGqpT3OeYvcP3wHEpUSOaIbQmt4BI4y";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.stripe_activity);

        final Intent intent = getIntent();
        String payAmount = intent.getStringExtra(PayActivity.EXTRA_MESSAGE);
//        TextView textView = new TextView(this);
//        textView.setTextSize(40);
//        textView.setText(payAmount);
//
//        ViewGroup layout = (ViewGroup) findViewById(R.id.activity_payment);
//        layout.addView(textView);

        final WebView webView = new WebView(this);
        webView.getSettings().setJavaScriptEnabled(true);
        setContentView(webView);
//        webView.loadUrl("https://connect.stripe.com/oauth/authorize?response_type=code&client_id=" + CLIENT_ID + "&scope=read_write");
        webView.loadUrl("https://connect.stripe.com/oauth/authorize?response_type=code&client_id=ca_9UGqpT3OeYvcP3wHEpUSOaIbQmt4BI4y&scope=read_write");
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
                    Uri uri = Uri.parse(url);
                    authCode = uri.getQueryParameter("code");
                    Log.i("", "CODE : " + authCode);
                    authComplete = true;
                    resultIntent.putExtra("code", authCode);
                    StripeActivity.this.setResult(Activity.RESULT_OK, resultIntent);
                    setResult(Activity.RESULT_CANCELED, resultIntent);

                    Toast.makeText(getApplicationContext(), "Authorization Code is: " + authCode, Toast.LENGTH_SHORT).show();
//                    String requestURL = "https://connect.stripe.com/oauth/token/?client_secret=sk_test_lj1HUiYDbkURQyxL36ED4XhY&code=" + authCode + "&grant_type=authorization_code";
                    String requestURL = "https://connect.stripe.com/oauth/token/";

                    try {
                        URL url1 = new URL(requestURL);
                        HttpURLConnection urlConnection = (HttpURLConnection) url1.openConnection();
                        urlConnection.setDoOutput(true);
                        urlConnection.setReadTimeout(100000);
                        urlConnection.setConnectTimeout(100000);
                        urlConnection.setRequestMethod("POST");
                        urlConnection.setChunkedStreamingMode(0);

                        try {
                            SSLContext sc;
                            sc = SSLContext.getInstance("TLS");
                            sc.init(null, null, new java.security.SecureRandom());
                            ((HttpsURLConnection)urlConnection).setSSLSocketFactory(sc.getSocketFactory());
                        } catch (Exception e) {
                            Log.i("", "Failed to construct SSL object");
                        }
//                        OutputStream out = new BufferedOutputStream(urlConnection.getOutputStream());
//                        BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(out, "UTF-8"));
//                        writer.write(requestURL);
//                        writer.flush();
//                        writer.close();
//                        out.close();
//                        urlConnection.connect();

                        Log.i("", "Success connecting");

//
//                        BufferedReader reader = new BufferedReader(new InputStreamReader(urlConnection.getInputStream()));
//                        StringBuilder sb = new StringBuilder();
//                        String line = null;
//
//                        while((line = reader.readLine()) != null) {
//                            sb.append(line + "\n");
//                        }
//                        Log.i("", "sb : " + sb.toString());

//                        Toast.makeText(getApplicationContext(), sb.toString(), Toast.LENGTH_SHORT).show();
                    } catch (MalformedURLException e) {
                        Log.i("", "Didn't happen: " + e.getMessage());
                        System.out.println(e.getMessage());
                        return;
                    } catch (ProtocolException e) {
                        Log.i("", "Didn't happen: " + e.getMessage());
                        System.out.println(e.getMessage());
                        return;
                    } catch (IOException e) {
                        Log.i("", "Didn't happen: " + e.getMessage());
                        System.out.println(e.getMessage());
                        return;
                    }
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

}