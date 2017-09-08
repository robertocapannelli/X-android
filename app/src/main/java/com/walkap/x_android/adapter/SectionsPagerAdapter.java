package com.walkap.x_android.adapter;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import com.walkap.x_android.fragment.days.FridayFragment;
import com.walkap.x_android.fragment.days.MondayFragment;
import com.walkap.x_android.fragment.days.SaturdayFragment;
import com.walkap.x_android.fragment.days.ThursdayFragment;
import com.walkap.x_android.fragment.days.TuesdayFragment;
import com.walkap.x_android.fragment.days.WednesdayFragment;

public class SectionsPagerAdapter extends FragmentPagerAdapter {

    private static final int FRAGMENT_COUNT = 6;

    public SectionsPagerAdapter(FragmentManager fragmentManager) {
        super(fragmentManager);
    }

    @Override
    public Fragment getItem(int position) {
        switch (position){
            case 0:
                return new MondayFragment();
            case 1:
                return new TuesdayFragment();
            case 2:
                return new WednesdayFragment();
            case 3:
                return new ThursdayFragment();
            case 4:
                return new FridayFragment();
            case 5:
                return new SaturdayFragment();
        }
        return null;
    }

    @Override
    public int getCount() {
        return FRAGMENT_COUNT;
    }

    @Override
    public CharSequence getPageTitle(int position) {
        switch (position){
            case 0:
                return "Monday";
            case 1:
                return "Tuesday";
            case 2:
                return "Wednesday";
            case 3:
                return "Thursday";
            case 4:
                return "Friday";
            case 5:
                return "Saturday";
        }
        return null;
    }
}
