package com.alliander.osgp.adapter.domain.smartmetering.application.mapping;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

import java.math.BigDecimal;

import org.junit.Test;

import com.alliander.osgp.domain.core.valueobjects.smartmetering.OsgpUnit;
import com.alliander.osgp.dto.valueobjects.smartmetering.DlmsMeterValue;
import com.alliander.osgp.dto.valueobjects.smartmetering.DlmsUnit;

public class StandardUnitConverterTest {

    @Test
    public void testCalculate() {
        final StandardUnitConverter calculator = new StandardUnitConverter();
        DlmsMeterValue response = new DlmsMeterValue(BigDecimal.valueOf(123456), DlmsUnit.WH);
        assertEquals(BigDecimal.valueOf(123.456d), calculator.calculateStandardizedValue(response).getValue());
        response = new DlmsMeterValue(BigDecimal.valueOf(123456), DlmsUnit.M3);
        assertEquals(BigDecimal.valueOf(123456d), calculator.calculateStandardizedValue(response).getValue());
        response = new DlmsMeterValue(BigDecimal.valueOf(123456), DlmsUnit.M3);
        assertEquals(BigDecimal.valueOf(123456d), calculator.calculateStandardizedValue(response).getValue());
        response = new DlmsMeterValue(BigDecimal.valueOf(123456), DlmsUnit.A);
        try {
            calculator.calculateStandardizedValue(response);
            fail("dlms unit A not supported, expected exception");
        } catch (final IllegalArgumentException ex) {

        }
    }

    @Test
    public void testUnit() {
        final StandardUnitConverter calculator = new StandardUnitConverter();
        assertEquals(OsgpUnit.KWH, calculator.toStandardUnit(DlmsUnit.WH));
        assertEquals(OsgpUnit.M3, calculator.toStandardUnit(DlmsUnit.M3));
        assertEquals(OsgpUnit.M3, calculator.toStandardUnit(DlmsUnit.M3COR));
        try {
            calculator.toStandardUnit(DlmsUnit.A);
            fail("dlms unit A not supported, expected exception");
        } catch (final IllegalArgumentException ex) {

        }
    }
}
