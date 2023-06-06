// SPDX-FileCopyrightText: Copyright Contributors to the GXF project
//
// SPDX-License-Identifier: Apache-2.0

package org.opensmartgridplatform.simulator.protocol.mqtt;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.hivemq.client.mqtt.MqttClientSslConfig;
import java.io.File;
import java.io.IOException;
import org.opensmartgridplatform.simulator.protocol.mqtt.spec.SimulatorSpec;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.ClassPathResource;

public class Simulator {

  private static final Logger LOG = LoggerFactory.getLogger(Simulator.class);

  public static void main(final String[] args) throws IOException {
    final String spec = getFirstArgOrNull(args);
    final MqttClientSslConfig clientSslConfig = null;
    final Simulator app = new Simulator();
    app.run(spec, Default.CLEAN_SESSION, Default.KEEP_ALIVE, clientSslConfig);
  }

  private static String getFirstArgOrNull(final String[] args) {
    String result = null;
    if (args.length > 0) {
      result = args[0];
    }
    return result;
  }

  public void run(
      final String specJsonPath,
      final boolean cleanSession,
      final int keepAlive,
      final MqttClientSslConfig clientSslConfig)
      throws IOException {
    this.run(this.getSimulatorSpec(specJsonPath), cleanSession, keepAlive, clientSslConfig);
  }

  public void run(
      final SimulatorSpec simulatorSpec,
      final boolean cleanSession,
      final int keepAlive,
      final MqttClientSslConfig clientSslConfig) {

    try {
      Thread.sleep(simulatorSpec.getStartupPauseMillis());
    } catch (final InterruptedException e) {
      LOG.warn("Interrupted sleep", e);
      Thread.currentThread().interrupt();
    }
    final SimulatorSpecPublishingClient publishingClient =
        new SimulatorSpecPublishingClient(simulatorSpec, cleanSession, keepAlive, clientSslConfig);
    publishingClient.start();
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
