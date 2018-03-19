/**
 * Copyright 2017 Smart Society Services B.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 */
package com.alliander.osgp.cucumber.platform.smartmetering.glue.steps.simulator;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.fail;

import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.List;

import org.apache.commons.codec.binary.Hex;
import org.openmuc.jdlms.ObisCode;
import org.openmuc.jdlms.interfaceclass.InterfaceClass;
import org.osgp.adapter.protocol.dlms.simulator.trigger.SimulatorTriggerClient;
import org.osgp.adapter.protocol.dlms.simulator.trigger.SimulatorTriggerClientException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import com.alliander.osgp.cucumber.core.GlueBase;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ObjectNode;

import cucumber.api.java.en.Given;
import cucumber.api.java.en.Then;

public class DeviceSimulatorSteps extends GlueBase {

    private static final Logger LOGGER = LoggerFactory.getLogger(DeviceSimulatorSteps.class);

    private static final String GENERIC_ATTRIBUTES_FROM_SCENARIO = "attributes as defined in scenario";

    @Autowired
    private SimulatorTriggerClient simulatorTriggerClient;

    @Autowired
    private JsonObjectCreator jsonObjectCreator;

    public void clearDlmsAttributeValues() {
        try {
            this.simulatorTriggerClient.clearDlmsAttributeValues();
        } catch (final SimulatorTriggerClientException stce) {
            LOGGER.error("Error calling simulatorTriggerClient.clearDlmsAttributeValues()", stce);
            fail("Error clearing DLMS attribute values for simulator");
        }
    }

    public ObjectNode getDlmsAttributeValues(final int classId, final ObisCode obisCode, final String description) {
        ObjectNode jsonAttributeValues = null;
        try {
            jsonAttributeValues = this.simulatorTriggerClient.getDlmsAttributeValues(classId, obisCode);
        } catch (final SimulatorTriggerClientException stce) {
            LOGGER.error(
                    "Error while getting DLMS attribute values with classId: {} and ObisCode: {} for {} with SimulatorTriggerClient",
                    classId, obisCode, description, stce);
            fail("Error getting DLMS attribute values for " + description + " on the simulator");
        }
        return jsonAttributeValues;
    }

    public ObjectNode getDlmsAttributeValue(final int classId, final ObisCode obisCode, final int attributeId,
            final String description) {
        ObjectNode jsonAttributeValue = null;
        try {
            jsonAttributeValue = this.simulatorTriggerClient.getDlmsAttributeValue(classId, obisCode, attributeId);
        } catch (final SimulatorTriggerClientException stce) {
            LOGGER.error(
                    "Error while getting DLMS attribute value with classId: {}, ObisCode: {} and attributeId: {} for {} with SimulatorTriggerClient",
                    classId, obisCode, attributeId, description, stce);
            fail("Error getting DLMS attribute value for " + description + " on the simulator");
        }
        return jsonAttributeValue;
    }

    public void setDlmsAttributeValues(final int classId, final ObisCode obisCode, final ObjectNode jsonAttributeValues,
            final String description) {

        try {
            this.simulatorTriggerClient.setDlmsAttributeValues(classId, obisCode, jsonAttributeValues);
        } catch (final SimulatorTriggerClientException stce) {
            LOGGER.error(
                    "Error while setting DLMS attribute values {} with classId: {} and ObisCode: {} for {} with SimulatorTriggerClient",
                    jsonAttributeValues, classId, obisCode, description, stce);
            fail("Error setting DLMS attribute values for " + description + " on the simulator");
        }
    }

    public void setDlmsAttributeValue(final int classId, final ObisCode obisCode, final int attributeId,
            final ObjectNode jsonAttributeValue, final String description) {

        try {
            this.simulatorTriggerClient.setDlmsAttributeValue(classId, obisCode, attributeId, jsonAttributeValue);
        } catch (final SimulatorTriggerClientException stce) {
            LOGGER.error(
                    "Error while setting DLMS attribute value {} with classId: {}, ObisCode: {}, and attributeId: {} for {} with SimulatorTriggerClient",
                    jsonAttributeValue, classId, obisCode, attributeId, description, stce);
            fail("Error setting DLMS attribute value for " + description + " on the simulator");
        }
    }

    public void deviceSimulationOfEquipmentIdentifier(final String deviceIdentification) {
        final ObisCode obisCodeEquipmentIdentifier = new ObisCode(0, 0, 96, 1, 1, 255);
        final ObjectNode attributeValues = this.jsonObjectCreator
                .convertTableToJsonObject(Arrays.asList(Arrays.asList("2", "octet-string",
                        Hex.encodeHexString(deviceIdentification.getBytes(StandardCharsets.US_ASCII)))));
        this.setDlmsAttributeValues(InterfaceClass.DATA.id(), obisCodeEquipmentIdentifier, attributeValues,
                "the E-meter Equipment Identifier");
    }

    @Given("^device simulation of \"([^\"]*)\" with classid (\\d+) obiscode \"([^\"]*)\" and attributes$")
    public void deviceSimulationOfObisCodeWithClassIdAndAttributes(final String deviceIdentification, final int classId,
            final String obisCode, final List<List<String>> attributes) throws Throwable {

        /*
         * Currently the first argument: deviceIdentification, is not used yet,
         * because in all scenarios created so far that make use of dynamic
         * device simulator properties, only one meter was read. In future
         * scenarios it may be possible that within a single scenario two (or
         * more) meters should be read, and that both meters should read their
         * own set of dynamic properties. In that case the deviceIdentification
         * parameter can be used to make this distinction.
         */

        this.deviceSimulationOfEquipmentIdentifier(deviceIdentification);

        this.setDlmsAttributeValues(classId, new ObisCode(obisCode),
                this.jsonObjectCreator.convertTableToJsonObject(attributes), GENERIC_ATTRIBUTES_FROM_SCENARIO);
    }

    @Then("^the values for classid (\\d+) obiscode \"([^\"]*)\" on device simulator \"([^\"]*)\" are$")
    public void theValuesForClassidObiscodeOnDeviceSimulatorAre(final int classId, final String obisCode,
            final String deviceIdentification, final List<List<String>> expectedAttributes) throws Throwable {

        final ObjectNode attributeValuesNode = this.getDlmsAttributeValues(classId, new ObisCode(obisCode),
                GENERIC_ATTRIBUTES_FROM_SCENARIO);
        final ObjectNode expectedAttributeValuesNode = this.jsonObjectCreator
                .convertTableToJsonObject(expectedAttributes);

        expectedAttributeValuesNode.fields().forEachRemaining(field -> {
            final String attributeId = field.getKey();
            final JsonNode expectedAttributeValue = field.getValue();
            final JsonNode actualAttributeValue = attributeValuesNode.get(attributeId);
            assertNotNull("a value must be available for attributeId: " + attributeId, actualAttributeValue);
            assertEquals("value for attributeId: " + attributeId, expectedAttributeValue, actualAttributeValue);
        });
    }

}
