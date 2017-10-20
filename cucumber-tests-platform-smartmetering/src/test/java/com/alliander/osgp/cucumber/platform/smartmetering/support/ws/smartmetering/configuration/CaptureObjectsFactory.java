/**
 * Copyright 2017 Smart Society Services B.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 */
package com.alliander.osgp.cucumber.platform.smartmetering.support.ws.smartmetering.configuration;

import java.util.List;
import java.util.Map;

import com.alliander.osgp.adapter.ws.schema.smartmetering.common.CaptureObjectDefinition;
import com.alliander.osgp.adapter.ws.schema.smartmetering.common.CaptureObjectDefinitions;
import com.alliander.osgp.adapter.ws.schema.smartmetering.common.ObisCodeValues;
import com.alliander.osgp.cucumber.core.Helpers;
import com.alliander.osgp.cucumber.platform.helpers.SettingsHelper;

public class CaptureObjectsFactory {

    private final static String NUMBER_OF_CAPTURE_OBJECTS = "NumberOfCaptureObjects";
    private final static String CLASS_ID = "CaptureObject_ClassId";
    private final static String LOGICAL_NAME = "CaptureObject_LogicalName";
    private final static String ATTRIBUTE_INDEX = "CaptureObject_AttributeIndex";
    private final static String DATA_INDEX = "CaptureObject_DataIndex";

    public static CaptureObjectDefinitions fromParameterMap(final Map<String, String> requestParameters) {

        if (!requestParameters.containsKey(NUMBER_OF_CAPTURE_OBJECTS)) {
            return null;
        }

        final CaptureObjectDefinitions captureObjectDefinitions = new CaptureObjectDefinitions();
        final List<CaptureObjectDefinition> captureObjects = captureObjectDefinitions.getCaptureObject();

        final int numberOfCaptureObjects = Helpers.getInteger(requestParameters, NUMBER_OF_CAPTURE_OBJECTS, 0);
        for (int i = 1; i <= numberOfCaptureObjects; i++) {
            final CaptureObjectDefinition captureObjectDefinition = new CaptureObjectDefinition();
            captureObjectDefinition.setClassId(SettingsHelper.getIntegerValue(requestParameters, CLASS_ID, i));
            captureObjectDefinition.setLogicalName(logicalNameFromParemeterMap(requestParameters, i));
            captureObjectDefinition
                    .setAttributeIndex(SettingsHelper.getByteValue(requestParameters, ATTRIBUTE_INDEX, i));
            captureObjectDefinition.setDataIndex(SettingsHelper.getIntegerValue(requestParameters, DATA_INDEX, i));
            captureObjects.add(captureObjectDefinition);
        }
        return captureObjectDefinitions;
    }

    private static ObisCodeValues logicalNameFromParemeterMap(final Map<String, String> requestParameters,
            final int i) {
        final String logicalName = SettingsHelper.getStringValue(requestParameters, LOGICAL_NAME, i);
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
