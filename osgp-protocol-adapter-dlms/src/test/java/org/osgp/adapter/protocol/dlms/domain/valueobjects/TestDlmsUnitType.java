/**
 * Copyright 2017 Smart Society Services B.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 */
package org.osgp.adapter.protocol.dlms.domain.valueobjects;

import org.junit.Assert;
import org.junit.Test;

import com.alliander.osgp.dto.valueobjects.smartmetering.DlmsUnitTypeDto;

public class TestDlmsUnitType {

    @Test
    public void testGetUnit() {
        final String result = DlmsUnitTypeDto.getUnit(1);
        Assert.assertTrue("Y".equals(result));
    }

    @Test
    public void testGetKwh() {
        final String result = DlmsUnitTypeDto.getUnit(30);
        Assert.assertTrue("KWH".equals(result));
    }

    @Test
    public void testGetUndefined() {
        String result = DlmsUnitTypeDto.getUnit(0);
        Assert.assertTrue("UNDEFINED".equals(result));

        result = DlmsUnitTypeDto.getUnit(100);
        Assert.assertTrue("UNDEFINED".equals(result));
    }
}
