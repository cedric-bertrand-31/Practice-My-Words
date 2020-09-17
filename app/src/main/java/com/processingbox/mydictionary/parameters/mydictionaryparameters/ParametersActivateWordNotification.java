package com.processingbox.mydictionary.parameters.mydictionaryparameters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;

import com.processingbox.mydictionary.Constants;
import com.processingbox.mydictionary.MainActivity;
import com.processingbox.mydictionary.MainApplication;
import com.processingbox.mydictionary.R;
import com.processingbox.mydictionary.parameters.EnumParameters;
import com.processingbox.mydictionary.parameters.IParameterView;

/**
 * Created by admin on 04/09/2016.
 */
public class ParametersActivateWordNotification implements IParameterView {

  @Override
  public View buildView(EnumParameters parameter, ViewGroup parent) {
    final Context context = MainApplication.instance;
    View view = ((LayoutInflater) MainApplication.instance.getSystemService(Context.LAYOUT_INFLATER_SERVICE)).inflate(R.layout.row_activate_notification, parent, false);
    CheckBox cbActivateNotification = (CheckBox) view.findViewById(R.id.checkBoxActivateNotification);
    cbActivateNotification.setChecked(MainApplication.isNotificationWordActivated());
    cbActivateNotification.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
      @Override
      public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
        MainApplication.savePreference(Constants.PREFERENCES_NOTIFICATION_ACTIVATED, isChecked);
        MainActivity.showToast(context.getString(R.string.preferencesSaved));
        MainApplication.refreshNotification();
      }
    });
    return view;
  }
}
