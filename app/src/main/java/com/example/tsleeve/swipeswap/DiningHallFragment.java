package com.example.tsleeve.swipeswap;


import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;
import android.widget.Button;

import java.util.Calendar;


/**
 * A simple {@link Fragment} subclass.
 */
public class DiningHallFragment extends Fragment {

    private WebView webView;
    private String mDiningHall;
    private String mUrl;
    private android.support.v4.app.FragmentManager manager = null;
    private android.app.FragmentManager dfmanager = null;
    private Fragment fragment = null;
    final public static String HALL = "HALL";

    public static DiningHallFragment getInstance(String DiningHall) {
        Bundle bundle = new Bundle();
        bundle.putString(HALL, DiningHall);
        DiningHallFragment fragment = new DiningHallFragment();
        fragment.setArguments(bundle);
        return fragment;
    }

    public DiningHallFragment() {
        // Required empty public constructor
    }


    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Bundle bundle = this.getArguments();
        if (bundle != null) {
            mDiningHall = bundle.getString(HALL);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_dining_hall, container, false);
        final Button button = (Button) view.findViewById(R.id.btnMenu);
        button.setText("Click for Menu");
        switch (mDiningHall) {
            case SwipeDataAuth.COVEL:
                mUrl = "http://menu.ha.ucla.edu/foodpro/default.asp?location=07";
                break;
            case SwipeDataAuth.BPLATE:
                mUrl = "http://menu.ha.ucla.edu/foodpro/default.asp?location=02";
                break;
            case SwipeDataAuth.DENEVE:
                mUrl = "http://menu.ha.ucla.edu/foodpro/default.asp?location=01";
                break;
            case SwipeDataAuth.FEAST:
                mUrl = "http://menu.ha.ucla.edu/foodpro/default.asp?location=04";
                break;
            default:
                mUrl = "http://menu.ha.ucla.edu/foodpro/default.asp";
        }
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MenuDialogFragment menuDialogFragment = new MenuDialogFragment();
                Bundle args = new Bundle();
                args.putString("URL", mUrl);
                menuDialogFragment.setArguments(args);
                menuDialogFragment.show(getFragmentManager(), "Menu");
            }
        });

        String swipeTarget = SwipeDataAuth.DINING_HALL + "/" + mDiningHall + "/" + "swipes";
        String requestTarget = SwipeDataAuth.DINING_HALL + "/" + mDiningHall + "/" + "requests";
        Calendar cal = Calendar.getInstance();
        fragment = CalendarDayFragment.getInstance(cal.getTimeInMillis(), swipeTarget, requestTarget);
        if (manager == null) manager = getChildFragmentManager();
        manager.beginTransaction().replace(R.id.dining_dayfragment_container, fragment).commit();

        return view;
    }

}
