package com.processingbox.mydictionary;


import android.view.View;
import android.view.ViewGroup;
import android.view.ViewParent;

import androidx.test.espresso.DataInteraction;
import androidx.test.espresso.ViewInteraction;
import androidx.test.filters.LargeTest;
import androidx.test.rule.ActivityTestRule;

import com.processingbox.mydictionary.gui.LanguageListAdapter;
import com.processingbox.mydictionary.model.Dictionary;

import org.hamcrest.Description;
import org.hamcrest.Matcher;
import org.hamcrest.TypeSafeMatcher;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

import java.util.Iterator;
import java.util.List;

import static androidx.test.espresso.Espresso.onData;
import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.action.ViewActions.closeSoftKeyboard;
import static androidx.test.espresso.action.ViewActions.replaceText;
import static androidx.test.espresso.action.ViewActions.scrollTo;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withClassName;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withText;
import static org.hamcrest.Matchers.allOf;
import static org.hamcrest.Matchers.anything;
import static org.hamcrest.Matchers.is;

@LargeTest
public class AddNewWordTest {

  @Rule
  public ActivityTestRule<MainActivity> mActivityTestRule = new ActivityTestRule<>(MainActivity.class);

  @Before
  public void startTest() {
    List<Dictionary> dictionaries = MainModel.getDictionaries();
    Iterator<Dictionary> dictionariesIterator = dictionaries.iterator();
    while (dictionariesIterator.hasNext()) {
      MainModel.remove(dictionariesIterator.next());
    }
  }

  @Test
  public void addNewWordTest() {
    String translation = "The dog";
    String word = "De hond";

    ViewInteraction floatingActionButton = onView(allOf(withId(R.id.addButton), childAtPosition(allOf(withId(R.id.main_layout), childAtPosition(withId(android.R.id.content), 0)), 1), isDisplayed()));
    floatingActionButton.perform(click());


    List<String> listLanguages = LanguageListAdapter.buildLangList();
    DataInteraction linearLayout = onData(anything()).inAdapterView(allOf(withId(R.id.listViewLanguages), childAtPosition(withClassName(is("android.widget.LinearLayout")), 0))).atPosition(listLanguages.indexOf("nl"));
    linearLayout.perform(click());

    ViewInteraction floatingActionButton2 = onView(allOf(withId(R.id.addButton), childAtPosition(allOf(withId(R.id.main_layout), childAtPosition(withId(android.R.id.content), 0)), 1), isDisplayed()));
    floatingActionButton2.perform(click());

    ViewInteraction appCompatEditText = onView(allOf(withId(R.id.editTextNewWord), childAtPosition(childAtPosition(withId(R.id.custom), 0), 0), isDisplayed()));
    appCompatEditText.perform(replaceText(word), closeSoftKeyboard());

    ViewInteraction appCompatEditText2 = onView(allOf(withId(R.id.editTextTranslationNewWord), childAtPosition(childAtPosition(withId(R.id.custom), 0), 1), isDisplayed()));
    appCompatEditText2.perform(replaceText(translation), closeSoftKeyboard());

    ViewInteraction appCompatButton = onView(allOf(withId(android.R.id.button1), withText("OK"), childAtPosition(childAtPosition(withId(R.id.buttonPanel), 0), 3)));
    appCompatButton.perform(scrollTo(), click());

    List<Dictionary> dictionaries = MainModel.getDictionaries();
    Assert.assertEquals(dictionaries.size(), 1);
    Assert.assertEquals(dictionaries.get(0).getLanguage(), "nl");
    Assert.assertEquals(dictionaries.get(0).getWords().size(), 1);
    Assert.assertEquals(dictionaries.get(0).getWords().get(0).getWord(), word);
    Assert.assertEquals(dictionaries.get(0).getWords().get(0).getTranslation(), translation);
  }

  private static Matcher<View> childAtPosition(final Matcher<View> parentMatcher, final int position) {

    return new TypeSafeMatcher<View>() {
      @Override
      public void describeTo(Description description) {
        description.appendText("Child at position " + position + " in parent ");
        parentMatcher.describeTo(description);
      }

      @Override
      public boolean matchesSafely(View view) {
        ViewParent parent = view.getParent();
        return parent instanceof ViewGroup && parentMatcher.matches(parent) && view.equals(((ViewGroup) parent).getChildAt(position));
      }
    };
  }
}
