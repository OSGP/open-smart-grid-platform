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

import io.moquette.BrokerConstants;
import java.util.Properties;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class SimulatorBrokerConfig {

  @Value("${mqtt.simulator.broker.host:localhost}")
  private String host;

  @Value("${mqtt.simulator.broker.port:1883}")
  private int port;

  @Value("${mqtt.simulator.ssl.enabled:false}")
  private boolean sslEnabled;

  @Value("${mqtt.simulator.broker.ssl.keystore.location}")
  private String sslServerKeystoreLocation;

  @Value("${mqtt.simulator.broker.ssl.keystore.password}")
  private String sslServerKeystorePassword;

  @Value("${mqtt.simulator.broker.ssl.keymanager.password}")
  private String sslServerKeymanagerPassword;

  @Value("${mqtt.simulator.broker.ssl.provider}")
  private String sslProvider;

  @Value("${mqtt.simulator.broker.ssl.port:8883}")
  private int sslPort;

  @Bean
  public Properties mqttBrokerProperties() {
    final Properties p = new Properties();
    p.put(BrokerConstants.HOST_PROPERTY_NAME, this.host);
    p.put(BrokerConstants.PORT_PROPERTY_NAME, String.valueOf(this.port));
    if (this.sslEnabled) {
      p.put(BrokerConstants.JKS_PATH_PROPERTY_NAME, this.sslServerKeystoreLocation);
      p.put(BrokerConstants.KEY_STORE_PASSWORD_PROPERTY_NAME, this.sslServerKeystorePassword);
      p.put(BrokerConstants.KEY_MANAGER_PASSWORD_PROPERTY_NAME, this.sslServerKeymanagerPassword);
      p.put(BrokerConstants.SSL_PROVIDER, this.sslProvider);
      p.put(BrokerConstants.SSL_PORT_PROPERTY_NAME, String.valueOf(this.sslPort));
    }
    return p;
  }
}
