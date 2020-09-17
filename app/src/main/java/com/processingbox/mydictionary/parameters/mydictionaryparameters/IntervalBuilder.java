package com.processingbox.mydictionary.parameters.mydictionaryparameters;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.view.View;
import android.widget.SeekBar;
import android.widget.TextView;

import com.processingbox.mydictionary.MainActivity;
import com.processingbox.mydictionary.MainApplication;
import com.processingbox.mydictionary.R;
import com.processingbox.mydictionary.parameters.ParametersActivity;
import com.processingbox.mydictionary.parameters.RowParameterBuilder;

public abstract class IntervalBuilder extends RowParameterBuilder {

  protected String getValue(int progress) {
    return progress + "";
  }

  @Override
  public void clicked() {
    ParametersActivity instance = ParametersActivity.instance;
    final AlertDialog alertDialog = new AlertDialog.Builder(instance).create();
    View layoutSeekBar = View.inflate(instance, R.layout.interval_chooser, null);
    final SeekBar seekBar = initSeekBar(layoutSeekBar);
    alertDialog.setView(layoutSeekBar);
    DialogInterface.OnClickListener listener = new DialogInterface.OnClickListener() {
      @Override
      public void onClick(DialogInterface dialog, int which) {
        if (seekBar != null && which == DialogInterface.BUTTON_POSITIVE) {
          validateResult(seekBar);
          MainActivity.showToast(MainApplication.getStringFromId(R.string.preferencesSaved));
        }
      }
    };
    alertDialog.setButton(AlertDialog.BUTTON_POSITIVE, MainApplication.getStringFromId(android.R.string.yes), listener);
    alertDialog.setButton(AlertDialog.BUTTON_NEGATIVE, MainApplication.getStringFromId(android.R.string.no), listener);
    alertDialog.show();
  }

  protected abstract void validateResult(final SeekBar seekBar);

  private SeekBar initSeekBar(View layoutSeekBar) {
    final SeekBar seekBar = (SeekBar) layoutSeekBar.findViewById(R.id.seekBar);
    final TextView progressTextView = (TextView) layoutSeekBar.findViewById(R.id.textViewProgressSeekBar);
    if (seekBar != null) {
      seekBar.setMax(getMaxSeekBarValue());
      int savedProgress = getPreferenceIntervalTurnIntoSeekBarValue();
      seekBar.setProgress(savedProgress);
      progressTextView.setText(getValue(getValidProgress(savedProgress)));
      seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
        @Override
        public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
          progressTextView.setText(getValue(getValidProgress(progress)));
        }

        @Override
        public void onStartTrackingTouch(SeekBar seekBar) {
        }

        @Override
        public void onStopTrackingTouch(SeekBar seekBar) {
        }
      });
    }
    return seekBar;
  }

  protected abstract int getMaxSeekBarValue();

  protected abstract int getPreferenceIntervalTurnIntoSeekBarValue();

  protected abstract int getValidProgress(int progress);
}
