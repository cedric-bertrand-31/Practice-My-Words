package com.processingbox.mydictionary.activities.practicetest;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Intent;
import android.content.res.ColorStateList;
import android.graphics.PorterDuff;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import androidx.core.content.ContextCompat;

import com.github.lzyzsd.circleprogress.DonutProgress;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.processingbox.mydictionary.MainApplication;
import com.processingbox.mydictionary.MainModel;
import com.processingbox.mydictionary.R;
import com.processingbox.mydictionary.common.StringHelper;
import com.processingbox.mydictionary.common.UIHelper;
import com.processingbox.mydictionary.model.Dictionary;
import com.processingbox.mydictionary.model.WeightedListOfWords;
import com.processingbox.mydictionary.model.Word;
import com.processingbox.mydictionary.model.WordDictionary;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Random;

/**
 * Activity for testing user's level by presenting him a set of words, he must then try to find the correct translation
 * in his language or in the learned language.
 * @author: Cédric BERTRAND
 * @date: 15/09/2020.
 */
public class PracticeTestActivity extends Activity {
  /** The dictionary on which words will be chosen for this practice test activity */
  protected Dictionary selectedDictionary;
  /** The expected result that must be typed by user */
  private String expectedResult;
  /** The remaining word indexes that will be displayed to the user */
  protected List<Integer> listRemainingIndex = new ArrayList<>();
  /** The index in the current dictionary of the current displayed word */
  protected Integer indexWord = -1;
  /** The number of correct answers */
  private int nbCorrectAnswer = 0;
  /** Total number of answers */
  private int nbAnswer = 0;
  /** Mention if the word is displayed in user language (isReversed = false) or in learned language (isReversed = true) */
  protected boolean isReverse;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_practice_text);
    findViewById(R.id.buttonPracticeNext).setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View v) {
        validateAnswer();
      }
    });

    if (savedInstanceState != null) {
      initFromSavedBundle(savedInstanceState);
    } else {
      initModel();
    }
  }

  /**
   * Init model from saved bundle. If savedInstanceState isn't null, it means that onSaveInstanceState has been
   * previously called, data needs to be restored
   * @param savedInstanceState the bundle that has been saved before this activity has been killed
   */
  private void initFromSavedBundle(Bundle savedInstanceState) {
    ArrayList<Integer> integerArrayList = savedInstanceState.getIntegerArrayList(EnumPracticeTestBundle.SAVE_LIST_REMAINING_INDEXES.toString());
    isReverse = savedInstanceState.getBoolean(EnumPracticeTestBundle.SAVE_IS_REVERSE.toString());
    indexWord = savedInstanceState.getInt(EnumPracticeTestBundle.SAVE_INDEX_WORD.toString(), -1);
    nbCorrectAnswer = savedInstanceState.getInt(EnumPracticeTestBundle.SAVE_NB_CORRECT_ANSWER.toString(), 0);
    nbAnswer = savedInstanceState.getInt(EnumPracticeTestBundle.SAVE_NB_ANSWER.toString(), 0);
    int indexDictionary = savedInstanceState.getInt(EnumPracticeTestBundle.SAVE_INDEX_DICTIONARY.toString(), -1);
    if (integerArrayList != null && indexWord >= 0 && indexDictionary >= 0) {
      selectedDictionary = MainModel.getDictionary(indexDictionary);
      listRemainingIndex = integerArrayList;
      updateGUI();
    } else {
      initModel();
    }
  }

  @Override
  protected void onNewIntent(Intent intent) {
    super.onNewIntent(intent);
    setIntent(intent);
    onCreate(null);
  }

  /**
   * Initialize from scratch the model linked to this activity.
   */
  protected void initModel() {
    selectedDictionary = MainModel.getSelectedDictionary();
    List<Word> practiceWords = MainApplication.isPracticeTestAllWordsSelected() ? selectedDictionary.getWords() : selectedDictionary.getPracticeTestWords();
    int numberOfWords = Math.min(practiceWords.size(), getNumberOfWords());
    WeightedListOfWords wRandom = new WeightedListOfWords(practiceWords);
    for (int i = 0; i < numberOfWords; i++) {
      listRemainingIndex.add(selectedDictionary.getWords().indexOf(practiceWords.get(wRandom.next())));
    }
    displayRandomWord();
  }

  @Override
  protected void onSaveInstanceState(Bundle outState) {
    outState.putIntegerArrayList(EnumPracticeTestBundle.SAVE_LIST_REMAINING_INDEXES.toString(), (ArrayList<Integer>) listRemainingIndex);
    outState.putBoolean(EnumPracticeTestBundle.SAVE_IS_REVERSE.toString(), isReverse);
    outState.putInt(EnumPracticeTestBundle.SAVE_INDEX_DICTIONARY.toString(), MainModel.getDictionaries().indexOf(selectedDictionary));
    outState.putInt(EnumPracticeTestBundle.SAVE_INDEX_WORD.toString(), indexWord);
    outState.putInt(EnumPracticeTestBundle.SAVE_NB_CORRECT_ANSWER.toString(), nbCorrectAnswer);
    outState.putInt(EnumPracticeTestBundle.SAVE_NB_ANSWER.toString(), nbAnswer);
    super.onSaveInstanceState(outState);
  }

  /**
   * Display a random word to user for trying to translate it without error.
   */
  protected void displayRandomWord() {
    if (listRemainingIndex.isEmpty()) {
      showSummaryDialog();
      return;
    }
    newWordSelection();
    listRemainingIndex.remove(indexWord);
    updateGUI();
  }

  /**
   * When practice test is finished, a summary is displayed to user: ratio of correct answers.
   */
  protected void showSummaryDialog() {
    UIHelper.hideKeyboard(this);
    findViewById(R.id.practiceMainLayout).setVisibility(View.INVISIBLE);
    final AlertDialog alertDialog = new AlertDialog.Builder(this).create();
    View layout = View.inflate(this, R.layout.circle_progress, null);
    DonutProgress donutProgress = (DonutProgress) layout.findViewById(R.id.donut_progress);
    FloatingActionButton doneButton = (FloatingActionButton) layout.findViewById(R.id.doneButton);
    View.OnClickListener onClickListener = new View.OnClickListener() {
      @Override
      public void onClick(View v) {
        alertDialog.dismiss();
        PracticeTestActivity.this.finish();
      }
    };
    doneButton.setOnClickListener(onClickListener);
    donutProgress.setProgress(nbAnswer == 0 ? 0 : Math.round(100 * nbCorrectAnswer / nbAnswer));
    alertDialog.setView(layout);
    alertDialog.show();
  }

  /**
   * Select a new word to display to user.
   */
  protected void newWordSelection() {
    Random random = new Random();
    indexWord = listRemainingIndex.get(random.nextInt(listRemainingIndex.size()));
    isReverse = random.nextBoolean();
  }

  /**
   * @return the number of answers
   */
  private int getNbAnswer() {
    return getNumberOfWords() - listRemainingIndex.size() - 1;
  }

  /**
   * @return the number of words that the user needs to guess
   */
  protected int getNumberOfWords() {
    return MainApplication.isPracticeTestAllWordsSelected() ? selectedDictionary.getWords().size() : MainApplication.getNumberOfWordsPerTest();
  }

  /**
   * Update the UI when a new word is proposed: propose a new word, pdate hint of TextEdit, prepare the expected
   * translation, etc.
   */
  @SuppressLint("SetTextI18n")
  private void updateGUI() {
    Word randomWord = getCurrentWord();
    ((TextView) findViewById(R.id.textViewPracticeProgression)).setText(nbAnswer + "/" + (nbAnswer + listRemainingIndex.size() + 1));
    String word, translationLang;
    if (isReverse) {
      word = randomWord.getTranslation();
      translationLang = selectedDictionary.getDisplayLanguage();
      expectedResult = randomWord.getWord();
    } else {
      word = randomWord.getWord();
      translationLang = Locale.getDefault().getDisplayLanguage();
      expectedResult = randomWord.getTranslation();
    }
    TextView tvWordToTranslate = findViewById(R.id.textViewPracticeTest);
    tvWordToTranslate.setText(word);
    int colorQuestion = getColorForThisQuestion();
    tvWordToTranslate.setTextColor(colorQuestion);
    EditText editText = findViewById(R.id.editTextPracticeTest);
    editText.getText().clear();
    editText.setHint(getString(R.string.translationHint, translationLang));
    editText.getBackground().mutate().setColorFilter(colorQuestion, PorterDuff.Mode.SRC_ATOP);

    ColorStateList tint = ColorStateList.valueOf(colorQuestion);
    FloatingActionButton nextButton = findViewById(R.id.buttonPracticeNext);
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
      nextButton.setBackgroundTintList(tint);
    } else {
      nextButton.setBackgroundTintList(tint);
    }
  }

  /**
   * This function allows to pick a color in order to apply a different color to every new proposed word
   * @return the color that will be assigned to the new proposed word
   */
  protected int getColorForThisQuestion() {
    return MainApplication.getColorFromShortList(getNbAnswer());
  }

  /**
   * @return the current displayed word
   */
  private Word getCurrentWord() {
    return selectedDictionary.getWords().get(indexWord);
  }

  /**
   * Manage the UI when an answer has been proposed by user and go to next word
   */
  private void validateAnswer() {
    String answer = getAnswer();
    if (!TextUtils.isEmpty(expectedResult) && !TextUtils.isEmpty(answer)) {
      boolean isAnswerCorrect = StringHelper.levenshteinDistance(expectedResult, getAnswer()) < 3;
      if (getCurrentWord().updateAnswerCoefficientMark(isAnswerCorrect)) {
        MainModel.addUpdatedMarkWord(new WordDictionary(getCurrentWord(), selectedDictionary));
      }

      final View viewToShow;
      TextView textViewPracticeResult = (TextView) findViewById(R.id.textViewPracticeResult);
      int delayShowAnswer = 2000;
      if (isAnswerCorrect) {
        nbCorrectAnswer++;
        findViewById(R.id.imageViewCorrect).setVisibility(View.VISIBLE);
        findViewById(R.id.imageViewNotCorrect).setVisibility(View.GONE);
        textViewPracticeResult.setTextColor(ContextCompat.getColor(this, R.color.correctAnswer));
        viewToShow = findViewById(R.id.layoutAnswer);
      } else {
        findViewById(R.id.imageViewCorrect).setVisibility(View.GONE);
        findViewById(R.id.imageViewNotCorrect).setVisibility(View.VISIBLE);
        textViewPracticeResult.setTextColor(ContextCompat.getColor(this, R.color.wrongAnswer));
        viewToShow = findViewById(R.id.layoutAnswer);
        delayShowAnswer = 3000;
      }
      nbAnswer++;

      textViewPracticeResult.setText(expectedResult);
      animatedTransition(findViewById(R.id.layoutQuestion), viewToShow);
      Handler handler = new Handler();
      handler.postDelayed(new Runnable() {
        public void run() {
          displayRandomWord();
          animatedTransition(viewToShow, findViewById(R.id.layoutQuestion));
        }
      }, delayShowAnswer);
      MainModel.saveInPreferences();
    }
  }

  /**
   * Function allowing to go from one {@link View} to another {@link View}. First view will fade out and second view
   * will fade in.
   * @param viewToHide the view that will slowly disappear
   * @param viewToShow the view that will slowly appear
   */
  private static void animatedTransition(final View viewToHide, final View viewToShow) {
    viewToShow.setAlpha(0f);
    viewToShow.setVisibility(View.VISIBLE);
    long mShortAnimationDuration = 700;
    viewToShow.animate().alpha(1f).setDuration(mShortAnimationDuration).setListener(null);

    viewToHide.animate().alpha(0f).setDuration(mShortAnimationDuration).setListener(new AnimatorListenerAdapter() {
      @Override
      public void onAnimationEnd(final Animator animation) {
        viewToHide.setVisibility(View.GONE);
      }
    });
  }

  /**
   * @return answer displayed in {@link R.id#editTextPracticeTest} view
   */
  public String getAnswer() {
    return ((EditText) findViewById(R.id.editTextPracticeTest)).getText().toString();
  }
}
