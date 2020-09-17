package com.processingbox.mydictionary.common;

import android.app.Activity;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.view.View;
import android.view.inputmethod.InputMethodManager;

/**
 * Common functions for managing User Interface
 * @author: Cédric BERTRAND
 * @date: 15/09/2016
 */
public class UIHelper {

  /**
   * Hide the keyboard displayed by phone
   * @param activity the linked activity
   */
  public static void hideKeyboard(final Activity activity) {
    View view = activity.getCurrentFocus();
    if (view != null) {
      InputMethodManager imm = (InputMethodManager) activity.getSystemService(Context.INPUT_METHOD_SERVICE);
      imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
    }
  }

  /**
   * Copy the given {@link String} to clipboard
   * @param context the linked context0
   * @param string the {@link String} to copy to clipboard
   */
  public static void copyToClipBoard(Context context, String string) {
    ClipboardManager clipboard = (ClipboardManager) context.getSystemService(Context.CLIPBOARD_SERVICE);
    clipboard.setPrimaryClip(ClipData.newPlainText(string, string));
  }
}
