package com.processingbox.mydictionary.parameters.mydictionaryparameters;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.view.View;

import com.processingbox.mydictionary.Constants;
import com.processingbox.mydictionary.MainActivity;
import com.processingbox.mydictionary.MainApplication;
import com.processingbox.mydictionary.R;
import com.processingbox.mydictionary.parameters.ParametersActivity;
import com.processingbox.mydictionary.parameters.RowParameterBuilder;

import org.florescu.android.rangeseekbar.RangeSeekBar;

public class PeriodNotificationBuilder extends RowParameterBuilder {

  @Override
  public String getValue() {
    return getHourStartDisplayNotification() + "H - " + getHourEndDisplayNotification() + "H";
  }

  private int getHourStartDisplayNotification() {
    return MainApplication.getHourStartDisplayNotification();
  }

  private int getHourEndDisplayNotification() {
    return MainApplication.getHourEndDisplayNotification();
  }

  @Override
  public void clicked() {
    ParametersActivity instance = ParametersActivity.instance;
    final AlertDialog alertDialog = new AlertDialog.Builder(instance).create();
    View layoutSeekBar = View.inflate(instance, R.layout.period_activate_notification, null);
    final RangeSeekBar<Integer> seekBar = initSeekBar(layoutSeekBar);
    alertDialog.setView(layoutSeekBar);
    DialogInterface.OnClickListener listener = new DialogInterface.OnClickListener() {
      @Override
      public void onClick(DialogInterface dialog, int which) {
        if (seekBar != null && which == DialogInterface.BUTTON_POSITIVE) {
          int start = seekBar.getSelectedMinValue();
          int end = seekBar.getSelectedMaxValue();
          MainApplication.savePreference(Constants.PREFERENCES_START_DISPLAYING_NOTIFICATION, start);
          MainApplication.savePreference(Constants.PREFERENCES_END_DISPLAYING_NOTIFICATION, end);
          MainActivity.showToast(MainApplication.getStringFromId(R.string.preferencesSaved));
          ParametersActivity.instance.refreshGUI();
          MainApplication.refreshNotification();
        }
      }
    };
    alertDialog.setButton(AlertDialog.BUTTON_POSITIVE, MainApplication.getStringFromId(android.R.string.yes), listener);
    alertDialog.setButton(AlertDialog.BUTTON_NEGATIVE, MainApplication.getStringFromId(android.R.string.no), listener);
    alertDialog.show();
  }

  private RangeSeekBar<Integer> initSeekBar(View layoutSeekBar) {
    final RangeSeekBar seekBar = (RangeSeekBar) layoutSeekBar.findViewById(R.id.seekBarRange);
    if (seekBar != null) {
      seekBar.setRangeValues(0, 24);
      seekBar.setSelectedMinValue(getHourStartDisplayNotification());
      seekBar.setSelectedMaxValue(getHourEndDisplayNotification());
      seekBar.setNotifyWhileDragging(true);
    }
    return seekBar;
  }

  @Override
  public String getPreferencesTag() {
    return Constants.PREFERENCES_START_DISPLAYING_NOTIFICATION;
  }
}
