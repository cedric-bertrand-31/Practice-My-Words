package com.processingbox.mydictionary.model;

import com.processingbox.mydictionary.Constants;

import org.junit.Assert;
import org.junit.Test;

import java.util.List;

public class DictionaryTest {

  @Test
  public void getPracticeTestWordsTest() throws Exception {
    Dictionary dic = buildNLDictionary();

    List<Word> words = dic.getWords();
    int halfSize = words.size() / 2;
    for (int i = 0; i < halfSize; i++) {
      words.get(i).setOccurrencesInPracticeTest(i + 1);
    }

    List<Word> practiceWords = dic.getPracticeTestWords();
    Assert.assertEquals(practiceWords.size(), words.size() - halfSize);
    for (int i = 0; i < practiceWords.size(); i++) {
      Assert.assertEquals(practiceWords.get(i), words.get(i + halfSize));
    }

    for (int i = words.size() - 1; i >= 0; i--) {
      words.get(words.size() - 1 - i).setOccurrencesInPracticeTest(i + 1);
    }

    practiceWords = dic.getPracticeTestWords();
    Assert.assertEquals(practiceWords.size(), Math.min(practiceWords.size(), Constants.WORD_OCCURRENCE_DIFFERENCE_REMOVE_FROM_TEST + 1));
    for (int i = 0; i < practiceWords.size(); i++) {
      Assert.assertEquals(practiceWords.get(i), words.get(words.size() - 1 - i));
    }
  }

  public static Dictionary buildNLDictionary() {
    Dictionary nlDic = new Dictionary("nl");

    Word one = new Word("een", "un");
    Word two = new Word("twee", "deux");
    Word three = new Word("drie", "trois");
    Word four = new Word("vier", "quatre");
    Word five = new Word("vijf", "cinq");

    nlDic.addWord(one);
    nlDic.addWord(two);
    nlDic.addWord(three);
    nlDic.addWord(four);
    nlDic.addWord(five);

    return nlDic;
  }
}
