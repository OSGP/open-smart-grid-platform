package com.alliander.osgp.test;

import java.text.ParseException;
import java.util.Date;

import org.junit.Assert;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.alliander.osgp.platform.cucumber.helpers.UtcDateHelper;

public class TestUtcDateHelper {

    private static final Logger LOGGER = LoggerFactory.getLogger(TestUtcDateHelper.class);

    @Test
    /**
     * This method checks that the UTC date is before the local date.
     * Because the UTC date is obtained before the actual, this method will always succeed
     * even if this junit test runs on a machine where UTC time is used.
     * Hence the purpose of this junit test was only to demonstrate the correct UTC vs local date on a local machine.
     * @throws ParseException
     */
    public void test() throws ParseException {
        final Date utc = UtcDateHelper.getUtcDate();
        LOGGER.info("utc date = " + utc + " local date = " + new Date());
        Assert.assertTrue("utc date is before actual date", utc.before(new Date()));
    }

}
