package com.example.tsleeve.swipeswap;


import android.support.test.rule.ActivityTestRule;
import android.support.test.runner.AndroidJUnit4;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.Calendar;
import java.util.concurrent.TimeUnit;

import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.action.ViewActions.closeSoftKeyboard;
import static android.support.test.espresso.action.ViewActions.typeText;
import static android.support.test.espresso.matcher.ViewMatchers.withId;

import static junit.framework.Assert.assertEquals;

/**
 * Created by akshay on 11/21/2016.
 */
@RunWith(AndroidJUnit4.class)
public class RegisterUiTest {
    private Calendar cal = Calendar.getInstance();
    private SwipeDataAuth dAuth = new SwipeDataAuth();
    private UserAuth uAuth = new UserAuth();

    private String mUsername;

    @Rule
    public ActivityTestRule<RegisterActivity> mActivityRule = new ActivityTestRule<>(
            RegisterActivity.class);

    @Before
    public void initValidString() {
        // Let username be current timestamp (to avoid repetition)
        mUsername = Long.toString(cal.getTimeInMillis());
    }

    @Test
    public void changeUsername() {
        // Type text and then press the button.
        onView(withId(R.id.editTextaddusername))
                .perform(typeText(mUsername), closeSoftKeyboard());
        onView(withId(R.id.buttonsubmitregistration)).perform(click());

        // Wait for username to update
        try {
            TimeUnit.SECONDS.sleep(1);
        } catch (InterruptedException ie) {
            ie.printStackTrace();
        }

        // Check that the username was changed in firebase.
        assertEquals(mUsername, dAuth.getUserName(uAuth.uid()));
    }
}
