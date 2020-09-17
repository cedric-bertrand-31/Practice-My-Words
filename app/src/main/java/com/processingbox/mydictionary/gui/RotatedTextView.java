package com.processingbox.mydictionary.gui;

import android.content.Context;
import android.graphics.Canvas;
import android.util.AttributeSet;

import androidx.appcompat.widget.AppCompatTextView;

import java.util.Random;

/**
 * Created by admin on 29/04/2016.
 */
public class RotatedTextView extends AppCompatTextView {

  private Random random = new Random();

  public RotatedTextView(Context context) {
    super(context);
  }

  public RotatedTextView(Context context, AttributeSet attrs) {
    super(context, attrs);
  }

  public RotatedTextView(Context context, AttributeSet attrs, int defStyleAttr) {
    super(context, attrs, defStyleAttr);
  }

  @Override
  protected void onDraw(Canvas canvas) {
    canvas.save();
    canvas.rotate((random.nextBoolean() ? -1 : 1) * random.nextFloat() * 5, getWidth() / 2f, getHeight() / 2f);
    super.onDraw(canvas);
    canvas.restore();
  }
}
