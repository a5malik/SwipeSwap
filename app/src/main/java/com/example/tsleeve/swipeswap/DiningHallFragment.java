package com.example.tsleeve.swipeswap;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;


/**
 * A simple {@link Fragment} subclass.
 */
public class DiningHallFragment extends Fragment {

    private WebView webView;
    private String DiningHall;
    private String url;

    public DiningHallFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_dining_hall, container, false);
        webView = (WebView) view.findViewById(R.id.wvDiningHall);
        webView.loadUrl("http://menu.ha.ucla.edu/foodpro/default.asp?location=02");
        return view;
    }

}
