package org.grameenfoundation.applabs.ledgerlinkmanager.adapters;

import android.content.Context;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import org.grameenfoundation.applabs.ledgerlinkmanager.fragments.VslaFragment;
import org.grameenfoundation.applabs.ledgerlinkmanager.fragments.LocationFragment;
import org.grameenfoundation.applabs.ledgerlinkmanager.fragments.SubmissionFragment;
import org.grameenfoundation.applabs.ledgerlinkmanager.fragments.TrainingFragment;

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
                return new VslaFragment();
            case 1:
                return new LocationFragment();
            case 2:
                return new TrainingFragment();
            case 3:
                return new SubmissionFragment();
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
