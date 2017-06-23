/**
 * Copyright 2017 Smart Society Services B.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 */
package com.alliander.osgp.cucumber.platform.smartmetering.support.ws.smartmetering.monitoring;

import static com.alliander.osgp.cucumber.core.Helpers.getShort;

import java.util.Map;

import com.alliander.osgp.adapter.ws.schema.smartmetering.common.ObisCodeValues;
import com.alliander.osgp.cucumber.platform.smartmetering.PlatformSmartmeteringKeys;

public class ObisCodeValuesFactory {

    private ObisCodeValuesFactory() {
        // Private constructor for utility class
    }

    public static ObisCodeValues fromParameterMap(final Map<String, String> parameterMap) {

        final ObisCodeValues result = new ObisCodeValues();
        result.setA(getShort(parameterMap, PlatformSmartmeteringKeys.OBIS_CODE_A, (short) 0));
        result.setB(getShort(parameterMap, PlatformSmartmeteringKeys.OBIS_CODE_B, (short) 0));
        result.setC(getShort(parameterMap, PlatformSmartmeteringKeys.OBIS_CODE_C, (short) 0));
        result.setD(getShort(parameterMap, PlatformSmartmeteringKeys.OBIS_CODE_D, (short) 0));
        result.setE(getShort(parameterMap, PlatformSmartmeteringKeys.OBIS_CODE_E, (short) 0));
        result.setF(getShort(parameterMap, PlatformSmartmeteringKeys.OBIS_CODE_F, (short) 0));
        return result;

    }

}
