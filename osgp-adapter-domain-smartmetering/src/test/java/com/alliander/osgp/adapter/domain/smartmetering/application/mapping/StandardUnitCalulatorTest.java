package com.alliander.osgp.adapter.domain.smartmetering.application.mapping;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

import org.junit.Test;

import com.alliander.osgp.dto.valueobjects.smartmetering.DlmsUnit;
import com.alliander.osgp.dto.valueobjects.smartmetering.ScalerUnit;

public class StandardUnitCalulatorTest {

    @Test
    public void test() {
        final StandardUnitCalculator calculator = new StandardUnitCalculator();
        ScalerUnit scalerUnit = new ScalerUnit(DlmsUnit.WH, 0);
        assertEquals(Double.valueOf(123.456d),
                Double.valueOf(calculator.calculateStandardizedValue(123456, scalerUnit)));
        scalerUnit = new ScalerUnit(DlmsUnit.WH, 2);
        assertEquals(Double.valueOf(1.235d), Double.valueOf(calculator.calculateStandardizedValue(123456, scalerUnit)));
        scalerUnit = new ScalerUnit(DlmsUnit.M3, 2);
        assertEquals(Double.valueOf(1234.56d),
                Double.valueOf(calculator.calculateStandardizedValue(123456, scalerUnit)));
        scalerUnit = new ScalerUnit(DlmsUnit.M3COR, 2);
        assertEquals(Double.valueOf(1234.56d),
                Double.valueOf(calculator.calculateStandardizedValue(123456, scalerUnit)));
        scalerUnit = new ScalerUnit(DlmsUnit.A, 2);
        try {
            calculator.calculateStandardizedValue(123456, scalerUnit);
            fail("dlms unit A not supported, expected exception");
        } catch (final IllegalArgumentException ex) {

        }
    }

}
