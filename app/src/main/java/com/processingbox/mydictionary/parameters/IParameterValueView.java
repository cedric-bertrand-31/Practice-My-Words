package com.processingbox.mydictionary.parameters;

public interface IParameterValueView extends IParameterView {

  String getValue();

  void clicked();

  String getPreferencesTag();
}
