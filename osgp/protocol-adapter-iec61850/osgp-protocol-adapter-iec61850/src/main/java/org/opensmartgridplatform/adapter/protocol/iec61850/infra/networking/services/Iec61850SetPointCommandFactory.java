// SPDX-FileCopyrightText: 2023 Contributors to the GXF project
// SPDX-FileCopyrightText: Copyright Contributors to the GXF project
//
// SPDX-License-Identifier: Apache-2.0

package org.opensmartgridplatform.adapter.protocol.iec61850.infra.networking.services;

import java.util.HashMap;
import java.util.Map;
import org.opensmartgridplatform.adapter.protocol.iec61850.device.rtu.RtuWriteCommand;
import org.opensmartgridplatform.adapter.protocol.iec61850.device.rtu.RtuWriteCommandFactory;
import org.opensmartgridplatform.adapter.protocol.iec61850.infra.networking.helper.DataAttribute;
import org.opensmartgridplatform.adapter.protocol.iec61850.infra.networking.services.commands.Iec61850ScheduleCatCommand;
import org.opensmartgridplatform.adapter.protocol.iec61850.infra.networking.services.commands.Iec61850ScheduleIdCommand;
import org.opensmartgridplatform.adapter.protocol.iec61850.infra.networking.services.commands.Iec61850ScheduleTypeCommand;
import org.opensmartgridplatform.dto.valueobjects.microgrids.SetPointDto;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public final class Iec61850SetPointCommandFactory implements RtuWriteCommandFactory<SetPointDto> {

  private static final Logger LOGGER =
      LoggerFactory.getLogger(Iec61850SetPointCommandFactory.class);

  private static final int ID_START = 1;
  private static final int ID_END = 4;

  private static final Map<String, RtuWriteCommand<SetPointDto>> RTU_COMMAND_MAP = new HashMap<>();

  static {
    initializeCommandMap();
  }

  private static Iec61850SetPointCommandFactory instance;

  private Iec61850SetPointCommandFactory() {}

  public static synchronized Iec61850SetPointCommandFactory getInstance() {
    if (instance == null) {
      instance = new Iec61850SetPointCommandFactory();
    }
    return instance;
  }

  @Override
  public RtuWriteCommand<SetPointDto> getCommand(final String node) {

    final RtuWriteCommand<SetPointDto> command = RTU_COMMAND_MAP.get(node);

    if (command == null) {
      LOGGER.warn("No command found for data attribute {}", node);
    }
    return command;
  }

  private static void initializeCommandMap() {
    for (int i = ID_START; i <= ID_END; i++) {
      RTU_COMMAND_MAP.put(
          createMapKey(DataAttribute.SCHEDULE_ID, i), new Iec61850ScheduleIdCommand(i));
      RTU_COMMAND_MAP.put(
          createMapKey(DataAttribute.SCHEDULE_TYPE, i), new Iec61850ScheduleTypeCommand(i));
      RTU_COMMAND_MAP.put(
          createMapKey(DataAttribute.SCHEDULE_CAT, i), new Iec61850ScheduleCatCommand(i));
    }
  }

  private static String createMapKey(final DataAttribute dataAttribute, final int index) {
    return dataAttribute.getDescription() + index;
  }
}
