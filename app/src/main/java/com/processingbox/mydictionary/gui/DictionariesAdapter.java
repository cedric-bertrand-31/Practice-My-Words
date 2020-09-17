package com.processingbox.mydictionary.gui;

import android.content.Context;
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
import com.processingbox.mydictionary.activities.ILanguageSelector;
import com.processingbox.mydictionary.model.Dictionary;

import java.util.List;
import java.util.Locale;
import java.util.Random;

/**
 * Created by admin on 03/04/2016.
 */
public class DictionariesAdapter extends BaseAdapter {
  /** The associated context */
  private final Context context;
  /** the list of all existing Languages */
  private List<Dictionary> listExistingLanguages;
  /** Random object in field for avoiding to recreate it at each draw */
  private Random random = new Random();

  public DictionariesAdapter(final Context context) {
    this.context = context;
    listExistingLanguages = MainModel.getDictionaries();
  }

  @Override
  public int getCount() {
    return listExistingLanguages.size();
  }

  @Override
  public String getItem(final int position) {
    return new Locale(listExistingLanguages.get(position).getLanguage()).getDisplayLanguage();
  }

  @Override
  public long getItemId(final int position) {
    return position;
  }

  @Override
  public View getView(final int position, View view, final ViewGroup parent) {
    if (view == null) {
      LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
      view = inflater.inflate(R.layout.row_language_delete, parent, false);
    }
    TextView langText = (TextView) view.findViewById(R.id.textViewLanguage);
    View deleteImage = view.findViewById(R.id.imageViewDelete);
    String text = getItem(position);
    langText.setText(text);

    deleteImage.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View v) {
        MainActivity.instance.displayConfirmDeleteDictionaryDialog(listExistingLanguages.get(position), context);
      }
    });

    view.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(final View view) {
        if (view != null && context instanceof ILanguageSelector) {
          ((ILanguageSelector) context).languageSelected(listExistingLanguages.get(position).getLanguage());
        }
      }
    });

    langText.setTextColor(MainApplication.getColorFromList(position));
    langText.setTextSize(TypedValue.COMPLEX_UNIT_SP, 24);

    return view;
  }
}
