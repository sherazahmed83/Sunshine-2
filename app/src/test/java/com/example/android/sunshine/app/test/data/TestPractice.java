package com.example.android.sunshine.app.test.data;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.annotation.Config;

@RunWith(RobolectricTestRunner.class)
@Config( shadows = { ShadowSQLiteOpenHelper.class }, constants = BuildConfig.class, manifest = Config.NONE)
public class TestPractice {
    /*
        This gets run before every test.
     */
    @Before
    public void setUp() throws Exception {
//        super.setUp();
    }

    @Test
    public void testThatDemonstratesAssertions() throws Throwable {
        int a = 5;
        int b = 3;
        int c = 5;
        int d = 10;

        Assert.assertEquals("X should be equal", a, c);
        Assert.assertTrue("Y should be true", d > a);
        Assert.assertFalse("Z should be false", a == b);

        if (b > d) {
            Assert.fail("XX should never happen");
        }
    }
    @After
    public void tearDown() throws Exception {
//        super.tearDown();
    }
}
