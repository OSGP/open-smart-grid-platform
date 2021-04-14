/*
 * Copyright 2016 Smart Society Services B.V.
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

/**
 * @deprecated the structure of multiple mmxu/mmtr nodes within a single load device is replaced by
 *     multiple load devices with single mmxu/mmtr nodes. This code should be removed when all rtu
 *     devices are using the new structure
 */
@Deprecated
@Component
public final class Iec61850CombinedLoadCommandFactory
    extends AbstractIec61850RtuReadCommandFactory {

  private static final int POWER_ID_START = 1;
  private static final int POWER_ID_END = 5;
  private static final int ENERGY_ID_START = 1;
  private static final int ENERGY_ID_END = 5;

  private Iec61850CombinedLoadCommandFactory() {
    super(rtuCommandMap(), dataAttributesUsingFilterId());
  }

  private static final class Iec61850CombinedLoadCommandFactoryHolder {
    private static final Iec61850CombinedLoadCommandFactory instance =
        new Iec61850CombinedLoadCommandFactory();

    private Iec61850CombinedLoadCommandFactoryHolder() {
      throw new AssertionError("Noninstantiable static lazy initialization Holder class");
    }
  }

  public static Iec61850CombinedLoadCommandFactory getInstance() {
    return Iec61850CombinedLoadCommandFactoryHolder.instance;
  }

  private static final Set<DataAttribute> dataAttributesUsingFilterId() {
    return EnumSet.of(
        DataAttribute.ACTUAL_POWER,
        DataAttribute.MAX_ACTUAL_POWER,
        DataAttribute.MIN_ACTUAL_POWER,
        DataAttribute.TOTAL_ENERGY);
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

    final Set<DataAttribute> powerCommandAttributes =
        EnumSet.of(
            DataAttribute.ACTUAL_POWER,
            DataAttribute.MAX_ACTUAL_POWER,
            DataAttribute.MIN_ACTUAL_POWER);
    builder.withIndexedCommandsFor(powerCommandAttributes, POWER_ID_START, POWER_ID_END);

    final Set<DataAttribute> energyCommandAttributes = EnumSet.of(DataAttribute.TOTAL_ENERGY);
    builder.withIndexedCommandsFor(energyCommandAttributes, ENERGY_ID_START, ENERGY_ID_END);

    return builder.build();
  }
}
