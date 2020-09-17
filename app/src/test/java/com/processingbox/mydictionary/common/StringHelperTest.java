package com.processingbox.mydictionary.common;

import org.junit.Assert;
import org.junit.Test;

public class StringHelperTest {

  @Test
  public void upperLetterFirstLetter() {
    String string = "toto";
    String result = StringHelper.upperLetterFirstLetter(string);
    Assert.assertEquals(result, "Toto");
    Assert.assertEquals(StringHelper.upperLetterFirstLetter(result), "Toto");
  }

  @Test
  public void levenshteinDistance() {
    Assert.assertEquals(StringHelper.levenshteinDistance("spéciàl", "special"), 0);
    Assert.assertEquals(StringHelper.levenshteinDistance("spéciàl", "speciale"), 1);
  }

  @Test
  public void removeAccents() {
    Assert.assertEquals(StringHelper.removeAccents("spéciàl èçò"), "special eco");
  }
}