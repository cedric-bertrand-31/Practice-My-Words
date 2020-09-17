package com.processingbox.mydictionary.parameters;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.processingbox.mydictionary.MainActivity;
import com.processingbox.mydictionary.MainApplication;
import com.processingbox.mydictionary.R;

public abstract class SingleChoiceBuilder extends RowParameterBuilder {

  private final String[] choices;

  public SingleChoiceBuilder(String[] choices) {
    this.choices = choices;
  }

  @Override
  public void clicked() {
    ParametersActivity instance = ParametersActivity.instance;
    final AlertDialog alertDialog = new AlertDialog.Builder(instance).create();
    View layoutSingleChoice = View.inflate(instance, R.layout.single_chooser, null);
    ListView listView = (ListView) layoutSingleChoice.findViewById(R.id.listViewChoice);
    final ArrayAdapter<String> adapter = new ArrayAdapter<>(MainApplication.instance, R.layout.row_textview, choices);
    listView.setAdapter(adapter);
    alertDialog.setView(layoutSingleChoice);
    listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
      @Override
      public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        elementClicked(position);
        alertDialog.dismiss();
        ParametersActivity.instance.refreshGUI();
        if (MainActivity.instance != null) {
          MainActivity.showToast(MainApplication.getStringFromId(R.string.preferencesSaved));
          MainActivity.notifyWordListChanged();
        }
      }
    });

    DialogInterface.OnClickListener listener = new DialogInterface.OnClickListener() {
      @Override
      public void onClick(DialogInterface dialog, int which) {
      }
    };

    alertDialog.setButton(AlertDialog.BUTTON_NEGATIVE, MainApplication.getStringFromId(android.R.string.cancel), listener);
    alertDialog.show();
  }

  protected abstract void elementClicked(int position);

  public String[] getChoices() {
    return choices;
  }
}
