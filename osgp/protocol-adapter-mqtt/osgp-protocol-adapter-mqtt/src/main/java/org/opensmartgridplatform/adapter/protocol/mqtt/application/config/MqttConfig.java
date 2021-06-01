/*
 * Copyright 2020 Smart Society Services B.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 */
package org.opensmartgridplatform.adapter.protocol.mqtt.application.config;

import java.util.Properties;
import org.opensmartgridplatform.shared.application.config.AbstractConfig;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;

@Configuration
@PropertySource("classpath:osgp-adapter-protocol-mqtt.properties")
@PropertySource(value = "file:${osgp/Global/config}", ignoreResourceNotFound = true)
@PropertySource(value = "file:${osgp/AdapterProtocolMqtt/config}", ignoreResourceNotFound = true)
public class MqttConfig extends AbstractConfig {

  @Value("${mqtt.default.host:localhost}")
  private String defaultHost;

  @Value("${mqtt.default.port:1883}")
  private int defaultPort;

  @Value("${mqtt.default.qos:AT_LEAST_ONCE}")
  private String defaultQos;

  @Value("${mqtt.default.topics:+/measurement,+/congestion}")
  private String defaultTopics;

  @Value("${mqtt.ssl.enabled:false}")
  private boolean sslEnabled;

  @Value("${mqtt.ssl.client.truststore.location}")
  private String sslClientTruststoreLocation;

  @Value("${mqtt.ssl.client.truststore.password}")
  private String sslClientTruststorePassword;

  @Value("${mqtt.ssl.client.truststore.type}")
  private String sslClientTruststoreType;

  @Bean("mqttClientProperties")
  public Properties mqttClientProperties() {
    final Properties p = new Properties();

    p.put(MqttConstants.DEFAULT_HOST_PROPERTY_NAME, this.defaultHost);
    p.put(MqttConstants.DEFAULT_PORT_PROPERTY_NAME, String.valueOf(this.defaultPort));
    p.put(MqttConstants.DEFAULT_QOS_PROPERTY_NAME, this.defaultQos);
    p.put(MqttConstants.DEFAULT_TOPICS_PROPERTY_NAME, this.defaultTopics);

    if (this.sslEnabled) {
      p.put(MqttConstants.SSL_TRUSTSTORE_TYPE_PROPERTY_NAME, this.sslClientTruststoreType);
      p.put(MqttConstants.SSL_TRUSTSTORE_PASSWORD_PROPERTY_NAME, this.sslClientTruststorePassword);
      p.put(MqttConstants.SSL_TRUSTSTORE_PATH_PROPERTY_NAME, this.sslClientTruststoreLocation);
    }
    return p;
  }
}
