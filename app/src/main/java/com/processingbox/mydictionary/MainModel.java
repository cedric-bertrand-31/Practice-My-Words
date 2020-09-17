package com.processingbox.mydictionary;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.processingbox.mydictionary.alarm.DBSender;
import com.processingbox.mydictionary.model.Dictionary;
import com.processingbox.mydictionary.model.Word;
import com.processingbox.mydictionary.model.WordDictionary;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

import static com.processingbox.mydictionary.Constants.PREFERENCES_INDEX_SELECTED_DICTIONARY;

/**
 * This model is the entry point for accessing the data typed by user. It contains all user's dictionaries and pending
 * data that needs to be synchronized with external DB
 * @author: Cédric BERTRAND
 * @date: 17/09/2020.
 */
public class MainModel {

  /** All the user's dictionaries */
  private static final List<Dictionary> dictionaries = new ArrayList<Dictionary>();
  /** Pending words that have been deleted locally waiting to be synchronized with external DB */
  private static List<WordDictionary> wordsWithUpdatedMarks = new ArrayList<WordDictionary>();
  /** Pending words that have a different mark than on external DB that need to be synchronized */
  private static List<WordDictionary> deletedWordNotSynchronizedWithDB = new ArrayList<WordDictionary>();

  /**
   * Initialize user's data thanks to what has been stored into shared preferences
   */
  public static void build() {
    dictionaries.clear();
    dictionaries.addAll(getListDictionariesFromPreferences());
    deletedWordNotSynchronizedWithDB.clear();
    deletedWordNotSynchronizedWithDB.addAll(getListDeletedWordNotSynchronizedWithDBFromPreferences());
    wordsWithUpdatedMarks.clear();
    wordsWithUpdatedMarks.addAll(getListWordsWithUpdatedMarksFromPreferences());
  }

  /**
   * Add a new dictionary (new language to learn)
   * @param dictionary the new dictionary associated to user account
   */
  public static void addDictionary(Dictionary dictionary) {
    dictionaries.add(dictionary);
    setIndexSelectedDictionary(dictionaries.size() - 1);
    saveInPreferences();
  }

  /**
   * Save dictionaries and pending data that needs to be ynchronized with external DB into shared preferences
   */
  public static void saveInPreferences() {
    MainApplication.savePreference(Constants.KEY_LIST_LANG, new Gson().toJson(dictionaries));
    MainApplication.savePreference(Constants.KEY_DELETED_WORDS, new Gson().toJson(deletedWordNotSynchronizedWithDB));
    MainApplication.savePreference(Constants.KEY_UPDATED_MARK_WORDS, new Gson().toJson(wordsWithUpdatedMarks));
    DBSender.synchronizeLocalStorageWithDB();
  }

  /**
   * @return dictionaries stored in shared preferences
   */
  private static List<Dictionary> getListDictionariesFromPreferences() {
    String dictionariesJSON = MainApplication.getStringPreference(Constants.KEY_LIST_LANG, null);
    if (dictionariesJSON != null) {
      Type type = new TypeToken<List<Dictionary>>() {
      }.getType();
      return new Gson().fromJson(dictionariesJSON, type);
    }
    return new ArrayList<>();
  }

  /**
   * @return deleted words that are still not synchronized with external DB
   */
  private static List<WordDictionary> getListDeletedWordNotSynchronizedWithDBFromPreferences() {
    String dictionariesJSON = MainApplication.getStringPreference(Constants.KEY_DELETED_WORDS, null);
    if (dictionariesJSON != null) {
      Type type = new TypeToken<List<WordDictionary>>() {
      }.getType();
      return new Gson().fromJson(dictionariesJSON, type);
    }
    return new ArrayList<>();
  }


  /**
   * @return words with an updates mark that are still not synchronized with external DB
   */
  private static List<WordDictionary> getListWordsWithUpdatedMarksFromPreferences() {
    String dictionariesJSON = MainApplication.getStringPreference(Constants.KEY_UPDATED_MARK_WORDS, null);
    if (dictionariesJSON != null) {
      Type type = new TypeToken<List<WordDictionary>>() {
      }.getType();
      return new Gson().fromJson(dictionariesJSON, type);
    }
    return new ArrayList<>();
  }

  /**
   * Only one dictionary is displayed to user, this method returns the one currently displayed
   * @return the currently displayed dictionary
   */
  public static Dictionary getSelectedDictionary() {
    checkDictionariesAreBuilt();
    int indexSelectedDictionary = getIndexSelectedDictionary();
    return indexSelectedDictionary < 0 || indexSelectedDictionary >= dictionaries.size() ? null : dictionaries.get(indexSelectedDictionary);
  }

  /**
   * Build the dictionaries object if it is empty
   */
  private static void checkDictionariesAreBuilt() {
    if (dictionaries.isEmpty()) {
      build();
    }
  }

  /**
   * Return the dictionary given its index in the dictionaries list
   * @param index the index in the dictionaries list
   * @return dictionary located at the mentioned index in the dictionaries list
   */
  public static Dictionary getDictionary(int index) {
    checkDictionariesAreBuilt();
    return dictionaries.get(index);
  }

  /**
   * Only one dictionary is displayed to user, this method returns the index of the one currently displayed
   * @return the index of the displayed dictionary
   */
  public static int getIndexSelectedDictionary() {
    return MainApplication.getIntPreference(PREFERENCES_INDEX_SELECTED_DICTIONARY, 0);
  }

  /**
   * Change the index of the current displayed dictionary
   * @param iSelectedDictionary the new index of the selected dictionary
   */
  public static void setIndexSelectedDictionary(final int iSelectedDictionary) {
    MainApplication.savePreference(Constants.PREFERENCES_INDEX_SELECTED_DICTIONARY, iSelectedDictionary);
  }

  /**
   * Switch the current displayed dictionary to the one given by its language
   * @param lang the language of the new selected dictionary
   */
  public static void setIndexSelectedDictionary(String lang) {
    int i = 0;
    for (Dictionary dictionary : dictionaries) {
      if (dictionary.getLanguage().equals(lang)) {
        setIndexSelectedDictionary(i);
        return;
      }
      i++;
    }
  }

  /**
   * @return all the user's dictionaries
   */
  public static List<Dictionary> getDictionaries() {
    checkDictionariesAreBuilt();
    return dictionaries;
  }

  /**
   * Delete the word at the mention index
   * @param wordIndex the index of the word to delete
   */
  public static void deleteWord(final int wordIndex) {
    Dictionary selectedDictionary = getSelectedDictionary();
    deletedWordNotSynchronizedWithDB.add(new WordDictionary(selectedDictionary.getWords().get(wordIndex), selectedDictionary));
    selectedDictionary.deleteWord(wordIndex);
    saveInPreferences();
  }

  /**
   * Add a new word to {@link MainModel#getSelectedDictionary()}
   * @param word the word to add to {@link MainModel#getSelectedDictionary()}
   */
  public static void addWord(final Word word) {
    getSelectedDictionary().addWord(word);
    saveInPreferences();
  }

  /**
   * Modify foreign language text and translation text of the given word
   * @param word the {@link Word} object to modify
   * @param wordString the word in the language to learn
   * @param translation the translation in user's language
   */
  public static void modifyWord(final Word word, final String wordString, final String translation) {
    deletedWordNotSynchronizedWithDB.add(new WordDictionary(word, getSelectedDictionary()));
    word.setWord(wordString);
    word.setTranslation(translation);
    word.setSynchronized(false);
    saveInPreferences();
  }

  /**
   * Remove the given dictionary from local storage
   * @param dictionary the dictionary to remove
   */
  public static void remove(final Dictionary dictionary) {
    for (Word word : dictionary.getWords()) {
      deletedWordNotSynchronizedWithDB.add(new WordDictionary(word, dictionary));
    }
    int index = dictionaries.indexOf(dictionary);
    dictionaries.remove(dictionary);
    setIndexSelectedDictionary(Math.max(index - 1, 0));
    saveInPreferences();
  }

  /**
   * Get the dictionary thanks to a language
   * @param lang the language at ISO 639-1 format
   * @return the dictionary associated to the given language, null if user has not such a dictionary
   */
  public static Dictionary getDictionary(final String lang) {
    for (Dictionary dictionary : dictionaries) {
      if (dictionary.getLanguage().equals(lang)) {
        return dictionary;
      }
    }
    return null;
  }

  /**
   * Return the {@link Word} instance contained in dictionary instance associated to the given language. Word is
   * searched thanks to word in learned language and word translated in user's language description of a word
   * @param lang the language to which belong the searched word
   * @param word the word in foreign language
   * @param translation the word in user's language
   * @return the {@link Word} instance is the associated dictionary
   */
  public static Word getWord(final String lang, final String word, final String translation) {
    List<Word> words = getDictionary(lang).getWords();
    return words.get(words.indexOf(new Word(word, translation)));
  }

  /**
   * @return all the deleted word that have been deleted locally waiting to be synchronized with external DB
   */
  public static List<WordDictionary> getDeletedWordNotSynchronizedWithDB() {
    return deletedWordNotSynchronizedWithDB;
  }

  /**
   * @return all words that have a different mark than on external DB that need to be synchronized
   */
  public static List<WordDictionary> getWordsWithUpdatedMarks() {
    return wordsWithUpdatedMarks;
  }

  /**
   * When a word had its mark that had changed, it this mentioned to this method that will store it so it can be sent to
   * external DB
   * @param wordDictionary the {@link WordDictionary} that had a mark updated
   */
  public static void addUpdatedMarkWord(final WordDictionary wordDictionary) {
    if (!wordsWithUpdatedMarks.contains(wordDictionary)) {
      wordsWithUpdatedMarks.add(wordDictionary);
    }
  }
}
