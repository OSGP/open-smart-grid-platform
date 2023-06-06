// SPDX-FileCopyrightText: Copyright Contributors to the GXF project
//
// SPDX-License-Identifier: Apache-2.0

package org.opensmartgridplatform.cucumber.platform.smartmetering.glue.steps.simulator;

import static org.assertj.core.api.Assertions.assertThat;
import static org.opensmartgridplatform.cucumber.platform.smartmetering.PlatformSmartmeteringKeys.MBUS_DEVICE_TYPE_IDENTIFICATION;
import static org.opensmartgridplatform.cucumber.platform.smartmetering.PlatformSmartmeteringKeys.MBUS_ENCRYPTION_KEY_STATUS;
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
import org.opensmartgridplatform.adapter.protocol.dlms.domain.commands.mbus.ManufacturerId;
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
  private static final int ATTRIBUTE_ID_ENCRYPTION_KEY_STATUS = 14;
  private static final String OBJECT_DESCRIPTION = "MBus client object";

  private static final int ASCII_VALUE_OF_A = 64;
  private static final int BASE = 32;

  @Autowired private DeviceSimulatorSteps deviceSimulatorSteps;

  @Autowired private JsonObjectCreator jsonObjectCreator;

  @Given(
      "^device simulation of \"([^\"]*)\" with M-Bus client version (\\d+) values for channel (\\d+)$")
  public void deviceSimulationOfMBusClientObject(
      final String deviceIdentification,
      final int version,
      final int channel,
      final Map<String, String> inputSettings)
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
              this.setIdentificationNumberAttribute(value, obisCode, version);
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
            case MBUS_ENCRYPTION_KEY_STATUS:
              this.setStandardAttribute(
                  value, "enumerate", ATTRIBUTE_ID_ENCRYPTION_KEY_STATUS, obisCode);
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

  private void setIdentificationNumberAttribute(
      final String value, final ObisCode obisCode, final int version) {
    final String identificationNumber;
    if (version == 0) {
      // For MbusClientSetup version 0 the identificationNumber is converted from the textual
      // representation to BCD format.
      // For example: 12056731 becomes 302343985
      identificationNumber = String.valueOf(Long.parseLong(value, HEX_RADIX));
    } else {
      identificationNumber = value;
    }
    this.setStandardAttribute(
        identificationNumber, "double-long-unsigned", ATTRIBUTE_ID_IDENTIFICATION_NUMBER, obisCode);
  }

  private void setManufacturerIdAttribute(final String value, final ObisCode obisCode) {
    // The Manufacturer id is converted from the textual representation to long value.
    // For example: LGB becomes 12514

    final String manufacturerIdAsValue;

    // An Id of "0" is used when a channel is empty and this should not be converted.
    if ("0".equals(value)) {
      manufacturerIdAsValue = "0";
    } else {
      manufacturerIdAsValue = String.valueOf(ManufacturerId.fromIdentification(value).getId());
    }

    this.setStandardAttribute(
        manufacturerIdAsValue, "long-unsigned", ATTRIBUTE_ID_MANUFACTURER_ID, obisCode);
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
    final int actualValue = actualAttributeValue.get("value").asInt();

    final String convertedString;

    // An Id of "0" is used when a channel is empty and this should not be converted.
    if (actualValue == 0) {
      convertedString = "0";
    } else {
      convertedString =
          ManufacturerId.fromId(actualAttributeValue.get("value").asInt()).getIdentification();
    }

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
}
