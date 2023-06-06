// SPDX-FileCopyrightText: Copyright Contributors to the GXF project
//
// SPDX-License-Identifier: Apache-2.0

package org.opensmartgridplatform.adapter.protocol.iec61850.infra.networking.services;

import java.util.EnumSet;
import java.util.Map;
import java.util.Set;
import org.opensmartgridplatform.adapter.protocol.iec61850.device.rtu.RtuReadCommand;
import org.opensmartgridplatform.adapter.protocol.iec61850.infra.networking.helper.DataAttribute;
import org.opensmartgridplatform.dto.valueobjects.microgrids.MeasurementDto;
import org.springframework.stereotype.Component;

@Component
public final class Iec61850WindCommandFactory extends AbstractIec61850RtuReadCommandFactory {

  private static final int NUMBER_OF_MMXU_NODES = 3;
  private static final int ACTIVE_POWER_PHASE_ID_START = 1;
  private static final int ACTIVE_POWER_PHASE_ID_END = NUMBER_OF_MMXU_NODES;

  public Iec61850WindCommandFactory() {
    super(rtuCommandMap(), dataAttributesUsingFilterId());
  }

  private static final Set<DataAttribute> dataAttributesUsingFilterId() {
    return EnumSet.of(
        DataAttribute.ACTIVE_POWER_PHASE_A,
        DataAttribute.ACTIVE_POWER_PHASE_B,
        DataAttribute.ACTIVE_POWER_PHASE_C);
  }

  private static Map<String, RtuReadCommand<MeasurementDto>> rtuCommandMap() {

    final CommandsByAttributeBuilder builder = new CommandsByAttributeBuilder();

    final Set<DataAttribute> simpleCommandAttributes =
        EnumSet.of(
            DataAttribute.BEHAVIOR,
            DataAttribute.HEALTH,
            DataAttribute.OPERATIONAL_HOURS,
            DataAttribute.MODE,
            DataAttribute.ACTUAL_POWER,
            DataAttribute.MAX_ACTUAL_POWER,
            DataAttribute.MIN_ACTUAL_POWER,
            DataAttribute.ACTUAL_POWER_LIMIT,
            DataAttribute.TOTAL_ENERGY,
            DataAttribute.AVERAGE_POWER_FACTOR,
            DataAttribute.STATE,
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

    final Set<DataAttribute> activePowerPhaseCommandAttributes =
        EnumSet.of(
            DataAttribute.ACTIVE_POWER_PHASE_A,
            DataAttribute.ACTIVE_POWER_PHASE_B,
            DataAttribute.ACTIVE_POWER_PHASE_C);
    builder.withIndexedCommandsFor(
        activePowerPhaseCommandAttributes, ACTIVE_POWER_PHASE_ID_START, ACTIVE_POWER_PHASE_ID_END);

    return builder.build();
  }
}
