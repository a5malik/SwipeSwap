package com.example.tsleeve.swipeswap;

import android.support.v4.app.DialogFragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;

/**
 * Created by footb on 11/26/2016.
 */

public class MenuDialogFragment extends DialogFragment {

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.menu_dialog, container);
    }

    public void onViewCreated(View view, Bundle savedInstanceState) {
        Bundle b = getArguments();
        String url = b.getString("URL");
        WebView webView = (WebView) view.findViewById(R.id.webviewMenu);
        webView.loadUrl(url);
    }
}
