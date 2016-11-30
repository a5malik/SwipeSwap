package com.example.tsleeve.swipeswap;

import android.support.test.rule.ActivityTestRule;
import android.support.test.runner.AndroidJUnit4;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.concurrent.TimeUnit;

import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.action.ViewActions.closeSoftKeyboard;
import static android.support.test.espresso.action.ViewActions.typeText;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static junit.framework.Assert.assertTrue;

/**
 * Created by akshay on 11/28/2016.
 */

@RunWith(AndroidJUnit4.class)
public class MultipleUiTest {
    private SwipeDataAuth dAuth = new SwipeDataAuth();
    private UserAuth uAuth = new UserAuth();

    private String mEmail;
    private String mPassword;

    @Rule
    public ActivityTestRule<MainActivity> mActivityRule = new ActivityTestRule<>(
            MainActivity.class);

    @Before
    public void initValidString() {
        // Let username be current timestamp (to avoid repetition)
        mEmail = "akshaybhat214@gmail.com";
        mPassword= "swipeswap";
    }

    @Test
    public void addSwipe() {
        // Sign in if not already signed in
        if(!uAuth.validUser())
            onView(withId(R.id.buttonSignIn)).perform(click());

        //Get a count of total swipes by the user.
        int totalUserSwipes = dAuth.getAllSwipesByUser(uAuth.uid(), Swipe.Type.SALE).size();

        //Click the floating action button to add a swipe
        onView(withId(R.id.main_fab)).perform(click());

        //Set date and time to default
        //onView(withId(R.id.buttonSwipeDate)).perform(click());
        //onView(withId(R.id.buttonSwipeTime)).perform(click());

        onView(withId(R.id.checkBoxFeast)).perform(click());

        onView(withId(R.id.editTextswipeprice))
                .perform(typeText("100"), closeSoftKeyboard());

        onView(withId(R.id.buttonaddrequest)).perform(click());

        // Wait for swipe to be updated
        try {
            TimeUnit.SECONDS.sleep(2);
        } catch (InterruptedException ie) {
            ie.printStackTrace();
        }

        int newSwipeCount = dAuth.getAllSwipesByUser(uAuth.uid(), Swipe.Type.SALE).size();

        //One swipe should have been added.
        assertTrue(newSwipeCount == totalUserSwipes+1);
    }
}
