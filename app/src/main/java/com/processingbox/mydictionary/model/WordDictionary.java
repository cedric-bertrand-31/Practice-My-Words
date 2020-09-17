package com.processingbox.mydictionary.model;

/**
 * Created by admin on 13/09/2016.
 */
public class WordDictionary extends Word {

  /** The dictionary associated to this word */
  private Dictionary associatedDictionary;

  public WordDictionary(final Word word, final Dictionary associatedDictionary) {
    super(word.getWord(), word.getTranslation(), word.getAnswerCoefficientMark());
    this.associatedDictionary = associatedDictionary;
  }

  public Dictionary getAssociatedDictionary() {
    return associatedDictionary;
  }

  public void setAssociatedDictionary(Dictionary associatedDictionary) {
    this.associatedDictionary = associatedDictionary;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    if (!super.equals(o)) {
      return false;
    }

    WordDictionary that = (WordDictionary) o;
    if (that.associatedDictionary == null && associatedDictionary == null) {
      return true;
    }

    return (associatedDictionary != null && that.associatedDictionary != null) && associatedDictionary.getLanguage().equals(that.associatedDictionary.getLanguage());

  }

  @Override
  public int hashCode() {
    int result = super.hashCode();
    result = 31 * result + (associatedDictionary != null ? associatedDictionary.hashCode() : 0);
    return result;
  }
}
