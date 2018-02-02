/**
 * Copyright 2017 Smart Society Services B.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 */
package com.alliander.osgp.cucumber.platform.smartmetering.support.ws.smartmetering.configuration;

import static com.alliander.osgp.cucumber.core.ReadSettingsHelper.getInteger;

import java.util.List;
import java.util.Map;

import com.alliander.osgp.adapter.ws.schema.smartmetering.common.CaptureObjectDefinition;
import com.alliander.osgp.adapter.ws.schema.smartmetering.common.CaptureObjectDefinitions;
import com.alliander.osgp.adapter.ws.schema.smartmetering.common.ObisCodeValues;
import com.alliander.osgp.cucumber.platform.helpers.SettingsHelper;
import com.alliander.osgp.cucumber.platform.smartmetering.PlatformSmartmeteringKeys;

public class CaptureObjectsFactory {

    public static CaptureObjectDefinitions fromParameterMap(final Map<String, String> requestParameters) {

        if (!requestParameters.containsKey(PlatformSmartmeteringKeys.NUMBER_OF_CAPTURE_OBJECTS)) {
            return null;
        }

        final CaptureObjectDefinitions captureObjectDefinitions = new CaptureObjectDefinitions();
        final List<CaptureObjectDefinition> captureObjects = captureObjectDefinitions.getCaptureObject();

        final int numberOfCaptureObjects = getInteger(requestParameters,
                PlatformSmartmeteringKeys.NUMBER_OF_CAPTURE_OBJECTS, 0);
        for (int i = 1; i <= numberOfCaptureObjects; i++) {
            final CaptureObjectDefinition captureObjectDefinition = new CaptureObjectDefinition();
            captureObjectDefinition.setClassId(SettingsHelper.getIntegerValue(requestParameters,
                    PlatformSmartmeteringKeys.CAPTURE_OBJECT_CLASS_ID, i));
            captureObjectDefinition.setLogicalName(logicalNameFromParemeterMap(requestParameters, i));
            captureObjectDefinition.setAttributeIndex(SettingsHelper.getByteValue(requestParameters,
                    PlatformSmartmeteringKeys.CAPTURE_OBJECT_ATTRIBUTE_INDEX, i));
            captureObjectDefinition.setDataIndex(SettingsHelper.getIntegerValue(requestParameters,
                    PlatformSmartmeteringKeys.CAPTURE_OBJECT_DATA_INDEX, i));
            captureObjects.add(captureObjectDefinition);
        }
        return captureObjectDefinitions;
    }

    private static ObisCodeValues logicalNameFromParemeterMap(final Map<String, String> requestParameters,
            final int i) {
        final String logicalName = SettingsHelper.getStringValue(requestParameters,
                PlatformSmartmeteringKeys.CAPTURE_OBJECT_LOGICAL_NAME, i);
        final String[] obisBytes = logicalName.split("\\.");
        final ObisCodeValues obisCodeValues = new ObisCodeValues();
        obisCodeValues.setA(Short.parseShort(obisBytes[0]));
        obisCodeValues.setB(Short.parseShort(obisBytes[1]));
        obisCodeValues.setC(Short.parseShort(obisBytes[2]));
        obisCodeValues.setD(Short.parseShort(obisBytes[3]));
        obisCodeValues.setE(Short.parseShort(obisBytes[4]));
        obisCodeValues.setF(Short.parseShort(obisBytes[5]));
        return obisCodeValues;
    }

}
