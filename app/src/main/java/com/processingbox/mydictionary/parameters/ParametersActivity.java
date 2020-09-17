package com.processingbox.mydictionary.parameters;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import androidx.fragment.app.FragmentActivity;

import com.processingbox.mydictionary.R;

public class ParametersActivity extends FragmentActivity {

  /** The instance */
  public static ParametersActivity instance;
  /** The adapter */
  private ParametersAdapter adapter;
  /** The clickedParameter */
  private EnumParameters clickedParameter;

  @SuppressLint("NewApi")
  @Override
  protected void onCreate(final Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    super.setContentView(R.layout.activity_parameters);
    instance = this;
    adapter = new ParametersAdapter();
    ListView listView = (ListView) findViewById(R.id.parametersListView);
    listView.setAdapter(adapter);
    listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
      @Override
      public void onItemClick(final AdapterView<?> arg0, final View arg1, final int position, final long arg3) {
        clickedParameter = adapter.getItem(position);
        clickedParameter.clicked();
      }
    });
  }

  public void refreshGUI() {
    adapter.notifyDataSetChanged();
  }

  @Override
  public void onActivityResult(final int requestCode, final int resultCode, final Intent data) {
    refreshGUI();
    super.onActivityResult(requestCode, resultCode, data);
  }
}
