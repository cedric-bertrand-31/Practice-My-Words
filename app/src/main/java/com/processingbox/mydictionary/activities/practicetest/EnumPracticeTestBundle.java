package com.processingbox.mydictionary.activities.practicetest;

import android.os.Bundle;
import android.os.PersistableBundle;

/**
 * Keys used when saving bundle of Practice Test (when {@link android.app.Activity#onSaveInstanceState(Bundle,
 * PersistableBundle)}  is called}
 * @author: Cédric BERTRAND
 * @date: 15/09/2020.
 */
public enum EnumPracticeTestBundle {
  SAVE_LIST_REMAINING_INDEXES,
  SAVE_IS_REVERSE,
  SAVE_INDEX_WORD,
  SAVE_INDEX_DICTIONARY,
  SAVE_NB_CORRECT_ANSWER,
  SAVE_NB_ANSWER;
}
