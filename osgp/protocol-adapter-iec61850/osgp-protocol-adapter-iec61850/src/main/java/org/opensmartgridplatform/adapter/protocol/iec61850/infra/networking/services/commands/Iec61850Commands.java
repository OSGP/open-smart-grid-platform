//  SPDX-FileCopyrightText: Copyright Contributors to the GXF project
//  SPDX-License-Identifier: Apache-2.0
package org.opensmartgridplatform.adapter.protocol.iec61850.infra.networking.services.commands;

import com.beanit.openiec61850.BdaBoolean;
import com.beanit.openiec61850.Fc;
import org.opensmartgridplatform.adapter.protocol.iec61850.domain.valueobjects.DeviceMessageLog;
import org.opensmartgridplatform.adapter.protocol.iec61850.exceptions.NodeException;
import org.opensmartgridplatform.adapter.protocol.iec61850.infra.networking.Iec61850Client;
import org.opensmartgridplatform.adapter.protocol.iec61850.infra.networking.helper.DataAttribute;
import org.opensmartgridplatform.adapter.protocol.iec61850.infra.networking.helper.DeviceConnection;
import org.opensmartgridplatform.adapter.protocol.iec61850.infra.networking.helper.LogicalDevice;
import org.opensmartgridplatform.adapter.protocol.iec61850.infra.networking.helper.LogicalNode;
import org.opensmartgridplatform.adapter.protocol.iec61850.infra.networking.helper.NodeContainer;
import org.opensmartgridplatform.adapter.protocol.iec61850.infra.networking.helper.SubDataAttribute;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Utility class containing helper methods for shared functionality among IEC61850 Command classes.
 */
public class Iec61850Commands {

  private static final Logger LOGGER = LoggerFactory.getLogger(Iec61850Commands.class);

  private Iec61850Commands() {
    // Utility class, non-instantiable.
  }

  /**
   * Checks if {@code CfSt.enbOper [CF]} for the given {@code logicalNode} is set to {@code true} on
   * the Lighting device, because this is necessary to be able to operate the relay.
   *
   * <p>If it is {@code false}, switching of the relay is enabled by writing boolean {@code true} to
   * {@code CfSt.enbOper [CF]}.
   */
  public static void enableOperationOfRelay(
      final DeviceConnection deviceConnection,
      final Iec61850Client iec61850Client,
      final DeviceMessageLog deviceMessageLog,
      final LogicalNode logicalNode,
      final Integer index)
      throws NodeException {

    final NodeContainer masterControl =
        deviceConnection.getFcModelNode(
            LogicalDevice.LIGHTING, logicalNode, DataAttribute.MASTER_CONTROL, Fc.CF);
    iec61850Client.readNodeDataValues(
        deviceConnection.getConnection().getClientAssociation(), masterControl.getFcmodelNode());

    final BdaBoolean enbOper = masterControl.getBoolean(SubDataAttribute.ENABLE_OPERATION);
    if (enbOper.getValue()) {
      LOGGER.info("masterControl.enbOper is true, switching of relay {} is enabled", index);
    } else {
      LOGGER.info("masterControl.enbOper is false, switching of relay {} is disabled", index);
      masterControl.writeBoolean(SubDataAttribute.ENABLE_OPERATION, true);
      LOGGER.info("set masterControl.enbOper to true to enable switching of relay {}", index);

      deviceMessageLog.addVariable(
          logicalNode,
          DataAttribute.MASTER_CONTROL,
          Fc.CF,
          SubDataAttribute.ENABLE_OPERATION,
          Boolean.toString(true));
    }
  }
}
