// SPDX-FileCopyrightText: Copyright Contributors to the GXF project
//
// SPDX-License-Identifier: Apache-2.0

package org.opensmartgridplatform.simulator.protocol.dlms.triggered.api;

import jakarta.ws.rs.DefaultValue;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.QueryParam;
import jakarta.ws.rs.core.Response;
import org.opensmartgridplatform.simulator.protocol.dlms.triggered.SimulatorThreadStarter;
import org.opensmartgridplatform.simulator.protocol.dlms.triggered.utils.PortTracker;

@Path("/trigger")
public class SimulatorTriggerResource {

  private static final PortTracker PORT_TRACKER = new PortTracker();

  @GET
  public Response startSimulator(
      @DefaultValue("4059") @QueryParam("port") final Long port,
      @DefaultValue("1") @QueryParam("logicalId") final Long logicalId) {

    if (PORT_TRACKER.isPortUsed(port)) {
      return Response.serverError().build();
    }

    final SimulatorThreadStarter triggerSimulatorThread = new SimulatorThreadStarter();
    triggerSimulatorThread.startSimulatorThread(port, logicalId);

    return Response.ok().build();
  }
}
