package com.processingbox.mydictionary.gui;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Typeface;
import android.text.TextUtils;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.processingbox.mydictionary.MainActivity;
import com.processingbox.mydictionary.MainApplication;
import com.processingbox.mydictionary.MainModel;
import com.processingbox.mydictionary.R;
import com.processingbox.mydictionary.model.Dictionary;
import com.processingbox.mydictionary.model.Word;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Locale;
import java.util.Random;

/**
 * Created by admin on 03/04/2016.
 */
public class WordListAdapter extends BaseAdapter {
  /** The dictionary */
  private Dictionary dictionary;
  /** Random object in field for avoiding to recreate it at each draw */
  private Random random = new Random();
  /** The context */
  private final Context context;
  /** The list of indexes that sort the word list */
  private List<Word> displayedWords;
  /** The searched word */
  private String searchedWord = null;

  public WordListAdapter(final Context context, Dictionary dictionary) {
    this.context = context;
    this.dictionary = dictionary;
    displayedWords = dictionary.getWords();
  }

  @Override
  public int getCount() {
    return displayedWords.size();
  }

  @Override
  public Word getItem(final int position) {
    return displayedWords.get(position);
  }

  @Override
  public long getItemId(final int position) {
    return position;
  }

  @Override
  public View getView(final int position, View view, final ViewGroup parent) {
    if (view == null) {
      LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
      view = inflater.inflate(R.layout.row_word, parent, false);
    }
    TextView textViewWord = (TextView) view.findViewById(R.id.textViewWord);
    String text = !MainActivity.isTranslationReversed() ? getItem(position).getWord() : getItem(position).getTranslation();
    String translation = MainActivity.isTranslationReversed() ? getItem(position).getWord() : getItem(position).getTranslation();
    textViewWord.setText(text);
    textViewWord.setTextColor(MainApplication.getColorFromList(position));
    textViewWord.setTypeface(random.nextBoolean() ? Typeface.DEFAULT_BOLD : Typeface.DEFAULT);
    textViewWord.setTextSize(TypedValue.COMPLEX_UNIT_SP, MainApplication.getRandomTextSize(text));

    final TextView textViewTranslation = (TextView) view.findViewById(R.id.textViewTranslation);
    textViewTranslation.setText(translation);
    textViewTranslation.setVisibility(View.GONE);

    view.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(final View view) {
        int visibility = textViewTranslation.getVisibility() == View.VISIBLE ? View.GONE : View.VISIBLE;
        textViewTranslation.setVisibility(visibility);
      }
    });

    view.setOnLongClickListener(new View.OnLongClickListener() {
      @Override
      public boolean onLongClick(final View view) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context, R.style.StyledDialog);
        builder.setItems(new CharSequence[]{context.getString(R.string.modify), context.getString(R.string.delete), context.getString(R.string.copy) + " (" + dictionary.getDisplayLanguage() + ")", context.getString(R.string.copy) + " (" + Locale.getDefault().getDisplayLanguage() + ")"}, new DialogInterface.OnClickListener() {
          @Override
          public void onClick(final DialogInterface dialog, final int which) {
            switch (which) {
              case 0:
              default:
                MainActivity.instance.startNewWordDialog(getItem(position));
                break;
              case 1:
                MainModel.deleteWord(position);
                break;
              case 2:
                MainActivity.copyToClipboard(getItem(position).getWord());
                break;
              case 3:
                MainActivity.copyToClipboard(getItem(position).getTranslation());
                break;
            }
            MainActivity.notifyWordListChanged();
          }
        });
        builder.show();
        return true;
      }
    });

    if (position < getCount() - 1) {
      View row = view.findViewById(R.id.row_word);
      row.setPadding(row.getPaddingLeft(), row.getPaddingTop(), row.getPaddingRight(), (int) view.getResources().getDimension(R.dimen.wordVerticalPadding));
    } else {
      View row = view.findViewById(R.id.row_word);
      row.setPadding(row.getPaddingLeft(), row.getPaddingTop(), row.getPaddingRight(), 200);
    }
    return view;
  }

  private boolean isWordMatchingSearchQuery(String text, String translation) {
    return TextUtils.isEmpty(text) || TextUtils.isEmpty(searchedWord) || text.toLowerCase().contains(searchedWord.toLowerCase()) || translation.toLowerCase().contains(searchedWord.toLowerCase());
  }

  private boolean isWordMatchingSearchQuery(Word word) {
    return isWordMatchingSearchQuery(word.getWord(), word.getTranslation());
  }

  public void setSearchedWord(String searchedWord) {
    this.searchedWord = searchedWord;
    notifyDataSetChanged();
  }

  @Override
  public void notifyDataSetChanged() {
    if (!TextUtils.isEmpty(searchedWord)) {
      filterWords();
    } else if (MainActivity.isWordSorted()) {
      buildSortedIndexesList();
    } else {
      displayedWords = dictionary.getWords();
    }
    super.notifyDataSetChanged();
  }

  private void filterWords() {
    displayedWords = new ArrayList<Word>();
    List<Word> wordToFilter = dictionary.getWords();
    for (Word word : wordToFilter) {
      if (isWordMatchingSearchQuery(word)) {
        displayedWords.add(word);
      }
    }
    if (MainActivity.isWordSorted()) {
      Collections.sort(displayedWords);
    }
  }

  private void buildSortedIndexesList() {
    displayedWords = new ArrayList<>(dictionary.getWords());
    Collections.sort(displayedWords);
  }
}