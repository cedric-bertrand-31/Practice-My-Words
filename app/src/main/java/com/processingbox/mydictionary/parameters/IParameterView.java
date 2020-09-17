package com.processingbox.mydictionary.parameters;

import android.view.View;
import android.view.ViewGroup;

public interface IParameterView {

  View buildView(EnumParameters parameter, final ViewGroup parent);
}
