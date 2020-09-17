package com.processingbox.mydictionary.parameters;

import android.view.View;
import android.view.ViewGroup;

import com.processingbox.mydictionary.R;
import com.processingbox.mydictionary.parameters.mydictionaryparameters.IntervalNotificationBuilder;
import com.processingbox.mydictionary.parameters.mydictionaryparameters.NumberOfWordsPerTestBuilder;
import com.processingbox.mydictionary.parameters.mydictionaryparameters.ParameterSortWordBuilder;
import com.processingbox.mydictionary.parameters.mydictionaryparameters.ParametersActivateWordNotification;
import com.processingbox.mydictionary.parameters.mydictionaryparameters.PeriodNotificationBuilder;

public enum EnumParameters {
  ENABLE_WORD_NOTIFICATION(R.string.wordNotification, ParametersActivateWordNotification.class),
  INTERVAL_NOTIFICATION(R.string.intervalTimeNotification, IntervalNotificationBuilder.class),
  PERIOD_NOTIFICATION(R.string.periodActivateNotification, PeriodNotificationBuilder.class),
  SORT_WORD(R.string.wordOrder, ParameterSortWordBuilder.class),
  NUMBER_OF_WORDS_PER_TEST(R.string.numberOfWordsPerTest, NumberOfWordsPerTestBuilder.class);

  /** The idString */
  private final int idString;
  /** The parameterViewClass */
  private final Class<? extends IParameterView> parameterViewClass;

  EnumParameters(final int idString, final Class<? extends IParameterView> parameterViewClass) {
    this.idString = idString;
    this.parameterViewClass = parameterViewClass;
  }

  public int getIdString() {
    return idString;
  }

  public View getView(final ViewGroup parent) {
    IParameterView paramView = getParameterBuilder();
    return paramView != null ? paramView.buildView(this, parent) : null;
  }

  private IParameterView getParameterBuilder() {
    try {
      return parameterViewClass.newInstance();
    } catch (InstantiationException e) {
    } catch (IllegalAccessException e) {
    }
    return null;
  }

  public String getValue() {
    IParameterView paramBuilder = getParameterBuilder();
    return paramBuilder instanceof IParameterValueView ? ((IParameterValueView) paramBuilder).getValue() : null;
  }

  public void clicked() {
    IParameterView paramBuilder = getParameterBuilder();
    if (paramBuilder instanceof IParameterValueView) {
      ((IParameterValueView) paramBuilder).clicked();
    }
  }
}
