package com.alliander.osgp.test;

import java.text.ParseException;
import java.util.Date;

import org.junit.Assert;
import org.junit.Test;

import com.alliander.osgp.platform.cucumber.helpers.UtcDateHelper;

public class TestUTCDate {

    @Test
    public void test() throws ParseException {
        final Date utc = UtcDateHelper.getUtcDate();
        Assert.assertTrue("utc date is before actual date", utc.before(new Date()));
    }

}
