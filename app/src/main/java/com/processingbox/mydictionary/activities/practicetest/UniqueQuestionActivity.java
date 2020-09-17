package com.processingbox.mydictionary.activities.practicetest;

import android.os.Bundle;
import android.view.View;

import com.processingbox.mydictionary.Constants;
import com.processingbox.mydictionary.MainApplication;
import com.processingbox.mydictionary.MainModel;
import com.processingbox.mydictionary.R;

import java.util.Random;

/**
 * Activity showing a single word to translate by user.
 * @author: Cédric BERTRAND
 * @date: 15/09/2020.
 */
public class UniqueQuestionActivity extends PracticeTestActivity {

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    findViewById(R.id.textViewPracticeProgression).setVisibility(View.GONE);
  }

  @Override
  protected void initModel() {
    int indexDictionary = getIntent().getIntExtra(Constants.DICTIONARY_ID_EXTRA_INTENT, -1);
    indexWord = getIntent().getIntExtra(Constants.WORD_ID_EXTRA_INTENT, -1);
    if (indexDictionary < 0 || indexWord < 0) {
      finish();
      return;
    }
    isReverse = getIntent().getBooleanExtra(Constants.IS_REVERSED_EXTRA_INTENT, false);
    selectedDictionary = MainModel.getDictionary(indexDictionary);
    listRemainingIndex.clear();
    listRemainingIndex.add(indexWord);
    displayRandomWord();
  }

  @Override
  protected int getColorForThisQuestion() {
    return MainApplication.getColorFromShortList(new Random().nextInt(Constants.SHORT_COLOR_LIST.length));
  }

  @Override
  protected void newWordSelection() {
  }

  @Override
  protected void showSummaryDialog() {
    finish();
  }

  @Override
  protected int getNumberOfWords() {
    return 1;
  }
}
