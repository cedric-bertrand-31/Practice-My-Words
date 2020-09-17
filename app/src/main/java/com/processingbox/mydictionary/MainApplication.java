package com.processingbox.mydictionary;

import android.app.AlarmManager;
import android.app.Application;
import android.app.PendingIntent;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.os.AsyncTask;
import android.text.TextUtils;

import androidx.annotation.NonNull;
import androidx.preference.PreferenceManager;

import com.processingbox.mydictionary.alarm.AlarmReceiver;
import com.processingbox.mydictionary.alarm.DBSender;
import com.processingbox.mydictionary.common.Time;
import com.processingbox.mydictionary.model.Dictionary;
import com.processingbox.mydictionary.model.EnumExternalDBResult;
import com.processingbox.mydictionary.model.SerializableWord;
import com.processingbox.mydictionary.model.Word;
import com.processingbox.mydictionary.model.WordDictionary;
import com.processingbox.mydictionary.parameters.mydictionaryparameters.NumberOfWordsPerTestBuilder;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.Calendar;
import java.util.List;
import java.util.Locale;
import java.util.Random;

import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

/**
 * Application object is an persistent object that can manage local storage using {@link SharedPreferences}. This class
 * is composed of a singleton instance and a lot of static method allowing to access from anywhere to {@link
 * SharedPreferences}
 * @author: Cédric BERTRAND
 * @date: 17/09/2020.
 */
public class MainApplication extends Application {

  /** Application instance is always alive, that allows to access shared preferences at any time */
  public static MainApplication instance;

  @Override
  public void onCreate() {
    super.onCreate();
    instance = this;
  }

  /**
   * Save the given key/value couple
   * @param key the key of the saved preference
   * @param value the value of the saved preference
   */
  public static void savePreference(final String key, final String value) {
    getSharedPreferences().edit().putString(key, value).apply();
  }

  /**
   * Save the given key/value couple
   * @param key the key of the saved preference
   * @param value the value of the saved preference
   */
  public static void savePreference(final String key, final int value) {
    getSharedPreferences().edit().putInt(key, value).apply();
  }

  /**
   * Save the given key/value couple
   * @param key the key of the saved preference
   * @param value the value of the saved preference
   */
  public static void savePreference(final String key, final long value) {
    getSharedPreferences().edit().putLong(key, value).apply();
  }

  /**
   * User have an unique ID provided by the backend.
   * @return the unique user ID
   */
  public static int getUserId() {
    int idUser = getIntPreference(Constants.PREFERENCES_USER_ID, BuildConfig.DEBUG ? 0 : -1);
    if (idUser < 0) {
      requestUserIdToExternalDB();
    }
    return idUser;
  }

  public static String getStringPreference(final String key, final String defaultValue) {
    return getSharedPreferences().getString(key, defaultValue);
  }

  public static long getLongPreference(final String key) {
    return getSharedPreferences().getLong(key, -1);
  }

  public static int getIntPreference(final String key, int defaultValue) {
    return getSharedPreferences().getInt(key, defaultValue);
  }

  private static SharedPreferences getSharedPreferences() {
    return PreferenceManager.getDefaultSharedPreferences(instance.getBaseContext());
  }

  public static void savePreference(final String key, final boolean isActivated) {
    getSharedPreferences().edit().putBoolean(key, isActivated).apply();
  }

  public static boolean getBooleanPreference(final String key) {
    return getBooleanPreference(key, false);
  }

  public static boolean getBooleanPreference(final String key, boolean defaultValue) {
    return getSharedPreferences().getBoolean(key, defaultValue);
  }

  public static int getColorFromList(int position) {
    return Constants.COLOR_LIST[position % Constants.COLOR_LIST.length];
  }

  public static int getColorFromShortList(int position) {
    return Constants.SHORT_COLOR_LIST[position % Constants.SHORT_COLOR_LIST.length];
  }

  /**
   * Create a limited text size thanks to text length. Big text will have a limited size while {@link String} with few
   * characters will have a bigger size
   * @param text the text to which a font size must be applied
   * @return a random text taking into account text length
   */
  public static float getRandomTextSize(String text) {
    float randomFloat = (new Random().nextFloat() * 0.2f + 0.9f);
    Resources resources = instance.getResources();
    int textLength = text.length();
    return randomFloat * ((textLength > 20 ? resources.getDimension(R.dimen.veryBigWordTextSize) : textLength > 14 ? resources.getDimension(R.dimen.smallWordTextSize) : textLength > 8 ? resources.getDimension(R.dimen.mediumWordTextSize) : resources.getDimension(R.dimen.smallWordTextSize)));
  }

  /**
   * Ask to external DB to provide an user ID
   */
  public static void requestUserIdToExternalDB() {
    long lastRefresh = MainApplication.getLongPreference(Constants.PREFERENCES_LAST_REQUEST_ID_TIME_MILLIS);
    if (System.currentTimeMillis() - lastRefresh > Constants.DELAY_MINUTE_RESET_CONNECTION_TO_DB * 60000) {
      savePreference(Constants.PREFERENCES_LAST_REQUEST_ID_TIME_MILLIS, System.currentTimeMillis());
      sendToDB("", BuildConfig.REQUEST_USER_ID, EnumExternalDBResult.REQUEST_ID, true);
    }
  }

  public static void localToExternalDB(boolean forceSending) {
    if (getUserId() < 0) {
      return;
    }
    try {
      String wordsAndId = buildJSONSendWordToExternalDB(MainModel.getDictionaries(), forceSending);
      sendToDB(wordsAndId, BuildConfig.REQUEST_WRITE_WORD, EnumExternalDBResult.WRITE_WORDS);
    } catch (JSONException ignored) {
    }
  }

  @NonNull
  public static String buildJSONSendWordToExternalDB(final List<Dictionary> dictionaries, final boolean forceSending) throws JSONException {
    JSONArray jsonArray = new JSONArray();
    for (Dictionary dictionary : dictionaries) {
      for (Word word : dictionary.getWords()) {
        if (!word.isSynchronized() || forceSending) {
          jsonArray.put(new SerializableWord(Locale.getDefault().getLanguage(), dictionary.getLanguage(), word.getTranslation(), word.getWord(), word.getAnswerCoefficientMark()).toJSONString());
        }
      }
    }
    JSONObject wordsAndId = new JSONObject();
    wordsAndId.put("id_user", getUserId());
    wordsAndId.put("words_array", jsonArray);
    return jsonArray.length() > 0 ? wordsAndId.toString() : "";
  }

  public static void externalDBToLocal() {
    if (getUserId() < 0) {
      return;
    }
    sendToDB("{\"id_user\":" + getUserId() + "}", BuildConfig.REQUEST_READ_USER_WORDS, EnumExternalDBResult.READ_WORDS);
  }

  public static void sendToDB(final String json, final String url, final EnumExternalDBResult resultType) {
    sendToDB(json, url, resultType, false);
  }

  public static void sendToDB(final String json, final String url, final EnumExternalDBResult resultType, boolean noParametersFunction) {
    if (!noParametersFunction && (TextUtils.isEmpty(json) || json.equals("[]"))) {
      DBSender.isConnectionAllowed = true;
      return;
    }
    new AsyncTask<String, Void, JSONObject>() {
      @Override
      protected JSONObject doInBackground(String... params) {
        String json = params[0];
        MediaType JSON = MediaType.parse("application/json; charset=utf-8");
        OkHttpClient client = new OkHttpClient();
        RequestBody body = RequestBody.create(JSON, json);
        Request request = new Request.Builder().url(url).post(body).build();
        try {
          System.setProperty("http.keepAlive", "false");
          Response response = client.newCall(request).execute();
          String phpResult = response.body().string();
          return new JSONObject(phpResult);
        } catch (Exception e) {
          e.printStackTrace();
        } catch (Error e) {
          e.printStackTrace();
        }
        return null;
      }

      @Override
      protected void onPostExecute(JSONObject result) {
        try {
          handleExternalDBResult(resultType, result, json);
        } catch (JSONException e) {
          e.printStackTrace();
        }
      }
    }.execute(json);
  }

  private synchronized static void handleExternalDBResult(EnumExternalDBResult resultType, JSONObject result, String jsonInput) throws JSONException {
    if (result != null && result.get("success").equals(1)) {
      switch (resultType) {
        case READ_WORDS:
          updateLocalDictionaries(result);
          break;
        case WRITE_WORDS:
          updateSyncStatusWrittenWords(result, jsonInput);
          break;
        case DELETE_WORDS:
          cleanListDeleted(result);
          break;
        case REQUEST_ID:
          updateUserId(result);
          break;
        case UPDATE_MARKS:
          cleanListMarkUpdated(result);
          break;
      }
      MainModel.saveInPreferences();
      DBSender.isConnectionAllowed = true;
    }
  }

  private static void updateUserId(JSONObject result) throws JSONException {
    int id = (int) result.get("id");
    MainApplication.savePreference(Constants.PREFERENCES_USER_ID, id);
    DBSender.downloadDataToLocalDB();
  }

  private static void updateSyncStatusWrittenWords(JSONObject result, String jsonInput) throws JSONException {
    JSONObject wordsAndId = new JSONObject(jsonInput);
    JSONArray jsonArray = (JSONArray) wordsAndId.get("words_array"); // Keep reading input to be sure it is deleted in local
    for (int i = 0; i < jsonArray.length(); i++) {
      SerializableWord sWord = SerializableWord.buildFromJSON(jsonArray.getString(i));
      Word word = MainModel.getWord(sWord.getTranslation_lang(), sWord.getTranslation(), sWord.getWord());
      word.setSynchronized(true);
    }
  }

  private static void cleanListDeleted(JSONObject result) throws JSONException {
    JSONArray mJsonArray = new JSONArray((String) result.get("updated"));
    for (int i = 0; i < mJsonArray.length(); i++) {
      try {
        SerializableWord sWord = SerializableWord.buildFromJSON(mJsonArray.getString(i));
        Dictionary dictionary = new Dictionary(sWord.getTranslation_lang());
        WordDictionary updatedWord = new WordDictionary(new Word(sWord.getTranslation(), sWord.getWord()), dictionary);
        MainModel.getDeletedWordNotSynchronizedWithDB().remove(updatedWord);
      } catch (JSONException ignored) {
      }
    }
  }

  private static void cleanListMarkUpdated(JSONObject result) throws JSONException {
    JSONArray mJsonArray = new JSONArray((String) result.get("updated"));
    for (int i = 0; i < mJsonArray.length(); i++) {
      try {
        SerializableWord sWord = SerializableWord.buildFromJSON(mJsonArray.getString(i));
        Dictionary dictionary = new Dictionary(sWord.getTranslation_lang());
        WordDictionary updatedWord = new WordDictionary(new Word(sWord.getTranslation(), sWord.getWord()), dictionary);
        MainModel.getWordsWithUpdatedMarks().remove(updatedWord);
      } catch (JSONException ignored) {
      }
    }
  }

  private static void updateLocalDictionaries(JSONObject result) throws JSONException {
    JSONArray mJsonArray = new JSONArray((String) result.get("words"));
    JSONObject mJsonObject = new JSONObject();
    boolean refreshUI = false, isNewWordsAdded = false;
    for (int i = 0; i < mJsonArray.length(); i++) {
      mJsonObject = new JSONObject(mJsonArray.getString(i));
      String lang = mJsonObject.getString("translation_lang");
      Dictionary dictionary = MainModel.getDictionary(lang);
      if (dictionary == null) {
        dictionary = new Dictionary(lang);
        MainModel.addDictionary(dictionary);
        refreshUI = true;
      }
      Word word = new Word(mJsonObject.getString("translation").trim(), mJsonObject.getString("word").trim(), (float) mJsonObject.getDouble("mark"), true);
      if (!dictionary.contains(word)) {
        dictionary.addWord(word);
        isNewWordsAdded = true;
      }
    }
    if (isNewWordsAdded) {
      MainActivity.notifyWordListChanged();
    }
    MainModel.saveInPreferences();
    if (refreshUI && MainActivity.instance != null && MainModel.getSelectedDictionary() != null) {
      MainActivity.instance.refreshAfterNewDictionaryAdded(MainModel.getSelectedDictionary());
    }
  }

  public static void synchronizeRemoveFromLocalDB() {
    if (getUserId() < 0) {
      return;
    }
    JSONArray jsonArray = new JSONArray();
    for (WordDictionary deletedWord : MainModel.getDeletedWordNotSynchronizedWithDB()) {
      Dictionary dictionary = deletedWord.getAssociatedDictionary();
      jsonArray.put(new SerializableWord(Locale.getDefault().getLanguage(), dictionary.getLanguage(), deletedWord.getTranslation(), deletedWord.getWord(), deletedWord.getAnswerCoefficientMark()).toJSONString());
    }
    try {
      JSONObject wordsAndId = new JSONObject();
      wordsAndId.put("id_user", getUserId());
      wordsAndId.put("words_array", jsonArray);
      if (jsonArray.length() > 0) {
        sendToDB(wordsAndId.toString(), BuildConfig.REQUEST_DELETE_WORDS, EnumExternalDBResult.DELETE_WORDS);
      }
    } catch (JSONException ignored) {
    }
  }

  public static void synchronizeUpdatedMarks() {
    if (getUserId() < 0) {
      return;
    }
    JSONArray jsonArray = new JSONArray();
    for (WordDictionary updatedMarksWords : MainModel.getWordsWithUpdatedMarks()) {
      Dictionary dictionary = updatedMarksWords.getAssociatedDictionary();
      jsonArray.put(new SerializableWord(Locale.getDefault().getLanguage(), dictionary.getLanguage(), updatedMarksWords.getTranslation(), updatedMarksWords.getWord(), updatedMarksWords.getAnswerCoefficientMark()).toJSONString());
    }
    try {
      JSONObject wordsAndId = new JSONObject();
      wordsAndId.put("id_user", getUserId());
      wordsAndId.put("words_array", jsonArray);
      if (jsonArray.length() > 0) {
        sendToDB(wordsAndId.toString(), BuildConfig.REQUEST_UPDATE_MARK_WORDS, EnumExternalDBResult.UPDATE_MARKS);
      }
    } catch (JSONException ignored) {
    }
  }

  public static int getIntervalMinutesNotification() {
    return getIntPreference(Constants.PREFERENCES_INTERVAL_MINUTES_SHOW_NOTIFICATION, Constants.DEFAULT_INTERVAL_MINUTES_NOTIFICATION);
  }

  public static int getNumberOfWordsPerTest() {
    return getIntPreference(Constants.PREFERENCES_NUMBER_OF_WORDS_PER_TEST, Constants.DEFAULT_NUMBER_OF_WORDS_PER_TEST);
  }

  public static boolean isPracticeTestAllWordsSelected() {
    return NumberOfWordsPerTestBuilder.isAllWordsSelected(getNumberOfWordsPerTest());
  }

  public static String getStringFromId(final int id) {
    return instance.getString(id);
  }

  public static int getHourStartDisplayNotification() {
    return getIntPreference(Constants.PREFERENCES_START_DISPLAYING_NOTIFICATION, 8);
  }

  public static int getHourEndDisplayNotification() {
    return getIntPreference(Constants.PREFERENCES_END_DISPLAYING_NOTIFICATION, 22);
  }

  public static void refreshServices() {
    MainApplication.refreshNotification();
    MainApplication.refreshDBSender();
  }

  public static void refreshNotification() {
    cancelNotifications();
    if (isNotificationWordActivated()) {
      activateNotifications();
    }
  }

  public static boolean isNotificationWordActivated() {
    return getBooleanPreference(Constants.PREFERENCES_NOTIFICATION_ACTIVATED, true);
  }

  private static void activateNotifications() {
    PendingIntent pendingIntent = getPendingIntentNotification();
    AlarmManager am = (AlarmManager) instance.getSystemService(ALARM_SERVICE);
    if (am != null) {
      long intervalPreferenceMillis = 60000L * MainApplication.getIntervalMinutesNotification();
      am.setInexactRepeating(AlarmManager.RTC_WAKEUP, getNextAlarmHour(intervalPreferenceMillis), intervalPreferenceMillis, pendingIntent);
    }
  }

  public static void cancelNotifications() {
    PendingIntent pendingIntent = getPendingIntentNotification();
    AlarmManager am = (AlarmManager) instance.getSystemService(ALARM_SERVICE);
    if (am != null) {
      am.cancel(pendingIntent);
    }
  }

  private static long getNextAlarmHour(long intervalPreferenceMillis) {
    int hourStart = MainApplication.getHourStartDisplayNotification();
    Time intervalTime = new Time(intervalPreferenceMillis);
    int hour = intervalTime.getHours();
    int nbMinutes = intervalTime.getMinutes();

    Calendar currentTime = Calendar.getInstance();
    currentTime.setTimeInMillis(System.currentTimeMillis());
    int factor = nbMinutes > 0 ? 1 + currentTime.get(Calendar.MINUTE) / nbMinutes : 1;

    if (hour > 1) {
      currentTime.set(Calendar.HOUR_OF_DAY, hour * (int) Math.floor((currentTime.get(Calendar.HOUR_OF_DAY) - hourStart) / (float) hour) + hourStart);
    }
    currentTime.set(Calendar.MINUTE, 0);
    currentTime.set(Calendar.SECOND, 0);

    return currentTime.getTimeInMillis() + intervalPreferenceMillis * factor;
  }

  private static PendingIntent getPendingIntentNotification() {
    Intent intent = new Intent(MainApplication.instance, AlarmReceiver.class);
    return PendingIntent.getBroadcast(MainApplication.instance, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
  }

  public static void refreshDBSender() {
    DBSender.synchronizeLocalStorageWithDB();
    cancelDBSender();
    activateDBSender();
  }

  private static void activateDBSender() {
    PendingIntent pendingIntent = getPendingIntentDBSender();
    AlarmManager am = (AlarmManager) instance.getSystemService(ALARM_SERVICE);
    if (am != null) {
      long intervalPreferenceMillis = 60000L * 360;
      am.setInexactRepeating(AlarmManager.RTC_WAKEUP, getNextAlarmHour(intervalPreferenceMillis), intervalPreferenceMillis, pendingIntent);
    }
  }

  public static void cancelDBSender() {
    PendingIntent pendingIntent = getPendingIntentDBSender();
    AlarmManager am = (AlarmManager) instance.getSystemService(ALARM_SERVICE);
    if (am != null) {
      am.cancel(pendingIntent);
    }
  }

  private static PendingIntent getPendingIntentDBSender() {
    Intent intent = new Intent(MainApplication.instance, DBSender.class);
    return PendingIntent.getBroadcast(MainApplication.instance, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
  }
}
