package com.processingbox.mydictionary.parameters.mydictionaryparameters;

import com.processingbox.mydictionary.Constants;
import com.processingbox.mydictionary.MainApplication;
import com.processingbox.mydictionary.R;
import com.processingbox.mydictionary.parameters.SingleChoiceBuilder;

/**
 * Created by admin on 20/09/2016.
 */
public class ParameterSortWordBuilder extends SingleChoiceBuilder {

  public ParameterSortWordBuilder() {
    super(new String[]{MainApplication.getStringFromId(R.string.orderByDate), MainApplication.getStringFromId(R.string.orderAlphabetical)});
  }

  @Override
  protected void elementClicked(int position) {
    MainApplication.savePreference(Constants.KEY_IS_WORD_SORTED, position == 1);
  }

  @Override
  public String getValue() {
    return getChoices()[MainApplication.getBooleanPreference(Constants.KEY_IS_WORD_SORTED) ? 1 : 0];
  }

  @Override
  public String getPreferencesTag() {
    return Constants.KEY_IS_WORD_SORTED;
  }
}
