/**
 * Copyright 2014-2016 Smart Society Services B.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 */

package com.alliander.osgp.adapter.ws.smartmetering.application.mapping;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import ma.glasnost.orika.MapperFactory;
import ma.glasnost.orika.impl.DefaultMapperFactory;

import org.junit.Test;

import com.alliander.osgp.domain.core.valueobjects.smartmetering.CosemTime;

public class CosemTimeConverterTest {

    private MapperFactory mapperFactory = new DefaultMapperFactory.Builder().build();

    private static final byte BYTE_FOR_HOUR_OF_DAY = 10;
    private static final byte BYTE_FOR_MINUTE_OF_HOUR = 34;
    private static final byte BYTE_FOR_SECOND_OF_MINUTE = 35;
    private static final byte BYTE_FOR_HUNDREDS_0F_SECONDS = 10;
    private static final byte[] COSEMTIME_BYTE_ARRAY = { BYTE_FOR_HOUR_OF_DAY, BYTE_FOR_MINUTE_OF_HOUR,
            BYTE_FOR_SECOND_OF_MINUTE, BYTE_FOR_HUNDREDS_0F_SECONDS };

    /**
     * Tests if mapping a byte[] to a CosemTime object succeeds.
     */
    @Test
    public void testToCosemTimeMapping() {

        // register the CosemTimeConverter, since the test converts to a
        // CosemTime object.
        this.mapperFactory.getConverterFactory().registerConverter(new CosemTimeConverter());

        // actual mapping
        final CosemTime cosemTime = this.mapperFactory.getMapperFacade().map(COSEMTIME_BYTE_ARRAY, CosemTime.class);

        // check mapping
        assertNotNull(cosemTime);
        assertEquals(BYTE_FOR_HOUR_OF_DAY, cosemTime.getHour());
        assertEquals(BYTE_FOR_MINUTE_OF_HOUR, cosemTime.getMinute());
        assertEquals(BYTE_FOR_SECOND_OF_MINUTE, cosemTime.getSecond());
        assertEquals(BYTE_FOR_HUNDREDS_0F_SECONDS, cosemTime.getHundredths());
    }

}
