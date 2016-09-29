/**
 * Copyright 2016 Smart Society Services B.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 */
package com.alliander.osgp.adapter.domain.smartmetering.application.mapping;

import static org.junit.Assert.assertTrue;

import org.junit.Test;

import com.alliander.osgp.domain.core.valueobjects.smartmetering.ObisCodeValues;
import com.alliander.osgp.domain.core.valueobjects.smartmetering.SpecificAttributeValueRequestData;
import com.alliander.osgp.dto.valueobjects.smartmetering.SpecificAttributeValueRequestDto;

public class GetSpecificAttributeValueMapperTest {

    private CommonMapper commonMapper = new CommonMapper();

    @Test
    public void test() {
        SpecificAttributeValueRequestData reqData1 = makeRequestData();
        Object obj = commonMapper.map(reqData1, SpecificAttributeValueRequestDto.class);
        assertTrue(obj != null && obj instanceof SpecificAttributeValueRequestDto);
        Object reqData2 = commonMapper.map(obj, SpecificAttributeValueRequestData.class);
        assertTrue(reqData2 != null && reqData2 instanceof SpecificAttributeValueRequestData);
        assertTrue(reqData1.equals(reqData2));
    }


    private SpecificAttributeValueRequestData makeRequestData() {
        final ObisCodeValues obiscode = new ObisCodeValues((byte)2,(byte)3,(byte)4,(byte)5,(byte)6,(byte)7);
        return new SpecificAttributeValueRequestData(21, 1, obiscode);
    }
    
}
