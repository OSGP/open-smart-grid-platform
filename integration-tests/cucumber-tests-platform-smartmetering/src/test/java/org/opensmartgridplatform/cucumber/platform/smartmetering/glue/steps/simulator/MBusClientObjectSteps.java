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

import static org.opensmartgridplatform.cucumber.platform.smartmetering.PlatformSmartmeteringKeys.MBUS_DEVICE_TYPE_IDENTIFICATION;
import static org.opensmartgridplatform.cucumber.platform.smartmetering.PlatformSmartmeteringKeys.MBUS_IDENTIFICATION_NUMBER;
import static org.opensmartgridplatform.cucumber.platform.smartmetering.PlatformSmartmeteringKeys.MBUS_MANUFACTURER_IDENTIFICATION;
import static org.opensmartgridplatform.cucumber.platform.smartmetering.PlatformSmartmeteringKeys.MBUS_PRIMARY_ADDRESS;
import static org.opensmartgridplatform.cucumber.platform.smartmetering.PlatformSmartmeteringKeys.MBUS_VERSION;

import com.fasterxml.jackson.databind.node.ObjectNode;
import io.cucumber.java.en.Given;
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

  @Autowired private DeviceSimulatorSteps deviceSimulatorSteps;

  @Autowired private JsonObjectCreator jsonObjectCreator;

  @Given("^device simulation of \"([^\"]*)\" with M-Bus client values for channel (\\d+)$")
  public void deviceSimulationOfMBusClientObject(
      final String deviceIdentification, final int channel, final Map<String, String> inputSettings)
      throws Throwable {

    this.deviceSimulatorSteps.deviceSimulationOfEquipmentIdentifier(deviceIdentification);

    final ObisCode obisCode = new ObisCode(0, channel, 24, 1, 0, 255);

    // Primary address
    final ObjectNode attributeValuePrimaryAddress =
        this.jsonObjectCreator.createAttributeValue(
            "unsigned", inputSettings.get(MBUS_PRIMARY_ADDRESS));
    this.deviceSimulatorSteps.setDlmsAttributeValue(
        CLASS_ID,
        obisCode,
        ATTRIBUTE_ID_PRIMARY_ADDRESS,
        attributeValuePrimaryAddress,
        OBJECT_DESCRIPTION);

    // Identification number
    // Note: the identificationNumber is converted from the textual representation to BCD format.
    // For example: 12056731 becomes 302343985
    final String identificationNumber =
        String.valueOf(Long.parseLong(inputSettings.get(MBUS_IDENTIFICATION_NUMBER), HEX_RADIX));
    final ObjectNode attributeValueIdentificationNumber =
        this.jsonObjectCreator.createAttributeValue("double-long-unsigned", identificationNumber);
    this.deviceSimulatorSteps.setDlmsAttributeValue(
        CLASS_ID,
        obisCode,
        ATTRIBUTE_ID_IDENTIFICATION_NUMBER,
        attributeValueIdentificationNumber,
        OBJECT_DESCRIPTION);

    // Manufacturer id
    // Note: the Manufacturer id is converted from the textual representation to long value.
    // For example: LGB becomes 12514
    final String manufacturerId =
        this.convertManufacturerId(inputSettings.get(MBUS_MANUFACTURER_IDENTIFICATION));
    final ObjectNode attributeValueManufacturerId =
        this.jsonObjectCreator.createAttributeValue("long-unsigned", manufacturerId);
    this.deviceSimulatorSteps.setDlmsAttributeValue(
        CLASS_ID,
        obisCode,
        ATTRIBUTE_ID_MANUFACTURER_ID,
        attributeValueManufacturerId,
        OBJECT_DESCRIPTION);

    // Version
    final ObjectNode attributeValueVersion =
        this.jsonObjectCreator.createAttributeValue("unsigned", inputSettings.get(MBUS_VERSION));
    this.deviceSimulatorSteps.setDlmsAttributeValue(
        CLASS_ID, obisCode, ATTRIBUTE_ID_VERSION, attributeValueVersion, OBJECT_DESCRIPTION);

    // Device type
    final ObjectNode attributeValueDeviceType =
        this.jsonObjectCreator.createAttributeValue(
            "unsigned", inputSettings.get(MBUS_DEVICE_TYPE_IDENTIFICATION));
    this.deviceSimulatorSteps.setDlmsAttributeValue(
        CLASS_ID, obisCode, ATTRIBUTE_ID_DEVICE_TYPE, attributeValueDeviceType, OBJECT_DESCRIPTION);
  }

  private String convertManufacturerId(final String id) {

    if ("0".equals(id)) {
      return "0";
    }

    long convertedId = 0;

    for (int i = 0; i < id.length(); i++) {
      final char c = id.charAt(i);
      convertedId = convertedId + (((int) c) - 64) * (long) (Math.pow(32, (id.length() - i - 1)));
    }

    return String.valueOf(convertedId);
  }
}
