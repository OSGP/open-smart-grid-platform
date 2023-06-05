// SPDX-FileCopyrightText: 2023 Contributors to the GXF project
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
public final class Iec61850PvCommandFactory extends AbstractIec61850RtuReadCommandFactory {

  public Iec61850PvCommandFactory() {
    super(rtuCommandMap(), dataAttributesUsingFilterId());
  }

  private static final Set<DataAttribute> dataAttributesUsingFilterId() {
    return EnumSet.noneOf(DataAttribute.class);
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

    return builder.build();
  }
}
