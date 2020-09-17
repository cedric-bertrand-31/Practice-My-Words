package com.processingbox.mydictionary.parameters.mydictionaryparameters;

import android.widget.SeekBar;

import com.processingbox.mydictionary.Constants;
import com.processingbox.mydictionary.MainApplication;
import com.processingbox.mydictionary.common.Time;
import com.processingbox.mydictionary.parameters.ParametersActivity;

import java.util.Arrays;

public class IntervalNotificationBuilder extends IntervalBuilder {

  private int[] minutesInterval = new int[]{15, 30, 60, 120, 240, 360, 480, 720, 1440};

  @Override
  public String getValue() {
    return getValue(getPreferenceInterval());
  }

  protected String getValue(int progress) {
    return new Time(0, progress).getText(false);
  }

  private int getPreferenceInterval() {
    return MainApplication.getIntervalMinutesNotification();
  }

  @Override
  protected void validateResult(final SeekBar seekBar) {
    int value = getValidProgress(seekBar.getProgress());
    MainApplication.savePreference(getPreferencesTag(), value);
    ParametersActivity.instance.refreshGUI();
    MainApplication.refreshNotification();
  }

  @Override
  protected int getMaxSeekBarValue() {
    return 8;
  }

  protected int getPreferenceIntervalTurnIntoSeekBarValue() {
    int nbMinutes = getPreferenceInterval();
    return Arrays.binarySearch(minutesInterval, nbMinutes);
  }

  protected int getValidProgress(int progress) {
    return minutesInterval[progress];
  }

  @Override
  public String getPreferencesTag() {
    return Constants.PREFERENCES_INTERVAL_MINUTES_SHOW_NOTIFICATION;
  }
}
