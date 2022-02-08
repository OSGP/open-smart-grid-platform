/*
 * Copyright 2020 Smart Society Services B.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file
 * except in compliance with the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 */
package org.opensmartgridplatform.simulator.protocol.mqtt;

import com.hivemq.client.mqtt.MqttClientSslConfig;
import java.io.IOException;
import java.net.BindException;
import java.util.Properties;
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
      @Value("${mqtt.simulator.startClient}") final boolean startClient,
      @Value("${mqtt.simulator.client.clean.session:true}") final boolean cleanSession,
      @Value("${mqtt.simulator.client.keep.alive:60}") final int keepAlive,
      final Properties mqttBrokerProperties,
      final MqttClientSslConfig mqttClientSslConfig)
      throws IOException {
    LOG.info("Start MQTT simulator with spec={}, startClient={}", spec, startClient);
    final Simulator app = new Simulator();
    try {
      app.run(
          spec, startClient, cleanSession, keepAlive, mqttBrokerProperties, mqttClientSslConfig);
    } catch (final BindException e) {
      /*
       * Application context reloads may cause the simulator bean to be instantiated multiple times.
       * The simulator however starts an MQTT broker that runs on configured ports.
       * For now on the BindException ("Address already in use") swallow the exception in the hope
       * that the broker is usable. Log the error to leave information in case it is not.
       * An actual solution instead of this hack may be needed.
       */
      LOG.error(
          "Running the MQTT Simulator failed. Issues with the MQTT Broker may or may not occur.",
          e);
    }
    return app;
  }
}
