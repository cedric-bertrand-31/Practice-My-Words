package com.processingbox.mydictionary.parameters;

import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

public class ParametersAdapter extends BaseAdapter {

  private final EnumParameters[] parameters;

  public ParametersAdapter() {
    parameters = EnumParameters.values();
  }

  @Override
  public int getCount() {
    return parameters.length;
  }

  @Override
  public EnumParameters getItem(final int position) {
    return parameters[position];
  }

  @Override
  public long getItemId(final int position) {
    return position;
  }

  @Override
  public View getView(final int position, final View view, final ViewGroup parent) {
    View buildedView = getItem(position).getView(parent);
    View returnedView = buildedView != null ? buildedView : view;
    return returnedView;
  }
}
