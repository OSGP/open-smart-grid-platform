// SPDX-FileCopyrightText: Copyright Contributors to the GXF project
//
// SPDX-License-Identifier: Apache-2.0

package org.opensmartgridplatform.adapter.protocol.iec61850.infra.networking.services;

import java.util.HashMap;
import java.util.Map;
import org.opensmartgridplatform.adapter.protocol.iec61850.device.rtu.RtuWriteCommand;
import org.opensmartgridplatform.adapter.protocol.iec61850.device.rtu.RtuWriteCommandFactory;
import org.opensmartgridplatform.adapter.protocol.iec61850.infra.networking.helper.DataAttribute;
import org.opensmartgridplatform.adapter.protocol.iec61850.infra.networking.services.commands.Iec61850ScheduleAbsTimeCommand;
import org.opensmartgridplatform.dto.valueobjects.microgrids.ProfileDto;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public final class Iec61850WriteProfileCommandFactory
    implements RtuWriteCommandFactory<ProfileDto> {

  private static final Logger LOGGER =
      LoggerFactory.getLogger(Iec61850WriteProfileCommandFactory.class);

  private static final int ID_START = 1;
  private static final int ID_END = 4;

  private static final Map<String, RtuWriteCommand<ProfileDto>> RTU_COMMAND_MAP = new HashMap<>();

  static {
    initializeCommandMap();
  }

  private static Iec61850WriteProfileCommandFactory instance;

  private Iec61850WriteProfileCommandFactory() {}

  public static synchronized Iec61850WriteProfileCommandFactory getInstance() {
    if (instance == null) {
      instance = new Iec61850WriteProfileCommandFactory();
    }
    return instance;
  }

  @Override
  public RtuWriteCommand<ProfileDto> getCommand(final String node) {

    final RtuWriteCommand<ProfileDto> command = RTU_COMMAND_MAP.get(node);

    if (command == null) {
      LOGGER.warn("No command found for data attribute {}", node);
    }
    return command;
  }

  private static void initializeCommandMap() {
    for (int i = ID_START; i <= ID_END; i++) {

      RTU_COMMAND_MAP.put(
          createMapKey(DataAttribute.SCHEDULE_ABS_TIME, i), new Iec61850ScheduleAbsTimeCommand(i));
    }
  }

  private static String createMapKey(final DataAttribute dataAttribute, final int index) {
    return dataAttribute.getDescription() + index;
  }
}
