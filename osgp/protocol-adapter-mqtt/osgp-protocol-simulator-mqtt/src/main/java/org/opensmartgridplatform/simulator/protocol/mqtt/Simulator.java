/*
 * Copyright 2020 Smart Society Services B.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 */
package org.opensmartgridplatform.simulator.protocol.mqtt;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.hivemq.client.mqtt.MqttClientSslConfig;
import io.moquette.broker.config.MemoryConfig;
import java.io.File;
import java.io.IOException;
import java.util.Properties;
import org.opensmartgridplatform.simulator.protocol.mqtt.spec.SimulatorSpec;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.ClassPathResource;

public class Simulator {

  private static final Logger LOG = LoggerFactory.getLogger(Simulator.class);

  public static void main(final String[] args) throws IOException {
    final String spec = getFirstArgOrNull(args);
    final Properties sslServerProperties = new Properties();
    final MqttClientSslConfig sslClientProperties = null;
    final boolean startClient = getSecondArgOrTrue(args);
    final Simulator app = new Simulator();
    app.run(spec, startClient, sslServerProperties, sslClientProperties);
  }

  private static String getFirstArgOrNull(final String[] args) {
    String result = null;
    if (args.length > 0) {
      result = args[0];
    }
    return result;
  }

  private static boolean getSecondArgOrTrue(final String[] args) {
    if (args.length < 2) {
      return true;
    }
    return Boolean.parseBoolean(args[1]);
  }

  public void run(
      final String specJsonPath,
      final boolean startClient,
      final Properties brokerProperties,
      final MqttClientSslConfig clientSslConfig)
      throws IOException {
    this.run(this.getSimulatorSpec(specJsonPath), startClient, brokerProperties, clientSslConfig);
  }

  public void run(
      final SimulatorSpec simulatorSpec,
      final boolean startClient,
      final Properties brokerProperties,
      final MqttClientSslConfig clientSslConfig)
      throws IOException {
    final Broker broker = new Broker(new MemoryConfig(brokerProperties));
    broker.start();
    try {
      Thread.sleep(simulatorSpec.getStartupPauseMillis());
    } catch (final InterruptedException e) {
      LOG.warn("Interrupted sleep", e);
      Thread.currentThread().interrupt();
    }
    if (startClient) {
      final SimulatorSpecPublishingClient publishingClient =
          new SimulatorSpecPublishingClient(simulatorSpec, clientSslConfig);
      publishingClient.start();
    }
  }

  private SimulatorSpec getSimulatorSpec(final String jsonPath) throws IOException {
    final SimulatorSpec simulatorSpec;
    if (jsonPath != null) {
      File jsonFile = new File(jsonPath);
      if (!jsonFile.exists()) {
        final ClassPathResource jsonResource = new ClassPathResource(jsonPath);
        if (jsonResource.exists()) {
          jsonFile = jsonResource.getFile();
        } else {
          throw new IllegalArgumentException(
              String.format("Could not find file or class path resource %s", jsonPath));
        }
      }
      simulatorSpec = new ObjectMapper().readValue(jsonFile, SimulatorSpec.class);
    } else {
      simulatorSpec = new SimulatorSpec(Default.BROKER_HOST, Default.BROKER_PORT);
    }
    LOG.info("Simulator spec: {}", simulatorSpec);
    return simulatorSpec;
  }
}
