/*
 * Copyright 2017 Smart Society Services B.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file
 * except in compliance with the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 */
package org.opensmartgridplatform.simulator.protocol.iec61850.server.eventproducers;

import com.beanit.openiec61850.ServerSap;
import java.util.Timer;
import org.opensmartgridplatform.simulator.protocol.iec61850.server.events.ServerSapEvent;
import org.opensmartgridplatform.simulator.protocol.iec61850.server.tasks.UpdateValuesTask;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Component;

@Component
public class ServerSapEventProducer {
  private final ApplicationEventPublisher applicationEventPublisher;

  @Autowired
  public ServerSapEventProducer(final ApplicationEventPublisher applicationEventPublisher) {
    this.applicationEventPublisher = applicationEventPublisher;
  }

  public void createServerSapEvent(final ServerSap serverSap) {
    this.applicationEventPublisher.publishEvent(new ServerSapEvent(serverSap));
  }

  public void scheduleAtFixedRate(final ServerSap serverSap, final Long delay, final Long period) {
    final Timer timer = new Timer(true);
    if (delay != null && period != null) {
      timer.scheduleAtFixedRate(new UpdateValuesTask(serverSap, this), delay, period);
    }
  }
}
