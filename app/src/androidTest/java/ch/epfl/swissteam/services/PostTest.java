package ch.epfl.swissteam.services;

import android.support.test.runner.AndroidJUnit4;

import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.Calendar;
import java.util.Date;

import static org.junit.Assert.assertEquals;

@RunWith(AndroidJUnit4.class)
public class PostTest {

    static String title = "Hello", id = "Jean-Charles", body = "World";
    static long timestamp = 42;
    static String key = id + "_" + timestamp;
    private static Calendar cal = Calendar.getInstance();
    static String today = Post.dateToString(cal.getTime());

    @Test
    public void creationWorks() {
        Post p = new Post(key, title, id, body, timestamp, 10, 20, today);
        assertEquals(p.getTitle_(), title);
        assertEquals(p.getGoogleId_(), id);
        assertEquals(p.getBody_(), body);
        assertEquals(p.getTimestamp_(), timestamp);
        assertEquals(p.getKey_(), key);
        assertEquals(p.getTimeoutDateString_(), today);
    }

    @Test
    public void setTitle(){
        Post p = new Post(key, title, id, body, timestamp, 10, 20, today);
        String test = "testTitle";
        p.setTitle_(test);
        assertEquals(p.getTitle_(), test);
    }

    @Test
    public void setBody(){
        Post p = new Post(key, title, id, body, timestamp, 10, 20, today);
        String test = "testBody";
        p.setBody_(test);
        assertEquals(p.getBody_(), test);
    }

    @Test
    public void notValidDateFormatShouldGive6Month(){
        Post p = new Post(key, title, id, body, timestamp, 10, 20, "18-2-5");
        cal.add(Calendar.MONTH, 6);
        assertEquals(p.getTimeoutDateString_(), Post.dateToString(cal.getTime()));
    }

    @Test
    public void utilitaryMethodsDate(){
        assertEquals(null, Post.dateToString(null));
        assertEquals(null, Post.dateFromString(null));
    }
}
