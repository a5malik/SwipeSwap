package com.example.tsleeve.swipeswap;

import android.content.Context;
import android.support.test.InstrumentationRegistry;
import android.support.test.runner.AndroidJUnit4;
import android.util.Log;

import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.Calendar;

import static junit.framework.Assert.assertEquals;

/**
 * Instrumentation test, which will execute on an Android device.
 * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
 *
 * Add your tests as seperate methods within the class.
 */
@RunWith(AndroidJUnit4.class)
public class MainInstrumentedTest {
    private Calendar cal = Calendar.getInstance();
    SwipeDataAuth testAuth = new SwipeDataAuth();

    @Test
    public void useAppContext() throws Exception {
        // Context of the app under test.
        Context appContext = InstrumentationRegistry.getTargetContext();

        Log.d("Testing","useAppContext");
        assertEquals("com.example.tsleeve.swipeswap", appContext.getPackageName());
    }

    @Test
    public void testUserReference() throws Exception {

        Log.d("Testing","testUserReference");
        assertEquals("https://swipeswap-9995f.firebaseio.com/users", testAuth.getUsersReference().toString());
    }

    @Test
    public void testAddSwipe() throws Exception {
        Log.d("Testing","addSwipe");

        long curTime = cal.getTimeInMillis();
        Swipe s1 = new Swipe(40.0, curTime, curTime, "99999", 2);
        testAuth.addSwipe(s1,"99999TEST_USER");
        //TODO: Fix this so that it adds a swipe, and verify the swipe was added.
    }
}
