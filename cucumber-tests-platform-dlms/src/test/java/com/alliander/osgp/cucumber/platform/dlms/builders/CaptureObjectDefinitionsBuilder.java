/**
 * Copyright 2017 Smart Society Services B.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 */
package com.alliander.osgp.cucumber.platform.dlms.builders;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import com.alliander.osgp.adapter.ws.schema.smartmetering.common.CaptureObjectDefinition;
import com.alliander.osgp.adapter.ws.schema.smartmetering.common.CaptureObjectDefinitions;
import com.alliander.osgp.adapter.ws.schema.smartmetering.common.ObisCodeValues;
import com.alliander.osgp.cucumber.platform.core.Helpers;
import com.alliander.osgp.cucumber.platform.helpers.SettingsHelper;

public class CaptureObjectDefinitionsBuilder {

    private final static String NUMBER_OF_SELECTED_VALUES = "NumberOfSelectedValues";
    private final static String CLASS_ID = "SelectedValue_ClassId";
    private final static String LOGICAL_NAME = "SelectedValue_LogicalName";
    private final static String ATTRIBUTE_INDEX = "SelectedValue_AttributeIndex";
    private final static String DATA_INDEX = "SelectedValue_DataIndex";

    final List<CaptureObjectDefinition> selectedValues = new ArrayList<>();

    public CaptureObjectDefinitionsBuilder withNextSelectedValue(
            final CaptureObjectDefinition captureObjectDefinition) {
        this.selectedValues.add(captureObjectDefinition);
        return this;
    }

    public CaptureObjectDefinitionsBuilder fromSettings(final Map<String, String> settings) {
        final int numberOfSelectedValues = Helpers.getInteger(settings, NUMBER_OF_SELECTED_VALUES, 0);
        for (int i = 1; i <= numberOfSelectedValues; i++) {
            final CaptureObjectDefinition captureObjectDefinition = new CaptureObjectDefinition();
            captureObjectDefinition.setClassId(SettingsHelper.getIntegerValue(settings, CLASS_ID, i));
            captureObjectDefinition.setLogicalName(this.logicalNameFromSettings(settings, i));
            captureObjectDefinition.setAttributeIndex(SettingsHelper.getByteValue(settings, ATTRIBUTE_INDEX, i));
            captureObjectDefinition.setDataIndex(SettingsHelper.getIntegerValue(settings, DATA_INDEX, i));
            this.withNextSelectedValue(captureObjectDefinition);
        }
        return this;
    }

    private ObisCodeValues logicalNameFromSettings(final Map<String, String> settings, final int i) {
        final String logicalName = SettingsHelper.getStringValue(settings, LOGICAL_NAME, i);
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

    public CaptureObjectDefinitions build() {

        if (this.selectedValues.isEmpty()) {
            return null;
        }

        final CaptureObjectDefinitions result = new CaptureObjectDefinitions();
        result.getCaptureObject().addAll(this.selectedValues);
        return result;
    }
}
