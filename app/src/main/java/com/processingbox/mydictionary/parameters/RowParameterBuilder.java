package com.processingbox.mydictionary.parameters;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.processingbox.mydictionary.MainApplication;
import com.processingbox.mydictionary.R;

public abstract class RowParameterBuilder implements IParameterValueView {

  @SuppressLint("NewApi")
  @Override
  public View buildView(final EnumParameters parameter, final ViewGroup parent) {
    Context context = MainApplication.instance;
    View view = ((LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE)).inflate(R.layout.row_parameters, parent, false);

    TextView txtTime = (TextView) view.findViewById(R.id.parameterName);
    txtTime.setText(context.getString(parameter.getIdString()));

    TextView txtName = (TextView) view.findViewById(R.id.parameterValue);
    txtName.setText(parameter.getValue());
    Integer backgroundColor = getBackgroundColor();
    if (backgroundColor != null) {
      view.setBackgroundColor(backgroundColor);
    }
    return view;
  }

  protected Integer getBackgroundColor() {
    return null;
  }

}
