// SPDX-FileCopyrightText: 2023 Contributors to the GXF project
// SPDX-FileCopyrightText: Copyright Contributors to the GXF project
//
// SPDX-License-Identifier: Apache-2.0

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
