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

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.Arrays;
import java.util.EnumSet;
import java.util.List;
import java.util.Locale;

import org.openmuc.jdlms.ObisCode;
import org.openmuc.jdlms.datatypes.DataObject;
import org.openmuc.jdlms.interfaceclass.InterfaceClass;
import org.osgp.adapter.protocol.dlms.simulator.trigger.SimulatorTriggerClient;
import org.osgp.adapter.protocol.dlms.simulator.trigger.SimulatorTriggerClientException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import com.alliander.osgp.cucumber.core.GlueBase;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.BigIntegerNode;
import com.fasterxml.jackson.databind.node.DecimalNode;
import com.fasterxml.jackson.databind.node.IntNode;
import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import com.fasterxml.jackson.databind.node.LongNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.fasterxml.jackson.databind.node.TextNode;

import cucumber.api.java.en.Given;
import cucumber.api.java.en.Then;

public class DeviceSimulatorSteps extends GlueBase {

    @Autowired
    private SimulatorTriggerClient simulatorTriggerClient;

    private static final Logger LOGGER = LoggerFactory.getLogger(DeviceSimulatorSteps.class);

    private static final EnumSet<DataObject.Type> DATA_OBJECT_NO_VALUE_TYPES = EnumSet.of(DataObject.Type.DONT_CARE,
            DataObject.Type.NULL_DATA);

    private static final EnumSet<DataObject.Type> DATA_OBJECT_INTEGER_VALUE_TYPES = EnumSet.of(DataObject.Type.BCD,
            DataObject.Type.ENUMERATE, DataObject.Type.INTEGER, DataObject.Type.UNSIGNED, DataObject.Type.LONG_INTEGER,
            DataObject.Type.LONG_UNSIGNED, DataObject.Type.DOUBLE_LONG, DataObject.Type.DOUBLE_LONG_UNSIGNED,
            DataObject.Type.LONG64, DataObject.Type.LONG64_UNSIGNED);

    private static final EnumSet<DataObject.Type> DATA_OBJECT_DECIMAL_VALUE_TYPES = EnumSet.of(DataObject.Type.FLOAT32,
            DataObject.Type.FLOAT64);

    public void clearDlmsAttributeValues() {
        try {
            this.simulatorTriggerClient.clearDlmsAttributeValues();
        } catch (final SimulatorTriggerClientException stce) {
            LOGGER.error("Error calling simulatorTriggerClient.clearDlmsAttributeValues()", stce);
            fail("Error clearing DLMS attribute values for simulator");
        }
    }

    @Given("^device \"([^\"]*)\" has some alarms registered$")
    public void deviceHasSomeAlarmsRegistered(final String deviceIdentification) {
        final ObisCode obisCodeAlarmObject = new ObisCode(0, 0, 97, 98, 0, 255);
        final ObjectNode attributeValues = this
                .convertTableToJsonObject(Arrays.asList(Arrays.asList("2", "double-long-unsigned", "33693956")));
        try {
            this.simulatorTriggerClient.setDlmsAttributeValues(InterfaceClass.DATA.id(), obisCodeAlarmObject,
                    attributeValues);
        } catch (final SimulatorTriggerClientException stce) {
            LOGGER.error("Error while setting DLMS attribute values for alarm object with SimulatorTriggerClient",
                    stce);
            fail("Error setting DLMS attribute values for the alarm object on the simulator");
        }
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

        try {
            this.simulatorTriggerClient.setDlmsAttributeValues(classId, new ObisCode(obisCode),
                    this.convertTableToJsonObject(attributes));
        } catch (final SimulatorTriggerClientException stce) {
            LOGGER.error("Error while setting DLMS attribute values for classId: " + classId + ", obisCode: " + obisCode
                    + " and attributes: " + attributes + " with SimulatorTriggerClient", stce);
            fail("Error setting DLMS attribute values for simulator");
        }
    }

    private ObjectNode convertTableToJsonObject(final List<List<String>> tableRows) {

        final JsonNodeFactory jsonNodeFactory = new JsonNodeFactory(false);
        final ObjectNode attributeValues = jsonNodeFactory.objectNode();

        if (tableRows == null) {
            return attributeValues;
        }

        for (final List<String> tableRow : tableRows) {
            this.setAttributeValueFromTableRow(attributeValues, tableRow, jsonNodeFactory);
        }

        return attributeValues;
    }

    private void setAttributeValueFromTableRow(final ObjectNode attributeValues,
            final List<String> valuesForAttributeNode, final JsonNodeFactory jsonNodeFactory) {

        final String attributeId = valuesForAttributeNode.get(0);
        final String type = valuesForAttributeNode.get(1);
        final String textValue = valuesForAttributeNode.get(2);

        final ObjectNode attributeValue = this.createAttributeValue(type, textValue, jsonNodeFactory);
        attributeValues.set(attributeId, attributeValue);
    }

    private ObjectNode createAttributeValue(final String type, final String textValue,
            final JsonNodeFactory jsonNodeFactory) {

        final ObjectNode attributeValue = jsonNodeFactory.objectNode();
        this.setTypeNode(attributeValue, type);
        this.setValueNode(attributeValue, type, textValue);

        return attributeValue;
    }

    private void setTypeNode(final ObjectNode attributeValue, final String type) {
        attributeValue.set("type", TextNode.valueOf(type));
    }

    private void setValueNode(final ObjectNode attributeValue, final String type, final String textValue) {
        final DataObject.Type dataObjectType = DataObject.Type.valueOf(type.replace('-', '_').toUpperCase(Locale.UK));
        if (DATA_OBJECT_NO_VALUE_TYPES.contains(dataObjectType)) {
            return;
        }
        JsonNode valueNode;
        if (DATA_OBJECT_INTEGER_VALUE_TYPES.contains(dataObjectType)) {
            valueNode = this.createIntegerNode(new BigInteger(textValue));
        } else if (DATA_OBJECT_DECIMAL_VALUE_TYPES.contains(dataObjectType)) {
            valueNode = DecimalNode.valueOf(new BigDecimal(textValue));
        } else {
            valueNode = TextNode.valueOf(textValue);
        }
        attributeValue.set("value", valueNode);
    }

    private JsonNode createIntegerNode(final BigInteger value) {
        try {
            return IntNode.valueOf(value.intValueExact());
        } catch (final ArithmeticException e) {
            return this.createLongIntegerNode(value);
        }
    }

    private JsonNode createLongIntegerNode(final BigInteger value) {
        try {
            return LongNode.valueOf(value.longValueExact());
        } catch (final ArithmeticException e) {
            return BigIntegerNode.valueOf(value);
        }
    }

    @Then("^the values for classid (\\d+) obiscode \"([^\"]*)\" on device simulator \"([^\"]*)\" are$")
    public void theValuesForClassidObiscodeOnDeviceSimulatorAre(final int classId, final String obisCode,
            final String deviceIdentification, final List<List<String>> expectedAttributes) throws Throwable {

        try {
            final ObjectNode attributeValuesNode = this.simulatorTriggerClient.getDlmsAttributeValues(classId,
                    new ObisCode(obisCode));
            final ObjectNode expectedAttributeValuesNode = this.convertTableToJsonObject(expectedAttributes);

            expectedAttributeValuesNode.fields().forEachRemaining(field -> {
                final String attributeId = field.getKey();
                final JsonNode expectedAttributeValue = field.getValue();
                final JsonNode actualAttributeValue = attributeValuesNode.get(attributeId);
                assertNotNull("a value must be available for attributeId: " + attributeId, actualAttributeValue);
                assertEquals("value for attributeId: " + attributeId, expectedAttributeValue, actualAttributeValue);
            });
        } catch (final SimulatorTriggerClientException stce) {
            LOGGER.error("Error while getting DLMS attribute values", stce);
            fail("Error getting DLMS attribute values for simulator");
        }
    }
}
