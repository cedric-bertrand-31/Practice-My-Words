package com.processingbox.mydictionary;

import android.view.Menu;

import androidx.appcompat.app.AppCompatActivity;

public abstract class MenuActivity extends AppCompatActivity {

  @Override
  public boolean onCreateOptionsMenu(Menu menu) {
    getMenuInflater().inflate(R.menu.menu_main, menu);
    return true;
  }
}
