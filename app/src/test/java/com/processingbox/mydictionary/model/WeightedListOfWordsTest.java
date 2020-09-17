package com.processingbox.mydictionary.model;

import org.junit.Assert;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

public class WeightedListOfWordsTest {

  @Test
  public void constructionTest() throws Exception {
    List<Word> words = new ArrayList<>();
    words.add(new Word("un", "one"));
    words.add(new Word("deux", "two"));
    words.add(new Word("trois", "three"));
    words.add(new Word("quatre", "four"));
    WeightedListOfWords weightedList = new WeightedListOfWords(words);

    int size = words.size();
    List<Word> wordCopy = new ArrayList<>(words);
    for (int i = 0; i < size; i++) {
      Assert.assertTrue(wordCopy.remove(words.get(weightedList.next()))); // To insure that each call of next returns an other element
    }
  }
}