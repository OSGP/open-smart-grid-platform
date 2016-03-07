package com.alliander.osgp.adapter.ws.smartmetering.application.mapping;

import org.junit.Assert;
import org.junit.Test;

import com.alliander.osgp.adapter.ws.schema.smartmetering.configuration.SpecialDay;
import com.alliander.osgp.domain.core.valueobjects.smartmetering.CosemDate;

public class SpecialDayConversionTest {

    @Test
    public void testWildCards() {
        final SpecialDay source = new SpecialDay();
        source.setSpecialDayDate(new byte[] { (byte) 0x07, (byte) 0xDF, 6, (byte) 0xFF, (byte) 0xFF });
        final SpecialDayConverter converter = new SpecialDayConverter();
        com.alliander.osgp.domain.core.valueobjects.smartmetering.SpecialDay destination = converter.convert(source,
                converter.getBType());
        CosemDate date = destination.getSpecialDayDate();
        Assert.assertEquals(2015, date.getYear());
        Assert.assertEquals(6, date.getMonth());
        Assert.assertEquals(0xFF, date.getDayOfMonth());
        Assert.assertEquals(0xFF, date.getDayOfWeek());

        source.setSpecialDayDate(new byte[] { (byte) 0xFF, (byte) 0xFF, (byte) 0xFE, (byte) 0xFD, (byte) 0xFF });
        destination = converter.convert(source, converter.getBType());
        date = destination.getSpecialDayDate();
        Assert.assertEquals(0xFFFF, date.getYear());
        Assert.assertEquals(0xFE, date.getMonth());
        Assert.assertEquals(0xFD, date.getDayOfMonth());
        Assert.assertEquals(0xFF, date.getDayOfWeek());

        source.setSpecialDayDate(new byte[] { (byte) 0xFF, (byte) 0xFF, (byte) 0xFE, 21, 3 });
        destination = converter.convert(source, converter.getBType());
        date = destination.getSpecialDayDate();
        Assert.assertEquals(0xFFFF, date.getYear());
        Assert.assertEquals(0xFE, date.getMonth());
        Assert.assertEquals(21, date.getDayOfMonth());
        Assert.assertEquals(3, date.getDayOfWeek());
    }

}
