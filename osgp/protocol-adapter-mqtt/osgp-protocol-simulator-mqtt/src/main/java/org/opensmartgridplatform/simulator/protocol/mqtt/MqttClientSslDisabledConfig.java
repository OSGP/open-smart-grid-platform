//  SPDX-FileCopyrightText: Copyright Contributors to the GXF project
//  SPDX-License-Identifier: Apache-2.0
package org.opensmartgridplatform.simulator.protocol.mqtt;

import com.hivemq.client.mqtt.MqttClientSslConfig;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConditionalOnProperty(
    name = "mqtt.simulator.ssl.enabled",
    havingValue = "false",
    matchIfMissing = true)
public class MqttClientSslDisabledConfig {

  @Bean
  public MqttClientSslConfig mqttClientSslConfig() {
    return null;
  }
}
