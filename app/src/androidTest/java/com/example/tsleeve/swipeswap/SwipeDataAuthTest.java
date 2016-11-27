package com.example.tsleeve.swipeswap;

import android.support.test.runner.AndroidJUnit4;
import android.util.Log;

import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.ArrayList;
import java.util.Calendar;

import static junit.framework.Assert.assertEquals;

/**
 * Created by akshay on 11/13/2016.
 */

@RunWith(AndroidJUnit4.class)
public class SwipeDataAuthTest {
    private Calendar cal = Calendar.getInstance();
    SwipeDataAuth testAuth = new SwipeDataAuth();

    /*Returns Swipe with same start time as original swipe (or null if it doesn't exist)*/
    public Swipe getSwipe(String uid, final Swipe.Type type, Swipe original) {
        Swipe s2 = new Swipe();
        ArrayList<Swipe> Swipes = testAuth.getAllSwipesByUser(uid, type);
        Log.d("Testing: SwipeDataAuth", "list size=" + Swipes.size());
        for (Swipe cur : Swipes) {
            if (cur.getStartTime() == original.getStartTime()) {
                s2 = cur;
                Log.d("Testing AddSwipe ", "cur=s1");
                break;
            }
        }
        return s2;
    }

    @Test
    public void testAddSwipe() throws Exception {
/*
        long curTime = cal.getTimeInMillis();
        Swipe s1 = new Swipe(25.0, curTime, curTime, "99999", 2, Swipe.Type.REQUEST);
        testAuth.addRequest(s1,"99999TEST_USER");


        //TO-DO: FIX DELAY BETWEEN ADDING SWIPE AND IT SHOWING UP ON FIREBASE
         Swipe s2 = new Swipe();
        ArrayList<Swipe> testRequests = testAuth.getAllSwipesByUser("99999TEST_USER",Swipe.Type.REQUEST);
        Log.d("Testing addswipe", "testRequests size=" +testRequests.size());
        for (Swipe cur: testRequests) {
            if (cur.getStartTime() == s1.getStartTime()) {
                s2 = cur;
                Log.d("Testing AddSwipe ","cur=s1");
                break;
            }
        }
        assertEquals(s1.getPrice(), s2.getPrice());
        assertEquals(s1.getOwner_ID(), s2.getOwner_ID());
        assertEquals(s1.getDiningHall(), s2.getDiningHall());
        assertEquals(s1.getEndTime(), s2.getEndTime());*/
    }

    @Test
    public void testRemoveSwipe() throws Exception {
/*
        long curTime = cal.getTimeInMillis();
        Swipe s1 = new Swipe(8.0, curTime, curTime, "333", 1, Swipe.Type.SALE);
        testAuth.addSwipe(s1,"333TEST_USER");
        Swipe s2 = getSwipe("333TEST_USER",Swipe.Type.SALE, s1);

        //Swipe added successfully
        assertEquals(s1.getPrice(), s2.getPrice());
        assertEquals(s1.getOwner_ID(), s2.getOwner_ID());
        assertEquals(s1.getDiningHall(), s2.getDiningHall());
        assertEquals(s1.getEndTime(), s2.getEndTime());

        testAuth.removeSwipe("333TEST_USER",s1.getPostTime(), Swipe.Type.SALE);
        Swipe s3 = getSwipe("333TEST_USER",Swipe.Type.SALE, s1);
        assertEquals(s3, null);//The swipe should no longer exist*/
    }


    @Test
    public void testUserReference() throws Exception {
/*
        Log.d("Testing","testUserReference");
        assertEquals("https://swipeswap-9995f.firebaseio.com/users", testAuth.getUsersReference().toString());*/
    }

    @Test
    public void testGetUserName() throws Exception {
/*
        Log.d("Testing","testGetUserName");
        assertEquals("akshay", testAuth.getUserName("tvcyjBiCzcgJUiDx4eRTKsWBztf2").toString());*/

    }

    /*@Test
    public void testgetUserToken() throws Exception {

        Log.d("Testing","testGetUserName");
        assertEquals("cZBQu452br0:APA91bEBTjnEPgLwrVxcqy8lqxMsVbRNEqPSSbENu-NfyYFi7s19LHfkFdWJTqXmxnR3tYP2TX8_RtyXu1PlBvFTztZZJBI5eEmlDE8LSLdQGu6i4IewRmi8yh75v25HYPeZv1i5-_2g",
                testAuth.getUserToken("tvcyjBiCzcgJUiDx4eRTKsWBztf2").toString());

    }*/

}
