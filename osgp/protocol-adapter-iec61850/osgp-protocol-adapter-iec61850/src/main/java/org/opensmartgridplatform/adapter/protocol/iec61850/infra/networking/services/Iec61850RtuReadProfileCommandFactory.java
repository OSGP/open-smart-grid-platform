//  SPDX-FileCopyrightText: Copyright Contributors to the GXF project
//  SPDX-License-Identifier: Apache-2.0
package org.opensmartgridplatform.adapter.protocol.iec61850.infra.networking.services;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import org.opensmartgridplatform.adapter.protocol.iec61850.device.rtu.RtuReadCommand;
import org.opensmartgridplatform.adapter.protocol.iec61850.device.rtu.RtuReadCommandFactory;
import org.opensmartgridplatform.adapter.protocol.iec61850.infra.networking.helper.DataAttribute;
import org.opensmartgridplatform.adapter.protocol.iec61850.infra.networking.services.commands.Iec61850ScheduleAbsTimeCommand;
import org.opensmartgridplatform.dto.valueobjects.microgrids.ProfileDto;
import org.opensmartgridplatform.dto.valueobjects.microgrids.ProfileFilterDto;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public final class Iec61850RtuReadProfileCommandFactory
    implements RtuReadCommandFactory<ProfileDto, ProfileFilterDto> {

  private static final Logger LOGGER =
      LoggerFactory.getLogger(Iec61850RtuReadProfileCommandFactory.class);
  private static final int SCHEDULE_ID_START = 1;
  private static final int SCHEDULE_ID_END = 4;

  private static Iec61850RtuReadProfileCommandFactory instance;

  private static final Set<DataAttribute> DATA_ATTRIBUTE_USING_FILTER_ID_LIST = new HashSet<>();
  private static final Map<String, RtuReadCommand<ProfileDto>> RTU_COMMAND_MAP = new HashMap<>();

  static {
    initializeDataAttributeUsingFilterIdList();
    initializeRtuCommandMap();
  }

  private Iec61850RtuReadProfileCommandFactory() {}

  public static synchronized Iec61850RtuReadProfileCommandFactory getInstance() {
    if (instance == null) {
      instance = new Iec61850RtuReadProfileCommandFactory();
    }
    return instance;
  }

  @Override
  public RtuReadCommand<ProfileDto> getCommand(final ProfileFilterDto filter) {
    final DataAttribute dataAttribute = DataAttribute.fromString(filter.getNode());
    if (this.useFilterId(dataAttribute)) {
      return this.getCommand(filter.getNode() + filter.getId());
    } else {
      return this.getCommand(filter.getNode());
    }
  }

  @Override
  public RtuReadCommand<ProfileDto> getCommand(final String node) {
    final RtuReadCommand<ProfileDto> command = RTU_COMMAND_MAP.get(node);

    if (command == null) {
      LOGGER.warn("No command found for node {}", node);
    }
    return command;
  }

  private static void initializeDataAttributeUsingFilterIdList() {
    DATA_ATTRIBUTE_USING_FILTER_ID_LIST.add(DataAttribute.SCHEDULE_ABS_TIME);
  }

  private static void initializeRtuCommandMap() {
    for (int i = SCHEDULE_ID_START; i <= SCHEDULE_ID_END; i++) {
      RTU_COMMAND_MAP.put(
          DataAttribute.SCHEDULE_ABS_TIME.getDescription() + i,
          new Iec61850ScheduleAbsTimeCommand(i));
    }
  }

  private boolean useFilterId(final DataAttribute dataAttribute) {
    return DATA_ATTRIBUTE_USING_FILTER_ID_LIST.contains(dataAttribute);
  }
}
