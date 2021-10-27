/*
 * Copyright 2016 Smart Society Services B.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file
 * except in compliance with the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 */
package org.opensmartgridplatform.simulator.protocol.dlms.triggered.api;

import javax.ws.rs.DefaultValue;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Response;
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
