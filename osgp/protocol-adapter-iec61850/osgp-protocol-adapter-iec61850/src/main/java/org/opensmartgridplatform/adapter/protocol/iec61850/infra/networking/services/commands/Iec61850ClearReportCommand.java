// SPDX-FileCopyrightText: Copyright Contributors to the GXF project
//
// SPDX-License-Identifier: Apache-2.0

package org.opensmartgridplatform.adapter.protocol.iec61850.infra.networking.services.commands;

import com.beanit.openiec61850.Fc;
import org.opensmartgridplatform.adapter.protocol.iec61850.exceptions.NodeException;
import org.opensmartgridplatform.adapter.protocol.iec61850.infra.networking.helper.DataAttribute;
import org.opensmartgridplatform.adapter.protocol.iec61850.infra.networking.helper.DeviceConnection;
import org.opensmartgridplatform.adapter.protocol.iec61850.infra.networking.helper.LogicalDevice;
import org.opensmartgridplatform.adapter.protocol.iec61850.infra.networking.helper.LogicalNode;
import org.opensmartgridplatform.adapter.protocol.iec61850.infra.networking.helper.NodeContainer;
import org.opensmartgridplatform.adapter.protocol.iec61850.infra.networking.helper.SubDataAttribute;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Iec61850ClearReportCommand {

  private static final Logger LOGGER = LoggerFactory.getLogger(Iec61850ClearReportCommand.class);

  public void clearBufferedReportOnDevice(final DeviceConnection deviceConnection)
      throws NodeException {
    final NodeContainer reporting =
        deviceConnection.getFcModelNode(
            LogicalDevice.LIGHTING, LogicalNode.LOGICAL_NODE_ZERO, DataAttribute.REPORTING, Fc.BR);

    reporting.writeBoolean(SubDataAttribute.ENABLE_REPORTING, false);
    reporting.writeBoolean(SubDataAttribute.PURGE_BUF, true);
    LOGGER.info(
        "Cleared (buffered reporting) event buffer for device: {}",
        deviceConnection.getDeviceIdentification());
  }

  public void disableUnbufferedReportOnDevice(final DeviceConnection deviceConnection)
      throws NodeException {
    final NodeContainer reporting =
        deviceConnection.getFcModelNode(
            LogicalDevice.LIGHTING, LogicalNode.LOGICAL_NODE_ZERO, DataAttribute.REPORTING, Fc.RP);

    reporting.writeBoolean(SubDataAttribute.ENABLE_REPORTING, false);
    LOGGER.info(
        "Stop unbuffered reporting for device: {}", deviceConnection.getDeviceIdentification());
  }
}
