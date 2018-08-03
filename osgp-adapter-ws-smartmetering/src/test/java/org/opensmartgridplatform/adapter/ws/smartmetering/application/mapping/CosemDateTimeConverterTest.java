package org.opensmartgridplatform.adapter.ws.smartmetering.application.mapping;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import ma.glasnost.orika.MapperFactory;
import ma.glasnost.orika.impl.DefaultMapperFactory;

import org.junit.Before;
import org.junit.Test;

import org.opensmartgridplatform.domain.core.valueobjects.smartmetering.ClockStatus;
import org.opensmartgridplatform.domain.core.valueobjects.smartmetering.CosemDate;
import org.opensmartgridplatform.domain.core.valueobjects.smartmetering.CosemDateTime;
import org.opensmartgridplatform.domain.core.valueobjects.smartmetering.CosemTime;

public class CosemDateTimeConverterTest {

    private MapperFactory mapperFactory = new DefaultMapperFactory.Builder().build();

    private static final byte FIRST_BYTE_FOR_YEAR = (byte) 0x07;
    private static final byte SECOND_BYTE_FOR_YEAR = (byte) 0xE0;
    private static final byte BYTE_FOR_MONTH = 4;
    private static final byte BYTE_FOR_DAY_OF_MONTH = 7;
    private static final byte BYTE_FOR_DAY_OF_WEEK = (byte) 0xFF;
    private static final byte BYTE_FOR_HOUR_OF_DAY = 10;
    private static final byte BYTE_FOR_MINUTE_OF_HOUR = 34;
    private static final byte BYTE_FOR_SECOND_OF_MINUTE = 35;
    private static final byte BYTE_FOR_HUNDREDS_0F_SECONDS = 10;
    private static final byte FIRST_BYTE_FOR_DEVIATION = -1;
    private static final byte SECOND_BYTE_FOR_DEVIATION = -120;
    private static final byte BYTE_FOR_CLOCKSTATUS = (byte) 0xFF;

    private static final byte[] COSEMDATETIME_BYTE_ARRAY_NORMAL = { FIRST_BYTE_FOR_YEAR, SECOND_BYTE_FOR_YEAR,
        BYTE_FOR_MONTH, BYTE_FOR_DAY_OF_MONTH, BYTE_FOR_DAY_OF_WEEK, BYTE_FOR_HOUR_OF_DAY, BYTE_FOR_MINUTE_OF_HOUR,
            BYTE_FOR_SECOND_OF_MINUTE, BYTE_FOR_HUNDREDS_0F_SECONDS, FIRST_BYTE_FOR_DEVIATION,
            SECOND_BYTE_FOR_DEVIATION, BYTE_FOR_CLOCKSTATUS };

    private static final byte FIRST_BYTE_FOR_POSITIVE_DEVIATION = 1;
    private static final byte SECOND_BYTE_FOR_POSITIVE_DEVIATION = 120;

    private static final byte[] COSEMDATETIME_BYTE_ARRAY_POSITIVE_DEVIATION = { FIRST_BYTE_FOR_YEAR,
        SECOND_BYTE_FOR_YEAR, BYTE_FOR_MONTH, BYTE_FOR_DAY_OF_MONTH, BYTE_FOR_DAY_OF_WEEK, BYTE_FOR_HOUR_OF_DAY,
        BYTE_FOR_MINUTE_OF_HOUR, BYTE_FOR_SECOND_OF_MINUTE, BYTE_FOR_HUNDREDS_0F_SECONDS,
            FIRST_BYTE_FOR_POSITIVE_DEVIATION, SECOND_BYTE_FOR_POSITIVE_DEVIATION, BYTE_FOR_CLOCKSTATUS };

    private static final byte FIRST_BYTE_FOR_UNSPECIFIED_YEAR = (byte) 0xFF;
    private static final byte SECOND_BYTE_FOR_UNSPECIFIED_YEAR = (byte) 0xFF;

    private static final byte[] COSEMDATETIME_BYTE_ARRAY_UNSPECIFIED_YEAR = { FIRST_BYTE_FOR_UNSPECIFIED_YEAR,
        SECOND_BYTE_FOR_UNSPECIFIED_YEAR, BYTE_FOR_MONTH, BYTE_FOR_DAY_OF_MONTH, BYTE_FOR_DAY_OF_WEEK,
        BYTE_FOR_HOUR_OF_DAY, BYTE_FOR_MINUTE_OF_HOUR, BYTE_FOR_SECOND_OF_MINUTE, BYTE_FOR_HUNDREDS_0F_SECONDS,
        FIRST_BYTE_FOR_DEVIATION, SECOND_BYTE_FOR_DEVIATION, BYTE_FOR_CLOCKSTATUS };

    /**
     * Registers the CosemDateTimeConverter to be tested.
     */
    @Before
    public void init() {
        this.mapperFactory.getConverterFactory().registerConverter(new CosemDateTimeConverter());
    }

    /**
     * Tests if mapping a byte[] to a CosemDateTime object succeeds.
     */
    @Test
    public void testToCosemDateTimeMapping() {

        // actual mapping
        final CosemDateTime cosemDateTime = this.mapperFactory.getMapperFacade().map(COSEMDATETIME_BYTE_ARRAY_NORMAL,
                CosemDateTime.class);

        // check mapping
        assertNotNull(cosemDateTime);

        final CosemDate cosemDate = cosemDateTime.getDate();
        assertEquals(FIRST_BYTE_FOR_YEAR, ((byte) (cosemDate.getYear() >> 8)));
        assertEquals(SECOND_BYTE_FOR_YEAR, ((byte) (cosemDate.getYear() & 0xFF)));
        assertEquals(BYTE_FOR_MONTH, cosemDate.getMonth());
        assertEquals(BYTE_FOR_DAY_OF_MONTH, cosemDate.getDayOfMonth());
        assertEquals(BYTE_FOR_DAY_OF_WEEK, ((byte) cosemDate.getDayOfWeek()));

        final CosemTime cosemTime = cosemDateTime.getTime();
        assertEquals(BYTE_FOR_HOUR_OF_DAY, cosemTime.getHour());
        assertEquals(BYTE_FOR_MINUTE_OF_HOUR, cosemTime.getMinute());
        assertEquals(BYTE_FOR_SECOND_OF_MINUTE, cosemTime.getSecond());
        assertEquals(BYTE_FOR_HUNDREDS_0F_SECONDS, cosemTime.getHundredths());

        final int deviation = cosemDateTime.getDeviation();
        assertEquals(FIRST_BYTE_FOR_DEVIATION, ((byte) (deviation >> 8)));
        assertEquals(SECOND_BYTE_FOR_DEVIATION, ((byte) (deviation & 0xFF)));

        assertEquals(BYTE_FOR_CLOCKSTATUS, ((byte) ClockStatus.STATUS_NOT_SPECIFIED));
    }

    /**
     * Tests the mapping of the deviation property of a CosemDateTime object for
     * positive and negative values.
     */
    @Test
    public void deviationTest() {

        CosemDateTime cosemDateTime;

        // Test negative
        cosemDateTime = this.mapperFactory.getMapperFacade().map(COSEMDATETIME_BYTE_ARRAY_NORMAL, CosemDateTime.class);

        assertNotNull(cosemDateTime);
        assertNotNull(cosemDateTime.getDeviation());
        assertEquals(FIRST_BYTE_FOR_DEVIATION, (byte) (cosemDateTime.getDeviation() >> 8));
        assertEquals(SECOND_BYTE_FOR_DEVIATION, (byte) (cosemDateTime.getDeviation() & 0xFF));

        // Test positive
        cosemDateTime = this.mapperFactory.getMapperFacade().map(COSEMDATETIME_BYTE_ARRAY_POSITIVE_DEVIATION,
                CosemDateTime.class);

        assertNotNull(cosemDateTime);
        assertNotNull(cosemDateTime.getDeviation());
        assertEquals(FIRST_BYTE_FOR_POSITIVE_DEVIATION, (byte) (cosemDateTime.getDeviation() >> 8));
        assertEquals(SECOND_BYTE_FOR_POSITIVE_DEVIATION, (byte) (cosemDateTime.getDeviation() & 0xFF));
    }

    /**
     * Tests the mapping of the year property of a CosemDateTime object when it
     * is unspecified.
     */
    @Test
    public void yearTest() {

        final CosemDateTime cosemDateTime = this.mapperFactory.getMapperFacade().map(
                COSEMDATETIME_BYTE_ARRAY_UNSPECIFIED_YEAR, CosemDateTime.class);

        assertNotNull(cosemDateTime);
        assertNotNull(cosemDateTime.getDate());
        assertNotNull(cosemDateTime.getDate().getYear());
        assertEquals(CosemDate.YEAR_NOT_SPECIFIED, cosemDateTime.getDate().getYear());
    }
}
