package com.example.tsleeve.swipeswap;

import android.content.Context;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

/**
 * Created by footb on 10/19/2016.
 */

public class CalendarFragmentPagerAdapter extends FragmentPagerAdapter {
    final int CAL_PAGE_COUNT = 7;
    Context context;
    private String[] titles = new String[]{"Day", "Week", "Month", "Cov", "Fea", "DeN", "BPl"};

    public CalendarFragmentPagerAdapter(FragmentManager fm, Context context) {
        super(fm);
        this.context = context;
    }

    @Override
    public Fragment getItem(int position) {
        switch (position) {
            case 0:
                return new CalendarDayFragment();
            case 1:
                return new CalendarWeekFragment();
            case 2:
                return new CalendarMonthFragment();
            default:
                return new DiningHallFragment();
        }
    }

    @Override
    public int getCount() {
        return CAL_PAGE_COUNT;
    }

    @Override
    public CharSequence getPageTitle(int position) {
        return titles[position];
    }
}
