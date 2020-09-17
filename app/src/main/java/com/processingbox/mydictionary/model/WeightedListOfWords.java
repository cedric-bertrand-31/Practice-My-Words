package com.processingbox.mydictionary.model;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class WeightedListOfWords {

  private final Random random = new Random();
  private float total = 0;
  private List<IndexedWord> words = new ArrayList<>();

  public WeightedListOfWords(List<Word> words) {
    copyWords(words, words.size());
  }

  private void copyWords(List<Word> words, int numberOfWords) {
    for (int i = 0; i < numberOfWords; i++) {
      this.words.add(new IndexedWord(words.get(i), i));
      total += words.get(i).getAnswerCoefficientMark();
    }
  }

  public int next() {
    float value = random.nextFloat() * total;
    int indexWord = getCeilingEntry(value);
    IndexedWord indexedWord = words.get(indexWord);
    total -= indexedWord.getAnswerCoefficientMark();
    words.remove(indexWord);
    return indexedWord.getIndex();
  }

  private int getCeilingEntry(float value) {
    float sum = 0;
    int size = words.size();
    for (int i = 1; i < size; i++) {
      sum += words.get(i).getAnswerCoefficientMark();
      if (sum > value) {
        return i - 1;
      }
    }
    return size - 1;
  }
}