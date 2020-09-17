package com.processingbox.mydictionary.activities;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ListView;

import com.processingbox.mydictionary.Constants;
import com.processingbox.mydictionary.MainActivity;
import com.processingbox.mydictionary.R;
import com.processingbox.mydictionary.gui.DictionariesAdapter;
import com.processingbox.mydictionary.gui.INotifyDatasetChanged;

/**
 * Activity showing all dictionaries that user have created. From there he can delete or add new dictionaries
 * @author: Cédric BERTRAND
 * @date: 15/09/2020.
 */
public class DictionariesActivity extends Activity implements INewDictionaryLauncher, ILanguageSelector, INotifyDatasetChanged {

  /** The adapter allowing to display dictionaries in the {@link R.id#listViewLanguages} */
  DictionariesAdapter adapter = new DictionariesAdapter(this);

  @Override
  protected void onCreate(final Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_dictionary);
    ListView listView = findViewById(R.id.listViewLanguages);
    listView.setAdapter(adapter);
    View addButton = findViewById(R.id.addButton);
    View.OnClickListener onClickListener = new View.OnClickListener() {
      @Override
      public void onClick(View v) {
        startNewDictionaryActivity();
      }
    };
    addButton.setOnClickListener(onClickListener);
  }

  /**
   * User has click on new dictionary button, corresponding activity is launched and this activity waits for result
   */
  public void startNewDictionaryActivity() {
    startActivityForResult(new Intent(this, NewDictionaryActivity.class), Constants.NEW_DICTIONARY_ID);
  }

  @Override
  protected void onActivityResult(int requestCode, int resultCode, Intent data) {
    super.onActivityResult(requestCode, resultCode, data);
    if (requestCode == Constants.NEW_DICTIONARY_ID) {
      if (resultCode == RESULT_OK) {
        MainActivity.instance.addNewDictionary(data);
        finish();
      }
    }
  }

  @Override
  public void languageSelected(String lang) {
    MainActivity.instance.selectLanguage(lang);
    finish();
  }

  @Override
  public void notifyDataSetChanged() {
    if (adapter != null) {
      adapter.notifyDataSetChanged();
    }
  }
}
