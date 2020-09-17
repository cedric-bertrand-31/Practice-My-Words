package com.processingbox.mydictionary.alarm;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Handler;

import com.processingbox.mydictionary.Constants;
import com.processingbox.mydictionary.MainApplication;

/**
 * Receiver that is called by a recursive alarm. This class is in charge to send the non-synchronized data with the
 * backend
 * @author: Cédric BERTRAND
 * @date: 01/05/2016.
 */
public class DBSender extends BroadcastReceiver {

  /** To avoid to perform too many DB connection a timer is set and switch this variable state */
  public static boolean isConnectionAllowed = true;

  @Override
  public void onReceive(Context context, Intent intent) {
    synchronizeLocalStorageWithDB();
  }

  /**
   * Manage the downstream and upstream DB connection so data is the same on local device and on external DB
   */
  public static void synchronizeLocalStorageWithDB() {
    if (isConnectionAllowed) {
      manageConnectionAccess();
      uploadDataToExternalDB();
      if (isConnectionAllowed()) {
        downloadDataToLocalDB();
        MainApplication.savePreference(Constants.PREFERENCES_LAST_EXTERNAL_CONNECTION_TIME_MILLIS, System.currentTimeMillis());
      }
    }
  }

  /**
   * Start a timer that will unlock the DB connection after {@link Constants#DELAY_MINUTE_RESET_CONNECTION_TO_DB}
   * minutes
   */
  private static void manageConnectionAccess() {
    isConnectionAllowed = false;
    new Handler().postDelayed(new Runnable() {
      public void run() {
        isConnectionAllowed = true;
      }
    }, Constants.DELAY_MINUTE_RESET_CONNECTION_TO_DB * 60000);
  }

  /**
   * @return true if last DB connection has been made more than {@link Constants#NB_MINUTES_REFRESH_WITH_EXTERNAL}
   * minutes ago
   */
  private static boolean isConnectionAllowed() {
    long lastRefresh = MainApplication.getLongPreference(Constants.PREFERENCES_LAST_EXTERNAL_CONNECTION_TIME_MILLIS);
    return MainApplication.getUserId() >= 0 && (lastRefresh < 0 || System.currentTimeMillis() - lastRefresh > Constants.NB_MINUTES_REFRESH_WITH_EXTERNAL * 60000);
  }

  /**
   * Send non synchronized local data with external DB
   */
  private static void uploadDataToExternalDB() {
    MainApplication.localToExternalDB(false);
    MainApplication.synchronizeRemoveFromLocalDB();
    MainApplication.synchronizeUpdatedMarks();
  }

  /**
   * Recover data from external DB and store it locally
   */
  public static void downloadDataToLocalDB() {
    MainApplication.externalDBToLocal();
  }
}
