package com.example.tsleeve.swipeswap;

import android.content.Context;
import android.support.test.InstrumentationRegistry;
import android.support.test.runner.AndroidJUnit4;
import android.util.Log;

import org.junit.Test;
import org.junit.runner.RunWith;

import static junit.framework.Assert.assertEquals;

/**
 * Instrumentation test, which will execute on an Android device.
 * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
 *
 * Add your tests as seperate methods within the class.
 */
@RunWith(AndroidJUnit4.class)
public class MainInstrumentedTest {
    @Test
    public void useAppContext() throws Exception {
        // Context of the app under test.
        Context appContext = InstrumentationRegistry.getTargetContext();

        Log.d("Testing","useAppContext");
        assertEquals("com.example.tsleeve.swipeswap", appContext.getPackageName());
    }
}
