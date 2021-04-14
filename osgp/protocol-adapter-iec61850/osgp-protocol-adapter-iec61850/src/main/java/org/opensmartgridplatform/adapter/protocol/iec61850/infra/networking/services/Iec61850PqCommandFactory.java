/*
 * Copyright 2017 Smart Society Services B.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file
 * except in compliance with the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 */
package org.opensmartgridplatform.adapter.protocol.iec61850.infra.networking.services;

import java.util.EnumSet;
import java.util.Map;
import java.util.Set;
import org.opensmartgridplatform.adapter.protocol.iec61850.device.rtu.RtuReadCommand;
import org.opensmartgridplatform.adapter.protocol.iec61850.infra.networking.helper.DataAttribute;
import org.opensmartgridplatform.dto.valueobjects.microgrids.MeasurementDto;
import org.springframework.stereotype.Component;

@Component
public class Iec61850PqCommandFactory extends AbstractIec61850RtuReadCommandFactory {

  private static final int IMPEDANCE_PHASE_ID_START = 1;
  private static final int IMPEDANCE_PHASE_ID_END = 1;
  private static final int VOLTAGE_DIPS_ID_START = 1;
  private static final int VOLTAGE_DIPS_ID_END = 1;
  private static final int POWER_FACTOR_PHASE_ID_START = 1;
  private static final int POWER_FACTOR_PHASE_ID_END = 2;
  private static final int FREQUENCY_ID_START = 1;
  private static final int FREQUENCY_ID_END = 3;
  private static final int PHASE_TO_NEUTRAL_VOLTAGE_PHASE_ID_START = 1;
  private static final int PHASE_TO_NEUTRAL_VOLTAGE_PHASE_ID_END = 3;

  public Iec61850PqCommandFactory() {
    super(rtuCommandMap(), dataAttributesUsingFilterId());
  }

  private static final Set<DataAttribute> dataAttributesUsingFilterId() {
    return EnumSet.of(
        DataAttribute.FREQUENCY,
        DataAttribute.PHASE_TO_NEUTRAL_VOLTAGE_PHASE_A,
        DataAttribute.PHASE_TO_NEUTRAL_VOLTAGE_PHASE_B,
        DataAttribute.PHASE_TO_NEUTRAL_VOLTAGE_PHASE_C,
        DataAttribute.POWER_FACTOR_PHASE_A,
        DataAttribute.POWER_FACTOR_PHASE_B,
        DataAttribute.POWER_FACTOR_PHASE_C,
        DataAttribute.IMPEDANCE_PHASE_A,
        DataAttribute.IMPEDANCE_PHASE_B,
        DataAttribute.IMPEDANCE_PHASE_C,
        DataAttribute.VOLTAGE_DIPS);
  }

  private static Map<String, RtuReadCommand<MeasurementDto>> rtuCommandMap() {

    final CommandsByAttributeBuilder builder = new CommandsByAttributeBuilder();

    final Set<DataAttribute> simpleCommandAttributes =
        EnumSet.of(
            DataAttribute.BEHAVIOR,
            DataAttribute.HEALTH,
            DataAttribute.MODE,
            DataAttribute.ALARM_ONE,
            DataAttribute.ALARM_TWO,
            DataAttribute.ALARM_THREE,
            DataAttribute.ALARM_FOUR,
            DataAttribute.ALARM_OTHER,
            DataAttribute.WARNING_ONE,
            DataAttribute.WARNING_TWO,
            DataAttribute.WARNING_THREE,
            DataAttribute.WARNING_FOUR,
            DataAttribute.WARNING_OTHER);
    builder.withSimpleCommandsFor(simpleCommandAttributes);

    final Set<DataAttribute> impedancePhaseCommandAttributes =
        EnumSet.of(
            DataAttribute.IMPEDANCE_PHASE_A,
            DataAttribute.IMPEDANCE_PHASE_B,
            DataAttribute.IMPEDANCE_PHASE_C);
    builder.withIndexedCommandsFor(
        impedancePhaseCommandAttributes, IMPEDANCE_PHASE_ID_START, IMPEDANCE_PHASE_ID_END);

    final Set<DataAttribute> voltageDipsCommandAttributes = EnumSet.of(DataAttribute.VOLTAGE_DIPS);
    builder.withIndexedCommandsFor(
        voltageDipsCommandAttributes, VOLTAGE_DIPS_ID_START, VOLTAGE_DIPS_ID_END);

    final Set<DataAttribute> powerFactorPhaseCommandAttributes =
        EnumSet.of(
            DataAttribute.POWER_FACTOR_PHASE_A,
            DataAttribute.POWER_FACTOR_PHASE_B,
            DataAttribute.POWER_FACTOR_PHASE_C);
    builder.withIndexedCommandsFor(
        powerFactorPhaseCommandAttributes, POWER_FACTOR_PHASE_ID_START, POWER_FACTOR_PHASE_ID_END);

    final Set<DataAttribute> frequencyCommandAttributes = EnumSet.of(DataAttribute.FREQUENCY);
    builder.withIndexedCommandsFor(
        frequencyCommandAttributes, FREQUENCY_ID_START, FREQUENCY_ID_END);

    final Set<DataAttribute> phaseToNeutralVoltagePhaseCommandAttributes =
        EnumSet.of(
            DataAttribute.PHASE_TO_NEUTRAL_VOLTAGE_PHASE_A,
            DataAttribute.PHASE_TO_NEUTRAL_VOLTAGE_PHASE_B,
            DataAttribute.PHASE_TO_NEUTRAL_VOLTAGE_PHASE_C);
    builder.withIndexedCommandsFor(
        phaseToNeutralVoltagePhaseCommandAttributes,
        PHASE_TO_NEUTRAL_VOLTAGE_PHASE_ID_START,
        PHASE_TO_NEUTRAL_VOLTAGE_PHASE_ID_END);

    return builder.build();
  }
}
