package com.example.sparksrestapi;

import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

public class PagerAdapter2 extends FragmentPagerAdapter {

    public PagerAdapter2(FragmentManager fm) {
        super(fm);
    }

    @Override
    public Fragment getItem(int i) {

        switch(i) {

            case 0:
                return (new PersonalFragment());

            case 1:
                return (new EducationFragment());

            case 2:
                return (new ProfessionalFragment());

            default:
                return null;
        }

    }

    @Nullable
    @Override
    public CharSequence getPageTitle(int position) {
        switch(position){
            case 0:
                return "Personal";

            case 1:
                return "Education";

            case 2:
                return "Profession";
        }
        return null;
    }

    @Override
    public int getCount() {
        return 3;
    }
}
