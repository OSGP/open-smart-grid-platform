// SPDX-FileCopyrightText: Copyright Contributors to the GXF project
//
// SPDX-License-Identifier: Apache-2.0

package org.opensmartgridplatform.adapter.protocol.dlms.application.config;

import org.opensmartgridplatform.adapter.protocol.dlms.simulator.trigger.SimulatorTriggerClient;
import org.opensmartgridplatform.adapter.protocol.dlms.simulator.trigger.SimulatorTriggerClientException;
import org.opensmartgridplatform.shared.application.config.AbstractConfig;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;

@Configuration
@PropertySource("classpath:osgp-adapter-protocol-dlms.properties")
@PropertySource(value = "file:${osgp/Global/config}", ignoreResourceNotFound = true)
@PropertySource(value = "file:${osgp/AdapterProtocolDlms/config}", ignoreResourceNotFound = true)
public class SimulatorTriggerConfig extends AbstractConfig {

  @Value("${web.service.truststore.location}")
  private String truststoreLocation;

  @Value("${web.service.truststore.password}")
  private String truststorePassword;

  @Value("${web.service.truststore.type}")
  private String truststoreType;

  @Value("${triggered.simulator.url}")
  private String baseAddress;

  @Bean
  public SimulatorTriggerClient simulatorTriggerClient() throws SimulatorTriggerClientException {
    return new SimulatorTriggerClient(
        this.truststoreLocation, this.truststorePassword, this.truststoreType, this.baseAddress);
  }
}
