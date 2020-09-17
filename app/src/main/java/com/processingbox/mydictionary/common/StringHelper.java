package com.processingbox.mydictionary.common;

import java.text.Normalizer;

/**
 * Common functions that manage Strings
 * @author: Cédric BERTRAND
 * @date: 10/05/2016.
 */
public class StringHelper {

  /**
   * @param string the {@link String} to process
   * @return the input {@link String} with uppercase first character
   */
  public static String upperLetterFirstLetter(final String string) {
    return string.substring(0, 1).toUpperCase() + string.substring(1);
  }

  /**
   * The Levenshtein distance is a string metric for measuring the difference between two sequences. Informally, the
   * Levenshtein distance between two words is the minimum number of single-character edits (insertions, deletions or
   * substitutions) required to change one word into the other
   * @param string1 the first {@link String} to compare
   * @param string2 the second {@link String} to compare
   * @return the distance
   */
  public static int levenshteinDistance(final String string1, final String string2) {
    // Ignore accents for comparing strings
    String string1NoAccent = removeAccents(string1);
    String string2NoAccent = removeAccents(string2);

    int len0 = string1NoAccent.length() + 1;
    int len1 = string2NoAccent.length() + 1;

    // the array of distances
    int[] cost = new int[len0];
    int[] newcost = new int[len0];

    // initial cost of skipping prefix in String s0
    for (int i = 0; i < len0; i++) {
      cost[i] = i;
    }

    // dynamically computing the array of distances

    // transformation cost for each letter in s1
    for (int j = 1; j < len1; j++) {
      // initial cost of skipping prefix in String s1
      newcost[0] = j;

      // transformation cost for each letter in s0
      for (int i = 1; i < len0; i++) {
        // matching current letters in both strings
        int match = (string1NoAccent.charAt(i - 1) == string2NoAccent.charAt(j - 1)) ? 0 : 1;

        // computing cost for each transformation
        int cost_replace = cost[i - 1] + match;
        int cost_insert = cost[i] + 1;
        int cost_delete = newcost[i - 1] + 1;

        // keep minimum cost
        newcost[i] = Math.min(Math.min(cost_insert, cost_delete), cost_replace);
      }

      // swap cost/newcost arrays
      int[] swap = cost;
      cost = newcost;
      newcost = swap;
    }

    // the distance is the cost for transforming all letters in both strings
    return cost[len0 - 1];
  }

  /**
   * Remove accent of given {@link String}
   * @param string the input {@link String} with potential accents
   * @return the input {@link String} without accent
   */
  public static String removeAccents(final String string) {
    return Normalizer.normalize(string, Normalizer.Form.NFD).replaceAll("[^\\p{ASCII}]", "");
  }
}
