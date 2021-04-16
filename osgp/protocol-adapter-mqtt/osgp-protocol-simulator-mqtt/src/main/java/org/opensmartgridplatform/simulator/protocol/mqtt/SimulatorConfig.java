/*
 * Copyright 2020 Smart Society Services B.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file
 * except in compliance with the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 */
package org.opensmartgridplatform.simulator.protocol.mqtt;

import java.io.IOException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class SimulatorConfig {

  private static final Logger LOG = LoggerFactory.getLogger(SimulatorConfig.class);

  @Bean
  public Simulator simulator(
      @Value("${mqtt.simulator.spec}") final String spec,
      @Value("${mqtt.simulator.startClient}") final boolean startClient)
      throws IOException {
    LOG.info("Start MQTT simulator with spec={}, startClient={}", spec, startClient);
    final Simulator app = new Simulator();
    app.run(spec, startClient);
    return app;
  }
}
