package com.example.tsleeve.swipeswap;

import android.content.Context;
import android.support.test.InstrumentationRegistry;
import android.util.Log;

import org.junit.Test;

import static junit.framework.Assert.assertEquals;

/**
 * Created by akshay on 11/13/2016.
 *
 * User should run app before running these tests.
 */

public class UserAuthTest {
    UserAuth fireAuth = new UserAuth();

    @Test
    public void useAppContext() throws Exception {
        // Context of the app under test.
        Context appContext = InstrumentationRegistry.getTargetContext();

        assertEquals("com.example.tsleeve.swipeswap", appContext.getPackageName());
    }

    @Test
    public void testValidUser() throws Exception {
        //Should return true if user has logeed in on device.
        assertEquals(true, fireAuth.validUser());
    }


    @Test
    public void testSignOut() throws Exception {
        Log.d("Testing UserAuth: UID=",fireAuth.uid());//log emulator/device user ID

        fireAuth.signOut();
        //Check user is signed out
        assertEquals(false, fireAuth.validUser());
    }
}
