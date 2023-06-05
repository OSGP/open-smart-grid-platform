// SPDX-FileCopyrightText: 2023 Contributors to the GXF project
// SPDX-FileCopyrightText: Copyright Contributors to the GXF project
//
// SPDX-License-Identifier: Apache-2.0

package org.opensmartgridplatform.simulator.protocol.mqtt;

import com.hivemq.client.mqtt.MqttClientSslConfig;
import java.io.IOException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class SimulatorConfig {

  private static final Logger LOG = LoggerFactory.getLogger(SimulatorConfig.class);

  @Bean
  @ConditionalOnProperty(name = "mqtt.simulator.startClient", havingValue = "true")
  public Simulator simulator(
      @Value("${mqtt.simulator.spec}") final String spec,
      @Value("${mqtt.simulator.client.clean.session:true}") final boolean cleanSession,
      @Value("${mqtt.simulator.client.keep.alive:60}") final int keepAlive,
      final MqttClientSslConfig mqttClientSslConfig)
      throws IOException {

    LOG.info("Start MQTT simulator with spec={}", spec);
    final Simulator app = new Simulator();
    app.run(spec, cleanSession, keepAlive, mqttClientSslConfig);
    return app;
  }
}
