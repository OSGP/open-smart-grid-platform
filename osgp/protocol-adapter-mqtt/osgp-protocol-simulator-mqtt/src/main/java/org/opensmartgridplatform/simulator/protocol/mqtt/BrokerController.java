// SPDX-FileCopyrightText: 2023 Contributors to the GXF project
// SPDX-FileCopyrightText: Copyright Contributors to the GXF project
//
// SPDX-License-Identifier: Apache-2.0

package org.opensmartgridplatform.simulator.protocol.mqtt;

import io.moquette.broker.ClientDescriptor;
import java.io.IOException;
import java.util.Collection;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/broker")
public class BrokerController {
  private static final Logger LOGGER = LoggerFactory.getLogger(BrokerController.class);

  private final Broker broker;

  public BrokerController(final Broker broker) {
    this.broker = broker;
  }

  @GetMapping(path = "/start")
  public void start() throws IOException {
    LOGGER.info("Starting broker");
    this.broker.start();
  }

  @GetMapping(path = "/stop")
  public void stop() {
    LOGGER.info("Stopping broker");
    this.broker.stop();
  }

  @GetMapping(path = "/clients")
  public Collection<ClientDescriptor> clients() {
    return this.broker.getConnectedClients();
  }
}
