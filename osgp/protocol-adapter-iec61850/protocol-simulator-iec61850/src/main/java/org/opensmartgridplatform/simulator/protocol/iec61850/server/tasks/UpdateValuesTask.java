// SPDX-FileCopyrightText: Copyright Contributors to the GXF project
//
// SPDX-License-Identifier: Apache-2.0

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
