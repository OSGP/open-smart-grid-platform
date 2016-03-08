package com.alliander.osgp.adapter.ws.smartmetering.application.mapping;

import org.junit.Assert;
import org.junit.Test;

import com.alliander.osgp.domain.core.valueobjects.smartmetering.CosemDate;

/**
 * Copyright 2015 Smart Society Services B.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 */
public class CosemDateConversionTest {

    @Test
    public void testWildCards() {
        byte[] source = new byte[] { (byte) 0x07, (byte) 0xDF, 6, (byte) 0xFF, (byte) 0xFF };
        final CosemDateConverter converter = new CosemDateConverter();
        CosemDate date = converter.convert(source, converter.getBType());
        Assert.assertEquals(2015, date.getYear());
        Assert.assertEquals(6, date.getMonth());
        Assert.assertEquals(0xFF, date.getDayOfMonth());
        Assert.assertEquals(0xFF, date.getDayOfWeek());

        source = new byte[] { (byte) 0xFF, (byte) 0xFF, (byte) 0xFE, (byte) 0xFD, (byte) 0xFF };
        date = converter.convert(source, converter.getBType());
        Assert.assertEquals(0xFFFF, date.getYear());
        Assert.assertEquals(0xFE, date.getMonth());
        Assert.assertEquals(0xFD, date.getDayOfMonth());
        Assert.assertEquals(0xFF, date.getDayOfWeek());

        source = new byte[] { (byte) 0xFF, (byte) 0xFF, (byte) 0xFE, 21, 3 };
        date = converter.convert(source, converter.getBType());
        Assert.assertEquals(0xFFFF, date.getYear());
        Assert.assertEquals(0xFE, date.getMonth());
        Assert.assertEquals(21, date.getDayOfMonth());
        Assert.assertEquals(3, date.getDayOfWeek());
    }

}
