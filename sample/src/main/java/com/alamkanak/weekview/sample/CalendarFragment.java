package com.alamkanak.weekview.sample;

import android.support.v4.app.Fragment;

/**
 * Created by mangospring5 on 21/7/16.
 */
public class CalendarFragment extends Fragment {

  protected static String TAG = CalendarFragment.class.getName();

  private static CalendarFragment instance;

  public static CalendarFragment getInstance() {
    if (instance == null) {
      instance = new CalendarFragment();
    }
    return instance;
  }
}
