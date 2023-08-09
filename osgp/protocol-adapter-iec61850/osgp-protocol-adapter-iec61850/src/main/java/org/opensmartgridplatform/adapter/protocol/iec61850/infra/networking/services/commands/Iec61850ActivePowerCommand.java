// SPDX-FileCopyrightText: Copyright Contributors to the GXF project
//
// SPDX-License-Identifier: Apache-2.0

package org.opensmartgridplatform.adapter.protocol.iec61850.infra.networking.services.commands;

import com.beanit.openiec61850.Fc;
import java.time.ZoneId;
import java.time.ZonedDateTime;
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

public class Iec61850ActivePowerCommand implements RtuReadCommand<MeasurementDto> {

  private final LogicalNode logicalNode;
  private final DataAttribute dataAttribute;
  private final int index;

  public Iec61850ActivePowerCommand(final int index, final DataAttribute dataAttribute) {
    this.logicalNode = LogicalNode.fromString("MMXU" + index);
    this.index = index;
    this.dataAttribute = dataAttribute;
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
            logicalDevice, logicalDeviceIndex, this.logicalNode, this.dataAttribute, Fc.MX);
    client.readNodeDataValues(
        connection.getConnection().getClientAssociation(), containingNode.getFcmodelNode());
    return this.translate(containingNode);
  }

  @Override
  public MeasurementDto translate(final NodeContainer containingNode) {
    return new MeasurementDto(
        this.index,
        this.dataAttribute.getDescription(),
        QualityConverter.toShort(containingNode.getQuality(SubDataAttribute.QUALITY).getValue()),
        ZonedDateTime.ofInstant(
            containingNode.getDate(SubDataAttribute.TIME).toInstant(), ZoneId.of("UTC")),
        containingNode
            .getChild(SubDataAttribute.C_VALUES)
            .getChild(SubDataAttribute.MAGNITUDE)
            .getFloat(SubDataAttribute.FLOAT)
            .getFloat());
  }
}
