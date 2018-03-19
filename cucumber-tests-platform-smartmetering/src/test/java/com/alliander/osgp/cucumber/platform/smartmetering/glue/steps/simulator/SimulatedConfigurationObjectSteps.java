/**
 * Copyright 2018 Smart Society Services B.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 */
package com.alliander.osgp.cucumber.platform.smartmetering.glue.steps.simulator;

import static org.junit.Assert.assertEquals;

import java.util.List;
import java.util.Map;

import org.openmuc.jdlms.ObisCode;
import org.openmuc.jdlms.interfaceclass.InterfaceClass;
import org.openmuc.jdlms.interfaceclass.attribute.DataAttribute;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import com.alliander.osgp.adapter.ws.schema.smartmetering.configuration.ConfigurationFlag;
import com.alliander.osgp.adapter.ws.schema.smartmetering.configuration.ConfigurationFlags;
import com.alliander.osgp.adapter.ws.schema.smartmetering.configuration.ConfigurationObject;
import com.alliander.osgp.adapter.ws.schema.smartmetering.configuration.GprsOperationModeType;
import com.alliander.osgp.cucumber.platform.smartmetering.support.ws.smartmetering.configuration.ConfigurationObjectFactory;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import com.fasterxml.jackson.databind.node.ObjectNode;

import cucumber.api.java.en.Given;
import cucumber.api.java.en.Then;

public class SimulatedConfigurationObjectSteps {

    private static final Logger LOGGER = LoggerFactory.getLogger(SimulatedConfigurationObjectSteps.class);

    private static final int CLASS_ID = InterfaceClass.DATA.id();
    private static final ObisCode OBIS_CODE = new ObisCode(0, 1, 94, 31, 3, 255);
    private static final int ATTRIBUTE_ID_VALUE = DataAttribute.VALUE.attributeId();
    private static final String OBJECT_DESCRIPTION = "the configuration object";

    @Autowired
    private DeviceSimulatorSteps deviceSimulatorSteps;

    @Autowired
    private JsonObjectCreator jsonObjectCreator;

    @Given("device simulation of \"([^\"]*)\" with configuration object")
    public void deviceSimulationOfConfigurationObject(final String deviceIdentification,
            final Map<String, String> settings) {

        this.deviceSimulatorSteps.deviceSimulationOfEquipmentIdentifier(deviceIdentification);

        final ConfigurationObject configurationObject = ConfigurationObjectFactory.fromParameterMap(settings);
        final ObjectNode attributeValue = this.createStructureForConfigurationObject(configurationObject,
                new JsonNodeFactory(false));
        this.deviceSimulatorSteps.setDlmsAttributeValue(CLASS_ID, OBIS_CODE, ATTRIBUTE_ID_VALUE, attributeValue,
                OBJECT_DESCRIPTION);
    }

    @Then("device simulation of \"([^\"]*)\" should be with configuration object")
    public void deviceSimulationOfShouldBeWithConfigurationObject(final String deviceIdentification,
            final Map<String, String> settings) {

        final ConfigurationObject expectedConfigurationObject = ConfigurationObjectFactory.fromParameterMap(settings);
        final ObjectNode expectedValue = this.createStructureForConfigurationObject(expectedConfigurationObject,
                new JsonNodeFactory(false));

        final ObjectNode actualValue = this.deviceSimulatorSteps.getDlmsAttributeValue(CLASS_ID, OBIS_CODE,
                ATTRIBUTE_ID_VALUE, OBJECT_DESCRIPTION);

        assertEquals("Simulated ConfigurationObject value", expectedValue, actualValue);
    }

    private ObjectNode createStructureForConfigurationObject(final ConfigurationObject configurationObject,
            final JsonNodeFactory jsonNodeFactory) {

        final ObjectNode structureForConfigurationObject = jsonNodeFactory.objectNode();
        this.jsonObjectCreator.setTypeNode(structureForConfigurationObject, "structure");
        final ArrayNode configurationObjectElements = jsonNodeFactory.arrayNode();
        configurationObjectElements.add(this.createGprsOperationModeForConfigurationObject(
                configurationObject.getGprsOperationMode(), jsonNodeFactory));
        configurationObjectElements.add(
                this.createFlagsForConfigurationObject(configurationObject.getConfigurationFlags(), jsonNodeFactory));
        structureForConfigurationObject.set("value", configurationObjectElements);

        return structureForConfigurationObject;
    }

    private ObjectNode createGprsOperationModeForConfigurationObject(final GprsOperationModeType gprsOperationMode,
            final JsonNodeFactory jsonNodeFactory) {

        String textValue;
        if (gprsOperationMode == GprsOperationModeType.ALWAYS_ON) {
            textValue = "1";
        } else if (gprsOperationMode == GprsOperationModeType.TRIGGERED) {
            textValue = "2";
        } else {
            textValue = "0";
        }
        return this.jsonObjectCreator.createAttributeValue("enumerate", textValue, jsonNodeFactory);
    }

    private JsonNode createFlagsForConfigurationObject(final ConfigurationFlags configurationFlags,
            final JsonNodeFactory jsonNodeFactory) {

        final int bitStringLength = 16;
        final char[] flags = new char[bitStringLength];
        for (int i = 0; i < bitStringLength; i++) {
            flags[i] = '0';
        }
        if (configurationFlags != null && configurationFlags.getConfigurationFlag() != null) {
            final List<ConfigurationFlag> configurationFlagList = configurationFlags.getConfigurationFlag();
            for (final ConfigurationFlag configurationFlag : configurationFlagList) {
                if (configurationFlag.isEnabled()) {
                    flags[configurationFlag.getConfigurationFlagType().ordinal()] = '1';
                }
            }
        }
        return this.jsonObjectCreator.createAttributeValue("bit-string", new String(flags), jsonNodeFactory);
    }
}
