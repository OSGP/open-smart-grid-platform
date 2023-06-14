// SPDX-FileCopyrightText: Copyright Contributors to the GXF project
//
// SPDX-License-Identifier: Apache-2.0

package org.opensmartgridplatform.cucumber.platform.smartmetering.glue.steps.simulator;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.BigIntegerNode;
import com.fasterxml.jackson.databind.node.DecimalNode;
import com.fasterxml.jackson.databind.node.IntNode;
import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import com.fasterxml.jackson.databind.node.LongNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.fasterxml.jackson.databind.node.TextNode;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.EnumSet;
import java.util.List;
import java.util.Locale;
import org.openmuc.jdlms.datatypes.DataObject;
import org.springframework.stereotype.Service;

/**
 * Helper class for Cucumber scenario's with factory methods for JsonNodes as applicable in
 * communication with the DLMS attribute values resource through the SimulatorTriggerClient.
 */
@Service
public class JsonObjectCreator {

  private static final EnumSet<DataObject.Type> DATA_OBJECT_NO_VALUE_TYPES =
      EnumSet.of(DataObject.Type.DONT_CARE, DataObject.Type.NULL_DATA);

  private static final EnumSet<DataObject.Type> DATA_OBJECT_INTEGER_VALUE_TYPES =
      EnumSet.of(
          DataObject.Type.BCD,
          DataObject.Type.ENUMERATE,
          DataObject.Type.INTEGER,
          DataObject.Type.UNSIGNED,
          DataObject.Type.LONG_INTEGER,
          DataObject.Type.LONG_UNSIGNED,
          DataObject.Type.DOUBLE_LONG,
          DataObject.Type.DOUBLE_LONG_UNSIGNED,
          DataObject.Type.LONG64,
          DataObject.Type.LONG64_UNSIGNED);

  private static final EnumSet<DataObject.Type> DATA_OBJECT_DECIMAL_VALUE_TYPES =
      EnumSet.of(DataObject.Type.FLOAT32, DataObject.Type.FLOAT64);

  public ObjectNode convertTableToJsonObject(final List<List<String>> tableRows) {

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

  private void setAttributeValueFromTableRow(
      final ObjectNode attributeValues,
      final List<String> valuesForAttributeNode,
      final JsonNodeFactory jsonNodeFactory) {

    final String attributeId = valuesForAttributeNode.get(0);
    final String type = valuesForAttributeNode.get(1);
    final String textValue = valuesForAttributeNode.get(2);

    final ObjectNode attributeValue = this.createAttributeValue(type, textValue, jsonNodeFactory);
    attributeValues.set(attributeId, attributeValue);
  }

  public ObjectNode createAttributeValue(final String type, final String textValue) {
    return this.createAttributeValue(type, textValue, new JsonNodeFactory(false));
  }

  public ObjectNode createAttributeValue(
      final String type, final String textValue, final JsonNodeFactory jsonNodeFactory) {

    final ObjectNode attributeValue = jsonNodeFactory.objectNode();
    this.setTypeNode(attributeValue, type);
    this.setValueNode(attributeValue, type, textValue);

    return attributeValue;
  }

  public void setTypeNode(final ObjectNode attributeValue, final String type) {
    attributeValue.set("type", TextNode.valueOf(type));
  }

  private void setValueNode(
      final ObjectNode attributeValue, final String type, final String textValue) {
    final DataObject.Type dataObjectType =
        DataObject.Type.valueOf(type.replace('-', '_').toUpperCase(Locale.UK));
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
}
