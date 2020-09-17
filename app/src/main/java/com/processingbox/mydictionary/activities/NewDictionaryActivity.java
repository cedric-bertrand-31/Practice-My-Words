package com.processingbox.mydictionary.activities;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.widget.ListView;

import com.processingbox.mydictionary.Constants;
import com.processingbox.mydictionary.MainModel;
import com.processingbox.mydictionary.R;
import com.processingbox.mydictionary.gui.LanguageListAdapter;
import com.processingbox.mydictionary.model.Dictionary;

/**
 * Activity proposing to create a new dictionary: user will choose a new language and wil then be able to add new words
 * in it.
 * @author: Cédric BERTRAND
 * @date: 01/05/2016.
 */
public class NewDictionaryActivity extends Activity implements ILanguageSelector {

  @Override
  protected void onCreate(final Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_new_dictionary);
    ListView listView = (ListView) findViewById(R.id.listViewLanguages);
    listView.setAdapter(new LanguageListAdapter(this));
  }

  /**
   * User has choose a language, a new dictionary will be created with it.
   * @param lang the language that will be used for creating a new dictionary
   */
  @Override
  public void languageSelected(String lang) {
    Dictionary dictionary = new Dictionary(lang);
    MainModel.addDictionary(dictionary);
    Intent data = new Intent();
    data.putExtra(Constants.INDEX_NEW_DICTIONARY, MainModel.getIndexSelectedDictionary());
    setResult(RESULT_OK, data);
    finish();
  }
}
