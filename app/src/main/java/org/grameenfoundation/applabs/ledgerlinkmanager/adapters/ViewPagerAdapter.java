package org.grameenfoundation.applabs.ledgerlinkmanager.adapters;

import android.content.Context;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import org.grameenfoundation.applabs.ledgerlinkmanager.fragments.GroupInformationFrag;
import org.grameenfoundation.applabs.ledgerlinkmanager.fragments.LocationInformationFrag;
import org.grameenfoundation.applabs.ledgerlinkmanager.fragments.SubmitDataFrag;
import org.grameenfoundation.applabs.ledgerlinkmanager.fragments.SupportAndTrainingFrag;

public class ViewPagerAdapter extends FragmentPagerAdapter {
    final int PAGE_COUNT = 4;
    public Context context;

    public ViewPagerAdapter(FragmentManager fm, Context context) {
        super(fm);
        this.context = context;
    }

    @Override
    public Fragment getItem(int position) {
        switch (position) {
            case 0:
                return new GroupInformationFrag();
            case 1:
                return new LocationInformationFrag();
            case 2:
                return new SupportAndTrainingFrag();
            case 3:
                return new SubmitDataFrag();
        }
        return null;
    }

    @Override
    public int getCount() {
        return PAGE_COUNT;
    }

    @Override
    public CharSequence getPageTitle(int position) {

        String tabTitles[] = new String[]{"Group Info", "Location", "Training", "Send"};

        return tabTitles[position];
    }
}
