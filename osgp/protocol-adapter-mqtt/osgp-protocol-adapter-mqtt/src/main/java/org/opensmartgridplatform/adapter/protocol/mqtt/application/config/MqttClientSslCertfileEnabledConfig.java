// SPDX-FileCopyrightText: 2023 Contributors to the GXF project
// SPDX-FileCopyrightText: Copyright Contributors to the GXF project
//
// SPDX-License-Identifier: Apache-2.0

package org.opensmartgridplatform.adapter.protocol.mqtt.application.config;

import com.hivemq.client.mqtt.MqttClientSslConfig;
import org.opensmartgridplatform.shared.application.config.mqtt.MqttClientSslConfigFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Conditional;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.Resource;

@Configuration
@Conditional(MqttClientSslCertfileEnabledCondition.class)
public class MqttClientSslCertfileEnabledConfig {
  @Bean
  public MqttClientSslConfig mqttClientSslConfig(
      @Value("${mqtt.client.ssl.certFile.location}") final Resource certFileLocation) {

    return MqttClientSslConfigFactory.getMqttClientSslConfig(certFileLocation);
  }
}
