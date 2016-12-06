package com.alliander.osgp.test;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimeZone;

import org.junit.Assert;
import org.junit.Test;

public class TestUTCDate {

    @Test
    public void test() throws ParseException {
        final SimpleDateFormat dateFormatGmt = new SimpleDateFormat("yyyy-MMM-dd HH:mm:ss");
        dateFormatGmt.setTimeZone(TimeZone.getTimeZone("GMT"));
        final SimpleDateFormat dateFormatLocal = new SimpleDateFormat("yyyy-MMM-dd HH:mm:ss");
        final Date utc = dateFormatLocal.parse(dateFormatGmt.format(new Date()));
        Assert.assertTrue("utc date is before actual date", utc.before(new Date()));
    }

}
