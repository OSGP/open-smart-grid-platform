package com.alliander.osgp.adapter.ws.smartmetering.application.mapping;

import org.junit.Assert;
import org.junit.Test;

import com.alliander.osgp.adapter.ws.schema.smartmetering.configuration.SpecialDay;
import com.alliander.osgp.domain.core.valueobjects.smartmetering.CosemDate;

public class SpecialDayConversionTest {

    @Test
    public void testWildCards() {
        final SpecialDay source = new SpecialDay();
        source.setSpecialDayDate(new byte[] { 20, 15, 6, (byte) 0xFF, (byte) 0xFF });
        final SpecialDayConverter converter = new SpecialDayConverter();
        final com.alliander.osgp.domain.core.valueobjects.smartmetering.SpecialDay destination = converter.convert(
                source, converter.getBType());
        final CosemDate date = destination.getSpecialDayDate();
        Assert.assertTrue(date.getYear() == 2015);
        Assert.assertTrue(date.getMonth() == 6);
        Assert.assertTrue(date.getDayOfMonth() == 0xFF);
        Assert.assertTrue(date.getDayOfWeek() == 0xFF);
    }

}
