package com.processingbox.mydictionary;

/**
 * Created by admin on 03/04/2016.
 */
public interface Constants {

  /** The COLOR_LIST */
  int[] COLOR_LIST = new int[]{//
      MainApplication.instance.getResources().getColor(R.color.redListWord), //
      MainApplication.instance.getResources().getColor(R.color.blueListWord), //
      MainApplication.instance.getResources().getColor(R.color.orangeListWord), //
      MainApplication.instance.getResources().getColor(R.color.violetListWord), //
      MainApplication.instance.getResources().getColor(R.color.greenListWord), //
      MainApplication.instance.getResources().getColor(R.color.yellowListWord)};

  /** The SHORT_COLOR_LIST */
  int[] SHORT_COLOR_LIST = new int[]{//
      MainApplication.instance.getResources().getColor(R.color.blueListWord), //
      MainApplication.instance.getResources().getColor(R.color.orangeListWord), //
      MainApplication.instance.getResources().getColor(R.color.violetListWord), //
      MainApplication.instance.getResources().getColor(R.color.purpleListWord)};

  int DEFAULT_INTERVAL_MINUTES_NOTIFICATION = 60;
  int DEFAULT_NUMBER_OF_WORDS_PER_TEST = 20;
  int WORD_OCCURRENCE_DIFFERENCE_REMOVE_FROM_TEST = 2;
  int INITIAL_MARK = 3;
  int MIN_MARK = 1;
  int MAX_MARK = 5;
  int NB_MINUTES_REFRESH_WITH_EXTERNAL = 5;
  int DELAY_MINUTE_RESET_CONNECTION_TO_DB = 2;

  String KEY_LIST_LANG = "KEY_LIST_LANG";
  String KEY_IS_TRANSLATION_REVERSED = "KEY_IS_TRANSLATION_REVERSED";
  String KEY_IS_WORD_SORTED = "KEY_IS_WORD_SORTED";
  String INDEX_NEW_DICTIONARY = "INDEX_NEW_DICTIONARY";
  String INDEX_DICTIONARY = "INDEX_DICTIONARY";
  String KEY_DELETED_WORDS = "KEY_DELETED_WORDS";
  String KEY_UPDATED_MARK_WORDS = "KEY_UPDATED_MARK_WORDS";

  String PREFERENCES_USER_ID = "PREFERENCES_USER_ID";
  String PREFERENCES_INDEX_SELECTED_DICTIONARY = "PREFERENCES_INDEX_SELECTED_DICTIONARY";
  String PREFERENCES_NOTIFICATION_ACTIVATED = "PREFERENCES_NOTIFICATION_ACTIVATED";
  String PREFERENCES_INTERVAL_MINUTES_SHOW_NOTIFICATION = "PREFERENCES_INTERVAL_MINUTES_SHOW_NOTIFICATION";
  String PREFERENCES_START_DISPLAYING_NOTIFICATION = "PREFERENCES_START_DISPLAYING_NOTIFICATION";
  String PREFERENCES_END_DISPLAYING_NOTIFICATION = "PREFERENCES_END_DISPLAYING_NOTIFICATION";
  String PREFERENCES_NUMBER_OF_WORDS_PER_TEST = "PREFERENCES_NUMBER_OF_WORDS_PER_TEST";
  String PREFERENCES_LAST_EXTERNAL_CONNECTION_TIME_MILLIS = "PREFERENCES_LAST_EXTERNAL_CONNECTION_TIME_MILLIS";
  String PREFERENCES_LAST_REQUEST_ID_TIME_MILLIS = "PREFERENCES_LAST_REQUEST_ID_TIME_MILLIS";

  int NEW_DICTIONARY_ID = 0;
  int DICTIONARIES_ID = 1;

  String DICTIONARY_ID_EXTRA_INTENT = "DICTIONARY_ID_EXTRA_INTENT";
  String WORD_ID_EXTRA_INTENT = "WORD_ID_EXTRA_INTENT";
  String IS_REVERSED_EXTRA_INTENT = "IS_REVERSED_EXTRA_INTENT";

  int ALARM_RECEIVER_ID = 0;
  String ALARM_MANAGER_CHANNEL_ID = "ALARM_MANAGER_CHANNEL_ID";
}
