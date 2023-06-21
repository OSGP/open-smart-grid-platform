// SPDX-FileCopyrightText: Copyright Contributors to the GXF project
//
// SPDX-License-Identifier: Apache-2.0

package org.opensmartgridplatform.cucumber.platform.smartmetering.glue.steps.simulator;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

import com.fasterxml.jackson.databind.node.ObjectNode;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import org.openmuc.jdlms.ObisCode;
import org.opensmartgridplatform.dlms.interfaceclass.InterfaceClass;
import org.opensmartgridplatform.dlms.interfaceclass.attribute.DataAttribute;
import org.springframework.beans.factory.annotation.Autowired;

public class SimulatedAlarmObjectSteps {

  private static final int CLASS_ID = InterfaceClass.DATA.id();
  private static final int ATTRIBUTE_ID_VALUE = DataAttribute.VALUE.attributeId();
  private static final String OBJECT_DESCRIPTION = "the alarm object";

  private static final ObisCode OBIS_CODE_ALARM_OBJECT_1 = new ObisCode(0, 0, 97, 98, 0, 255);
  private static final ObisCode OBIS_CODE_ALARM_OBJECT_2 = new ObisCode(0, 0, 97, 98, 1, 255);
  private static final ObisCode OBIS_CODE_ALARM_OBJECT_3 = new ObisCode(0, 0, 97, 98, 2, 255);

  private static final String ALARM_VALUE = "33693956";

  @Autowired private DeviceSimulatorSteps deviceSimulatorSteps;

  @Autowired private JsonObjectCreator jsonObjectCreator;

  @Given("^device \"([^\"]*)\" has alarm register \"([^\"]*)\" with some value$")
  public void deviceHasSomeAlarmsRegistered(
      final String deviceIdentification, final int alarmRegisterNr) {
    this.deviceSimulatorSteps.deviceSimulationOfEquipmentIdentifier(deviceIdentification);

    final ObisCode alarmRegisterObisCode = this.getAlarmRegisterObisCode(alarmRegisterNr);
    final ObjectNode attributeValue =
        this.jsonObjectCreator.createAttributeValue("double-long-unsigned", ALARM_VALUE);
    this.deviceSimulatorSteps.setDlmsAttributeValue(
        CLASS_ID, alarmRegisterObisCode, ATTRIBUTE_ID_VALUE, attributeValue, OBJECT_DESCRIPTION);
  }

  @Then("^alarm register \"([^\"]*)\" of device \"([^\"]*)\" has been cleared$")
  public void deviceAlarmRegistersHasBeenCleared(
      final int alarmRegisterNr, final String deviceIdentification) {
    this.deviceSimulatorSteps.deviceSimulationOfEquipmentIdentifier(deviceIdentification);

    final ObisCode alarmRegisterObisCode = this.getAlarmRegisterObisCode(alarmRegisterNr);
    final ObjectNode result =
        this.deviceSimulatorSteps.getDlmsAttributeValue(
            CLASS_ID, alarmRegisterObisCode, ATTRIBUTE_ID_VALUE, OBJECT_DESCRIPTION);
    assertThat(result.get("value").intValue()).isZero();
  }

  @Then("^alarm register \"([^\"]*)\" of device \"([^\"]*)\" has not been cleared$")
  public void deviceAlarmRegistersHasNotBeenCleared(
      final int alarmRegisterNr, final String deviceIdentification) {
    this.deviceSimulatorSteps.deviceSimulationOfEquipmentIdentifier(deviceIdentification);

    final ObisCode alarmRegisterObisCode = this.getAlarmRegisterObisCode(alarmRegisterNr);
    final ObjectNode result =
        this.deviceSimulatorSteps.getDlmsAttributeValue(
            CLASS_ID, alarmRegisterObisCode, ATTRIBUTE_ID_VALUE, OBJECT_DESCRIPTION);
    assertThat(result.get("value").intValue()).isNotZero();
  }

  private ObisCode getAlarmRegisterObisCode(final int alarmRegisterNr) {
    switch (alarmRegisterNr) {
      case 1:
        return OBIS_CODE_ALARM_OBJECT_1;
      case 2:
        return OBIS_CODE_ALARM_OBJECT_2;
      case 3:
        return OBIS_CODE_ALARM_OBJECT_3;
      default:
        throw new IllegalArgumentException("There is no alarmRegister: " + alarmRegisterNr);
    }
  }
}
