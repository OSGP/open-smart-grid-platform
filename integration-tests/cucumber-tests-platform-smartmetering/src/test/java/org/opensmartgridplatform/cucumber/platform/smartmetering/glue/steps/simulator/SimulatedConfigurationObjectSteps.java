// SPDX-FileCopyrightText: Copyright Contributors to the GXF project
//
// SPDX-License-Identifier: Apache-2.0

package org.opensmartgridplatform.cucumber.platform.smartmetering.glue.steps.simulator;

import static org.assertj.core.api.Assertions.assertThat;

import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import com.fasterxml.jackson.databind.node.ObjectNode;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import java.util.List;
import java.util.Map;
import lombok.extern.slf4j.Slf4j;
import org.openmuc.jdlms.ObisCode;
import org.opensmartgridplatform.adapter.ws.schema.smartmetering.configuration.ConfigurationFlag;
import org.opensmartgridplatform.adapter.ws.schema.smartmetering.configuration.ConfigurationFlags;
import org.opensmartgridplatform.adapter.ws.schema.smartmetering.configuration.ConfigurationObject;
import org.opensmartgridplatform.adapter.ws.schema.smartmetering.configuration.GprsOperationModeType;
import org.opensmartgridplatform.cucumber.platform.smartmetering.support.ws.smartmetering.configuration.ConfigurationObjectFactory;
import org.opensmartgridplatform.dlms.interfaceclass.InterfaceClass;
import org.opensmartgridplatform.dlms.interfaceclass.attribute.DataAttribute;
import org.springframework.beans.factory.annotation.Autowired;

@Slf4j
public class SimulatedConfigurationObjectSteps {

  private static final int CLASS_ID = InterfaceClass.DATA.id();
  private static final ObisCode OBIS_CODE = new ObisCode(0, 1, 94, 31, 3, 255);
  private static final int ATTRIBUTE_ID_VALUE = DataAttribute.VALUE.attributeId();
  private static final String OBJECT_DESCRIPTION = "the configuration object";

  @Autowired private DeviceSimulatorSteps deviceSimulatorSteps;

  @Autowired private JsonObjectCreator jsonObjectCreator;

  @Given(
      "device simulation of {string} with configuration object values in structure type value attribute")
  public void deviceSimulationOfConfigurationObjectValuesInStructureTypeValueAttribute(
      final String deviceIdentification, final Map<String, String> settings) {

    this.deviceSimulatorSteps.deviceSimulationOfEquipmentIdentifier(deviceIdentification);

    final ConfigurationObject configurationObject =
        ConfigurationObjectFactory.fromParameterMap(settings);
    final ObjectNode attributeValue =
        this.createStructureForConfigurationObject(configurationObject, new JsonNodeFactory(false));
    this.deviceSimulatorSteps.setDlmsAttributeValue(
        CLASS_ID, OBIS_CODE, ATTRIBUTE_ID_VALUE, attributeValue, OBJECT_DESCRIPTION);
  }

  @Given(
      "device simulation of {string} with configuration object values in bitstring type value attribute")
  public void deviceSimulationOfConfigurationObjectValuesInBitstringTypeValueAttribute(
      final String deviceIdentification, final Map<String, String> settings) {

    this.deviceSimulatorSteps.deviceSimulationOfEquipmentIdentifier(deviceIdentification);

    final ConfigurationObject configurationObject =
        ConfigurationObjectFactory.fromParameterMap(settings);
    final ObjectNode attributeValue =
        this.createBitstringForConfigurationObject(configurationObject, new JsonNodeFactory(false));
    this.deviceSimulatorSteps.setDlmsAttributeValue(
        CLASS_ID, OBIS_CODE, ATTRIBUTE_ID_VALUE, attributeValue, OBJECT_DESCRIPTION);
  }

  @Then(
      "device simulation should have values in a bitstring type value attribute of the configuration object")
  public void
      deviceSimulationShouldHaveValuesInABitstringTypeValueAttributeOfTheConfigurationObject(
          final Map<String, String> settings) {

    final ConfigurationObject expectedConfigurationObject =
        ConfigurationObjectFactory.fromParameterMap(settings);
    final ObjectNode expectedValue =
        this.createBitstringForConfigurationObject(
            expectedConfigurationObject, new JsonNodeFactory(false));

    this.assertDlmsAttributeValue(expectedValue);
  }

  @Then(
      "device simulation should have values in a structure type value attribute of the configuration object")
  public void
      deviceSimulationShouldHaveValuesInAStructureTypeValueAttributeOfTheConfigurationObject(
          final Map<String, String> settings) {

    final ConfigurationObject expectedConfigurationObject =
        ConfigurationObjectFactory.fromParameterMap(settings);
    final ObjectNode expectedValue =
        this.createStructureForConfigurationObject(
            expectedConfigurationObject, new JsonNodeFactory(false));

    this.assertDlmsAttributeValue(expectedValue);
  }

  private void assertDlmsAttributeValue(final ObjectNode expectedValue) {
    final ObjectNode actualValue =
        this.deviceSimulatorSteps.getDlmsAttributeValue(
            CLASS_ID, OBIS_CODE, ATTRIBUTE_ID_VALUE, OBJECT_DESCRIPTION);

    assertThat(actualValue).as("Simulated ConfigurationObject value").isEqualTo(expectedValue);
  }

  private ObjectNode createStructureForConfigurationObject(
      final ConfigurationObject configurationObject, final JsonNodeFactory jsonNodeFactory) {

    final ObjectNode structureForConfigurationObject = jsonNodeFactory.objectNode();
    this.jsonObjectCreator.setTypeNode(structureForConfigurationObject, "structure");

    final ArrayNode configurationObjectElements = jsonNodeFactory.arrayNode();

    configurationObjectElements.add(
        this.createGprsOperationModeForConfigurationObject(
            configurationObject.getGprsOperationMode(), jsonNodeFactory));

    final String flagsString = this.createFlagString(configurationObject.getConfigurationFlags());
    final ObjectNode flagsForConfigurationObject =
        this.jsonObjectCreator.createAttributeValue("bit-string", flagsString, jsonNodeFactory);
    configurationObjectElements.add(flagsForConfigurationObject);

    structureForConfigurationObject.set("value", configurationObjectElements);

    return structureForConfigurationObject;
  }

  private ObjectNode createBitstringForConfigurationObject(
      final ConfigurationObject configurationObject, final JsonNodeFactory jsonNodeFactory) {

    final String flagsString = this.createFlagString(configurationObject.getConfigurationFlags());

    return this.jsonObjectCreator.createAttributeValue("bit-string", flagsString, jsonNodeFactory);
  }

  private ObjectNode createGprsOperationModeForConfigurationObject(
      final GprsOperationModeType gprsOperationMode, final JsonNodeFactory jsonNodeFactory) {

    final String textValue;
    if (gprsOperationMode == GprsOperationModeType.ALWAYS_ON) {
      textValue = "1";
    } else if (gprsOperationMode == GprsOperationModeType.TRIGGERED) {
      textValue = "2";
    } else {
      textValue = "0";
    }
    return this.jsonObjectCreator.createAttributeValue("enumerate", textValue, jsonNodeFactory);
  }

  private String createFlagString(final ConfigurationFlags configurationFlags) {
    final int bitStringLength = 16;
    final char[] flags = new char[bitStringLength];
    for (int i = 0; i < bitStringLength; i++) {
      flags[i] = '0';
    }
    if (configurationFlags != null && configurationFlags.getConfigurationFlag() != null) {
      final List<ConfigurationFlag> configurationFlagList =
          configurationFlags.getConfigurationFlag();
      for (final ConfigurationFlag configurationFlag : configurationFlagList) {
        if (configurationFlag.isEnabled()) {
          flags[configurationFlag.getConfigurationFlagType().ordinal()] = '1';
        }
      }
    }
    return new String(flags);
  }
}
