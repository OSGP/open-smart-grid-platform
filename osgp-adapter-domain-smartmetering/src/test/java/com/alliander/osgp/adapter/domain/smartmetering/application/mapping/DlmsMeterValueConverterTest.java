package com.alliander.osgp.adapter.domain.smartmetering.application.mapping;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

import java.math.BigDecimal;

import org.junit.Test;

import com.alliander.osgp.domain.core.valueobjects.smartmetering.OsgpMeterValue;
import com.alliander.osgp.domain.core.valueobjects.smartmetering.OsgpUnit;
import com.alliander.osgp.dto.valueobjects.smartmetering.DlmsMeterValue;
import com.alliander.osgp.dto.valueobjects.smartmetering.DlmsUnit;

public class DlmsMeterValueConverterTest {

    @Test
    public void testCalculate() {
        final MonitoringMapper calculator = new MonitoringMapper();
        DlmsMeterValue response = new DlmsMeterValue(BigDecimal.valueOf(123456), DlmsUnit.WH);
        assertEquals(BigDecimal.valueOf(123.456d), calculator.map(response, OsgpMeterValue.class).getValue());
        assertEquals(OsgpUnit.KWH, calculator.map(response, OsgpMeterValue.class).getOsgpUnit());

        response = new DlmsMeterValue(BigDecimal.valueOf(123456), DlmsUnit.M3);
        assertEquals(BigDecimal.valueOf(123456d), calculator.map(response, OsgpMeterValue.class).getValue());
        assertEquals(OsgpUnit.M3, calculator.map(response, OsgpMeterValue.class).getOsgpUnit());

        response = new DlmsMeterValue(BigDecimal.valueOf(123456), DlmsUnit.M3COR);
        assertEquals(BigDecimal.valueOf(123456d), calculator.map(response, OsgpMeterValue.class).getValue());
        assertEquals(OsgpUnit.M3, calculator.map(response, OsgpMeterValue.class).getOsgpUnit());

        response = new DlmsMeterValue(BigDecimal.valueOf(123456), DlmsUnit.A);
        try {
            calculator.map(response, OsgpMeterValue.class);
            fail("dlms unit A not supported, expected IllegalArgumentException");
        } catch (final IllegalArgumentException ex) {

        }
    }

}
