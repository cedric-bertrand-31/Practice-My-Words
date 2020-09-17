package com.processingbox.mydictionary.gui;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentStatePagerAdapter;

import com.processingbox.mydictionary.MainActivity;

public class CustomPagerAdapter extends FragmentStatePagerAdapter {

  public CustomPagerAdapter(final FragmentManager fm) {
    super(fm, FragmentStatePagerAdapter.BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT);
  }

  @Override
  public Fragment getItem(final int position) {
    return MainActivity.currentFragment;
  }

  @Override
  public int getCount() {
    return 1;
  }
}
