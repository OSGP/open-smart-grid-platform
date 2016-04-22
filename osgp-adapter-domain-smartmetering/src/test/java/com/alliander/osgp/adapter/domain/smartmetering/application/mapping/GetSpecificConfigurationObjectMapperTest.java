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

import com.alliander.osgp.domain.core.valueobjects.smartmetering.GetSpecificConfigurationObjectRequestData;
import com.alliander.osgp.domain.core.valueobjects.smartmetering.ObisCodeValues;
import com.alliander.osgp.domain.core.valueobjects.smartmetering.SpecificConfigurationObject;
import com.alliander.osgp.dto.valueobjects.smartmetering.GetSpecificConfigurationObjectRequestDataDto;

public class GetSpecificConfigurationObjectMapperTest {

    private CommonMapper commonMapper = new CommonMapper();

    @Test
    public void test() {
        GetSpecificConfigurationObjectRequestData reqData = makeRequestDatareqData();
        Object obj = commonMapper.map(reqData, GetSpecificConfigurationObjectRequestDataDto.class);
        assertTrue(obj != null && obj instanceof GetSpecificConfigurationObjectRequestDataDto);
    }


    private GetSpecificConfigurationObjectRequestData makeRequestDatareqData() {
        final ObisCodeValues obiscode = new ObisCodeValues((byte)1,(byte)1,(byte)1,(byte)1,(byte)1,(byte)1);
        final SpecificConfigurationObject configObj = new SpecificConfigurationObject(1, 1, obiscode);
        return new GetSpecificConfigurationObjectRequestData(configObj);
    }
    
}
