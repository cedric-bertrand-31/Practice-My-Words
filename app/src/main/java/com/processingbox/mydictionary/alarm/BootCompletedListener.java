package com.processingbox.mydictionary.alarm;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.processingbox.mydictionary.MainApplication;

/**
 * Receiver called when {@link Intent#ACTION_BOOT_COMPLETED} is triggered
 * @author: Cédric BERTRAND
 * @date: 01/05/2016.
 */
public class BootCompletedListener extends BroadcastReceiver {

  @Override
  public void onReceive(final Context context, final Intent intent) {
    try {
      MainApplication.refreshServices();
    } catch (Exception e) {
    } catch (Error e) {
    }
  }
}
