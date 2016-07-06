package com.pkmmte.pkrss;

import android.support.test.runner.AndroidJUnit4;
import android.test.AndroidTestCase;

import org.junit.After;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

/**
 * <a href="http://d.android.com/tools/testing/testing_android.html">Testing Fundamentals</a>
 */
@RunWith(AndroidJUnit4.class)
public class PkRssTest extends AndroidTestCase {

    private static final String RSS_URL = "http://lorem-rss.herokuapp.com/feed";

    @Test
    public void testSynchGet() {
        try {
            List<Article> articles = PkRSS.with(getContext()).load(RSS_URL).get();
            assertTrue(!articles.isEmpty());
        } catch (IOException e) {
            fail();
        }
    }

    @Test
    public void testAsynchGet() {
        try {
            final CountDownLatch lock = new CountDownLatch(1);
            final List<Article> articles = new ArrayList<>();

            Callback callback = new Callback() {

                @Override
                public void OnPreLoad() {
                    // do nothing
                }

                @Override
                public void OnLoaded(List<Article> newArticles) {
                    articles.addAll(newArticles);
                    lock.countDown();
                }

                @Override
                public void OnLoadFailed() {
                    lock.countDown();
                    fail();
                }
            };

            PkRSS.with(getContext()).load(RSS_URL).callback(callback).get();
            lock.await(5000, TimeUnit.MILLISECONDS);
            assertTrue(!articles.isEmpty());

        } catch (Exception e) {
            fail();
        }
    }

    @After
    public void tearDown() throws Exception {
        super.tearDown();
    }
}
