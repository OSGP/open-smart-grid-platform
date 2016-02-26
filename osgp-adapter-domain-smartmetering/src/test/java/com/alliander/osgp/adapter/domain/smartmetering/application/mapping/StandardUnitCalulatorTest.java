package com.alliander.osgp.adapter.domain.smartmetering.application.mapping;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

import org.junit.Test;

import com.alliander.osgp.domain.core.valueobjects.smartmetering.OsgpUnit;
import com.alliander.osgp.dto.valueobjects.smartmetering.DlmsUnit;
import com.alliander.osgp.dto.valueobjects.smartmetering.ScalerUnit;
import com.alliander.osgp.dto.valueobjects.smartmetering.ScalerUnitResponse;

public class StandardUnitCalulatorTest {

    @Test
    public void testCalculate() {
        final StandardUnitConverter calculator = new StandardUnitConverter();
        ScalerUnitResponse response = new ScalerUnitResponse() {

            @Override
            public ScalerUnit getScalerUnit() {
                return new ScalerUnit(DlmsUnit.WH, 0);
            }
        };
        assertEquals(Double.valueOf(123.456d), Double.valueOf(calculator.calculateStandardizedValue(123456l, response)));
        response = new ScalerUnitResponse() {

            @Override
            public ScalerUnit getScalerUnit() {
                return new ScalerUnit(DlmsUnit.WH, 2);
            }
        };
        assertEquals(Double.valueOf(1.235d), Double.valueOf(calculator.calculateStandardizedValue(123456l, response)));
        response = new ScalerUnitResponse() {

            @Override
            public ScalerUnit getScalerUnit() {
                return new ScalerUnit(DlmsUnit.M3, 2);
            }
        };
        assertEquals(Double.valueOf(1234.56d), Double.valueOf(calculator.calculateStandardizedValue(123456l, response)));
        response = new ScalerUnitResponse() {

            @Override
            public ScalerUnit getScalerUnit() {
                return new ScalerUnit(DlmsUnit.M3COR, 2);
            }
        };
        assertEquals(Double.valueOf(1234.56d), Double.valueOf(calculator.calculateStandardizedValue(123456l, response)));
        response = new ScalerUnitResponse() {

            @Override
            public ScalerUnit getScalerUnit() {
                return new ScalerUnit(DlmsUnit.A, 2);
            }
        };
        try {
            calculator.calculateStandardizedValue(123456l, response);
            fail("dlms unit A not supported, expected exception");
        } catch (final IllegalArgumentException ex) {

        }
    }

    @Test
    public void testUnit() {
        final StandardUnitConverter calculator = new StandardUnitConverter();
        ScalerUnitResponse response = new ScalerUnitResponse() {

            @Override
            public ScalerUnit getScalerUnit() {
                return new ScalerUnit(DlmsUnit.WH, 0);
            }
        };
        assertEquals(OsgpUnit.KWH, calculator.toStandardUnit(response));
        response = new ScalerUnitResponse() {

            @Override
            public ScalerUnit getScalerUnit() {
                return new ScalerUnit(DlmsUnit.M3, 2);
            }
        };
        assertEquals(OsgpUnit.M3, calculator.toStandardUnit(response));
        response = new ScalerUnitResponse() {

            @Override
            public ScalerUnit getScalerUnit() {
                return new ScalerUnit(DlmsUnit.M3COR, 2);
            }
        };
        assertEquals(OsgpUnit.M3, calculator.toStandardUnit(response));
        response = new ScalerUnitResponse() {

            @Override
            public ScalerUnit getScalerUnit() {
                return new ScalerUnit(DlmsUnit.A, 2);
            }
        };
        try {
            calculator.toStandardUnit(response);
            fail("dlms unit A not supported, expected exception");
        } catch (final IllegalArgumentException ex) {

        }
    }
}
