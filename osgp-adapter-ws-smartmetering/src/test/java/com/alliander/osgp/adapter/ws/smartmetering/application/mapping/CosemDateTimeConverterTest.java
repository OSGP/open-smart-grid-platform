package com.alliander.osgp.adapter.ws.smartmetering.application.mapping;

import static org.junit.Assert.assertEquals;
import ma.glasnost.orika.MapperFactory;
import ma.glasnost.orika.impl.ConfigurableMapper;

import org.junit.Test;

import com.alliander.osgp.domain.core.valueobjects.smartmetering.CosemDate;
import com.alliander.osgp.domain.core.valueobjects.smartmetering.CosemDateTime;

public class CosemDateTimeConverterTest {

    static final int BYTE_YEAR_HI = 0;
    static final int BYTE_YEAR_LO = 1;
    static final int BYTE_MONTH = 2;
    static final int BYTE_DAY_OF_MONTH = 3;
    static final int BYTE_DAY_OF_WEEK = 4;
    static final int BYTE_HOUR = 5;
    static final int BYTE_MINUTE = 6;
    static final int BYTE_SECOND = 7;
    static final int BYTE_HUNDREDTHS = 8;
    static final int BYTE_DEVIATION_HI = 9;
    static final int BYTE_DEVIATION_LO = 10;
    static final int BYTE_CLOCKSTATUS = 11;

    static short[] dateTime;
    static {
        dateTime = new short[12];
        dateTime[BYTE_YEAR_HI] = (short) (2016 >> 8) & 0xFF;
        dateTime[BYTE_YEAR_LO] = (short) 2016 & 0xFF;
        dateTime[BYTE_MONTH] = 2;
        dateTime[BYTE_DAY_OF_MONTH] = 18;
        dateTime[BYTE_DAY_OF_WEEK] = 0xFF;
        dateTime[BYTE_HOUR] = 7;
        dateTime[BYTE_MINUTE] = 13;
        dateTime[BYTE_SECOND] = 32;
        dateTime[BYTE_HUNDREDTHS] = 0;
        dateTime[BYTE_DEVIATION_HI] = (short) (120 >> 8) & 0xFF;
        dateTime[BYTE_DEVIATION_LO] = (short) 120 & 0xFF;
        dateTime[BYTE_CLOCKSTATUS] = 0;
    }

    public static class Mapper extends ConfigurableMapper {
        @Override
        public void configure(final MapperFactory mapperFactory) {
            mapperFactory.getConverterFactory().registerConverter(new CosemDateTimeConverter());
            mapperFactory.getConverterFactory().registerConverter(new CosemTimeConverter());
        }
    }

    public static Mapper mapper = new Mapper();

    private byte[] convertShortArray(final short[] input) {
        final byte[] output = new byte[input.length];
        for (int i = 0; i < input.length; ++i) {
            output[i] = (byte) input[i];
        }
        return output;
    }

    @Test
    public void deviationTest() {
        CosemDateTime cdt;

        // Test negative
        dateTime[BYTE_DEVIATION_HI] = (short) (-720 >> 8) & 0xFF;
        dateTime[BYTE_DEVIATION_LO] = (short) -720 & 0xFF;
        cdt = mapper.map(this.convertShortArray(dateTime), CosemDateTime.class);
        assertEquals(-720, cdt.getDeviation());

        // Test positive
        dateTime[BYTE_DEVIATION_HI] = (short) (120 >> 8) & 0xFF;
        dateTime[BYTE_DEVIATION_LO] = (short) 120 & 0xFF;
        cdt = mapper.map(this.convertShortArray(dateTime), CosemDateTime.class);
        assertEquals(120, cdt.getDeviation());
    }

    @Test
    public void yearTest() {
        CosemDateTime cdt;

        // Test positive
        dateTime[BYTE_YEAR_HI] = (short) (2016 >> 8) & 0xFF;
        dateTime[BYTE_YEAR_LO] = (short) 2016 & 0xFF;
        cdt = mapper.map(this.convertShortArray(dateTime), CosemDateTime.class);
        assertEquals(2016, cdt.getDate().getYear());

        // Test unspecified
        dateTime[BYTE_YEAR_HI] = (short) (CosemDate.YEAR_NOT_SPECIFIED >> 8) & 0xFF;
        dateTime[BYTE_YEAR_LO] = (short) CosemDate.YEAR_NOT_SPECIFIED & 0xFF;
        cdt = mapper.map(this.convertShortArray(dateTime), CosemDateTime.class);
        assertEquals(CosemDate.YEAR_NOT_SPECIFIED, cdt.getDate().getYear());
    }
}
