package com.alliander.osgp.cucumber.platform.core;

import org.joda.time.DateTime;
import org.junit.Assert;
import org.junit.Test;

public class TestHelper {

    @Test
    public void testGetDateTime() {
        try {
            final DateTime nowPlus4 = new DateTime().plusMinutes(4);
            final DateTime nowPlus6 = new DateTime().plusMinutes(6);
            final DateTime dt = Helpers.getDateTime("now + 5 minutes");
            Assert.assertTrue(nowPlus4.getMillis() < dt.getMillis());
            Assert.assertTrue(nowPlus6.getMillis() > dt.getMillis());
        } catch (final Exception e) {
            Assert.fail("error parsing date " + e);
        }
    }
}
