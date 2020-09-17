package com.processingbox.mydictionary.model;

import com.google.gson.Gson;

/**
 * Created by admin on 01/06/2016.
 */
public class SerializableWord {

  private String lang;
  private String translation_lang;
  private String word;
  private String translation;
  private float mark;

  public SerializableWord(String lang, String translation_lang, String word, String translation, float mark) {
    this.lang = lang;
    this.translation_lang = translation_lang;
    this.word = word;
    this.translation = translation;
    this.mark = mark;
  }

  public String getLang() {
    return lang;
  }

  public void setLang(String lang) {
    this.lang = lang;
  }

  public String getTranslation_lang() {
    return translation_lang;
  }

  public void setTranslation_lang(String translation_lang) {
    this.translation_lang = translation_lang;
  }

  public String getWord() {
    return word;
  }

  public void setWord(String word) {
    this.word = word;
  }

  public String getTranslation() {
    return translation;
  }

  public void setTranslation(String translation) {
    this.translation = translation;
  }

  public String toJSONString() {
    return new Gson().toJson(this);
  }

  public static SerializableWord buildFromJSON(String json) {
    return new Gson().fromJson(json, SerializableWord.class);
  }

  public float getMark() {
    return mark;
  }

  public void setMark(float mark) {
    this.mark = mark;
  }
}
