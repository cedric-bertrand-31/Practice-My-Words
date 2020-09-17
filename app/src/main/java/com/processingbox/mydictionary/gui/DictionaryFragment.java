package com.processingbox.mydictionary.gui;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import androidx.fragment.app.Fragment;

import com.processingbox.mydictionary.Constants;
import com.processingbox.mydictionary.MainActivity;
import com.processingbox.mydictionary.MainModel;
import com.processingbox.mydictionary.R;
import com.processingbox.mydictionary.model.Dictionary;

/**
 * The fragment linked to a dictionary
 * @author: Cédric BERTRAND
 * @date: 15/09/2016
 */
public class DictionaryFragment extends Fragment {

  /** The associated dictionary */
  private Dictionary dictionary;
  /** The adapter of word list */
  private WordListAdapter wordListAdapter;

  @Override
  public View onCreateView(final LayoutInflater inflater, final ViewGroup container, final Bundle savedInstanceState) {
    if (savedInstanceState != null) {
      int indexDictionary = savedInstanceState.getInt(Constants.INDEX_DICTIONARY, -1);
      if (indexDictionary >= 0 && indexDictionary < MainModel.getDictionaries().size()) {
        dictionary = MainModel.getDictionary(indexDictionary);
      }
    }
    View mainView = inflater.inflate(R.layout.fragment_main, container, false);
    if (dictionary != null && mainView != null) {
      wordListAdapter = new WordListAdapter(getContext(), dictionary);
      ListView listViewWords = mainView.findViewById(R.id.listViewWords);
      listViewWords.setChoiceMode(ListView.CHOICE_MODE_SINGLE);
      listViewWords.setAdapter(wordListAdapter);
      wordListAdapter.notifyDataSetChanged();
    }
    MainActivity.setCurrentFragment(this);
    return mainView;
  }

  @Override
  public void onSaveInstanceState(Bundle outState) {
    outState.putInt(Constants.INDEX_DICTIONARY, MainModel.getDictionaries().indexOf(dictionary));
    super.onSaveInstanceState(outState);
  }

  public Dictionary getDictionary() {
    return dictionary;
  }

  public void setDictionary(Dictionary dictionary) {
    this.dictionary = dictionary;
  }

  public WordListAdapter getWordListAdapter() {
    return wordListAdapter;
  }
}
