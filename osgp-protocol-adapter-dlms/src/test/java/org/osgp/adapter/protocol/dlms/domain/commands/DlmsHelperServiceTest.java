package org.osgp.adapter.protocol.dlms.domain.commands;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.nio.ByteBuffer;

import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;
import org.joda.time.format.ISODateTimeFormat;
import org.junit.Test;
import org.openmuc.jdlms.DataObject;
import org.openmuc.jdlms.internal.CosemDateTime;

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

        assertArrayEquals(this.byteArraySummerTime(), cosemDateTime.ocletString());
    }

    @Test
    public void testDateTimeWinterTime() {

        final DataObject dateInWinterTimeDataObject = this.dlmsHelperService.asDataObject(this.dateTimeWinterTime());

        assertTrue(dateInWinterTimeDataObject.isCosemDateFormat());
        assertTrue(dateInWinterTimeDataObject.value() instanceof CosemDateTime);

        final CosemDateTime cosemDateTime = (CosemDateTime) dateInWinterTimeDataObject.value();

        assertArrayEquals(this.byteArrayWinterTime(), cosemDateTime.ocletString());
    }

    @Test
    public void testFromByteArraySummerTime() {

        final DateTime dateInSummerTime = this.dlmsHelperService.fromDateTimeValue(this.byteArraySummerTime());

        assertEquals("2015-07-21T14:53:07.230+02:00", ISODateTimeFormat.dateTime().print(dateInSummerTime));
    }

    @Test
    public void testFromByteArrayWinterTime() {

        final DateTime dateInWinterTime = this.dlmsHelperService.fromDateTimeValue(this.byteArrayWinterTime());

        assertEquals("2015-02-21T14:53:07.230+01:00", ISODateTimeFormat.dateTime().print(dateInWinterTime));
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
}
