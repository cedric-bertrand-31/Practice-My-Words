package com.processingbox.mydictionary.gui;

import android.content.Context;
import android.graphics.Typeface;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.processingbox.mydictionary.MainApplication;
import com.processingbox.mydictionary.MainModel;
import com.processingbox.mydictionary.R;
import com.processingbox.mydictionary.activities.NewDictionaryActivity;
import com.processingbox.mydictionary.model.Dictionary;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Locale;
import java.util.Random;

/**
 * Created by admin on 03/04/2016.
 */
public class LanguageListAdapter extends BaseAdapter {
  /** The context */
  private final NewDictionaryActivity context;
  private List<String> listLanguages;
  Random random = new Random();

  public LanguageListAdapter(final NewDictionaryActivity context) {
    this.context = context;
    listLanguages = buildLangList();
  }

  public static List<String> buildLangList() {
    List<String> listLanguages = getListAllLanguages();
    listLanguages.remove(Locale.getDefault().getLanguage());
    for (Dictionary dic : MainModel.getDictionaries()) {
      listLanguages.remove(dic.getLanguage());
    }
    Collections.sort(listLanguages, new Comparator<String>() {
      @Override
      public int compare(String lhs, String rhs) {
        return new Locale(lhs).getDisplayLanguage().compareTo(new Locale(rhs).getDisplayLanguage());
      }
    });
    return listLanguages;
  }

  private static List<String> getListAllLanguages() {
    List<String> listLanguages = new ArrayList<String>();
    listLanguages.add("en");
    listLanguages.add("de");
    listLanguages.add("zh");
    listLanguages.add("cs");
    listLanguages.add("nl");
    listLanguages.add("fr");
    listLanguages.add("it");
    listLanguages.add("ja");
    listLanguages.add("ko");
    listLanguages.add("pl");
    listLanguages.add("ru");
    listLanguages.add("ar");
    listLanguages.add("bg");
    listLanguages.add("ca");
    listLanguages.add("hr");
    listLanguages.add("da");
    listLanguages.add("fi");
    listLanguages.add("el");
    listLanguages.add("iw");
    listLanguages.add("hi");
    listLanguages.add("hu");
    listLanguages.add("in");
    listLanguages.add("lv");
    listLanguages.add("lt");
    listLanguages.add("nb");
    listLanguages.add("pt");
    listLanguages.add("ro");
    listLanguages.add("sr");
    listLanguages.add("sk");
    listLanguages.add("sl");
    listLanguages.add("es");
    listLanguages.add("sv");
    listLanguages.add("tl");
    listLanguages.add("th");
    listLanguages.add("tr");
    listLanguages.add("uk");
    listLanguages.add("vi");
    return listLanguages;
  }

  @Override
  public int getCount() {
    return listLanguages.size();
  }

  @Override
  public String getItem(final int position) {
    return new Locale(listLanguages.get(position)).getDisplayLanguage();
  }

  @Override
  public long getItemId(final int position) {
    return position;
  }

  @Override
  public View getView(final int position, View view, final ViewGroup parent) {
    if (view == null) {
      LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
      view = inflater.inflate(R.layout.row_language, parent, false);
    }
    TextView textViewLanguage = (TextView) view.findViewById(R.id.textViewLanguage);
    String text = getItem(position);
    textViewLanguage.setText(text);

    textViewLanguage.setTextColor(MainApplication.getColorFromList(position));
    textViewLanguage.setTypeface(random.nextBoolean() ? Typeface.DEFAULT_BOLD : Typeface.DEFAULT);
    textViewLanguage.setTextSize(TypedValue.COMPLEX_UNIT_SP, MainApplication.getRandomTextSize(text));

    view.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(final View view) {
        if (view != null) {
          context.languageSelected(listLanguages.get(position));
        }
      }
    });

    return view;
  }
}
