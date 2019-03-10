package com.example.sparksrestapi;

import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

public class PagerAdapter extends FragmentPagerAdapter {


    public PagerAdapter(FragmentManager fm) {
        super(fm);
    }

    @Override
    public Fragment getItem(int i) {

        switch(i) {

            case 0:
                signupFragment signup = new signupFragment();
                return signup;

            case 1:
                loginFragment login = new loginFragment();
                return login;

            default:
                return null;
        }

    }

    @Nullable
    @Override
    public CharSequence getPageTitle(int position) {
        switch(position){
            case 0:
                return "Sign Up";

            case 1:
                return "Login";
        }
        return null;
    }

    @Override
    public int getCount() {
        return 2;
    }
}
