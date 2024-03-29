// SPDX-FileCopyrightText: Copyright Contributors to the GXF project
//
// SPDX-License-Identifier: Apache-2.0

package org.opensmartgridplatform.adapter.protocol.iec61850.infra.networking.services.commands;

import com.beanit.openiec61850.Fc;
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

/**
 * @deprecated the structure of multiple mmxu/mmtr nodes within a single load device is replaced by
 *     multiple load devices with single mmxu/mmtr nodes. This code should be removed when all rtu
 *     devices are using the new structure
 */
@Deprecated
public class Iec61850LoadMinimumActualPowerCommand implements RtuReadCommand<MeasurementDto> {

  private LogicalNode logicalNode;
  private int index;

  public Iec61850LoadMinimumActualPowerCommand(final int index) {
    this.logicalNode = LogicalNode.fromString("MMXU" + index);
    this.index = index;
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
            this.logicalNode,
            DataAttribute.MIN_ACTUAL_POWER,
            Fc.MX);
    client.readNodeDataValues(
        connection.getConnection().getClientAssociation(), containingNode.getFcmodelNode());
    return this.translate(containingNode);
  }

  @Override
  public MeasurementDto translate(final NodeContainer containingNode) {
    return new MeasurementDto(
        this.index,
        DataAttribute.MIN_ACTUAL_POWER.getDescription(),
        QualityConverter.toShort(containingNode.getQuality(SubDataAttribute.QUALITY).getValue()),
        new DateTime(containingNode.getDate(SubDataAttribute.TIME), DateTimeZone.UTC),
        containingNode
            .getChild(SubDataAttribute.MAGNITUDE)
            .getFloat(SubDataAttribute.FLOAT)
            .getFloat());
  }
}
