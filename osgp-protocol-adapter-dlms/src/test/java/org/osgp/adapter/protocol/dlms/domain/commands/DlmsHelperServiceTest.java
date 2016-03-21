/**
 * Copyright 2015 Smart Society Services B.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 */
package org.osgp.adapter.protocol.dlms.domain.commands;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import java.nio.ByteBuffer;

import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;
import org.joda.time.format.ISODateTimeFormat;
import org.junit.Test;
import org.openmuc.jdlms.datatypes.CosemDateTime;
import org.openmuc.jdlms.datatypes.DataObject;

import com.alliander.osgp.dto.valueobjects.smartmetering.ClockStatusDto;
import com.alliander.osgp.dto.valueobjects.smartmetering.CosemDateDto;
import com.alliander.osgp.dto.valueobjects.smartmetering.CosemTimeDto;

public class DlmsHelperServiceTest {

    public static final DateTimeZone DATE_TIME_ZONE_AMSTERDAM = DateTimeZone.forID("Europe/Amsterdam");
    public static final short YEAR = 2015;
    public static final byte MONTH_SUMMER_TIME = 7;
    public static final byte MONTH_WINTER_TIME = 2;
    public static final byte DAY = 21;
    public static final byte HOUR = 14;
    public static final byte MINUTE = 53;
    public static final byte SECOND = 7;
    public static final byte HUNDREDTHS = 23;

    public static final int NUM_BYTES_DATE_TIME = 12;
    public static final byte CLOCK_STATUS_DST = (byte) 0x80;
    public static final byte CLOCK_STATUS_NO_DST = 0;
    public static final byte DAY_OF_WEEK_UNDEFINED = (byte) 0xFF;
    public static final short DEVIATION_AMSTERDAM_SUMMER_TIME = -120;
    public static final short DEVIATION_AMSTERDAM_WINTER_TIME = -60;

    private final DlmsHelperService dlmsHelperService = new DlmsHelperService();

    @Test
    public void testDateTimeSummerTime() {

        final DataObject dateInSummerTimeDataObject = this.dlmsHelperService.asDataObject(this.dateTimeSummerTime());

        assertTrue(dateInSummerTimeDataObject.isCosemDateFormat());
        assertTrue(dateInSummerTimeDataObject.value() instanceof CosemDateTime);

        final CosemDateTime cosemDateTime = (CosemDateTime) dateInSummerTimeDataObject.value();

        assertArrayEquals(this.byteArraySummerTime(), cosemDateTime.encode());
    }

    @Test
    public void testDateTimeWinterTime() {

        final DataObject dateInWinterTimeDataObject = this.dlmsHelperService.asDataObject(this.dateTimeWinterTime());

        assertTrue(dateInWinterTimeDataObject.isCosemDateFormat());
        assertTrue(dateInWinterTimeDataObject.value() instanceof CosemDateTime);

        final CosemDateTime cosemDateTime = (CosemDateTime) dateInWinterTimeDataObject.value();

        assertArrayEquals(this.byteArrayWinterTime(), cosemDateTime.encode());
    }

    @Test
    public void testFromByteArraySummerTime() throws Exception {

        final com.alliander.osgp.dto.valueobjects.smartmetering.CosemDateTimeDto cosemDateTime = this.dlmsHelperService
                .fromDateTimeValue(this.byteArraySummerTime());

        assertTrue(cosemDateTime.isDateTimeSpecified());

        final DateTime dateInSummerTime = cosemDateTime.asDateTime();

        assertEquals("2015-07-21T14:53:07.230+02:00", ISODateTimeFormat.dateTime().print(dateInSummerTime));
    }

    @Test
    public void testFromByteArrayWinterTime() throws Exception {

        final com.alliander.osgp.dto.valueobjects.smartmetering.CosemDateTimeDto cosemDateTime = this.dlmsHelperService
                .fromDateTimeValue(this.byteArrayWinterTime());

        assertTrue(cosemDateTime.isDateTimeSpecified());

        final DateTime dateInWinterTime = cosemDateTime.asDateTime();

        assertEquals("2015-02-21T14:53:07.230+01:00", ISODateTimeFormat.dateTime().print(dateInWinterTime));
    }

    @Test
    public void testFromByteArrayUnspecifiedTime() throws Exception {

        final com.alliander.osgp.dto.valueobjects.smartmetering.CosemDateTimeDto cosemDateTime = this.dlmsHelperService
                .fromDateTimeValue(this.byteArrayUnspecifiedTime());

        assertFalse(cosemDateTime.isDateTimeSpecified());
        assertFalse(cosemDateTime.isLocalDateSpecified());
        assertFalse(cosemDateTime.isLocalDateTimeSpecified());
        assertFalse(cosemDateTime.isLocalTimeSpecified());
        assertFalse(cosemDateTime.isDeviationSpecified());

        assertNull(cosemDateTime.asDateTime());
    }

    @Test
    public void testCorrectLogMessageForBitStringObject() {
        final String expected = "number of bytes=2, value=37440, bits=10010010 01000000 ";
        final String logMessage = this.dlmsHelperService.getDebugInfoBitStringBytes(new byte[] { -110, 64 });

        assertEquals(expected, logMessage);
    }

    private DateTime dateTimeSummerTime() {
        return new DateTime(YEAR, MONTH_SUMMER_TIME, DAY, HOUR, MINUTE, SECOND, HUNDREDTHS * 10,
                DATE_TIME_ZONE_AMSTERDAM);
    }

    private DateTime dateTimeWinterTime() {
        return new DateTime(YEAR, MONTH_WINTER_TIME, DAY, HOUR, MINUTE, SECOND, HUNDREDTHS * 10,
                DATE_TIME_ZONE_AMSTERDAM);
    }

    private byte[] byteArraySummerTime() {

        final ByteBuffer bb = ByteBuffer.allocate(NUM_BYTES_DATE_TIME);
        bb.putShort(YEAR);
        bb.put(MONTH_SUMMER_TIME);
        bb.put(DAY);
        bb.put(DAY_OF_WEEK_UNDEFINED);
        bb.put(HOUR);
        bb.put(MINUTE);
        bb.put(SECOND);
        bb.put(HUNDREDTHS);
        bb.putShort(DEVIATION_AMSTERDAM_SUMMER_TIME);
        bb.put(CLOCK_STATUS_DST);

        return bb.array();
    }

    private byte[] byteArrayWinterTime() {

        final ByteBuffer bb = ByteBuffer.allocate(NUM_BYTES_DATE_TIME);
        bb.putShort(YEAR);
        bb.put(MONTH_WINTER_TIME);
        bb.put(DAY);
        bb.put(DAY_OF_WEEK_UNDEFINED);
        bb.put(HOUR);
        bb.put(MINUTE);
        bb.put(SECOND);
        bb.put(HUNDREDTHS);
        bb.putShort(DEVIATION_AMSTERDAM_WINTER_TIME);
        bb.put(CLOCK_STATUS_NO_DST);

        return bb.array();
    }

    private byte[] byteArrayUnspecifiedTime() {

        final ByteBuffer bb = ByteBuffer.allocate(NUM_BYTES_DATE_TIME);
        bb.putShort((short) CosemDateDto.YEAR_NOT_SPECIFIED);
        bb.put((byte) CosemDateDto.MONTH_NOT_SPECIFIED);
        bb.put((byte) CosemDateDto.DAY_OF_MONTH_NOT_SPECIFIED);
        bb.put((byte) CosemDateDto.DAY_OF_WEEK_NOT_SPECIFIED);
        bb.put((byte) CosemTimeDto.HOUR_NOT_SPECIFIED);
        bb.put((byte) CosemTimeDto.MINUTE_NOT_SPECIFIED);
        bb.put((byte) CosemTimeDto.SECOND_NOT_SPECIFIED);
        bb.put((byte) CosemTimeDto.HUNDREDTHS_NOT_SPECIFIED);
        bb.putShort((short) com.alliander.osgp.dto.valueobjects.smartmetering.CosemDateTimeDto.DEVIATION_NOT_SPECIFIED);
        bb.put((byte) ClockStatusDto.STATUS_NOT_SPECIFIED);

        return bb.array();
    }
}
