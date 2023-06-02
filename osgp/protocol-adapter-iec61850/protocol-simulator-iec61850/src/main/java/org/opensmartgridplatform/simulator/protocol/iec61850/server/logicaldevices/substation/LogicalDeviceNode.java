//  SPDX-FileCopyrightText: Copyright Contributors to the GXF project
//  SPDX-License-Identifier: Apache-2.0
package org.opensmartgridplatform.simulator.protocol.iec61850.server.logicaldevices.substation;

import com.beanit.openiec61850.ModelNode;
import com.beanit.openiec61850.ServerModel;
import java.util.NoSuchElementException;

public class LogicalDeviceNode {
  private final ServerModel serverModel;
  private final String serverName;

  public LogicalDeviceNode(final ServerModel serverModel) {
    this.serverModel = serverModel;
    this.serverName =
        serverModel.getChildren().stream()
            .findFirst()
            .map(ModelNode::getName)
            .orElseThrow(
                () ->
                    new NoSuchElementException(
                        "ServerModel does not contain any children, could not determine serverName."));
  }

  public ServerModel getServerModel() {
    return this.serverModel;
  }

  public String getServerName() {
    return this.serverName;
  }
}
