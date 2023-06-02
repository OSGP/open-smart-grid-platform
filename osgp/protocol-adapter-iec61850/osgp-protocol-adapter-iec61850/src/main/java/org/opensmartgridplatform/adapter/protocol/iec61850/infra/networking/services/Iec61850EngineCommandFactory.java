//  SPDX-FileCopyrightText: Copyright Contributors to the GXF project
//  SPDX-License-Identifier: Apache-2.0
package org.opensmartgridplatform.adapter.protocol.iec61850.infra.networking.services;

import java.util.EnumSet;
import java.util.Map;
import java.util.Set;
import org.opensmartgridplatform.adapter.protocol.iec61850.device.rtu.RtuReadCommand;
import org.opensmartgridplatform.adapter.protocol.iec61850.infra.networking.helper.DataAttribute;
import org.opensmartgridplatform.dto.valueobjects.microgrids.MeasurementDto;
import org.springframework.stereotype.Component;

@Component
public final class Iec61850EngineCommandFactory extends AbstractIec61850RtuReadCommandFactory {

  private static final int SCHEDULE_ID_START = 1;
  private static final int SCHEDULE_ID_END = 4;

  public Iec61850EngineCommandFactory() {
    super(rtuCommandMap(), dataAttributesUsingFilterId());
  }

  private static final Set<DataAttribute> dataAttributesUsingFilterId() {
    return EnumSet.of(
        DataAttribute.SCHEDULE_ID,
        DataAttribute.SCHEDULE_CAT,
        DataAttribute.SCHEDULE_CAT_RTU,
        DataAttribute.SCHEDULE_TYPE);
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

    final Set<DataAttribute> scheduleCommandAttributes =
        EnumSet.of(
            DataAttribute.SCHEDULE_ID,
            DataAttribute.SCHEDULE_CAT,
            DataAttribute.SCHEDULE_CAT_RTU,
            DataAttribute.SCHEDULE_TYPE);
    builder.withIndexedCommandsFor(scheduleCommandAttributes, SCHEDULE_ID_START, SCHEDULE_ID_END);

    return builder.build();
  }
}
