package com.sotosmen.socialnetworkapp;

import android.content.Context;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.fragment.app.FragmentStatePagerAdapter;

import java.util.ArrayList;

public class HomeScreenFragAdapter extends FragmentStatePagerAdapter {
    Context context;
    ArrayList<Fragment> fragments;

    public HomeScreenFragAdapter(@NonNull FragmentManager fm, int behavior, Context context, ArrayList<Fragment> fragments) {
        super(fm, behavior);
        this.context = context;
        this.fragments = fragments;
    }
    @NonNull
    @Override
    public Fragment getItem(int position) {
        return fragments.get(position);
    }

    @Override
    public int getCount() {
        return fragments.size();
    }

    @Override
    public CharSequence getPageTitle(int position) {
        switch (position) {
            case 0:
                return "Forum";
            case 2:
                return "Friends";
        }
        return null;
    }
}
