package com.processingbox.mydictionary;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.widget.SearchView;
import androidx.core.view.MenuItemCompat;
import androidx.fragment.app.Fragment;
import androidx.viewpager.widget.ViewPager;

import com.processingbox.mydictionary.activities.DictionariesActivity;
import com.processingbox.mydictionary.activities.NewDictionaryActivity;
import com.processingbox.mydictionary.activities.practicetest.PracticeTestActivity;
import com.processingbox.mydictionary.common.StringHelper;
import com.processingbox.mydictionary.common.UIHelper;
import com.processingbox.mydictionary.gui.CustomPagerAdapter;
import com.processingbox.mydictionary.gui.DictionaryFragment;
import com.processingbox.mydictionary.gui.INotifyDatasetChanged;
import com.processingbox.mydictionary.gui.WordListAdapter;
import com.processingbox.mydictionary.model.Dictionary;
import com.processingbox.mydictionary.model.Word;
import com.processingbox.mydictionary.parameters.ParametersActivity;

import java.util.Locale;

public class MainActivity extends MenuActivity {

  /** The instance */
  public static MainActivity instance;
  /** The fragments */
  public static DictionaryFragment currentFragment;
  /** If true, the words displayed are in the user's language */
  private static boolean isReversed = false;
  /** The common toast */
  public static Toast toast;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    super.setContentView(R.layout.activity_main);
    refresh();
  }

  /**
   * Refresh model and user interface of this main activity so that we have always up-to-date data displayed
   */
  private void refresh() {
    instance = this;
    isReversed = MainApplication.getBooleanPreference(Constants.KEY_IS_TRANSLATION_REVERSED);
    MainModel.build();
    buildGUI();
    MainApplication.refreshServices();
  }

  /**
   * Initialize the user interface of this activity
   */
  protected void buildGUI() {
    androidx.appcompat.app.ActionBar actionBar = getSupportActionBar();
    if (actionBar != null) {
      if (MainModel.getSelectedDictionary() != null) {
        actionBar.setTitle(StringHelper.upperLetterFirstLetter(MainModel.getSelectedDictionary().getDisplayLanguage()));
      } else {
        actionBar.setTitle(MainApplication.getStringFromId(R.string.app_name));
      }
    }
    buildFragments();
    initViewPager();
    buildAddButton();
  }

  private void buildAddButton() {
    View addButton = getAddButton();
    View.OnClickListener onClickListener = new View.OnClickListener() {
      @Override
      public void onClick(View v) {
        addNewWord();
      }
    };
    addButton.setOnClickListener(onClickListener);
  }

  /**
   * Add a new word to {@link MainModel#getSelectedDictionary()} or if no dictionary has been set, start the {@link
   * NewDictionaryActivity} for asking to user to create a dictionary
   */
  private void addNewWord() {
    if (MainModel.getDictionaries().isEmpty()) {
      startActivityForResult(new Intent(this, NewDictionaryActivity.class), Constants.NEW_DICTIONARY_ID);
    } else {
      startNewWordDialog(null);
    }
  }

  /**
   * Select a new dictionary or if no dictionary has been set, start the {@link NewDictionaryActivity} for asking to
   * user to create a dictionary
   */
  private void dictionarySelected() {
    if (MainModel.getDictionaries().isEmpty()) {
      startActivityForResult(new Intent(this, NewDictionaryActivity.class), Constants.NEW_DICTIONARY_ID);
    } else {
      startDictionariesActivity();
    }
  }

  /**
   * Open a dialog asking to user to enter a new word he has learned + its translation. If user approve, word is saved
   * in local storage + send to backend.
   * @param word if provided, the word to learn will {@link EditText} be already filled. Set to null if {@link EditText}
   * must be empty
   */
  public void startNewWordDialog(final Word word) {
    AlertDialog.Builder alert = new AlertDialog.Builder(this);
    alert.setTitle(getString(R.string.newLangWord, MainModel.getSelectedDictionary().getDisplayLanguage()));

    LinearLayout layout = (LinearLayout) LayoutInflater.from(this).inflate(R.layout.dialog_new_word, null);

    final TextView input = (EditText) layout.findViewById(R.id.editTextNewWord);
    final TextView translationEditText = (TextView) layout.findViewById(R.id.editTextTranslationNewWord);
    translationEditText.setHint(getString(R.string.translationHint, Locale.getDefault().getDisplayLanguage()));

    if (word != null) {
      input.setText(word.getWord());
      translationEditText.setText(word.getTranslation());
      translationEditText.setVisibility(View.VISIBLE);
    }

    alert.setView(layout);
    alert.setPositiveButton(getString(android.R.string.ok), new DialogInterface.OnClickListener() {
      public void onClick(DialogInterface dialog, int whichButton) {
        String inputWord = input.getText().toString().trim();
        String inputTranslation = translationEditText.getText().toString().trim();
        if (!TextUtils.isEmpty(inputWord) && !TextUtils.isEmpty(inputTranslation)) {
          if (word == null) {
            MainModel.addWord(new Word(inputWord, inputTranslation));
          } else {
            MainModel.modifyWord(word, inputWord, inputTranslation);
          }
          MainActivity.notifyWordListChanged();
        }
      }
    });
    alert.setNegativeButton(getString(android.R.string.cancel), null);
    alert.show();

    input.addTextChangedListener(new TextWatcher() {
      @Override
      public void beforeTextChanged(CharSequence s, int start, int count, int after) {
      }

      @Override
      public void afterTextChanged(Editable s) {
      }

      @Override
      public void onTextChanged(CharSequence s, int start, int before, int count) {
        CharSequence text = input.getText();
        translationEditText.setVisibility(TextUtils.isEmpty(text) ? View.GONE : View.VISIBLE);
      }
    });
  }

  public static void notifyWordListChanged() {
    WordListAdapter adapter = getWordListAdapter();
    if (adapter != null) {
      adapter.notifyDataSetChanged();
    }
  }

  public static WordListAdapter getWordListAdapter() {
    return currentFragment != null ? currentFragment.getWordListAdapter() : null;
  }

  private void startDictionariesActivity() {
    startActivityForResult(new Intent(this, DictionariesActivity.class), Constants.DICTIONARIES_ID);
  }

  @Override
  protected void onActivityResult(int requestCode, int resultCode, Intent data) {
    super.onActivityResult(requestCode, resultCode, data);
    if (requestCode == Constants.DICTIONARIES_ID || requestCode == Constants.NEW_DICTIONARY_ID) {
      refresh();
    }
  }

  public void addNewDictionary(Intent data) {
    int indexNewDictionary = data.getIntExtra(Constants.INDEX_NEW_DICTIONARY, -1);
    if (indexNewDictionary >= 0) {
      Dictionary dictionary = MainModel.getDictionary(indexNewDictionary);
      refreshAfterNewDictionaryAdded(dictionary);
    }
  }

  public void refreshAfterNewDictionaryAdded(Dictionary dictionary) {
    currentFragment = buildFragment(dictionary);
    refresh();
  }

  public View getAddButton() {
    return findViewById(R.id.addButton);
  }

  private void buildFragments() {
    currentFragment = buildFragment(MainModel.getSelectedDictionary());
  }

  private DictionaryFragment buildFragment(Dictionary dictionary) {
    DictionaryFragment fragment = (DictionaryFragment) Fragment.instantiate(this, DictionaryFragment.class.getName());
    fragment.setDictionary(dictionary);
    return fragment;
  }

  private void initViewPager() {
    final ViewPager viewPager = getViewPager();
    final CustomPagerAdapter adapter = new CustomPagerAdapter(getSupportFragmentManager());
    viewPager.setAdapter(adapter);
    refreshVisibility();
  }

  private void refreshPosition(int position) {
    getViewPager().setCurrentItem(position);
    MainModel.setIndexSelectedDictionary(position);
  }

  private void refreshVisibility() {
    getViewPager().setVisibility(currentFragment == null ? View.INVISIBLE : View.VISIBLE);
  }

  protected ViewPager getViewPager() {
    return (ViewPager) super.findViewById(R.id.viewPagerMain);
  }

  @Override
  public boolean onCreateOptionsMenu(Menu menu) {
    super.onCreateOptionsMenu(menu);
    MenuItem searchItem = menu.findItem(R.id.search);
    SearchView searchView = (SearchView) MenuItemCompat.getActionView(searchItem);
    searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
      @Override
      public boolean onQueryTextSubmit(String query) {
        return false;
      }

      @Override
      public boolean onQueryTextChange(String newText) {
        WordListAdapter wordListAdapter = getWordListAdapter();
        if (wordListAdapter != null) {
          wordListAdapter.setSearchedWord(newText);
        }
        return false;
      }
    });
    return true;
  }

  @Override
  public boolean onOptionsItemSelected(MenuItem item) {
    int id = item.getItemId();
    if (id == R.id.dictionaries) {
      dictionarySelected();
      return true;
    } else if (id == R.id.reverse_translation) {
      reverseTranslation();
      return true;
    } else if (id == R.id.start_test) {
      startPracticeTest();
      return true;
    } else if (id == R.id.parameters) {
      startActivity(new Intent(this, ParametersActivity.class));
      return true;
    }
    return super.onOptionsItemSelected(item);
  }

  /**
   * Launch the practice test where user will try to guess the translation of the word he had provided
   */
  private void startPracticeTest() {
    if (MainModel.getSelectedDictionary() != null && !MainModel.getSelectedDictionary().getWords().isEmpty()) {
      startActivity(new Intent(this, PracticeTestActivity.class));
    } else if (MainModel.getSelectedDictionary() == null) {
      showToast(MainApplication.getStringFromId(R.string.createADictionary));
    } else if (MainModel.getSelectedDictionary() != null && MainModel.getSelectedDictionary().getWords().isEmpty()) {
      showToast(MainApplication.getStringFromId(R.string.addAtLeastOneWord));
    }
  }

  /**
   * Display all word in language to learn if isReversed = false, in user's language in the other hand
   */
  private void reverseTranslation() {
    if (!MainModel.getDictionaries().isEmpty()) {
      isReversed = !isReversed;
      MainApplication.savePreference(Constants.KEY_IS_TRANSLATION_REVERSED, isReversed);
      refresh();
      showToast(getString(R.string.reverseTranslationToastMessage, !isReversed ? MainModel.getSelectedDictionary().getDisplayLanguage() : Locale.getDefault().getDisplayLanguage()));
    }
  }

  public static void showToast(String string) {
    if (toast != null) {
      toast.cancel();
    }
    toast = Toast.makeText(MainApplication.instance, string, Toast.LENGTH_LONG);
    toast.show();
  }

  private void deleteSelectedDictionary(Dictionary dictionary) {
    MainModel.remove(dictionary);
    refreshPosition(MainModel.getIndexSelectedDictionary());
    refresh();
  }

  public void displayConfirmDeleteDictionaryDialog(final Dictionary dictionary, final Context context) {
    DialogInterface.OnClickListener listener = new DialogInterface.OnClickListener() {
      @Override
      public void onClick(final DialogInterface dialog, final int which) {
        if (which == DialogInterface.BUTTON_POSITIVE) {
          deleteSelectedDictionary(dictionary);
          if (context instanceof INotifyDatasetChanged) {
            ((INotifyDatasetChanged) context).notifyDataSetChanged();
          }
        }
      }
    };
    final AlertDialog.Builder alertDialog = new AlertDialog.Builder(context, R.style.StyledDialog).setMessage(getString(R.string.deleteDictionaryConfirmDialog, dictionary.getDisplayLanguage())).setPositiveButton(getString(android.R.string.yes), listener).setNegativeButton(getString(android.R.string.no), listener);
    alertDialog.show();
  }

  public static boolean isTranslationReversed() {
    return isReversed;
  }

  public static boolean isWordSorted() {
    return MainApplication.getBooleanPreference(Constants.KEY_IS_WORD_SORTED);
  }

  public void selectLanguage(String lang) {
    MainModel.setIndexSelectedDictionary(lang);
    currentFragment = buildFragment(MainModel.getSelectedDictionary());
    refresh();
  }

  public static void copyToClipboard(String string) {
    UIHelper.copyToClipBoard(instance, string);
    showToast(MainApplication.getStringFromId(R.string.copiedToClipboard));
  }

  public static void setCurrentFragment(DictionaryFragment dictionaryFragment) {
    currentFragment = dictionaryFragment;
  }
}
