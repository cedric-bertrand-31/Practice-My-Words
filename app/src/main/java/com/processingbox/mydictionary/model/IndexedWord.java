package com.processingbox.mydictionary.model;

public class IndexedWord extends Word {

  private final int index;

  public IndexedWord(Word word, int index) {
    super(word.getWord(), word.getTranslation());
    this.index = index;
  }

  public int getIndex() {
    return index;
  }
}
