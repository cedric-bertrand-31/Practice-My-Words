package com.processingbox.mydictionary.model;

import androidx.annotation.NonNull;

import com.processingbox.mydictionary.Constants;
import com.processingbox.mydictionary.common.sorting.SortComparator;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Locale;

/**
 * Created by admin on 03/04/2016.
 */
public class Dictionary {
  /** The dictionaries */
  public final String language;
  /** The words */
  private final List<Word> words;

  public Dictionary(String language) {
    this.language = language;
    this.words = new ArrayList<>();
  }

  public String getLanguage() {
    return language;
  }

  public boolean contains(Word word) {
    return words.contains(word);
  }

  @NonNull
  public String getDisplayLanguage() {
    return new Locale(language).getDisplayLanguage();
  }

  public List<Word> getWords() {
    return words;
  }

  @Override
  @NonNull
  public String toString() {
    return language;
  }

  public void addWord(Word word) {
    words.add(word);
  }

  public void setWord(int index, Word word) { // For saving in JSON
    words.set(index, word);
  }

  public void deleteWord(int position) {
    words.remove(position);
  }

  public List<Word> getPracticeTestWords() {
    List<Word> practiceWords = getWordsRarelyProposedInPracticeTest();
    return practiceWords.isEmpty() ? words : practiceWords;
  }

  @NonNull
  private List<Word> getWordsRarelyProposedInPracticeTest() {
    List<Word> practiceWords = new ArrayList<>(words);
    Collections.sort(practiceWords, new SortComparator());

    if (!practiceWords.isEmpty()) {
      int minOccurrences = practiceWords.get(0).getOccurrencesInPracticeTest();
      int size = practiceWords.size() - 1;
      for (int i = size; i >= 0; i--) {
        int wordOccurrences = practiceWords.get(i).getOccurrencesInPracticeTest();
        if (wordOccurrences == 0 || (minOccurrences != 0 && wordOccurrences - minOccurrences <= Constants.WORD_OCCURRENCE_DIFFERENCE_REMOVE_FROM_TEST)) {
          break;
        }
        practiceWords.remove(i);
      }
    }
    return practiceWords;
  }
}
