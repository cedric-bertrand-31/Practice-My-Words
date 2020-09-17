package com.processingbox.mydictionary.parameters.mydictionaryparameters;

import android.widget.SeekBar;

import com.processingbox.mydictionary.Constants;
import com.processingbox.mydictionary.MainApplication;
import com.processingbox.mydictionary.R;
import com.processingbox.mydictionary.parameters.ParametersActivity;

public class NumberOfWordsPerTestBuilder extends IntervalBuilder {

  /** The interval value of the seek bar. */
  private final static int INTERVAL_NUMBER = 10;
  /** The number of intervals of the seek bar. */
  private final static int NUMBER_OF_INTERVALS = 5;

  @Override
  public String getValue() {
    return getValue(getPreferenceValue());
  }

  protected String getValue(int progress) {
    return isAllWordsSelected(progress) ? MainApplication.getStringFromId(R.string.all) : super.getValue(progress);
  }

  public static boolean isAllWordsSelected(int progress) {
    return progress == (NUMBER_OF_INTERVALS + 1) * INTERVAL_NUMBER;
  }

  private int getPreferenceValue() {
    return MainApplication.getNumberOfWordsPerTest();
  }

  @Override
  protected void validateResult(final SeekBar seekBar) {
    MainApplication.savePreference(getPreferencesTag(), getValidProgress(seekBar.getProgress()));
    ParametersActivity.instance.refreshGUI();
  }

  @Override
  protected int getMaxSeekBarValue() {
    return NUMBER_OF_INTERVALS;
  }

  protected int getPreferenceIntervalTurnIntoSeekBarValue() {
    return getPreferenceValue() / INTERVAL_NUMBER - 1;
  }

  protected int getValidProgress(int progress) {
    return (progress + 1) * INTERVAL_NUMBER;
  }

  @Override
  public String getPreferencesTag() {
    return Constants.PREFERENCES_NUMBER_OF_WORDS_PER_TEST;
  }
}
