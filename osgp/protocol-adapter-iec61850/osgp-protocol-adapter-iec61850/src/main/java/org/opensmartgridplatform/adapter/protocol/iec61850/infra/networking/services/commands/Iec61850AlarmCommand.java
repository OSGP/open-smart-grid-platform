// SPDX-FileCopyrightText: 2023 Contributors to the GXF project
// SPDX-FileCopyrightText: Copyright Contributors to the GXF project
//
// SPDX-License-Identifier: Apache-2.0

package org.opensmartgridplatform.adapter.protocol.iec61850.infra.networking.services.commands;

import com.beanit.openiec61850.Fc;
import java.util.HashMap;
import java.util.Map;
import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;
import org.opensmartgridplatform.adapter.protocol.iec61850.device.rtu.RtuReadCommand;
import org.opensmartgridplatform.adapter.protocol.iec61850.exceptions.NodeException;
import org.opensmartgridplatform.adapter.protocol.iec61850.infra.networking.Iec61850Client;
import org.opensmartgridplatform.adapter.protocol.iec61850.infra.networking.helper.DataAttribute;
import org.opensmartgridplatform.adapter.protocol.iec61850.infra.networking.helper.DeviceConnection;
import org.opensmartgridplatform.adapter.protocol.iec61850.infra.networking.helper.LogicalDevice;
import org.opensmartgridplatform.adapter.protocol.iec61850.infra.networking.helper.LogicalNode;
import org.opensmartgridplatform.adapter.protocol.iec61850.infra.networking.helper.NodeContainer;
import org.opensmartgridplatform.adapter.protocol.iec61850.infra.networking.helper.QualityConverter;
import org.opensmartgridplatform.adapter.protocol.iec61850.infra.networking.helper.SubDataAttribute;
import org.opensmartgridplatform.dto.valueobjects.microgrids.MeasurementDto;

public class Iec61850AlarmCommand implements RtuReadCommand<MeasurementDto> {

  private static final Map<Integer, DataAttribute> map;

  private static final int ONE = 1;
  private static final int TWO = 2;
  private static final int THREE = 3;
  private static final int FOUR = 4;

  static {
    map = new HashMap<>();
    map.put(ONE, DataAttribute.ALARM_ONE);
    map.put(TWO, DataAttribute.ALARM_TWO);
    map.put(THREE, DataAttribute.ALARM_THREE);
    map.put(FOUR, DataAttribute.ALARM_FOUR);
  }

  private int alarmIndex;

  public Iec61850AlarmCommand(final int alarmIndex) {
    this.alarmIndex = alarmIndex;
  }

  @Override
  public MeasurementDto execute(
      final Iec61850Client client,
      final DeviceConnection connection,
      final LogicalDevice logicalDevice,
      final int logicalDeviceIndex)
      throws NodeException {
    final NodeContainer containingNode =
        connection.getFcModelNode(
            logicalDevice,
            logicalDeviceIndex,
            LogicalNode.GENERIC_PROCESS_I_O,
            map.get(this.alarmIndex),
            Fc.ST);
    client.readNodeDataValues(
        connection.getConnection().getClientAssociation(), containingNode.getFcmodelNode());
    return this.translate(containingNode);
  }

  @Override
  public MeasurementDto translate(final NodeContainer containingNode) {
    return new MeasurementDto(
        1,
        map.get(this.alarmIndex).getDescription(),
        QualityConverter.toShort(containingNode.getQuality(SubDataAttribute.QUALITY).getValue()),
        new DateTime(containingNode.getDate(SubDataAttribute.TIME), DateTimeZone.UTC),
        this.translateStateValue(containingNode));
  }

  private int translateStateValue(final NodeContainer containingNode) {
    final int value;
    if (containingNode.getBoolean(SubDataAttribute.STATE).getValue()) {
      value = 1;
    } else {
      value = 0;
    }
    return value;
  }
}
