/**
 * Copyright 2015 Smart Society Services B.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 */
package org.opensmartgridplatform.adapter.protocol.dlms.domain.commands.utils;

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
import org.opensmartgridplatform.dto.valueobjects.smartmetering.ClockStatusDto;
import org.opensmartgridplatform.dto.valueobjects.smartmetering.CosemDateDto;
import org.opensmartgridplatform.dto.valueobjects.smartmetering.CosemDateTimeDto;
import org.opensmartgridplatform.dto.valueobjects.smartmetering.CosemTimeDto;

public class DlmsHelperTest {

    public static final DateTimeZone DATE_TIME_ZONE_AMSTERDAM = DateTimeZone.forID("Europe/Amsterdam");
    public static final DateTimeZone DATE_TIME_ZONE_NEW_YORK = DateTimeZone.forID("America/New_York");
    public static final DateTimeZone DATE_TIME_ZONE_UTC = DateTimeZone.UTC;
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

    private final DlmsHelper dlmsHelper = new DlmsHelper();

    @Test
    public void testDateTimeSummerTime() {

        final DataObject dateInSummerTimeDataObject = this.dlmsHelper.asDataObject(this.dateTimeSummerTime());

        assertTrue(dateInSummerTimeDataObject.isCosemDateFormat());
        assertTrue(dateInSummerTimeDataObject.getValue() instanceof CosemDateTime);

        final CosemDateTime cosemDateTime = dateInSummerTimeDataObject.getValue();

        assertArrayEquals(this.byteArraySummerTime(), cosemDateTime.encode());
    }

    @Test
    public void testDateTimeSummerTimeWithDeviationAndDst() {

        final DataObject dateInSummerTimeDataObject = this.dlmsHelper
                .asDataObject(this.dateTimeSummerTime(), -120, true);

        assertTrue(dateInSummerTimeDataObject.isCosemDateFormat());
        assertTrue(dateInSummerTimeDataObject.getValue() instanceof CosemDateTime);

        final CosemDateTime cosemDateTime = dateInSummerTimeDataObject.getValue();

        assertArrayEquals(this.byteArraySummerTime(), cosemDateTime.encode());
    }

    @Test
    public void testDateTimeSummerTimeWithDeviationAndDstFromOtherTimeZone() {

        /*
         * The date and time on the device should be set according to the
         * deviation and daylight savings information provided as parameters.
         * The time of the server can be given in another time zone than the
         * device is in, but the instant in time should remain the same.
         *
         * This test has a time input as if a server in the UTC time zone would
         * be synchronizing time on a device in the Amsterdam time zone.
         */
        final DataObject dateInSummerTimeDataObject = this.dlmsHelper
                .asDataObject(this.dateTimeSummerTimeUtc(), -120, true);

        assertTrue(dateInSummerTimeDataObject.isCosemDateFormat());
        assertTrue(dateInSummerTimeDataObject.getValue() instanceof CosemDateTime);

        final CosemDateTime cosemDateTime = dateInSummerTimeDataObject.getValue();

        assertArrayEquals(this.byteArraySummerTime(), cosemDateTime.encode());
    }

    @Test
    public void testDateTimeWinterTime() {

        final DataObject dateInWinterTimeDataObject = this.dlmsHelper.asDataObject(this.dateTimeWinterTime());

        assertTrue(dateInWinterTimeDataObject.isCosemDateFormat());
        assertTrue(dateInWinterTimeDataObject.getValue() instanceof CosemDateTime);

        final CosemDateTime cosemDateTime = dateInWinterTimeDataObject.getValue();

        assertArrayEquals(this.byteArrayWinterTime(), cosemDateTime.encode());
    }

    @Test
    public void testDateTimeWinterTimeWithDeviationAndDst() {

        final DataObject dateInWinterTimeDataObject = this.dlmsHelper
                .asDataObject(this.dateTimeWinterTime(), -60, false);

        assertTrue(dateInWinterTimeDataObject.isCosemDateFormat());
        assertTrue(dateInWinterTimeDataObject.getValue() instanceof CosemDateTime);

        final CosemDateTime cosemDateTime = dateInWinterTimeDataObject.getValue();

        assertArrayEquals(this.byteArrayWinterTime(), cosemDateTime.encode());
    }

    @Test
    public void testDateTimeWinterTimeWithDeviationAndDstFromOtherTimeZone() {

        /*
         * The date and time on the device should be set according to the
         * deviation and daylight savings information provided as parameters.
         * The time of the server can be given in another time zone than the
         * device is in, but the instant in time should remain the same.
         *
         * This test has a time input as if a server in the New York time zone
         * would be synchronizing time on a device in the Amsterdam time zone.
         */
        final DataObject dateInWinterTimeDataObject = this.dlmsHelper
                .asDataObject(this.dateTimeWinterTimeNewYork(), -60, false);

        assertTrue(dateInWinterTimeDataObject.isCosemDateFormat());
        assertTrue(dateInWinterTimeDataObject.getValue() instanceof CosemDateTime);

        final CosemDateTime cosemDateTime = dateInWinterTimeDataObject.getValue();

        assertArrayEquals(this.byteArrayWinterTime(), cosemDateTime.encode());
    }

    @Test
    public void testFromByteArraySummerTime() throws Exception {

        final CosemDateTimeDto cosemDateTime = this.dlmsHelper.fromDateTimeValue(this.byteArraySummerTime());

        assertTrue(cosemDateTime.isDateTimeSpecified());

        final DateTime dateInSummerTime = cosemDateTime.asDateTime();

        assertEquals("2015-07-21T14:53:07.230+02:00", ISODateTimeFormat.dateTime().print(dateInSummerTime));
    }

    @Test
    public void testFromByteArrayWinterTime() throws Exception {

        final CosemDateTimeDto cosemDateTime = this.dlmsHelper.fromDateTimeValue(this.byteArrayWinterTime());

        assertTrue(cosemDateTime.isDateTimeSpecified());

        final DateTime dateInWinterTime = cosemDateTime.asDateTime();

        assertEquals("2015-02-21T14:53:07.230+01:00", ISODateTimeFormat.dateTime().print(dateInWinterTime));
    }

    @Test
    public void testFromByteArrayUnspecifiedTime() throws Exception {

        final CosemDateTimeDto cosemDateTime = this.dlmsHelper.fromDateTimeValue(this.byteArrayUnspecifiedTime());

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
        final String logMessage = this.dlmsHelper.getDebugInfoBitStringBytes(new byte[] { -110, 64 });

        assertEquals(expected, logMessage);
    }

    private DateTime dateTimeSummerTime() {
        return new DateTime(YEAR, MONTH_SUMMER_TIME, DAY, HOUR, MINUTE, SECOND, HUNDREDTHS * 10,
                DATE_TIME_ZONE_AMSTERDAM);
    }

    private DateTime dateTimeSummerTimeUtc() {
        /*
         * Original time in Europe/Amsterdam is in UTC+2 for the summer time, so
         * subtract 2 from the hour for UTC time at the same instant.
         */
        return new DateTime(YEAR, MONTH_SUMMER_TIME, DAY, HOUR - 2, MINUTE, SECOND, HUNDREDTHS * 10,
                DATE_TIME_ZONE_UTC);
    }

    private DateTime dateTimeWinterTime() {
        return new DateTime(YEAR, MONTH_WINTER_TIME, DAY, HOUR, MINUTE, SECOND, HUNDREDTHS * 10,
                DATE_TIME_ZONE_AMSTERDAM);
    }

    private DateTime dateTimeWinterTimeNewYork() {
        /*
         * New York - for the winter date time - is in UTC-5, original time in
         * Europe/Amsterdam is in UTC+1 then, so subtract 6 from the hour to get
         * New York time for the same instant.
         */
        return new DateTime(YEAR, MONTH_WINTER_TIME, DAY, HOUR - 6, MINUTE, SECOND, HUNDREDTHS * 10,
                DATE_TIME_ZONE_NEW_YORK);
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
        bb.putShort((short) CosemDateTimeDto.DEVIATION_NOT_SPECIFIED);
        bb.put((byte) ClockStatusDto.STATUS_NOT_SPECIFIED);

        return bb.array();
    }
}
