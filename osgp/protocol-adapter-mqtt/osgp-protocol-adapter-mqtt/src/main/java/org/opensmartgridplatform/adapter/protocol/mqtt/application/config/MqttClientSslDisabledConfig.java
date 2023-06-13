// SPDX-FileCopyrightText: Copyright Contributors to the GXF project
//
// SPDX-License-Identifier: Apache-2.0

package org.opensmartgridplatform.adapter.protocol.mqtt.application.config;

import com.hivemq.client.mqtt.MqttClientSslConfig;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Conditional;
import org.springframework.context.annotation.Configuration;

@Configuration
@Conditional(MqttClientSslDisabledCondition.class)
public class MqttClientSslDisabledConfig {

  private static final Logger LOG = LoggerFactory.getLogger(MqttClientSslDisabledConfig.class);

  @Bean
  public MqttClientSslConfig mqttClientSslConfig() {

    LOG.info("MQTT SSL DISABLED.");

    return null;
  }
}
