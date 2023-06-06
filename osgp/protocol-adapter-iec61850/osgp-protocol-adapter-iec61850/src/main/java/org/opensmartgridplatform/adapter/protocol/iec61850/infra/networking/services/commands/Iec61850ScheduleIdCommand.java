// SPDX-FileCopyrightText: Copyright Contributors to the GXF project
//
// SPDX-License-Identifier: Apache-2.0

package org.opensmartgridplatform.adapter.protocol.iec61850.infra.networking.services.commands;

import com.beanit.openiec61850.Fc;
import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;
import org.opensmartgridplatform.adapter.protocol.iec61850.device.rtu.RtuReadCommand;
import org.opensmartgridplatform.adapter.protocol.iec61850.device.rtu.RtuWriteCommand;
import org.opensmartgridplatform.adapter.protocol.iec61850.exceptions.NodeException;
import org.opensmartgridplatform.adapter.protocol.iec61850.exceptions.NodeWriteException;
import org.opensmartgridplatform.adapter.protocol.iec61850.infra.networking.Iec61850Client;
import org.opensmartgridplatform.adapter.protocol.iec61850.infra.networking.helper.DataAttribute;
import org.opensmartgridplatform.adapter.protocol.iec61850.infra.networking.helper.DeviceConnection;
import org.opensmartgridplatform.adapter.protocol.iec61850.infra.networking.helper.LogicalDevice;
import org.opensmartgridplatform.adapter.protocol.iec61850.infra.networking.helper.LogicalNode;
import org.opensmartgridplatform.adapter.protocol.iec61850.infra.networking.helper.NodeContainer;
import org.opensmartgridplatform.adapter.protocol.iec61850.infra.networking.helper.SubDataAttribute;
import org.opensmartgridplatform.dto.valueobjects.microgrids.MeasurementDto;
import org.opensmartgridplatform.dto.valueobjects.microgrids.SetPointDto;

public class Iec61850ScheduleIdCommand
    implements RtuReadCommand<MeasurementDto>, RtuWriteCommand<SetPointDto> {

  private static final String NODE_NAME = "DSCH";
  private static final DataAttribute DATA_ATTRIBUTE = DataAttribute.SCHEDULE_ID;
  private static final SubDataAttribute SUB_DATA_ATTRIBUTE = SubDataAttribute.SETPOINT_VALUE;
  private static final Fc FC = Fc.SP;

  private final LogicalNode logicalNode;
  private final int index;

  public Iec61850ScheduleIdCommand(final int index) {
    this.index = index;
    this.logicalNode = LogicalNode.fromString(NODE_NAME + index);
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
            logicalDevice, logicalDeviceIndex, this.logicalNode, DATA_ATTRIBUTE, FC);
    client.readNodeDataValues(
        connection.getConnection().getClientAssociation(), containingNode.getFcmodelNode());
    return this.translate(containingNode);
  }

  @Override
  public MeasurementDto translate(final NodeContainer containingNode) {
    return new MeasurementDto(
        this.index,
        DATA_ATTRIBUTE.getDescription(),
        0,
        new DateTime(DateTimeZone.UTC),
        containingNode.getInteger(SUB_DATA_ATTRIBUTE).getValue());
  }

  @Override
  public void executeWrite(
      final Iec61850Client client,
      final DeviceConnection connection,
      final LogicalDevice logicalDevice,
      final int logicalDeviceIndex,
      final SetPointDto setPoint)
      throws NodeException {

    final int value = this.checkValue(setPoint.getValue());

    final NodeContainer containingNode =
        connection.getFcModelNode(
            logicalDevice, logicalDeviceIndex, this.logicalNode, DATA_ATTRIBUTE, FC);
    containingNode.writeInteger(SUB_DATA_ATTRIBUTE, value);
  }

  private int checkValue(final double value) throws NodeWriteException {
    int result;
    try {
      result = (int) value;
    } catch (final ClassCastException e) {
      throw new NodeWriteException(String.format("Invalid value %f.", value), e);
    }
    return result;
  }
}
