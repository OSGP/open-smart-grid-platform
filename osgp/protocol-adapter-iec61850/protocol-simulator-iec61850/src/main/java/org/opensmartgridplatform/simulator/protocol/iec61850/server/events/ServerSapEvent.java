//  SPDX-FileCopyrightText: Copyright Contributors to the GXF project
//  SPDX-License-Identifier: Apache-2.0
package org.opensmartgridplatform.simulator.protocol.iec61850.server.events;

import com.beanit.openiec61850.ServerSap;

public class ServerSapEvent {
  private final ServerSap serverSap;

  public ServerSapEvent(final ServerSap serverSap) {
    this.serverSap = serverSap;
  }

  public ServerSap getServerSap() {
    return this.serverSap;
  }
}
