/**
 * Copyright 2017 Smart Society Services B.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 */
package com.alliander.osgp.cucumber.platform.core;

import org.joda.time.DateTime;
import org.junit.Assert;
import org.junit.Test;

public class TestGetDateTime {

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
