package com.processingbox.mydictionary.common;


import com.processingbox.mydictionary.MainApplication;
import com.processingbox.mydictionary.R;

import java.util.ArrayList;
import java.util.List;

/**
 * Custom time management class
 * @author: Cédric BERTRAND
 * @date: 10/05/2016.
 */
public class Time {

  /** The timeInMilliseconds */
  private long timeInMilliseconds;

  public Time(final int hours, final int minutes) {
    timeInMilliseconds = hours * 3600000l + minutes * 60000l;
  }

  public Time(final long timeInMilliseconds) {
    this.timeInMilliseconds = timeInMilliseconds;
  }

  private int getDays() {
    return (int) (timeInMilliseconds / 86400000);
  }

  public int getHours() {
    return (int) ((timeInMilliseconds % 86400000) / 3600000);
  }

  public int getMinutes() {
    return (int) (((timeInMilliseconds % 86400000) % 3600000) / 60000f);
  }

  public int getSeconds() {
    return (int) ((((timeInMilliseconds % 86400000) % 3600000) % 60000) / 1000f);
  }

  public String getText(final boolean displaySeconds) {
    return getText(displaySeconds, true);
  }

  /**
   * Format this time in a {@link String}
   * @param displaySeconds if the output {@link String} must display seconds
   * @param isFirstLoop use for recursive call, must be true when called
   * @return the formatted {@link String}
   */
  private String getText(final boolean displaySeconds, final boolean isFirstLoop) {
    int hours = getHours(), minutes = getMinutes(), seconds = getSeconds(), days = getDays(), indexMinutes = -1;
    String sDays, sHours, sMinutes, sSeconds;
    List<String> strings = new ArrayList<String>();
    if ((sDays = timeIntToString(days, R.string.day, R.string.days)) != null) {
      strings.add(sDays);
    }
    if ((sHours = timeIntToString(hours, R.string.hour, R.string.hours)) != null) {
      strings.add(sHours);
    }
    sSeconds = timeIntToString(seconds, R.string.second, R.string.seconds);
    if ((sMinutes = timeIntToString(minutes, R.string.minute, R.string.minutes)) != null) {
      indexMinutes = strings.size();
      strings.add(sMinutes);
    }

    if (strings.isEmpty() && !displaySeconds) {
      return buildInferiorOneMinuteText();
    } else {
      if (strings.size() <= 1 && displaySeconds && sSeconds != null && (minutes != 0 || isFirstLoop)) {
        strings.add(sSeconds);
      } else if (indexMinutes >= 0 && isFirstLoop && seconds > 0) {
        return new Time(timeInMilliseconds + 60000).getText(displaySeconds, false);
      }
    }

    int length = strings.size();
    String sReturn = "";
    for (int i = 0; i < length; i++) {
      sReturn += (i > 0 ? " " : "") + strings.get(i) + (i == length - 1 ? "" : i == length - 2 ? " " + MainApplication.getStringFromId(R.string.and) : ",");
    }
    return sReturn;
  }

  /**
   * Convert the int value to {@link String} managing the plural format.
   * @param timeInt the time int value
   * @param resourceSingular the singular {@link String} to display
   * @param resourcePlural the plural {@link String} to display
   * @return the formatted {@link String}
   */
  private String timeIntToString(final int timeInt, final int resourceSingular, final int resourcePlural) {
    return timeInt == 0 ? null : timeInt + " " + (timeInt > 1 ? MainApplication.getStringFromId(resourcePlural) : MainApplication.getStringFromId(resourceSingular));
  }

  /**
   * @return "> 1 minutes" translated
   */
  private String buildInferiorOneMinuteText() {
    return "> 1 " + MainApplication.getStringFromId(R.string.minute);
  }

  @Override
  public String toString() {
    int hours = getHours();
    int minutes = getMinutes();
    return hours == 0 ? minutes + " " + MainApplication.getStringFromId(R.string.minutes) : hours + "H" + (minutes > 0 ? minutes : "");
  }
}
