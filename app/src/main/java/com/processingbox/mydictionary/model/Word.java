package com.processingbox.mydictionary.model;

import com.processingbox.mydictionary.Constants;
import com.processingbox.mydictionary.MainActivity;
import com.processingbox.mydictionary.common.sorting.ISortObject;

/**
 * Created by admin on 04/04/2016.
 */
public class Word implements Comparable, ISortObject {

  private String word;
  private String translation;
  private boolean isSynchronized = false;
  private float answerCoefficientMark = Constants.INITIAL_MARK;
  private int occurrencesInPracticeTest = 0;

  public Word(String word, String translation) {
    this.word = word;
    this.translation = translation;
  }

  public Word(String word, String translation, boolean isSynchronized) {
    this.word = word;
    this.translation = translation;
    this.isSynchronized = isSynchronized;
  }

  public Word(String word, String translation, float answerCoefficientMark) {
    this.word = word;
    this.translation = translation;
    this.answerCoefficientMark = answerCoefficientMark;
  }

  public Word(String word, String translation, float answerCoefficientMark, boolean isSynchronized) {
    this.word = word;
    this.translation = translation;
    this.answerCoefficientMark = answerCoefficientMark;
    this.isSynchronized = isSynchronized;
  }

  public void setWord(String word) {
    this.word = word;
  }

  public void setTranslation(String translation) {
    this.translation = translation;
  }

  public String getWord() {
    return word;
  }

  public String getTranslation() {
    return translation;
  }

  public boolean isSynchronized() {
    return isSynchronized;
  }

  public void setSynchronized(boolean aSynchronized) {
    isSynchronized = aSynchronized;
  }

  public void setAnswerCoefficientMark(float answerCoefficientMark) {
    this.answerCoefficientMark = answerCoefficientMark;
  }

  public float getAnswerCoefficientMark() {
    return answerCoefficientMark;
  }

  public int getOccurrencesInPracticeTest() {
    return occurrencesInPracticeTest;
  }

  public void setOccurrencesInPracticeTest(int occurrencesInPracticeTest) {
    this.occurrencesInPracticeTest = occurrencesInPracticeTest;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || !(o instanceof Word)) {
      return false;
    }

    Word word1 = (Word) o;

    if (word != null ? !word.equals(word1.word) : word1.word != null) {
      return false;
    }
    return translation != null ? translation.equals(word1.translation) : word1.translation == null;
  }

  @Override
  public int hashCode() {
    int result = word != null ? word.hashCode() : 0;
    result = 31 * result + (translation != null ? translation.hashCode() : 0);
    return result;
  }

  public String getStringForComparison() {
    return !MainActivity.isTranslationReversed() ? word : translation;
  }

  @Override
  public int compareTo(Object another) {
    String anotherString = another instanceof Word ? ((Word) another).getStringForComparison() : another.toString();
    return getStringForComparison().compareTo(anotherString);
  }

  public boolean updateAnswerCoefficientMark(boolean isAnswerCorrect) {
    occurrencesInPracticeTest++;
    float previousMark = answerCoefficientMark;
    answerCoefficientMark *= isAnswerCorrect ? 0.85f : 1.1f;
    answerCoefficientMark = answerCoefficientMark < Constants.MIN_MARK ? Constants.MIN_MARK : answerCoefficientMark;
    answerCoefficientMark = answerCoefficientMark > Constants.MAX_MARK ? Constants.MAX_MARK : answerCoefficientMark;
    return Math.abs(previousMark - answerCoefficientMark) > 1E-3f;
  }

  public float getWeight() {
    return answerCoefficientMark;
  }

  @Override
  public double getComparableValue() {
    return occurrencesInPracticeTest;
  }
}
