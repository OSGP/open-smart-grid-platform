package com.alliander.osgp.adapter.ws.smartmetering.application.mapping;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import ma.glasnost.orika.MapperFactory;
import ma.glasnost.orika.impl.DefaultMapperFactory;

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
public class CosemDateConverterTest {

    private MapperFactory mapperFactory = new DefaultMapperFactory.Builder().build();

    private static final byte FIRST_BYTE_FOR_YEAR = (byte) 0x07;
    private static final byte SECOND_BYTE_FOR_YEAR = (byte) 0xE0;
    private static final byte BYTE_FOR_MONTH = 4;
    private static final byte BYTE_FOR_DAY_OF_MONTH = 7;
    private static final byte BYTE_FOR_DAY_OF_WEEK = (byte) 0xFF;
    private static final byte[] COSEMDATE_BYTE_ARRAY = { FIRST_BYTE_FOR_YEAR, SECOND_BYTE_FOR_YEAR, BYTE_FOR_MONTH,
            BYTE_FOR_DAY_OF_MONTH, BYTE_FOR_DAY_OF_WEEK };

    /**
     * Tests if mapping a byte[] to a CosemDate object succeeds.
     */
    @Test
    public void testToCosemDateMapping() {

        // register the CosemDateConverter, since the test converts to a
        // CosemDate object.
        this.mapperFactory.getConverterFactory().registerConverter(new CosemDateConverter());

        // actual mapping
        final CosemDate cosemDate = this.mapperFactory.getMapperFacade().map(COSEMDATE_BYTE_ARRAY, CosemDate.class);

        // check mapping
        assertNotNull(cosemDate);
        assertEquals(FIRST_BYTE_FOR_YEAR, ((byte) (cosemDate.getYear() >> 8)));
        assertEquals(SECOND_BYTE_FOR_YEAR, ((byte) (cosemDate.getYear() & 0xFF)));
        assertEquals(BYTE_FOR_MONTH, cosemDate.getMonth());
        assertEquals(BYTE_FOR_DAY_OF_MONTH, cosemDate.getDayOfMonth());
        assertEquals(BYTE_FOR_DAY_OF_WEEK, ((byte) cosemDate.getDayOfWeek()));
    }

    /**
     * Tests the mapping of wildcards.
     */
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
