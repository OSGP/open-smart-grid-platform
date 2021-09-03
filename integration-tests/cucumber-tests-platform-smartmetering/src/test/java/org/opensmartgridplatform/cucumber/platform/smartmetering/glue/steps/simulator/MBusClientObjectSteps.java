/*
 * Copyright 2021 Alliander N.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 */
package org.opensmartgridplatform.cucumber.platform.smartmetering.glue.steps.simulator;

import static org.assertj.core.api.Assertions.assertThat;
import static org.opensmartgridplatform.cucumber.platform.smartmetering.PlatformSmartmeteringKeys.MBUS_DEVICE_TYPE_IDENTIFICATION;
import static org.opensmartgridplatform.cucumber.platform.smartmetering.PlatformSmartmeteringKeys.MBUS_IDENTIFICATION_NUMBER;
import static org.opensmartgridplatform.cucumber.platform.smartmetering.PlatformSmartmeteringKeys.MBUS_MANUFACTURER_IDENTIFICATION;
import static org.opensmartgridplatform.cucumber.platform.smartmetering.PlatformSmartmeteringKeys.MBUS_PRIMARY_ADDRESS;
import static org.opensmartgridplatform.cucumber.platform.smartmetering.PlatformSmartmeteringKeys.MBUS_VERSION;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import java.util.Map;
import org.openmuc.jdlms.ObisCode;
import org.opensmartgridplatform.dlms.interfaceclass.InterfaceClass;
import org.springframework.beans.factory.annotation.Autowired;

public class MBusClientObjectSteps {

  private static final int HEX_RADIX = 16;

  private static final int CLASS_ID = InterfaceClass.MBUS_CLIENT.id();
  private static final int ATTRIBUTE_ID_PRIMARY_ADDRESS = 5;
  private static final int ATTRIBUTE_ID_IDENTIFICATION_NUMBER = 6;
  private static final int ATTRIBUTE_ID_MANUFACTURER_ID = 7;
  private static final int ATTRIBUTE_ID_VERSION = 8;
  private static final int ATTRIBUTE_ID_DEVICE_TYPE = 9;
  private static final String OBJECT_DESCRIPTION = "MBus client object";

  private static final int ASCII_VALUE_OF_A = 64;
  private static final int BASE = 32;

  @Autowired private DeviceSimulatorSteps deviceSimulatorSteps;

  @Autowired private JsonObjectCreator jsonObjectCreator;

  @Given("^device simulation of \"([^\"]*)\" with M-Bus client values for channel (\\d+)$")
  public void deviceSimulationOfMBusClientObject(
      final String deviceIdentification, final int channel, final Map<String, String> inputSettings)
      throws Throwable {

    this.deviceSimulatorSteps.deviceSimulationOfEquipmentIdentifier(deviceIdentification);

    final ObisCode obisCode = new ObisCode(0, channel, 24, 1, 0, 255);

    inputSettings.forEach(
        (key, value) -> {
          switch (key) {
            case MBUS_PRIMARY_ADDRESS:
              this.setStandardAttribute(value, "unsigned", ATTRIBUTE_ID_PRIMARY_ADDRESS, obisCode);
              break;
            case MBUS_IDENTIFICATION_NUMBER:
              this.setIdentificationNumberAttribute(value, obisCode);
              break;
            case MBUS_MANUFACTURER_IDENTIFICATION:
              this.setManufacturerIdAttribute(value, obisCode);
              break;
            case MBUS_VERSION:
              this.setStandardAttribute(value, "unsigned", ATTRIBUTE_ID_VERSION, obisCode);
              break;
            case MBUS_DEVICE_TYPE_IDENTIFICATION:
              this.setStandardAttribute(value, "unsigned", ATTRIBUTE_ID_DEVICE_TYPE, obisCode);
              break;
            default:
              throw new IllegalArgumentException("Unsupported attribute: " + key);
          }
        });
  }

  private void setStandardAttribute(
      final String value, final String type, final int attributeId, final ObisCode obisCode) {
    final ObjectNode attributeValuePrimaryAddress =
        this.jsonObjectCreator.createAttributeValue(type, value);
    this.deviceSimulatorSteps.setDlmsAttributeValue(
        CLASS_ID, obisCode, attributeId, attributeValuePrimaryAddress, OBJECT_DESCRIPTION);
  }

  private void setIdentificationNumberAttribute(final String value, final ObisCode obisCode) {
    // The identificationNumber is converted from the textual representation to BCD format.
    // For example: 12056731 becomes 302343985
    final String identificationNumber = String.valueOf(Long.parseLong(value, HEX_RADIX));
    this.setStandardAttribute(
        identificationNumber, "double-long-unsigned", ATTRIBUTE_ID_IDENTIFICATION_NUMBER, obisCode);
  }

  private void setManufacturerIdAttribute(final String value, final ObisCode obisCode) {
    // The Manufacturer id is converted from the textual representation to long value.
    // For example: LGB becomes 12514
    final String manufacturerId = this.convertManufacturerIdToValue(value);
    this.setStandardAttribute(
        manufacturerId, "long-unsigned", ATTRIBUTE_ID_MANUFACTURER_ID, obisCode);
  }

  @Then("^the values for the M-Bus client for channel (\\d+) on device simulator \"([^\"]*)\" are$")
  public void theValuesForMbusClientOnDeviceSimulatorAre(
      final int channel,
      final String deviceIdentification,
      final Map<String, String> expectedAttributes)
      throws Throwable {

    final ObisCode obisCode = new ObisCode(0, channel, 24, 1, 0, 255);

    final ObjectNode attributeValuesNode =
        this.deviceSimulatorSteps.getDlmsAttributeValues(
            CLASS_ID, obisCode, "M-Bus client attributes");

    expectedAttributes.forEach(
        (key, value) -> {
          switch (key) {
            case MBUS_PRIMARY_ADDRESS:
              this.verifyStandardAttribute(
                  attributeValuesNode, ATTRIBUTE_ID_PRIMARY_ADDRESS, value);
              break;
            case MBUS_VERSION:
              this.verifyStandardAttribute(attributeValuesNode, ATTRIBUTE_ID_VERSION, value);
              break;
            case MBUS_DEVICE_TYPE_IDENTIFICATION:
              this.verifyStandardAttribute(attributeValuesNode, ATTRIBUTE_ID_DEVICE_TYPE, value);
              break;
            case MBUS_IDENTIFICATION_NUMBER:
              this.verifyIdentificationNumber(attributeValuesNode, value);
              break;
            case MBUS_MANUFACTURER_IDENTIFICATION:
              this.verifyManufacturerIdentification(attributeValuesNode, value);
              break;
            default:
              throw new IllegalArgumentException("Unsupported attribute: " + key);
          }
        });
  }

  private void verifyStandardAttribute(
      final ObjectNode attributeValuesNode, final int attributeId, final String expectedValue) {
    final JsonNode actualAttributeValue = attributeValuesNode.get(String.valueOf(attributeId));

    this.verifyAttributeValue(actualAttributeValue, attributeId, expectedValue);
  }

  private void verifyIdentificationNumber(
      final ObjectNode attributeValuesNode, final String expectedValue) {

    final JsonNode actualAttributeValue =
        attributeValuesNode.get(String.valueOf(ATTRIBUTE_ID_IDENTIFICATION_NUMBER));

    final String convertedString =
        String.format("%08X", actualAttributeValue.get("value").asLong());
    final ObjectNode convertedValue =
        this.jsonObjectCreator.createAttributeValue("double-long-unsigned", convertedString);

    this.verifyAttributeValue(convertedValue, ATTRIBUTE_ID_IDENTIFICATION_NUMBER, expectedValue);
  }

  private void verifyManufacturerIdentification(
      final ObjectNode attributeValuesNode, final String expectedValue) {
    final JsonNode actualAttributeValue =
        attributeValuesNode.get(String.valueOf(ATTRIBUTE_ID_MANUFACTURER_ID));

    final String convertedString =
        this.convertValueToManufacturerId(actualAttributeValue.get("value").asText());
    final ObjectNode convertedValue =
        this.jsonObjectCreator.createAttributeValue("VISIBLE_STRING", convertedString);

    this.verifyAttributeValue(convertedValue, ATTRIBUTE_ID_MANUFACTURER_ID, expectedValue);
  }

  private void verifyAttributeValue(
      final JsonNode actualAttributeValue, final int attributeId, final String expectedValue) {
    assertThat(actualAttributeValue)
        .as("a value must be available for attributeId: " + attributeId)
        .isNotNull();
    assertThat(actualAttributeValue.findValuesAsText("value").get(0))
        .as("value for attributeId: " + attributeId)
        .isEqualTo(expectedValue);
  }

  // The manufacturedId is stored as number in the MBusClient object.
  // For example: LGB becomes 12514
  // The value is calculated as follows:
  // - Each character gets a value based on the place in the alphabet: A = 1, B = 2, ...
  // - Then these values are multiplied by a power of 32, based on its place in the Id: the last
  // character is multiplied by 32^0, the character before that by 32^1, ...
  // - And finally all values are summed.
  private String convertManufacturerIdToValue(final String id) {

    // An Id of "0" is used when a channel is empty and this should not be converted.
    if ("0".equals(id)) {
      return "0";
    }

    long convertedId = 0;
    final int positionOfLastCharacter = id.length() - 1;

    for (int i = 0; i < id.length(); i++) {
      final int value = id.charAt(i) - ASCII_VALUE_OF_A;
      convertedId = convertedId + value * (long) (Math.pow(BASE, (positionOfLastCharacter - i)));
    }

    return String.valueOf(convertedId);
  }

  private String convertValueToManufacturerId(final String id) {

    long value = Long.parseLong(id);
    long remainderAfterDevision = 0;
    final StringBuilder manufacturerId = new StringBuilder();
    int step = 0;

    while (value != 0) {
      remainderAfterDevision = value % (long) (Math.pow(BASE, step + 1));
      final char character =
          (char) (remainderAfterDevision / (long) (Math.pow(BASE, step)) + ASCII_VALUE_OF_A);

      manufacturerId.insert(0, character);

      value -= remainderAfterDevision;
      step++;
    }

    return manufacturerId.toString();
  }
}
