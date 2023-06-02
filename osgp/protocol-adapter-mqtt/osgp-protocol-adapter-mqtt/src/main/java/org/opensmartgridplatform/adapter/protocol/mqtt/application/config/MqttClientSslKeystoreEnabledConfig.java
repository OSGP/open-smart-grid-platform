//  SPDX-FileCopyrightText: Copyright Contributors to the GXF project
//  SPDX-License-Identifier: Apache-2.0
package org.opensmartgridplatform.adapter.protocol.mqtt.application.config;

import com.hivemq.client.mqtt.MqttClientSslConfig;
import org.opensmartgridplatform.shared.application.config.mqtt.MqttClientSslConfigFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Conditional;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.Resource;

@Configuration
@Conditional(MqttClientSslKeystoreEnabledCondition.class)
public class MqttClientSslKeystoreEnabledConfig {
  @Bean
  public MqttClientSslConfig mqttClientSslConfig(
      @Value("${mqtt.client.ssl.truststore.location}") final Resource truststoreLocation,
      @Value("${mqtt.client.ssl.truststore.password}") final String truststorePassword,
      @Value("${mqtt.client.ssl.truststore.type}") final String truststoreType) {

    return MqttClientSslConfigFactory.getMqttClientSslConfig(
        truststoreLocation, truststorePassword, truststoreType);
  }
}
