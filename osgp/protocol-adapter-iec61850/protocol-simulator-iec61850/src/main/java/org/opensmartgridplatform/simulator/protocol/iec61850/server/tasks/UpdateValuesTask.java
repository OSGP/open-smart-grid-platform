/*
 * Copyright 2017 Smart Society Services B.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file
 * except in compliance with the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 */
package org.opensmartgridplatform.simulator.protocol.iec61850.server.tasks;

import com.beanit.openiec61850.ServerSap;
import java.util.TimerTask;
import org.opensmartgridplatform.simulator.protocol.iec61850.server.eventproducers.ServerSapEventProducer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class UpdateValuesTask extends TimerTask {
  private static final Logger LOGGER = LoggerFactory.getLogger(UpdateValuesTask.class);
  private final ServerSap serverSap;
  private final ServerSapEventProducer serverSapEventProducer;

  public UpdateValuesTask(
      final ServerSap serverSap, final ServerSapEventProducer serverSapEventProducer) {
    this.serverSap = serverSap;
    this.serverSapEventProducer = serverSapEventProducer;
  }

  @Override
  public void run() {
    LOGGER.debug("Publish update values event");
    this.serverSapEventProducer.createServerSapEvent(this.serverSap);
  }
}
