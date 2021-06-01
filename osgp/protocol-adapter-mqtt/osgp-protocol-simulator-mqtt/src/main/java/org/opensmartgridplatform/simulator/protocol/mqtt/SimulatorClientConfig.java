/*
 * Copyright 2021 Alliander N.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 */
package org.opensmartgridplatform.simulator.protocol.mqtt;

import java.util.Properties;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class SimulatorClientConfig {

  @Value("${mqtt.simulator.ssl.enabled}")
  private boolean sslEnabled;

  @Value("${mqtt.simulator.client.ssl.truststore.location}")
  private String sslClientTruststoreLocation;

  @Value("${mqtt.simulator.client.ssl.truststore.password}")
  private String sslClientTruststorePassword;

  @Value("${mqtt.simulator.client.ssl.truststore.type}")
  private String sslClientTruststoreType;

  @Bean
  public Properties mqttClientProperties() {
    final Properties p = new Properties();
    if (this.sslEnabled) {
      p.put(ClientConstants.SSL_TRUSTSTORE_TYPE_PROPERTY_NAME, this.sslClientTruststoreType);
      p.put(
          ClientConstants.SSL_TRUSTSTORE_PASSWORD_PROPERTY_NAME, this.sslClientTruststorePassword);
      p.put(ClientConstants.SSL_TRUSTSTORE_PATH_PROPERTY_NAME, this.sslClientTruststoreLocation);
    }
    return p;
  }
}
