package com.example.tsleeve.swipeswap;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

/**
 * Created by footb on 10/18/2016.
 */

public class SwipesFragment extends Fragment {
    TabLayout tabLayout;
    ViewPager viewPager;
    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.swipes_fragment, container, false);
        tabLayout = (TabLayout) view.findViewById(R.id.calendar_tab_layout);
        viewPager = (ViewPager) view.findViewById(R.id.calendar_viewpager);
        viewPager.setAdapter(new CalendarFragmentPagerAdapter(getChildFragmentManager(), getContext()));
        viewPager.setOffscreenPageLimit(3);
        tabLayout.setupWithViewPager(viewPager);
        FloatingActionButton fab = (FloatingActionButton) view.findViewById(R.id.main_fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ((MainActivity)getActivity()).showAddSwipe();
            }
        });
        return view;
    }
}
