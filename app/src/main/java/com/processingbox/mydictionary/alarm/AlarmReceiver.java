package com.processingbox.mydictionary.alarm;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import androidx.core.app.NotificationCompat;
import androidx.core.content.ContextCompat;

import com.processingbox.mydictionary.Constants;
import com.processingbox.mydictionary.MainApplication;
import com.processingbox.mydictionary.MainModel;
import com.processingbox.mydictionary.R;
import com.processingbox.mydictionary.activities.practicetest.UniqueQuestionActivity;
import com.processingbox.mydictionary.model.Dictionary;
import com.processingbox.mydictionary.model.WeightedListOfWords;
import com.processingbox.mydictionary.model.Word;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Random;

/**
 * Receiver that will be called recursively for triggering again the notification. In fact, this workaround has been set
 * to avoid that Android's overlay of manufacturers kills the {@link android.app.AlarmManager#setInexactRepeating(int,
 * long, long, PendingIntent)} process. For that, the App reset the notification {@link PendingIntent} regularly.
 * @author: Cédric BERTRAND
 * @date: 01/05/2016.
 */
public class AlarmReceiver extends BroadcastReceiver {

  @Override
  public void onReceive(Context context, Intent intent) {
    if (!isCurrentTimeInUserPreferencesInterval()) {
      return;
    }
    List<Dictionary> dictionaries = getAllNonEmptyDictionaries();
    if (dictionaries.isEmpty()) {
      return;
    }

    Random random = new Random();
    int indexDictionary = random.nextInt(dictionaries.size());
    Dictionary selectedDictionary = dictionaries.get(indexDictionary);
    List<Word> wordsToTest = selectedDictionary.getPracticeTestWords();
    WeightedListOfWords wRandom = new WeightedListOfWords(wordsToTest);

    int indexWord = wRandom.next();
    Word randomWord = wordsToTest.get(indexWord);
    boolean isReversed = random.nextBoolean();
    String word = isReversed ? randomWord.getTranslation() : randomWord.getWord();
    int indexChosenWord = selectedDictionary.getWords().indexOf(randomWord);

    Intent notificationIntent = buildNotificationIntent(indexDictionary, indexChosenWord, isReversed);
    NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);

    PendingIntent pendingIntent = PendingIntent.getActivity(context, 0, notificationIntent, PendingIntent.FLAG_UPDATE_CURRENT);

    NotificationCompat.Builder mNotifyBuilder = new NotificationCompat.Builder(context, Constants.ALARM_MANAGER_CHANNEL_ID).setSmallIcon(R.drawable.notification).setContentTitle(MainApplication.instance.getString(R.string.practiceTestSentence, isReversed ? selectedDictionary.getDisplayLanguage() : Locale.getDefault().getDisplayLanguage())).setContentText(word).setAutoCancel(true).setWhen(System.currentTimeMillis()).setColor(ContextCompat.getColor(context, R.color.material_blue_500)).setContentIntent(pendingIntent);
    notificationManager.notify(Constants.ALARM_RECEIVER_ID, mNotifyBuilder.build());
  }

  /**
   * @return all user's dictionaries stored into preferences that are not empty.
   */
  private List<Dictionary> getAllNonEmptyDictionaries() {
    List<Dictionary> dictionaries = new ArrayList<>(MainModel.getDictionaries());
    Iterator<Dictionary> iterator = dictionaries.iterator();
    while (iterator.hasNext()) {
      if (iterator.next().getWords().isEmpty()) {
        iterator.remove();
      }
    }
    return dictionaries;
  }

  /**
   * User can set the interval of time when he wants to receive notifications. This function check if the current time
   * is in this interval
   * @return true if notification can be sent, it means that current time is in user preferences interval
   */
  private boolean isCurrentTimeInUserPreferencesInterval() {
    int hour = Calendar.getInstance().get(Calendar.HOUR_OF_DAY);
    int hourStartNotification = MainApplication.getHourStartDisplayNotification();
    int hourEndNotification = MainApplication.getHourEndDisplayNotification();
    return hour >= hourStartNotification && hour <= hourEndNotification;
  }

  /**
   * Build the key/value information needed for displaying notification for testing user knowledge
   * @param indexDictionary yje chosen dictionary index
   * @param indexChosenWord the index of the choseen word in the selected dictionary
   * @param isReversed if true, the translation of the word is displayed and user needs to find word in his language.
   * @return The {@link Intent} containing information needed for displaying notification
   */
  private Intent buildNotificationIntent(final int indexDictionary, final int indexChosenWord, final boolean isReversed) {
    Intent notificationIntent = new Intent(MainApplication.instance, UniqueQuestionActivity.class);
    notificationIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
    notificationIntent.putExtra(Constants.IS_REVERSED_EXTRA_INTENT, isReversed);
    notificationIntent.putExtra(Constants.DICTIONARY_ID_EXTRA_INTENT, indexDictionary);
    notificationIntent.putExtra(Constants.WORD_ID_EXTRA_INTENT, indexChosenWord);
    return notificationIntent;
  }
}
